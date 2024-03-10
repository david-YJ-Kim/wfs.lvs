package com.abs.wfs.lvs.service;

import com.abs.wfs.lvs.util.CommonDate;
import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.code.LogNameConstant;
import com.abs.wfs.lvs.util.code.LvsConstant;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LvsLogStoreManager  {


    /**
     * Scenario 여러 개의 event key로 구성된 이벤트 들의 흐름
     * EventLog는 하나의 event key로 발생한 여러 로그의 집합
     *
     * Scenario map | key: eqp ID / value : log key list
     */
    ConcurrentHashMap<String, ArrayList<EventStreamVo>> scenarioCollection = null;
    ConcurrentHashMap<String, ArrayList<EventLogVo>> eventStreamCollection = null;


    private void initializeDataStore(){
        this.scenarioCollection = LvsDataStore.getInstance().getScenarioOngoingEqpMap();
        this.eventStreamCollection = LvsDataStore.getInstance().getLogMessageMap();
    }

    /**
     * 신규  log 발생 시, 처리 로직 수행
     * 1.
     * @param vo
     */
    public void execute(EventLogVo vo){
        log.info(vo.toString());

        if(this.scenarioCollection == null || this.scenarioCollection == null){
            this.initializeDataStore();
        }

        // Store logs with message key
        if(this.eventStreamCollection.containsKey(vo.getMessageKey())){
            this.eventStreamCollection.get(vo.getMessageKey()).add(vo);

        }else{
            ArrayList logList = new ArrayList<>();
            logList.add(vo);
            this.eventStreamCollection.put(vo.getMessageKey(), logList);

//            EventStreamVo eventLogCollectionVo = EventStreamVo.builder()
//                                                .messageKey(vo.getMessageKey())
//                                                .build();
//            LvsDataStore.getInstance().getLogMessageMap().put(vo.getMessageKey(), eventLogCollectionVo);
//            log.info("New messageKey begin");
        }
        log.info("key:{} stored in Log Map. size: {}", vo.getMessageKey(), LvsDataStore.getInstance().getLogMessageMap().size());




        /**
         * Set Scenario Key : eqpId
         */
        if(vo.getLogName().compareTo(LogNameConstant.ScenarioStartLog.name()) == 0){

            ArrayList<EventStreamVo> streamVoArrayList = null;
            EventStreamVo eventStreamVo = this.generateEventStreamVo(vo);

            if(this.scenarioCollection.containsKey(eventStreamVo.getEqpId())){
                streamVoArrayList = this.scenarioCollection.get(eventStreamVo.getEqpId());

            }else{
                streamVoArrayList = new ArrayList<>();
            }
            streamVoArrayList.add(eventStreamVo);
            this.scenarioCollection.put(eventStreamVo.getEqpId(), streamVoArrayList);

        }



    }


    /**
     * Get payload and parsing to get additional data
     * such as, cid, eqpId, portId, carrId
     * @param vo
     */
    private EventStreamVo generateEventStreamVo(EventLogVo vo){


        String cid = null;
        String eqpId = null;
        String portId= null;
        String carrId= null;

        JSONObject object = XML.toJSONObject(vo.getPayload());

        for(String key : object.keySet()){
            if(key.contains(LvsConstant.tns.name()) && key.contains(LvsConstant.StartElement.name())){
                for(String subKey : object.getJSONObject(key).keySet()){
                    if(subKey.contains(LvsConstant.cid.name())){

                        cid = object.getJSONObject(key).getString(subKey).trim();
                    }

                    if(subKey.contains(LvsConstant.message.name()) && !subKey.contains(LvsConstant.messageKey.name())){
                        JSONObject bodyJson = new JSONObject(object.getJSONObject(key).getString(subKey)).getJSONObject(LvsConstant.body.name());
                        eqpId = bodyJson.isNull(LvsConstant.eqpId.name()) ? "" : bodyJson.getString(LvsConstant.eqpId.name()).trim();
                        portId = bodyJson.isNull(LvsConstant.portId.name()) ? "" : bodyJson.getString(LvsConstant.portId.name()).trim();
                        carrId = bodyJson.isNull(LvsConstant.carrId.name()) ? "" : bodyJson.getString(LvsConstant.carrId.name()).trim();

                    }
                }
            }
        }

        EventStreamVo eventStreamVo = EventStreamVo.builder()
                .messageKey(vo.getMessageKey())
                .cid(cid)
                .eqpId(eqpId)
                .carrId(carrId)
                .portId(portId)
                .timestamp(vo.getTimestamp())
                .formattedTime(CommonDate.getTimeUI(vo.getTimestamp()))
                .logLevel(vo.getLogLevel())
                .build();
        return eventStreamVo;
    }

}
