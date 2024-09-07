package com.abs.wfs.lvs.util;

import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LvsDataStore {



    private static LvsDataStore instance;

    private LvsDataStore(){}

    // Key: eqp Id, value: [messageKey-A, messageKey-B] which is represent event name (cid).
    private ConcurrentHashMap<String, ArrayList<EventStreamVo>> scenarioOngoingEqpMap;

    // key: messageKey-A, value: logs
    private ConcurrentHashMap<String, ArrayList<EventLogVo>> logMessageMap;

    private ArrayList<String> undefinedArray;

    public static LvsDataStore getInstance(){
        if(instance == null){
            instance = new LvsDataStore();

            instance.initDataStore();
        }
        return instance;
    }

    public ArrayList<String> getUndefinedArray(){return undefinedArray;}

    public ConcurrentHashMap<String, ArrayList<EventLogVo>> getLogMessageMap() {
        return logMessageMap;
    }

    public ConcurrentHashMap<String, ArrayList<EventStreamVo>> getScenarioOngoingEqpMap() {
        return scenarioOngoingEqpMap;
    }

    private void initDataStore(){
        instance.scenarioOngoingEqpMap = new ConcurrentHashMap<String, ArrayList<EventStreamVo>>();
        instance.logMessageMap = new ConcurrentHashMap<String, ArrayList<EventLogVo>>();
        instance.undefinedArray = new ArrayList<>();

    }




}
