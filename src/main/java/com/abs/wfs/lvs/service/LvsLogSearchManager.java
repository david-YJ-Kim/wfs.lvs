package com.abs.wfs.lvs.service;

import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LvsLogSearchManager {

    ConcurrentHashMap<String, ArrayList<EventLogVo>> logMap = LvsDataStore.getInstance().getLogMessageMap();

    // eqp,
    ConcurrentHashMap<String, EvictingQueue<EventStreamVo>> scenarioMap = LvsDataStore.getInstance().getScenarioOngoingEqpMap();

    public String printLogMap(){

        log.info("map size : {}, key List : {}",
                LvsDataStore.getInstance().getLogMessageMap().size(),
                LvsDataStore.getInstance().getLogMessageMap().keys().toString()
                );
        log.info(LvsDataStore.getInstance().getLogMessageMap().toString());
        return LvsDataStore.getInstance().getLogMessageMap().toString();
    }

    public ArrayList<EventLogVo> getEventFlow(String messageKey){

        if(this.logMap.containsKey(messageKey)){

            return this.logMap.get(messageKey);
        }
        return null;
    }

//    public JSONArray getEqpFlow(String eqpId){
//
//
//        ArrayList<EventStreamVo> eventList = LvsDataStore.getInstance().getScenarioOngoingEqpMap().get(eqpId);
//        JSONArray jsonArray = new JSONArray();
//        for(EventStreamVo event : eventList){
//            JSONArray jsonVoArray = new JSONArray(LvsDataStore.getInstance().getLogMessageMap().get(event));
//            JSONObject object = new JSONObject();
//            object.put(event, jsonVoArray);
//
//            jsonArray.put(object);
//
//        }
//        return jsonArray;
//
//    }

    public ArrayList getEqpIdList(){

        ArrayList<String> arrayList = new ArrayList();
        for(String key : this.scenarioMap.keySet()){
            arrayList.add(key);
        }
        return arrayList;
    }

    public ArrayList getScenarioFlowWithEqpId(String eqpId){


        return new ArrayList<>(this.scenarioMap.get(eqpId));

    }

    public void deleteAllLogs(){
        this.logMap.clear();
        this.scenarioMap.clear();
    }

    public void deleteLogByKey(String eqpId, String key){
        if(this.scenarioMap.containsKey(eqpId)){
            ArrayList<EventStreamVo> keyList = new ArrayList<>(this.scenarioMap.get(eqpId));
            keyList.remove(key);
        }

        if(this.logMap.containsKey(key)){
            this.logMap.remove(key);
        }
    }

    public void deleteScenarioByKey(String key){
        if(this.scenarioMap.containsKey(key)){

            this.scenarioMap.remove(key);
        }


    }


}
