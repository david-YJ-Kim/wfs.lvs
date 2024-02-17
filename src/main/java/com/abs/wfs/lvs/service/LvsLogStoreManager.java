package com.abs.wfs.lvs.service;

import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.code.LogNameConstant;
import com.abs.wfs.lvs.util.vo.EventLogsVo;
import com.abs.wfs.lvs.util.vo.LogMessageVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LvsLogStoreManager  {


    ConcurrentHashMap<String, EventLogsVo> logMap = null;
    ConcurrentHashMap<String, ArrayList<String>> scenarioMap = null;


    private void initializeDataStore(){
        this.logMap = LvsDataStore.getInstance().getLogMessageMap();
        this.scenarioMap = LvsDataStore.getInstance().getScenarioOngoingEqpMap();
    }
    public void execute(LogMessageVo vo){
        log.info(vo.toString());

        if(this.logMap == null || this.scenarioMap == null){
            this.initializeDataStore();
        }

        // Store logs
        if(this.logMap.containsKey(vo.getMessageKey())){
            this.logMap.get(vo.getMessageKey()).getLogMessages().add(vo);
        }else{
            ArrayList logList = new ArrayList<>();
            logList.add(vo);
            EventLogsVo eventLogsVo = EventLogsVo.builder()
                    .messageKey(vo.getMessageKey())
                    .logMessages(logList)
                    .build();
            LvsDataStore.getInstance().getLogMessageMap().put(vo.getMessageKey(), eventLogsVo);
            log.info("New messageKey begin");
        }
        log.info("key:{} stored in Log Map. size: {}", vo.getMessageKey(), LvsDataStore.getInstance().getLogMessageMap().size());


        // Get Cid.
        if(vo.getLogName().compareTo(LogNameConstant.ScenarioStartLog.name()) == 0){

            JSONObject object = XML.toJSONObject(vo.getPayload());
            String jsonPrettyPrintString = object.toString(4);
            log.info("==========================================================");
            log.info(jsonPrettyPrintString);
            String cid = null;
            for(String key : object.keySet()){
                if(key.contains("tns") && key.contains("StartElement")){
                    for(String subKey : object.getJSONObject(key).keySet()){
                        if(subKey.contains("cid")){

                            cid = object.getJSONObject(key).getString(subKey);
                        }
                    }
                }
            }

            this.logMap.get(vo.getMessageKey()).setCid(cid);
            log.info("GetCidInserted. printMap: {}]", this.logMap.get(vo.getMessageKey()).toString());
        }



        log.info("====================PROCESSEQPID caompare: {}", vo.getLogName().compareTo(LogNameConstant.ProcessEqpIdLog.name()));
        if(vo.getLogName().compareTo(LogNameConstant.ProcessEqpIdLog.name()) == 0){

            String eqpId = vo.getPayload().trim();
            ArrayList<String> eventList = null;
            if(this.scenarioMap.containsKey(eqpId)){
                eventList = this.scenarioMap.get(eqpId);
                log.info("============CONTAIN=========== eqpId:{}, map: {}", eqpId, this.scenarioMap.toString());

            }else{
                eventList = new ArrayList<>();
            }
            eventList.add(vo.getMessageKey());
            this.scenarioMap.put(eqpId, eventList);
            log.info("======================= eqpId:{}, map: {}", eqpId, this.scenarioMap);

        }



    }


}
