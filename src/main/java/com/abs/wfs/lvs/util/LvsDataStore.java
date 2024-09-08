package com.abs.wfs.lvs.util;

import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LvsDataStore {



    private static LvsDataStore instance;

    private LvsDataStore(){}

    // Key: eqp Id, value: [messageKey-A, messageKey-B] which is represent event name (cid).
    private ConcurrentHashMap<String, EvictingQueue<EventStreamVo>> scenarioOngoingEqpMap;

    // key: messageKey-A, value: logs
    private ConcurrentHashMap<String, ArrayList<EventLogVo>> logMessageMap;

    /**
     * logNonStoreCollection: Log 적재를 막는 Collection
     * key: messageKey / value: 등록된 시점
     * ※ Cleaner Thread, 기준 시간 마다 돌아서, 해당 Collection 에 시간 초과된 항목들을 삭제
     */
    ConcurrentHashMap<String, Long> logNonStoreCollection = null;

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

    public ConcurrentHashMap<String, EvictingQueue<EventStreamVo>> getScenarioOngoingEqpMap() {
        return scenarioOngoingEqpMap;
    }

    public ConcurrentHashMap<String, Long> getLogNonStoreCollection() {
        return logNonStoreCollection;
    }

    private void initDataStore(){
        instance.scenarioOngoingEqpMap = new ConcurrentHashMap<String, EvictingQueue<EventStreamVo>>();
        instance.logMessageMap = new ConcurrentHashMap<String, ArrayList<EventLogVo>>();
        instance.logNonStoreCollection = new ConcurrentHashMap<String, Long>();
        instance.undefinedArray = new ArrayList<>();

    }




}
