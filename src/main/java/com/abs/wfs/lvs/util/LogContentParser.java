package com.abs.wfs.lvs.util;

import com.abs.wfs.lvs.util.code.LvsConstant;
import com.abs.wfs.lvs.util.vo.AbnormalStartLogVo;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import jdk.jfr.internal.consumer.EventLog;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class LogContentParser {


    /**
     * AbnormalStartLog를 파싱해서 해당 body의 값을 가져옴
     * @param vo
     * @return
     */
    public AbnormalStartLogVo generateAbnormalStartLogVo(EventLogVo vo){


        JSONObject object = XML.toJSONObject(vo.getPayload());
        for(String key : object.keySet()){
            if(key.contains(LvsConstant.tns.name()) && key.contains(LvsConstant.StartElement.name())) {
                for (String subKey : object.getJSONObject(key).keySet()) {
                    if (subKey.contains(LvsConstant.message.name()) && !subKey.contains(LvsConstant.messageKey.name())) {

                        
                        // TODO Parsing 완료
                        JSONObject bodyJson = new JSONObject(object.getJSONObject(key).getString(subKey)).getJSONObject(LvsConstant.body.name());
                        String errCd = "Get From Parsing Data";
                        String errCm = "Get From Parsing Data";


                        return AbnormalStartLogVo.builder()
                                .errCd(errCd)
                                .errCm(errCm)
                                .build();
                    }
                }
            }
        }

        return null;
    }

}
