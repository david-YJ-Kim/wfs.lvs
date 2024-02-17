package com.abs.wfs.lvs.util.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogMessageVo {


    private Long timestamp;
    private String logLevel;
    private String threadName;
    private String classTrace;
    private String logName;
    private String messageKey;
    private String payload;
}
