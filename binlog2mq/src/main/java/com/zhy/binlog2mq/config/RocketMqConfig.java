package com.zhy.binlog2mq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhy
 * @date 2019/11/6 11:49
 */
@Configuration
public class RocketMqConfig {
    @Value("${rocketmq.host}")
    public String host;
    @Value("${rocketmq.tags}")
    public String tags;
    @Value("${rocketmq.producer.group}")
    public String producerGroup;
}
