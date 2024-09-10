package com.abs.wfs.lvs.util;


import com.abs.wfs.lvs.config.LvsPropertyObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NonStoreKeyCleaner {


    Long cleanerPollingIntervalHr = null;
    Long expiredHrTimeStandard = null;


    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * logNonStoreCollection: Log 적재를 막는 Collection
     * key: messageKey / value: 등록된 시점
     * ※ Cleaner Thread, 기준 시간 마다 돌아서, 해당 Collection 에 시간 초과된 항목들을 삭제
     */
    ConcurrentHashMap<String, Long> logNonStoreCollection = null;


    public void executeTimer(){

        if(cleanerPollingIntervalHr == null){
            this.initializeDataStore();
        }

        scheduler.scheduleAtFixedRate(() -> {
            Long currentTime = System.currentTimeMillis();
            log.info("Clean Timer is Start.currnet Timne: {}", currentTime);
            keyCleanTask(currentTime);


            // TEST 10분 마다 실행
        }, 0, 1, TimeUnit.MINUTES);

//    }, 0, cleanerPollingIntervalHr, TimeUnit.HOURS);
    }

    public void initializeDataStore(){
        this.logNonStoreCollection = LvsDataStore.getInstance().getLogNonStoreCollection();

        this.cleanerPollingIntervalHr = Long.valueOf(LvsPropertyObject.getInstance().getPollingCleaner());
        this.expiredHrTimeStandard = Long.valueOf(LvsPropertyObject.getInstance().getExpiredLimitHr());
    }


    /**
     * 시간 차이 나는 항목 조회
     * @param currentTime
     */
    public void keyCleanTask(Long currentTime){

        if(logNonStoreCollection == null){this.initializeDataStore();}

        ArrayList<String> expiredKeys = new ArrayList<>();

        for(String key : this.logNonStoreCollection.keySet()){

//            if(currentTime - this.logNonStoreCollection.get(key) >= TimeUnit.HOURS.toMillis(expiredHrTimeStandard)){
//                expiredKeys.add(key);
//            }


            // For Test, 10분 이상된 항목 삭제
            if(currentTime - this.logNonStoreCollection.get(key) >= TimeUnit.MINUTES.toMillis(1)){
                expiredKeys.add(key);
            }
        }


        log.info("Cleaner Timer job done. Expired keys : {} ea, {}", expiredKeys.size(), expiredKeys);
        for(String key : expiredKeys){
            this.logNonStoreCollection.remove(key);
            log.debug("Ban Key has been expired. key: {}, sizeOfMap: {}", key, this.logNonStoreCollection.size());
        }

    }
}
