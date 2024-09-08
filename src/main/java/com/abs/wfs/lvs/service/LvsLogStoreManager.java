package com.abs.wfs.lvs.service;

import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WnLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WhLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WnLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.service.WnLvsEventReportServiceImpl;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.vo.WhLvsEventReportDto;
import com.abs.wfs.lvs.util.CommonDate;
import com.abs.wfs.lvs.util.LvsDataStore;
import com.abs.wfs.lvs.util.code.LogNameConstant;
import com.abs.wfs.lvs.util.code.LvsConstant;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.UseYn;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Scenario map | key: eqp ID / value : log key list
     */
    ConcurrentHashMap<String, ArrayList<EventStreamVo>> eqpEventCollection = null;
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
        if(vo.getLogName().compareTo(LogNameConstant.ScenarioStartLog) == 0 ||
                vo.getLogName().compareTo(LogNameConstant.RecvPayloadLog) == 0 ){

            ArrayList<EventStreamVo> streamVoArrayList = null;
            EventStreamVo eventStreamVo = this.generateEventStreamVo(vo);


            if(eventStreamVo.getEqpId() != null){

                if(this.eqpEventCollection.containsKey(eventStreamVo.getEqpId())){
                    streamVoArrayList = this.eqpEventCollection.get(eventStreamVo.getEqpId());

                }else{
                    streamVoArrayList = new ArrayList<>();
                }
                streamVoArrayList.add(eventStreamVo);
                this.eqpEventCollection.put(eventStreamVo.getEqpId(), streamVoArrayList);


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
        String lotId = null;

        JSONObject object = XML.toJSONObject(vo.getPayload());

        try{

            JSONObject payloadObj = object.getJSONObject("JavaToXmlActivityOutput").getJSONObject("RequestVo");
            JSONObject bodyObject = new JSONObject(payloadObj.getString("Payload")).getJSONObject("body");

            eqpId = bodyObject.isNull(LvsConstant.eqpId.name()) ? "" : bodyObject.getString(LvsConstant.eqpId.name()).trim();
            portId = bodyObject.isNull(LvsConstant.portId.name()) ? "" : bodyObject.getString(LvsConstant.portId.name()).trim();
            carrId = bodyObject.isNull(LvsConstant.carrId.name()) ? "" : bodyObject.getString(LvsConstant.carrId.name()).trim();
            lotId = bodyObject.isNull(LvsConstant.lotId.name()) ? "" : bodyObject.getString(LvsConstant.lotId.name()).trim();

        }catch (Exception e){
            e.printStackTrace();
        }




        EventStreamVo eventStreamVo = EventStreamVo.builder()
                .messageKey(vo.getMessageKey())
                .cid(cid)
                .eqpId(eqpId)
                .lotId(lotId)
                .carrId(carrId)
                .portId(portId)
                .timestamp(vo.getTimestamp())
                .formattedTime(CommonDate.getTimeUI(vo.getTimestamp()))
                .logLevel(vo.getLogLevel())
                .build();

        return eventStreamVo;
    }

}
