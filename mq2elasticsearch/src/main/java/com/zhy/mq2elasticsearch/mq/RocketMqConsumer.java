package com.zhy.mq2elasticsearch.mq;

import com.zhy.mq2elasticsearch.config.RocketMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

/**
 * @author zhy
 * @date 2019/11/6 14:35
 */
@Service
@Slf4j
public class RocketMqConsumer {
    @Autowired
    private RocketMqConfig mqConfig;
    @Autowired
    private RocketMqListener mqListener;
    private DefaultMQPushConsumer consumer;

    @PostConstruct
    public void init() {
        consumer = new DefaultMQPushConsumer(mqConfig.consumerGroup);
        consumer.setNamesrvAddr(mqConfig.host);
        consumer.setConsumeMessageBatchMaxSize(32);
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMax(20);
        consumer.setInstanceName(UUID.randomUUID().toString());
        consumer.registerMessageListener(mqListener);
        try {
            consumer.start();
            consumer.subscribe(mqConfig.studentTopic, mqConfig.tags);
            consumer.subscribe(mqConfig.courseTopic, mqConfig.tags);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @PreDestroy
    public void destroy() {
        consumer.shutdown();
    }
}
