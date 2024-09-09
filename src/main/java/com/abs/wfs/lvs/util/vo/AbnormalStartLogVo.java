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
