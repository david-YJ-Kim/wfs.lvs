package com.abs.wfs.lvs.util.vo;


import lombok.Builder;
import lombok.Data;


/**
 * Abnormal Start Log 데이터를 표현하는 Vo 객체
 */
@Data
@Builder
public class AbnormalStartLogVo {

    // Vo내 항목리스트
    public static final String ErrCd = "ErrCd";
    public static final String ErrCm = "ErrCm";


    private String errCd;
    private String errCm;



}

/*
messageKey=1725979322286-ae867b42-6d84-4f8f-bbe5-b853cb04ae93,
logName=AbnormalStartLog,
timestamp=1725979322650,
logLevel=INFO,
threadName=bwEngThread:In-Memory Process Worker-1,
classTrace=com.tibco.bw.palette.generalactivities.Log.workflow.AbnormalStartLog,
payload=
<tns9:ErrorElement xmlns:tns9="http://www.example.org/SystemLevelProcess">
    <tns9:messageKey>1725979322286-ae867b42-6d84-4f8f-bbe5-b853cb04ae93</tns9:messageKey>
    <tns9:scenarioTyp>INOUT_SINGLE</tns9:scenarioTyp>
    <tns9:eventName>WFS_DSP_WORK_REP</tns9:eventName>
    <tns9:errCd>ReasonCdFail</tns9:errCd>
</tns9:ErrorElement>
 */