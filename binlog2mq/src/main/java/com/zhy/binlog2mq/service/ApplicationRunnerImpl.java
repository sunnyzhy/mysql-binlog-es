package com.zhy.binlog2mq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author zhy
 * @date 2019/11/5 17:23
 */
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {
    @Autowired
    private BinglogService binglogService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        binglogService.run();
    }
}
