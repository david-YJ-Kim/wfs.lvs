package com.abs.wfs.lvs.service;

import com.abs.wfs.lvs.config.LvsPropertyObject;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WhLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WnLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WhLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WnLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.service.WnLvsEventReportServiceImpl;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.vo.WhLvsEventReportDto;
import com.abs.wfs.lvs.util.CommonDate;
import com.abs.wfs.lvs.util.LogContentParser;
import com.abs.wfs.lvs.util.LvsBanMessageList;
import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.code.LogNameConstant;
import com.abs.wfs.lvs.util.code.LvsConstant;
import com.abs.wfs.lvs.util.vo.AbnormalStartLogVo;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.UseYn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    LogContentParser logContentParser;


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


    /**
     * logNonStoreCollection: Log 적재를 막는 Collection
     * key: messageKey / value: 등록된 시점
     * ※ Cleaner Thread, 기준 시간 마다 돌아서, 해당 Collection 에 시간 초과된 항목들을 삭제
     */
    ConcurrentHashMap<String, Long> logNonStoreCollection = null;


    ArrayList<String> eventStoreBlockList = null;


    // TODO EvictingQueue 큐를 사용해서 메모리 사용량에 제한
    ArrayList<String> undefinedArray = null;


    private void initializeDataStore(){
        this.eqpEventCollection = LvsDataStore.getInstance().getScenarioOngoingEqpMap();
        this.eventLogCollection = LvsDataStore.getInstance().getLogMessageMap();
        this.logNonStoreCollection = LvsDataStore.getInstance().getLogNonStoreCollection();
        this.undefinedArray = LvsDataStore.getInstance().getUndefinedArray();
        this.eventStoreBlockList = new ArrayList<>(
                Arrays.asList(LvsPropertyObject.getInstance().getEventStoreBlockString().split(","))
        );
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
        log.info("IncomingLog. {}",vo.toString());

        if(this.eqpEventCollection == null){
            this.initializeDataStore();
        }


        if(logNonStoreCollection.containsKey(vo.getMessageKey())){
            log.debug("Log is no need to store in memory. LogVo: {}", vo);
            return;
        }
        this.putEventWithLog(vo);


        /**
         * Set Scenario Key : eqpId
         */
//        if(vo.getLogName().compareTo(LogNameConstant.ScenarioStartLog) == 0 ||
        if(vo.getLogName().compareTo(LogNameConstant.ScenarioStartLog) == 0 ){

            // ArrayList<EventStreamVo> streamVoArrayList = null;
            // → EvictingQueue로 변경
            EvictingQueue<EventStreamVo> eventStorage = null;

            EventStreamVo eventStreamVo = this.generateEventStreamVo(vo);

            if(eventStreamVo == null || eventStreamVo.getEqpId() == null){
                log.info("Fail to create EventStreamVo. or EqpId is null.  Delete in EventLogCollection");
                this.deleteLogAndBanForward(vo);
            }


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

            WnLvsEventReport updateRecord = this.wnLvsEventReportService.updateLogTimeEventRecord(vo);
        }




        /**
         * 이벤트 처리 마무리
         */
        if(vo.getLogName().compareTo(LogNameConstant.ScenarioEndLog) == 0 ||
                vo.getLogName().compareTo(LogNameConstant.AbnormalStartLog) == 0 ){

            WnLvsEventReport crntRecord = this.wnLvsEventReportService.findByTrkIdAndUseStatCd(vo.getMessageKey());

            if(crntRecord != null){
                this.endEventStream(vo, crntRecord);
            }else {
                log.error("Current record is not defined. messageKey : {}, logName: {}", vo.getMessageKey(), vo.getLogName());
            }

        }

    }


    /**
     * Storage 저장된 LogVo 삭제 후, 동일한 Key 로 발생하는 향후 로그로 저장 방지
     * @param vo
     */
    private void deleteLogAndBanForward(EventLogVo vo){
        this.eventLogCollection.remove(vo.getMessageKey());

        // EventStreamVo를 생성하지 못한 LogVo로 추가적인 로그는 미 적재
        this.logNonStoreCollection.put(vo.getMessageKey(), System.currentTimeMillis());
        log.info("LogVo is removed from storage and no more log stored with same messageKey. LogVo: {}", vo);
    }
    
    
    /**
     * LogVo를 Storage 저장
     * @param vo
     */
    private void putEventWithLog(EventLogVo vo){

        // Store logs with message key
        if(this.eventLogCollection.containsKey(vo.getMessageKey())){
            this.eventLogCollection.get(vo.getMessageKey()).add(vo);

        }else{
            ArrayList logList = new ArrayList<>();
            logList.add(vo);
            this.eventLogCollection.put(vo.getMessageKey(), logList);

        }
        log.info("key:{} stored in Log Map. size: {}", vo.getMessageKey(), LvsDataStore.getInstance().getLogMessageMap().size());
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


    // Json 포맷으로 Pretty print 활성화 하기 위함
    ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);


    /**
     * 종료를 알리는 로그에 대한 처리 방안
     * 종료 타입: 정상 종료 / 비정상 종료
     * 
     * 1. 현행 테이블 SucceeYn 완료
     * 2. 히스토리 테이블 적재
     * 3. 현행 테이블 삭제
     *
     * @param logVo
     * @param record
     */
    private void endEventStream(EventLogVo logVo, WnLvsEventReport record){

        switch (logVo.getLogName()){
            case LogNameConstant.ScenarioEndLog:
                if(record.getSuccessYn().equals(UseYn.N)){
                    record.setSuccessYn(UseYn.Y);
                }
                break;

            case LogNameConstant.AbnormalStartLog:
                record.setSuccessYn(UseYn.N);
                AbnormalStartLogVo abnormalStartLogVo = this.logContentParser.generateAbnormalStartLogVo(logVo);
                if(abnormalStartLogVo != null){

                    record.setErrCd(abnormalStartLogVo.getErrCd());
                    record.setErrCm(abnormalStartLogVo.getErrCm());
                }
                break;

            case LogNameConstant.UndefinedExceptionLog:
                record.setSuccessYn(UseYn.N);
                record.setErrCd(LogNameConstant.UndefinedExceptionLog);
                break;
        }

        WnLvsEventReport latestRecord = this.wnLvsEventReportRepository.save(record);

        this.whLvsEventReportRepository.save(new WhLvsEventReportDto(latestRecord).toEntity());


        this.wnLvsEventReportRepository.delete(latestRecord);


        
        // TODO Elastic 에 적재
        // Update All Logs Json store in column
//        log.debug("Get all log stream. LogStream: {}", this.eventLogCollection.get(logVo.getMessageKey()));

//;        ArrayList<EventLogVo> logVoList = this.eventLogCollection.get(logVo.getMessageKey());
//        String formattedLogVo = null;
//        if(!logVoList.isEmpty()){
//
//                formattedLogVo = logVoList.toString();
//            try{
//                formattedLogVo = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logVoList);
//            }catch (Exception e){
//            }
//        }
//        record.setLogStream(formattedLogVo);
//        record.setLogStream("formattedLogVo");
//        log.info(formattedLogVo);




    }



    /**
     * Get payload and parsing to get additional data
     * such as, cid, eqpId, portId, carrId
     * @param vo
     */
    private EventStreamVo generateEventStreamVo(EventLogVo vo)  {


        String cid = null;
        String eqpId = null;
        String portId= null;
        String carrId= null;
        String lotId= null;
        String payload = null;

        JSONObject object = XML.toJSONObject(vo.getPayload());

        for(String key : object.keySet()){
            if(key.contains(LvsConstant.tns.name()) && key.contains(LvsConstant.StartElement.name())){
                for(String subKey : object.getJSONObject(key).keySet()){

                    if(subKey.contains(LvsConstant.message.name()) && !subKey.contains(LvsConstant.messageKey.name())){

                        payload = object.getJSONObject(key).getString(subKey);

                        JSONObject headJson = new JSONObject(payload).getJSONObject(LvsConstant.head.name());
                        JSONObject bodyJson = new JSONObject(payload).getJSONObject(LvsConstant.body.name());


                        cid = headJson.getString(LvsConstant.cid.name()).trim();
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


        try {
            eventStreamVo.setPayload(objectMapper.writeValueAsString(
                    objectMapper.readValue(payload, Object.class)
            ));
        } catch (IOException e) {
            eventStreamVo.setPayload(payload);
        }


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

        if(cid.isEmpty()) return false;

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
                    if(rsltCm.contains("ERR CODE:25") || rsltCm.contains("FAIL: 42, 요청 설비에 예약된 SORTER JOB 없음")) {
                        log.info("Data is not gonna store. It's empty port detection. Cid: {} rsltCm: {}", cid, rsltCm);
                        return true;
                    }

                }

                break;

            default:
                if(eventStoreBlockList.contains(cid)){
                    log.info("Cid is no need to store. cid : {}", cid);
                    return true;
                }
        }

        return false;
    }

}
