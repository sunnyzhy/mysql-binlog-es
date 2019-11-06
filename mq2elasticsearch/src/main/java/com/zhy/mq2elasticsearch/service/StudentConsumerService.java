package com.zhy.mq2elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhy.mq2elasticsearch.dto.BinlogDto;
import com.zhy.mq2elasticsearch.model.EventType;
import org.apache.rocketmq.common.message.MessageExt;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhy
 * @date 2019/11/6 17:07
 */
@Service
public class StudentConsumerService implements ConsumerService {
    @Override
    public void execute(MessageExt msg, BulkRequest bulkRequest) throws Exception {
        String message = new String(msg.getBody(), charset);
        String index = msg.getTopic();
        BinlogDto binlog = JSON.parseObject(message, BinlogDto.class);
        JSONArray array = JSON.parseArray(binlog.getData().toString());
        String id = array.get(0).toString();
        if (binlog.getEvent().equals(EventType.INSERT.getCode())) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", array.get(0));
            map.put("name", array.get(1));
            IndexRequest request = new IndexRequest(index)
                    .id(id)
                    .source(map);
            bulkRequest.add(request);
        } else if (binlog.getEvent().equals(EventType.UPDATE.getCode())) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", array.get(1));
            UpdateRequest request = new UpdateRequest(index, id)
                    .doc(map);
            bulkRequest.add(request);
        } else if (binlog.getEvent().equals(EventType.DELETE.getCode())) {
            DeleteRequest request = new DeleteRequest(index, id);
            bulkRequest.add(request);
        }
    }
}
