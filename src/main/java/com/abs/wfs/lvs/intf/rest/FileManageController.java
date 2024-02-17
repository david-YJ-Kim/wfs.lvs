package com.abs.wfs.lvs.intf.rest;

import com.abs.wfs.lvs.service.LvsLogStoreManager;
import com.abs.wfs.lvs.util.vo.FileReadDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(value = "/lvs/file")
public class FileManageController {

    @Autowired
    LvsLogStoreManager manager;


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void uploadFile(@RequestParam("file") MultipartFile file) throws Exception {

        log.info(file.getName());
    }


}
