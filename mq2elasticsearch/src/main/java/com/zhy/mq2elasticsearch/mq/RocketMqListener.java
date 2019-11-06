package com.zhy.mq2elasticsearch.mq;

import com.zhy.mq2elasticsearch.config.RocketMqConfig;
import com.zhy.mq2elasticsearch.service.StudentConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhy
 * @date 2019/11/6 14:29
 */
@Component
@Slf4j
public class RocketMqListener implements MessageListenerConcurrently {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RocketMqConfig mqConfig;
    @Autowired
    private StudentConsumerService studentConsumerService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        BulkRequest bulkRequest = new BulkRequest();
        try {
            for (MessageExt message : list) {
                if (message.getTopic().equals(mqConfig.studentTopic)) {
                    studentConsumerService.execute(message, bulkRequest);
                } else if (message.getTopic().equals(mqConfig.courseTopic)) {

                }
            }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

}
