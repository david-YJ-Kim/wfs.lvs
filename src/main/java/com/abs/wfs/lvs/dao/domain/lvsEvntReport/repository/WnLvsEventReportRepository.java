package com.abs.wfs.lvs.dao.domain.lvsEvntReport.repository;

import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WnLvsEventReport;
import com.abs.wfs.lvs.util.vo.UseStatCd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WnLvsEventReportRepository extends JpaRepository<WnLvsEventReport, String> {


    /**
     * 해당 이벤트 명과 메시지 키, 설비로 저장된 레코드가 있는지 조회
     * @param trkId
     * @param eqpId
     * @param evntNm
     * @param useStatCd
     * @return
     */
    WnLvsEventReport findByTrkIdAndEqpIdAndEvntNmAndUseStatCd(String trkId, String eqpId, String evntNm, UseStatCd useStatCd);


    /**
     * 현행 테이블 로그 업데이트를 위해 사용
     * @param trkId
     * @param useStatCd
     * @return
     */
    WnLvsEventReport findByTrkIdAndUseStatCd(String trkId, UseStatCd useStatCd);
}
