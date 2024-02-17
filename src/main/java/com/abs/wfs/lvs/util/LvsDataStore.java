package com.abs.wfs.lvs.util;

import com.abs.wfs.lvs.util.vo.EventLogsVo;
import com.abs.wfs.lvs.util.vo.LogMessageVo;
import com.solacesystems.jcsmp.BytesXMLMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LvsDataStore {



    private static LvsDataStore instance;

    private LvsDataStore(){}

    // Key: eqp Id, value: [messageKey-A, messageKey-B] which is represent event name (cid).
    private ConcurrentHashMap<String, ArrayList<String>> scenarioOngoingEqpMap;

    // key: messageKey-A, value: logs
    private ConcurrentHashMap<String, EventLogsVo> logMessageMap;

    public static LvsDataStore getInstance(){
        if(instance == null){
            instance = new LvsDataStore();

            instance.initDataStore();
        }
        return instance;
    }

    public ConcurrentHashMap<String, EventLogsVo> getLogMessageMap() {
        return logMessageMap;
    }

    public ConcurrentHashMap<String, ArrayList<String>> getScenarioOngoingEqpMap() {
        return scenarioOngoingEqpMap;
    }

    private void initDataStore(){
        instance.logMessageMap = new ConcurrentHashMap<String, EventLogsVo>();
        instance.scenarioOngoingEqpMap = new ConcurrentHashMap<String, ArrayList<String>>();

    }




}
