package com.zhy.binlog2mq.mq;

import com.zhy.binlog2mq.config.RocketMqConfig;
import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

/**
 * @author zhy
 * @date 2019/11/6 11:48
 */
@Component
@Slf4j
public class RocketMqProducer {
    @Autowired
    private RocketMqConfig mqConfig;
    private DefaultMQProducer producer;
    private String charset = "UTF-8";

    @PostConstruct
    public void init() {
        producer = new DefaultMQProducer(mqConfig.producerGroup);
        producer.setNamesrvAddr(mqConfig.host);
        producer.setInstanceName(UUID.randomUUID().toString());
        try {
            producer.start();
        } catch (MQClientException ex) {
            log.error(ex.getErrorMessage(), ex);
        }
    }

    public boolean send(String topic, String msg) {
        try {
            Message message = new Message(
                    topic,
                    mqConfig.tags,
                    msg.getBytes(charset));
            SendResult sendResult = producer.send(message);
            return sendResult.getSendStatus() == SendStatus.SEND_OK ? true : false;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @PreDestroy
    public void destroy() {
        producer.shutdown();
    }
}
