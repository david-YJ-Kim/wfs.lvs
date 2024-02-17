package com.abs.wfs.lvs.intf.rest;

import com.abs.wfs.lvs.service.LvsLogSearchManager;
import com.abs.wfs.lvs.util.vo.EventLogsVo;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping(value = "/lvs/get")
public class LogSearchController {

    @Autowired
    private LvsLogSearchManager searchManager;

    @RequestMapping(value = "/allLogs", method = RequestMethod.GET)
    public String printLogMap() {

        return searchManager.printLogMap();
    }

    @RequestMapping(value = "/logs/{key}", method = RequestMethod.GET)
    public EventLogsVo printLogMap(@PathVariable String key) {

        return searchManager.getEventFlow(key);
    }

    // get eqp id list 가져오기
    @RequestMapping(value = "/eqpList", method = RequestMethod.GET)
    public List<String> getEqpIdList() {

        return searchManager.getEqpIdList();
    }
    @RequestMapping(value = "/scenario/{eqpId}", method = RequestMethod.GET)
    public List<String> getScenarioFlowWithEqpId(@PathVariable String eqpId) {

        return searchManager.getScenarioFlowWithEqpId(eqpId);
    }


}
