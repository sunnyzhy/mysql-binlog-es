package com.zhy.binlog2mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Binlog2mqApplication {

    public static void main(String[] args) {
        SpringApplication.run(Binlog2mqApplication.class, args);
    }

}
