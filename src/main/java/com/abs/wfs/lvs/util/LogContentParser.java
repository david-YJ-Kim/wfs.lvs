package com.abs.wfs.lvs.util;

import com.abs.wfs.lvs.util.code.LvsConstant;
import com.abs.wfs.lvs.util.vo.AbnormalStartLogVo;
import com.abs.wfs.lvs.util.vo.EventLogVo;
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

        String errCd = null;
        String errCm = null;

        JSONObject object = XML.toJSONObject(vo.getPayload());
        for(String key : object.keySet()){
            log.info(key);
            if(key.contains(LvsConstant.tns.name()) && key.contains(LvsConstant.ErrorElement.name())) {

                JSONObject errorElement = object.getJSONObject(key);
                for(String eleKey : errorElement.keySet()){
                    if(eleKey.contains(LvsConstant.errCd.name())) {
                        errCd = errorElement.getString(eleKey);
                    }

                    if(eleKey.contains(LvsConstant.errCm.name())){
                        errCm = errorElement.getString(eleKey);
                    }
                }

                return AbnormalStartLogVo.builder()
                        .errCd(errCd)
                        .errCm(errCm)
                        .build();
            }
        }

        return null;
    }

}
