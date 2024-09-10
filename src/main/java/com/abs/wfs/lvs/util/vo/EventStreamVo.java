package com.abs.wfs.lvs.util.vo;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

/**
 * Event Log Collection Vo
 * 단위 BWP 파일의 로그들을 저장, 하나의 message key로 값을 구분한다.
 *
 */
@Data
@Builder
public class EventStreamVo {

    /**
     * 발생된 메시지 키로, 각 bwp에서 해당 key를 통해서 흐름을 tracking
     */
    private String messageKey;

    private String siteId; // TODO siteId 가져오는 로직 추가 필요
    private String userId; // TODO userId 가져오는 로직 추가 필요

    /**
     * 각 이벤트를 의마하는 이벤트명
     */
    private String cid;




    /**
     * 이벤트에서 주요 필터를 거는 항목들
     * 부재 시, "" || null 구분
     */
    private String eqpId;
    private String lotId;
    private String carrId;
    private String portId;


    private Long timestamp;

    private String formattedTime;

    private String logLevel;

    private String payload;


    @Override
    public String toString() {
        return "EventStreamVo{" +
                "messageKey='" + messageKey + '\'' +
                ", siteId='" + siteId + '\'' +
                ", userId='" + userId + '\'' +
                ", cid='" + cid + '\'' +
                ", eqpId='" + eqpId + '\'' +
                ", lotId='" + lotId + '\'' +
                ", carrId='" + carrId + '\'' +
                ", portId='" + portId + '\'' +
                ", timestamp=" + timestamp +
                ", formattedTime='" + formattedTime + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
