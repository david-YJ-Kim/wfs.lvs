package com.abs.wfs.lvs.service;

import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.vo.EventLogsVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LvsLogSearchManager {

    ConcurrentHashMap<String, EventLogsVo> logMap = LvsDataStore.getInstance().getLogMessageMap();

    // eqp,
    ConcurrentHashMap<String, ArrayList<String>> scenarioMap = LvsDataStore.getInstance().getScenarioOngoingEqpMap();

    public String printLogMap(){

        log.info("map size : {}, key List : {}",
                LvsDataStore.getInstance().getLogMessageMap().size(),
                LvsDataStore.getInstance().getLogMessageMap().keys().toString()
                );
        log.info(LvsDataStore.getInstance().getLogMessageMap().toString());
        return LvsDataStore.getInstance().getLogMessageMap().toString();
    }

    public EventLogsVo getEventFlow(String messageKey){

        if(this.logMap.containsKey(messageKey)){

            return this.logMap.get(messageKey);
        }
        return null;
    }

    public JSONArray getEqpFlow(String eqpId){


        ArrayList<String> eventList = LvsDataStore.getInstance().getScenarioOngoingEqpMap().get(eqpId);
        JSONArray jsonArray = new JSONArray();
        for(String event : eventList){
            JSONArray jsonVoArray = new JSONArray(LvsDataStore.getInstance().getLogMessageMap().get(event));
            JSONObject object = new JSONObject();
            object.put(event, jsonVoArray);

            jsonArray.put(object);

        }
        return jsonArray;

    }

    public ArrayList getEqpIdList(){

        ArrayList<String> arrayList = new ArrayList();
        for(String key : this.scenarioMap.keySet()){
            arrayList.add(key);
        }
        return arrayList;
    }

    public ArrayList getScenarioFlowWithEqpId(String eqpId){


        return this.scenarioMap.get(eqpId);

    }

    public void deleteAllLogs(){
        this.logMap.clear();
        this.scenarioMap.clear();
    }

    public void deleteLogByKey(String eqpId, String key){
        if(this.scenarioMap.containsKey(eqpId)){
            ArrayList<String> keyList = this.scenarioMap.get(eqpId);
            keyList.remove(key);
        }

        if(this.logMap.containsKey(key)){
            this.logMap.remove(key);
        }
    }

    public void deleteScenarioByKey(String key){
        if(this.scenarioMap.containsKey(key)){

            for(String logKey : this.scenarioMap.get(key)){
                this.logMap.remove(logKey);
            }
            this.scenarioMap.remove(key);
        }


    }


}
