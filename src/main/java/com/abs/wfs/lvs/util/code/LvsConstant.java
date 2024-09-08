package com.abs.wfs.lvs.util.code;

public enum LvsConstant {
    _,

    BRS,
    FIS,
    cid,
    messageId,
   R,
   local,
   remote,

   receiver,

   RELOAD_RULE,
   PATCH_RULE,
   
   A, 				// 파싱 컬럼 정보 기준 숫자
    FAIL,
    SUCCESS,

    ParsingFaile,	// 파싱 실패 롤백 명령

    InfoTribe,		// 메세지로 송신된 파일 정보가 부족

    DELETE_BATCH,		// rollback
    DELETE_FAIL,	// rollback Fail
    sftp,
    body,
    head,
    fileName,
    fileType,
    filePath,
    eqpId,

    logLevel,
    threadName,
    timestamp,
    classTrace,
    tns,
    StartElement,
    message,
    portId,
    carrId,
    lotId,
    messageKey,

    evntCm,
    rsltCm

    ;
}
