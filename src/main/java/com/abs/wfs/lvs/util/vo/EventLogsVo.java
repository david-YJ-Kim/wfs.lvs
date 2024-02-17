package com.abs.wfs.lvs.util.vo;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class EventLogsVo {

    private String messageKey;
    private String cid;
    ArrayList<LogMessageVo> logMessages;
}
