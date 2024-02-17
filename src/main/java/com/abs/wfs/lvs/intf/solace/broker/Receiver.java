package com.abs.wfs.lvs.intf.solace.broker;

import com.abs.wfs.lvs.service.LvsLogStoreManager;
import com.abs.wfs.lvs.util.ApplicationContextProvider;
import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.code.LvsConstant;
import com.abs.wfs.lvs.util.vo.LogMessageVo;
import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class Receiver implements Runnable {


	private JCSMPSession session;
	private EndpointProperties endPointProps;
	private FlowReceiver consumer;
//	private String module_name;
	private String thread_name;
	private String topic_name;

	private boolean stopFlagOn = false;
	

	public Receiver(JCSMPSession session, String thread_name, String topic_name) {
		this.session = session;
		this.topic_name = topic_name;
		this.thread_name = thread_name;
	}

	@Override
	public void run() {
		try {
			log.info("Receiver Thread Start # " + this.thread_name);

			// Queue - SolAdmin에서 생성한 queue에 접속, SolAdmin에 생성되지 않은 경우 Application에서 생성
			final Topic topic = JCSMPFactory.onlyInstance().createTopic(topic_name);

			final CountDownLatch latch = new CountDownLatch(1); // used for
			// synchronizing b/w threads
			/** Anonymous inner-class for MessageListener
			 *  This demonstrates the async threaded message callback */
			final XMLMessageConsumer cons = session.getMessageConsumer(new XMLMessageListener() {
				@SneakyThrows
				@Override
				public void onReceive(BytesXMLMessage msg) {
					String payload = "";

					SDTMap userProperty = msg.getProperties();
					String logLevel = userProperty.getString("logLevel");
					String threadName = userProperty.getString("threadName");
					Long timestamp = userProperty.getLong("timestamp");
					String classTrace = userProperty.getString("classTrace");


					if ( msg instanceof TextMessage) {
						payload = ((TextMessage) msg).getText();
					} else {
						payload = new String( msg.getBytes(), "UTF-8");
					}


					if(payload.length() == 0){
						log.warn("Message has been passed. ClassTrace: {} ", classTrace);
						return;
					}

					try{
						int payloadColonIndex = payload.indexOf(":");
						LogMessageVo vo = LogMessageVo.builder()
								.timestamp(timestamp)
								.logLevel(logLevel)
								.threadName(threadName)
								.classTrace(classTrace)
								.logName(classTrace.substring(classTrace.lastIndexOf(".")+ 1 ))
								.messageKey(payload.substring(0, payloadColonIndex))
								.payload(payload.substring(payloadColonIndex + 2))
								.build();
						LvsLogStoreManager manager = ApplicationContextProvider.getBean(LvsLogStoreManager.class);


						if((vo.getMessageKey().length() > 30 && vo.getMessageKey().length() < 60)){

							manager.execute(vo);
						}
					}catch (Exception e){
						log.error("payload {}",payload );
						e.printStackTrace();
					}



					latch.countDown();  // unblock main thread
				}

				@Override
				public void onException(JCSMPException e) {
					System.out.printf("Consumer received exception: %s%n",e);
					latch.countDown();  // unblock main thread
				}
			});
			session.addSubscription(topic);
			System.out.println("Connected. Awaiting message...");
			cons.start();

			try {
				latch.await(); // block here until message received, and latch will flip
			} catch (InterruptedException e) {
				System.out.println("I was awoken while waiting");
			}



		} catch (InvalidPropertiesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JCSMPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void switchStopFlag(){
		try {
			this.session.deleteSubscriber();
		} catch (JCSMPException e) {
			e.printStackTrace();
		}
		this.stopFlagOn = true;
	}


	public boolean stopReceiver() throws JCSMPInterruptedException {
 
//		this.consumer.stopSync();
		this.switchStopFlag();
		this.consumer.stop();
		log.info("Consumer Stop!!");
		return true;
	}

}
