package com.abs.wfs.lvs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LvsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LvsApplication.class, args);
    }
}
