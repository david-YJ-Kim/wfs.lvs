package com.abs.wfs.lvs.dao.domain.lvsEvntReport.service;

import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WhLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WnLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WhLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WnLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.vo.WhLvsEventReportDto;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.vo.WnLvsEventReportDto;
import com.abs.wfs.lvs.util.code.LogNameConstant;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.abs.wfs.lvs.util.vo.UseStatCd;
import com.abs.wfs.lvs.util.vo.UseYn;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@Slf4j
public class WnLvsEventReportServiceImpl {

    @Autowired
    WnLvsEventReportRepository wnLvsEventReportRepository;

    @Autowired
    WhLvsEventReportRepository whLvsEventReportRepository;

    public WnLvsEventReport searchEventReportWithEqpAndEvntNmAndTrkId(String trkId, String eqpId, String evntNm){

        return this.wnLvsEventReportRepository.findByTrkIdAndEqpIdAndEvntNmAndUseStatCd(trkId, eqpId, evntNm, UseStatCd.Usable);

    }


    /**
     * 현행 테이블 로그 업데이트를 위해 사용
     * @param trkId
     * @return
     */
    public WnLvsEventReport findByTrkIdAndUseStatCd(String trkId){

        return this.wnLvsEventReportRepository.findByTrkIdAndUseStatCd(trkId, UseStatCd.Usable);
    }



    /**
     * LVS 로그 테이블에 신규 이벤트 레코드 생성
     * 호출 시점: EventStreamVo 생성 시점
     * @param logName
     * @param eventStreamVo
     */
    public WnLvsEventReport insertNewEventRecord(String logName, EventStreamVo eventStreamVo){

        // 동일한 데이터로 테이블에 적재가 되어있는지 확인
        // 데이터 조회
        String trkId = eventStreamVo.getMessageKey();
        String eqpId = eventStreamVo.getEqpId();
        String evntNm = eventStreamVo.getCid();
        WnLvsEventReport crntRecord = this.searchEventReportWithEqpAndEvntNmAndTrkId(trkId, eqpId, evntNm);

        // 조회 결과 없음 → 신규 추가 필요
        if(crntRecord == null){
            WnLvsEventReportDto wnLvsEventReportDto = new WnLvsEventReportDto(eventStreamVo);
            WnLvsEventReport createdRecord = this.wnLvsEventReportRepository.save(wnLvsEventReportDto.toEntity());

            return createdRecord;
        }
        return null;
    }


    /**
     * LVS 로그 테이블에 보고된 로그 내용 업데이트
     * @param vo
     * @return
     */
    public WnLvsEventReport updateLogTimeEventRecord(EventLogVo vo){

        // 데이터 조회
        String trkId = vo.getMessageKey();

        WnLvsEventReport crntEvntRecord = this.findByTrkIdAndUseStatCd(trkId);


        if(crntEvntRecord != null){

            // When other log is coming
            switch (vo.getLogName()){
                case LogNameConstant.ScenarioStartLog:
                    crntEvntRecord.setScenStartDt(Timestamp.from(Instant.now()));
                    break;

                case LogNameConstant.EventStartLog:
                    crntEvntRecord.setEvntStartDt(Timestamp.from(Instant.now()));
                    break;

                case LogNameConstant.EventEndLog:
                    crntEvntRecord.setEvntEndDt(Timestamp.from(Instant.now()));
                    break;

                case LogNameConstant.ScenarioEndLog:
                    crntEvntRecord.setScenEndDt(Timestamp.from(Instant.now()));
                    crntEvntRecord.setSuccessYn(UseYn.Y);
                    break;
                default:
                    return null;
            }
            crntEvntRecord.setMdfyDt(Timestamp.from(Instant.now()));

            return this.wnLvsEventReportRepository.save(crntEvntRecord);
        }else{
            log.error("There is no log in current table. tracking key: {}", trkId);
            return null;
        }
    }


    /**
     * Insert history record
     * @return
     */
    private WhLvsEventReport insertHistoryData(WnLvsEventReport record){

        WhLvsEventReportDto dto = new WhLvsEventReportDto(record);

        return this.whLvsEventReportRepository.save(dto.toEntity());

    }
}
