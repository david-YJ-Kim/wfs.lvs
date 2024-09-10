package com.abs.wfs.lvs.dao.domain.lvsEvntReport.model;


import com.abs.wfs.lvs.util.vo.UseStatCd;
import com.abs.wfs.lvs.util.vo.UseYn;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "WH_LVS_EVENT_REPORT")
public class WhLvsEventReport {

    @Id
    @GenericGenerator(name = "WH_LVS_EVENT_REPORT_SEQ_GENERATOR", strategy = "com.abs.wfs.lvs.util.ObjIdGenerator")
    @GeneratedValue(generator = "WH_LVS_EVENT_REPORT_SEQ_GENERATOR")
    @Column(name = "OBJ_ID")
    private String objId;
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

    @Enumerated(EnumType.STRING)
    private UseYn successYn;


    @Enumerated(EnumType.STRING)
    private UseStatCd useStatCd;

    private String errCd;
    private String errCm;

    private String crtUserId;
    private Timestamp crtDt;
    private String mdfyUserId;
    private Timestamp mdfyDt; // NOT NULL ENABLE


    private String payload; // 수신한 메시지 전문

    @Builder
    public WhLvsEventReport(String objId, String refObjId, String trkId, String siteId, String lotId, String eqpId, String portId, String carrId, String evntNm, Timestamp scenStartDt, Timestamp evntStartDt, Timestamp evntEndDt, Timestamp scenEndDt, Timestamp clearMemoryDt, UseYn successYn, UseStatCd useStatCd, String errCd, String errCm, String crtUserId, Timestamp crtDt, String mdfyUserId, Timestamp mdfyDt, String payload) {
        this.objId = objId;
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
        this.payload = payload;
    }
}
