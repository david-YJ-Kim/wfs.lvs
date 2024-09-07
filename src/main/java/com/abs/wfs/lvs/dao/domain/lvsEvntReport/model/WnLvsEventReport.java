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
@Entity(name = "WN_LVS_EVENT_REPORT")
public class WnLvsEventReport {

    @Id
    @GenericGenerator(name = "WN_LVS_EVENT_REPORT_SEQ_GENERATOR", strategy = "com.abs.wfs.lvs.util.ObjIdGenerator")
    @GeneratedValue(generator = "WN_LVS_EVENT_REPORT_SEQ_GENERATOR")
    @Column(name = "OBJ_ID")
    private String objId;
    private String trkId;

    private String siteId;
    private String lotId;
    private String eqpId;
    private String portId;
    private String carrId;
    private String evntNm;

    @Enumerated(EnumType.STRING)
    private UseYn scenStartYn;

    @Enumerated(EnumType.STRING)
    private UseYn evntStartYn;

    @Enumerated(EnumType.STRING)
    private UseYn evntEndYn;

    @Enumerated(EnumType.STRING)
    private UseYn scenEndYn;

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


    @Builder
    public WnLvsEventReport(String objId, String trkId, String siteId, String lotId, String eqpId, String portId, String carrId, String evntNm, UseYn scenStartYn, UseYn evntStartYn, UseYn evntEndYn, UseYn scenEndYn, UseYn successYn, UseStatCd useStatCd, String errCd, String errCm, String crtUserId, Timestamp crtDt, String mdfyUserId, Timestamp mdfyDt) {
        this.objId = objId;
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
}
