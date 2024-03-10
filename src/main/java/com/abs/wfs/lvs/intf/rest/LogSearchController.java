package com.abs.wfs.lvs.intf.rest;

import com.abs.wfs.lvs.service.LvsLogSearchManager;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import com.abs.wfs.lvs.util.vo.EventStreamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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
    public ArrayList<EventLogVo> printLogMap(@PathVariable String key) {

        return searchManager.getEventFlow(key);
    }

    // get eqp id list 가져오기
    @RequestMapping(value = "/eqpList", method = RequestMethod.GET)
    public List<String> getEqpIdList() {

        return searchManager.getEqpIdList();
    }
    @RequestMapping(value = "/scenario/{eqpId}", method = RequestMethod.GET)
    public List<EventStreamVo> getScenarioFlowWithEqpId(@PathVariable String eqpId) {

        return searchManager.getScenarioFlowWithEqpId(eqpId);
    }


}
