package com.abs.wfs.lvs.util.vo;

import lombok.Builder;
import lombok.Data;

/**
 * Event Log Vo
 * 하나의 이벤트 메시지 처리하는데 있어서 발생한 로그 Vo
 * Appender를 통해 수신한 단일 메시지
 * 로그 자체
 */
@Data
@Builder
public class EventLogVo {


    /**
     * 이벤트 키로, 동일한 키를 가진 로그의 집합 존재
     */
    private String messageKey;
    private String logName;
    private Long timestamp;
    private String logLevel;
    private String threadName;
    private String classTrace;
    private String payload;
}
