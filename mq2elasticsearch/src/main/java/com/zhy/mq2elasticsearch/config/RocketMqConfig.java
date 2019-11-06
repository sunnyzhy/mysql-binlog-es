package com.zhy.mq2elasticsearch.config;

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
    @Value("${rocketmq.consumer.group}")
    public String consumerGroup;
    @Value("${rocketmq.consumer.topic.student}")
    public String studentTopic;
    @Value("${rocketmq.consumer.topic.course}")
    public String courseTopic;
}
