package com.abs.wfs.lvs.dao.domain.lvsEvntReport.vo;


import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WhLvsEventReport;
import com.abs.wfs.lvs.dao.domain.lvsEvntReport.model.WnLvsEventReport;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import com.abs.wfs.lvs.util.vo.UseStatCd;
import com.abs.wfs.lvs.util.vo.UseYn;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@NoArgsConstructor
@Data
public class WhLvsEventReportDto {

    private String refObjId;
    private String trkId;
    private String siteId;
    private String lotId;
    private String eqpId;
    private String portId;
    private String carrId;
    private String evntNm;
    private Timestamp scenStartDt;
    private Timestamp evntStartDt;
    private Timestamp evntEndDt;
    private Timestamp scenEndDt;
    private Timestamp clearMemoryDt;
    private UseYn successYn;
    private UseStatCd useStatCd;
    private String errCd;
    private String errCm;
    private String crtUserId;
    private Timestamp crtDt;
    private String mdfyUserId;
    private Timestamp mdfyDt; // NOT NULL ENABLE


    public WhLvsEventReportDto(String refObjId, String trkId, String siteId, String lotId, String eqpId, String portId, String carrId, String evntNm, Timestamp scenStartDt, Timestamp evntStartDt, Timestamp evntEndDt, Timestamp scenEndDt, Timestamp clearMemoryDt, UseYn successYn, UseStatCd useStatCd, String errCd, String errCm, String crtUserId, Timestamp crtDt, String mdfyUserId, Timestamp mdfyDt) {
        this.refObjId = refObjId;
        this.trkId = trkId;
        this.siteId = siteId;
        this.lotId = lotId;
        this.eqpId = eqpId;
        this.portId = portId;
        this.carrId = carrId;
        this.evntNm = evntNm;
        this.scenStartDt = scenStartDt;
        this.evntStartDt = evntStartDt;
        this.evntEndDt = evntEndDt;
        this.scenEndDt = scenEndDt;
        this.clearMemoryDt = clearMemoryDt;
        this.successYn = successYn;
        this.useStatCd = useStatCd;
        this.errCd = errCd;
        this.errCm = errCm;
        this.crtUserId = crtUserId;
        this.crtDt = crtDt;
        this.mdfyUserId = mdfyUserId;
        this.mdfyDt = mdfyDt;
    }

    @Builder
    public WhLvsEventReportDto(WnLvsEventReport report){
        this.refObjId = report.getObjId();
        this.trkId = report.getTrkId();
        this.siteId = report.getSiteId();
        this.lotId = report.getLotId();
        this.eqpId = report.getEqpId();
        this.portId = report.getPortId();
        this.carrId = report.getCarrId();
        this.evntNm = report.getEvntNm();
        this.scenStartDt = report.getScenStartDt();
        this.evntStartDt = report.getEvntStartDt();
        this.evntEndDt = report.getEvntEndDt();
        this.clearMemoryDt = report.getClearMemoryDt();
        this.scenEndDt = report.getScenEndDt();
        this.successYn = report.getSuccessYn();
        this.useStatCd = report.getUseStatCd();
        this.errCd = report.getErrCd();
        this.errCm = report.getErrCm();
        this.crtUserId = report.getCrtUserId();
        this.crtDt = report.getCrtDt();
        this.mdfyUserId = report.getMdfyUserId();
        this.mdfyDt = report.getMdfyDt();
    }




    public WhLvsEventReport toEntity(){
        return WhLvsEventReport.builder()
                .refObjId(refObjId)
                .trkId(trkId)
                .siteId(siteId)
                .lotId(lotId)
                .eqpId(eqpId)
                .portId(portId)
                .carrId(carrId)
                .evntNm(evntNm)
                .scenStartDt(scenStartDt)
                .evntStartDt(evntStartDt)
                .evntEndDt(evntEndDt)
                .scenEndDt(scenEndDt)
                .successYn(successYn)
                .useStatCd(useStatCd)
                .errCd(errCd)
                .errCm(errCm)
                .crtUserId(crtUserId)
                .crtDt(crtDt)
                .mdfyUserId(mdfyUserId)
                .mdfyDt(mdfyDt)
                .build();
    }
}
