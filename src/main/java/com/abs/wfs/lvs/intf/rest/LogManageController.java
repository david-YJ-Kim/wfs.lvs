package com.abs.wfs.lvs.intf.rest;

import com.abs.wfs.lvs.service.LvsLogSearchManager;
import com.abs.wfs.lvs.service.LvsLogStoreManager;
import com.abs.wfs.lvs.util.vo.EventLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping(value = "/lvs/manage")
public class LogManageController {

    @Autowired
    private LvsLogSearchManager manager;

    @Autowired
    LvsLogStoreManager lvsLogStoreManager;


    @RequestMapping(value = "/delete/allLogs", method = RequestMethod.DELETE)
    public void deleteAllLogs() {
        manager.deleteAllLogs();
    }


    @RequestMapping(value = "/delete/eqpId/{eqpId}/logs/{key}", method = RequestMethod.DELETE)
    public void deleteLog(@PathVariable String eqpId, @PathVariable String key) {
        manager.deleteLogByKey(eqpId, key);
    }

    @RequestMapping(value = "/delete/eqpId/{eqpId}", method = RequestMethod.DELETE)
    public void deleteScenario(@PathVariable String eqpId) {
        manager.deleteScenarioByKey(eqpId);
    }


    @RequestMapping(value = "/insert/log", method = RequestMethod.POST)
    public void postLogVo(@RequestBody EventLogVo vo){

        this.lvsLogStoreManager.execute(vo);

    }


}
