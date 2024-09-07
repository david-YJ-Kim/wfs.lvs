package com.abs.wfs.lvs.dao.domain.lvsEvntReport.vo;


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
public class WnLvsEventReportDto {

    private String trkId;
    private String siteId;
    private String lotId;
    private String eqpId;
    private String portId;
    private String carrId;
    private String evntNm;
    private UseYn scenStartYn;
    private UseYn evntStartYn;
    private UseYn evntEndYn;
    private UseYn scenEndYn;
    private UseYn successYn;
    private UseStatCd useStatCd;
    private String errCd;
    private String errCm;
    private String crtUserId;
    private Timestamp crtDt;
    private String mdfyUserId;
    private Timestamp mdfyDt; // NOT NULL ENABLE


    @Builder
    public WnLvsEventReportDto(String trkId, String siteId, String lotId, String eqpId, String portId, String carrId, String evntNm, UseYn scenStartYn, UseYn evntStartYn, UseYn evntEndYn, UseYn scenEndYn, UseYn successYn, UseStatCd useStatCd, String errCd, String errCm, String crtUserId, Timestamp crtDt, String mdfyUserId, Timestamp mdfyDt) {
        this.trkId = trkId;
        this.siteId = siteId;
        this.lotId = lotId;
        this.eqpId = eqpId;
        this.portId = portId;
        this.carrId = carrId;
        this.evntNm = evntNm;
        this.scenStartYn = scenStartYn;
        this.evntStartYn = evntStartYn;
        this.evntEndYn = evntEndYn;
        this.scenEndYn = scenEndYn;
        this.successYn = successYn;
        this.useStatCd = useStatCd;
        this.errCd = errCd;
        this.errCm = errCm;
        this.crtUserId = crtUserId;
        this.crtDt = crtDt;
        this.mdfyUserId = mdfyUserId;
        this.mdfyDt = mdfyDt;
    }


    public WnLvsEventReportDto(EventStreamVo vo){
        this.trkId = vo.getMessageKey();
        this.siteId = vo.getSiteId();
        this.lotId = vo.getLotId();
        this.eqpId = vo.getEqpId();
        this.portId = vo.getPortId();
        this.carrId = vo.getCarrId();
        this.evntNm = vo.getCid();

        this.crtUserId = vo.getUserId();
        this.mdfyUserId = vo.getUserId();
        this.crtDt = Timestamp.from(Instant.now());
        this.mdfyDt = Timestamp.from(Instant.now());
    }


    public WnLvsEventReport toEntity(){
        return WnLvsEventReport.builder()
                .trkId(trkId)
                .siteId(siteId)
                .lotId(lotId)
                .eqpId(eqpId)
                .portId(portId)
                .carrId(carrId)
                .evntNm(evntNm)
                .scenStartYn(scenStartYn)
                .evntStartYn(evntStartYn)
                .evntEndYn(evntEndYn)
                .scenEndYn(scenEndYn)
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
