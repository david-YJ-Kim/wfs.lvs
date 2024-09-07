package com.abs.wfs.lvs.dao.domain.lvsEvntReport.service;

import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WnLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository.WnLvsEventReportRepository;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.vo.WnLvsEventReportDto;
import com.abs.wfs.lvs.util.code.LogNameConstant;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.abs.wfs.lvs.util.vo.UseStatCd;
import com.abs.wfs.lvs.util.vo.UseYn;
import org.springframework.beans.factory.annotation.Autowired;

public class WnLvsEventReportServiceImpl {

    @Autowired
    WnLvsEventReportRepository wnLvsEventReportRepository;

    public WnLvsEventReport searchEventReportWithEqpAndEvntNmAndTrkId(String trkId, String eqpId, String evntNm){

        return this.wnLvsEventReportRepository.findByTrkIdAndEqpIdAndEntNmAndUseStatCd(trkId, eqpId, evntNm, UseStatCd.Usable);

    }

    /**
     * LVS 로그 테이블에 신규 이벤트 레코드 생성
     * 호출 시점: EventStreamVo 생성 시점
     * @param logName
     * @param eventStreamVo
     */
    private WnLvsEventReport insertEventRecord(String logName, EventStreamVo eventStreamVo){

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
        }else {

            if(logName.compareTo(LogNameConstant.ScenarioStartLog.name()) == 0){

                crntRecord.setScenStartYn(UseYn.Y);
                WnLvsEventReport updatededRecord = this.wnLvsEventReportRepository.save(crntRecord);

                return updatededRecord;
            }

        }

        return null;





        // 존재해서 있다면, 업데이트 (logName = 'scenarioStartLog' 시)

    }

}
