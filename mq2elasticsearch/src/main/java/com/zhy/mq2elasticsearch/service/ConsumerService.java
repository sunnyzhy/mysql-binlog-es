package com.zhy.mq2elasticsearch.service;

import org.apache.rocketmq.common.message.MessageExt;
import org.elasticsearch.action.bulk.BulkRequest;

/**
 * @author zhy
 * @date 2019/11/6 17:02
 */
public interface ConsumerService {
    String charset = "UTF-8";

    void execute(MessageExt msg, BulkRequest bulkRequest) throws Exception;
}
