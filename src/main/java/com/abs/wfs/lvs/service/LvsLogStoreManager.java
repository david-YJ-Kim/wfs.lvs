package com.abs.wfs.lvs.service;

import com.abs.wfs.lvs.config.LvsPropertyObject;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WnLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WhLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WnLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.service.WnLvsEventReportServiceImpl;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.vo.WhLvsEventReportDto;
import com.abs.wfs.lvs.util.CommonDate;
import com.abs.wfs.lvs.util.LvsBanMessageList;
import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.code.LogNameConstant;
import com.abs.wfs.lvs.util.code.LvsConstant;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.UseYn;
import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LvsLogStoreManager  {


    @Autowired
    WnLvsEventReportServiceImpl wnLvsEventReportService;

    @Autowired
    WnLvsEventReportRepository wnLvsEventReportRepository;

    @Autowired
    WhLvsEventReportRepository whLvsEventReportRepository;


    /**
     * Scenario 여러 개의 event key로 구성된 이벤트 들의 흐름
     * EventLog는 하나의 event key로 발생한 여러 로그의 집합
     *
     * eqp - event(message key) list map | key: eqp ID / value : log key list
     */
    ConcurrentHashMap<String, EvictingQueue<EventStreamVo>> eqpEventCollection = null;

    /**
     * event(message key) - log list map
     */
    ConcurrentHashMap<String, ArrayList<EventLogVo>> eventLogCollection = null;
    
    // TODO EvictingQueue 큐를 사용해서 메모리 사용량에 제한
    ArrayList<String> undefinedArray = null;


    private void initializeDataStore(){
        this.eqpEventCollection = LvsDataStore.getInstance().getScenarioOngoingEqpMap();
        this.eventLogCollection = LvsDataStore.getInstance().getLogMessageMap();
        this.undefinedArray = LvsDataStore.getInstance().getUndefinedArray();
    }

    public void undefinedStore(String logPayload){
        if(this.undefinedArray == null){
            this.initializeDataStore();
        }
        this.undefinedArray.add(logPayload);
    }

    /**
     * 신규  log 발생 시, 처리 로직 수행
     * 1.
     * @param vo
     */
    public void execute(EventLogVo vo){
        log.info(vo.toString());

        if(this.eqpEventCollection == null || this.eqpEventCollection == null){
            this.initializeDataStore();
        }

        // Store logs with message key
        if(this.eventLogCollection.containsKey(vo.getMessageKey())){
            this.eventLogCollection.get(vo.getMessageKey()).add(vo);

        }else{
            ArrayList logList = new ArrayList<>();
            logList.add(vo);
            this.eventLogCollection.put(vo.getMessageKey(), logList);

        }
        log.info("key:{} stored in Log Map. size: {}", vo.getMessageKey(), LvsDataStore.getInstance().getLogMessageMap().size());



        /**
         * Set Scenario Key : eqpId
         */
//        if(vo.getLogName().compareTo(LogNameConstant.ScenarioStartLog) == 0 ||
        if(vo.getLogName().compareTo(LogNameConstant.RecvPayloadLog) == 0 ){

            // ArrayList<EventStreamVo> streamVoArrayList = null;
            // → EvictingQueue로 변경
            EvictingQueue<EventStreamVo> eventStorage = null;

            EventStreamVo eventStreamVo = this.generateEventStreamVo(vo);

            if(eventStreamVo == null){
                log.info("Fail to create EventStreamVo. Delete in EventLogCollection");
                this.eventLogCollection.remove(vo.getMessageKey());
            }

            if(eventStreamVo.getEqpId() != null){

                if(this.eqpEventCollection.containsKey(eventStreamVo.getEqpId())){
                    eventStorage = this.eqpEventCollection.get(eventStreamVo.getEqpId());

                }else{
                    eventStorage = EvictingQueue.create(Integer.parseInt(LvsPropertyObject.getInstance().getStorageCapa()));
                }
                this.addIntoQueue(eventStorage, eventStreamVo);
                this.eqpEventCollection.put(eventStreamVo.getEqpId(), eventStorage);


                WnLvsEventReport insertedRecord = this.wnLvsEventReportService.insertNewEventRecord(vo.getLogName(), eventStreamVo);
                if(insertedRecord != null){
                    log.info("Success to insert || update record like this : {}", insertedRecord);
                }

            }else{
                log.error("Eqp id is null. EventStreamVo: {}", eventStreamVo.toString());
            }

        }



        WnLvsEventReport updateRecord = this.wnLvsEventReportService.updateLogTimeEventRecord(vo.getLogName(), vo);

        /**
         * 이벤트 처리 마무리
         */
        if(vo.getLogName().compareTo(LogNameConstant.ScenarioEndLog) == 0 ||
                vo.getLogName().compareTo(LogNameConstant.AbnormalStartLog) == 0 ){

            WnLvsEventReport crntRecord = this.wnLvsEventReportService.findByTrkIdAndUseStatCd(vo.getMessageKey());

            if(crntRecord != null){
                this.endEventStream(vo.getLogName(), crntRecord);
            }else {
                log.error("Current record is not defined. messageKey : {}, logName: {}", vo.getMessageKey(), vo.getLogName());
            }

        }

    }


    /**
     * Evicting Queue에 넘치게 add 되면 오래된 element는 삭제
     * 삭제 대상 element를 조회해서 삭제 처리 로직 호출
     * @param eventStorage
     * @param vo
     * @return
     */
    private boolean addIntoQueue(EvictingQueue<EventStreamVo> eventStorage, EventStreamVo vo){

        if(eventStorage.size() == eventStorage.remainingCapacity()){
            EventStreamVo evictedVo = eventStorage.peek();
            if(evictedVo != null) this.onEviction(evictedVo);
        }

        return eventStorage.add(vo);
        

    }

    /**
     * 초과되어 삭제 대상 element를 eventLogCollection에서 삭제 필요
     * @param removedVo
     */
    private void onEviction(EventStreamVo removedVo){

        log.info("EventStreamVo is removed now. Gonna clear at eventLogCollection.  Removed EvenStreamVo: {}", removedVo.toString());
        String messageKey = removedVo.getMessageKey();

        if(eventLogCollection.containsKey(messageKey)){

            int size = eventLogCollection.get(messageKey).size();
            eventLogCollection.remove(messageKey);
            log.info("EventLogCollection remove before size : {}, after size: {}", size, eventLogCollection.size());
        }


        WnLvsEventReport record = this.wnLvsEventReportService.findByTrkIdAndUseStatCd(messageKey);
        if(record != null){
            log.warn("Remain record at current table. Data will be deleted in memory. Tracing Key: {}, Record: {}",
                    messageKey, record);

            record.setClearMemoryDt(Timestamp.from(Instant.now()));

            this.wnLvsEventReportRepository.save(record);

        }

    }


    /**
     * 종료를 알리는 로그에 대한 처리 방안
     * 종료 타입: 정상 종료 / 비정상 종료
     * 
     * 1. 현행 테이블 SucceeYn 완료
     * 2. 히스토리 테이블 적재
     * 3. 현행 테이블 삭제
     *
     * @param logName
     * @param record
     */
    private void endEventStream(String logName, WnLvsEventReport record){


        switch (logName){
            case LogNameConstant.ScenarioEndLog:
                if(record.getSuccessYn().equals(UseYn.N)){
                    record.setSuccessYn(UseYn.Y);
                }
                break;

            case LogNameConstant.AbnormalStartLog:
                record.setSuccessYn(UseYn.N);

                // TODO 에러 내용 추출해서 업데이트
                record.setErrCd("ERROR_CODE");
                record.setErrCm("ERROR COMMENT");
                break;
        }

        WnLvsEventReport latestRecord = this.wnLvsEventReportRepository.save(record);

        this.whLvsEventReportRepository.save(new WhLvsEventReportDto(latestRecord).toEntity());


        this.wnLvsEventReportRepository.delete(latestRecord);


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
        String lotId= null;

        JSONObject object = XML.toJSONObject(vo.getPayload());

        for(String key : object.keySet()){
            if(key.contains(LvsConstant.tns.name()) && key.contains(LvsConstant.StartElement.name())){
                for(String subKey : object.getJSONObject(key).keySet()){
                    if(subKey.contains(LvsConstant.cid.name())){

                        cid = object.getJSONObject(key).getString(subKey).trim();
                    }

                    if(subKey.contains(LvsConstant.message.name()) && !subKey.contains(LvsConstant.messageKey.name())){
                        JSONObject bodyJson = new JSONObject(object.getJSONObject(key).getString(subKey)).getJSONObject(LvsConstant.body.name());

                        if(this.judgeNonStoreEvent(cid, bodyJson)){
                            log.info("This EventLogVo is not necessary to store data. LogVo: {}.", vo.toString());
                            return null;
                        }

                        eqpId = bodyJson.isNull(LvsConstant.eqpId.name()) ? "" : bodyJson.getString(LvsConstant.eqpId.name()).trim();
                        portId = bodyJson.isNull(LvsConstant.portId.name()) ? "" : bodyJson.getString(LvsConstant.portId.name()).trim();
                        carrId = bodyJson.isNull(LvsConstant.carrId.name()) ? "" : bodyJson.getString(LvsConstant.carrId.name()).trim();
                        lotId = bodyJson.isNull(LvsConstant.lotId.name()) ? "" : bodyJson.getString(LvsConstant.lotId.name()).trim();


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
                .lotId(lotId)
                .timestamp(vo.getTimestamp())
                .formattedTime(CommonDate.getTimeUI(vo.getTimestamp()))
                .logLevel(vo.getLogLevel())
                .build();

        return eventStreamVo;
    }


    /**
     * If not need to store log in storage
     * return true
     * @param cid
     * @param bodyObj
     * @return
     */
    private boolean judgeNonStoreEvent(String cid, JSONObject bodyObj){

        switch (cid){
            case LvsBanMessageList.WFS_LOAD_REQ:

                String evntCm = bodyObj.getString(LvsConstant.evntCm.name());
                if(!evntCm.isEmpty()){
                    if(evntCm.contains("EMPTY PORT DETECTION")) {
                        log.info("Data is not gonna store. It's empty port detection. Cid: {} eventCm: {}", cid, evntCm);
                        return true;
                    }
                }

                break;

            case LvsBanMessageList.WFS_DSP_WORK_REP:
                String rsltCm = bodyObj.getString(LvsConstant.rsltCm.name());
                if(!rsltCm.isEmpty()){
                    if(rsltCm.contains("ERR CODE:25")) {
                        log.info("Data is not gonna store. It's empty port detection. Cid: {} rsltCm: {}", cid, rsltCm);
                        return true;
                    }

                }

                break;
        }

        return false;
    }

}
