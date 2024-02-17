package com.abs.wfs.lvs.config;

import com.abs.wfs.lvs.intf.solace.InterfaceSolacePub;
import com.abs.wfs.lvs.intf.solace.InterfaceSolaceSub;
import com.abs.wfs.lvs.util.LvsCommonUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class LvsPropertyObject {


    Environment env;
    @Value("${ap.info.group}")
    private String groupName;
    @Value("${ap.info.site}")
    private String siteName;
    @Value("${ap.info.env}")
    private String envType;
    @Value("${ap.info.sequence}")
    private String processSeq;
    
    private String clientName;

    @Value("${ap.interface.destination.receive.topic}")
    private String receiveTopicName;
    @Value("${ap.interface.destination.receive.queue}")
    private String receiveQueueName;
    @Value("${ap.interface.destination.receive.init}")
    private String receiveInitTopic;
    @Value("${ap.interface.destination.send.topic}")
    private String sendTopicName;
    
    @Value("${ap.shutdown.force.timeout.ms}")
    private int apShutdownForceTimeoutMs;

    @Value("${ap.shutdown.polling.interval.ms}")
    private int apShutdownPollingIntervalMs;

    @Value("${ap.worker.pool-size.core}")
    private int corePoolSize;  // 기본 실행 대기하는 Thread 수

    @Value("${ap.worker.pool-size.max}")
    private int maxPoolSize;  // 동시 동작하는 최대 Thread 수

    @Value("${ap.worker.capacity}")
    private int queueCapacity;  // MaxPoolSize 초과 요청 시, 최대 Queue 저장 수

    @Value("${ap.worker.name.prefix}")
    private String threadPrefixName; // 생성되는 Thread 접두사 명


    // 프로세스에서 사용하는 룰 객체 
//    private List<ParseRuleVo> parsingRule;
//    private List<ParseRuleRelVo> mappingRule;
    
    // 패치 예정인 룰정보

    private InterfaceSolaceSub interfaceSolaceSub;

    private InterfaceSolacePub interfaceSolacePub;
    


    private static LvsPropertyObject instance;

    // Public method to get the Singleton instance
    public static LvsPropertyObject createInstance(Environment env) {
        if (instance == null) {
            synchronized (LvsPropertyObject.class) {
                // Double-check to ensure only one instance is created
                if (instance == null) {
                    instance = new LvsPropertyObject(env);
                }
            }
        }

        if(instance.clientName == null){
            instance.clientName = LvsCommonUtil.generateClientName(instance.groupName, instance.siteName, instance.envType, instance.processSeq);
        }


        return instance;
    }

    public static LvsPropertyObject getInstance(){
        return instance;
    }
    public LvsPropertyObject(Environment env) {
        this.env = env;
        instance = this;
    }


    public void setInterfaceSolaceSub(InterfaceSolaceSub interfaceSolaceSub) {
        this.interfaceSolaceSub = interfaceSolaceSub;
    }

    public void setInterfaceSolacePub(InterfaceSolacePub interfaceSolacePub) {
        this.interfaceSolacePub = interfaceSolacePub;
    }
    

    
    

}
