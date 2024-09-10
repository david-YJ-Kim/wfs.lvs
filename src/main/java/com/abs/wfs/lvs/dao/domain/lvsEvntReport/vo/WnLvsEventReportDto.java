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
    private String payload;


    @Builder
    public WnLvsEventReportDto(String trkId, String siteId, String lotId, String eqpId, String portId, String carrId, String evntNm, Timestamp scenStartDt, Timestamp evntStartDt, Timestamp evntEndDt,Timestamp clearMemoryDt, Timestamp scenEndDt, UseYn successYn, UseStatCd useStatCd, String errCd, String errCm, String crtUserId, Timestamp crtDt, String mdfyUserId, Timestamp mdfyDt, String payload) {
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
        this.clearMemoryDt = clearMemoryDt;
        this.scenEndDt = scenEndDt;
        this.successYn = successYn;
        this.useStatCd = useStatCd;
        this.errCd = errCd;
        this.errCm = errCm;
        this.crtUserId = crtUserId;
        this.crtDt = crtDt;
        this.mdfyUserId = mdfyUserId;
        this.mdfyDt = mdfyDt;
        this.payload = payload;
    }



    public WnLvsEventReportDto(EventStreamVo vo){
        this.trkId = vo.getMessageKey();
        this.siteId = vo.getSiteId();
        this.lotId = vo.getLotId();
        this.eqpId = vo.getEqpId();
        this.portId = vo.getPortId();
        this.carrId = vo.getCarrId();
        this.evntNm = vo.getCid();
        this.scenStartDt = Timestamp.from(Instant.now());
        this.crtUserId = vo.getUserId();
        this.mdfyUserId = vo.getUserId();
        this.useStatCd = UseStatCd.Usable;
        this.crtDt = Timestamp.from(Instant.now());
        this.mdfyDt = Timestamp.from(Instant.now());
        this.payload = vo.getPayload();
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
                .scenStartDt(scenStartDt)
                .evntStartDt(evntStartDt)
                .evntEndDt(evntEndDt)
                .scenEndDt(scenEndDt)
                .clearMemoryDt(clearMemoryDt)
                .successYn(successYn)
                .useStatCd(useStatCd)
                .errCd(errCd)
                .errCm(errCm)
                .crtUserId(crtUserId)
                .crtDt(crtDt)
                .mdfyUserId(mdfyUserId)
                .mdfyDt(mdfyDt)
                .payload(payload)
                .build();
    }
}
