package com.zhy.binlog2mq.service;

import com.alibaba.fastjson.JSON;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.zhy.binlog2mq.dto.BinlogDto;
import com.zhy.binlog2mq.model.EventType;
import com.zhy.binlog2mq.mq.RocketMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhy
 * @date 2019/11/5 16:02
 */
@Service
public class BinglogService {
    @Autowired
    private BinaryLogClient client;
    @Autowired
    private RocketMqProducer mqProducer;
    @Autowired
    private List<String> table;
    private ConcurrentHashMap<Long, String> tableMap = new ConcurrentHashMap<>();

    @Async
    public void run() throws IOException {
        // 注册监听事件
        client.registerEventListener(event -> {
            EventData data = event.getData();
            if (data == null) {
                return;
            }
            if (data instanceof TableMapEventData) {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                String value = tableMapEventData.getDatabase() + "_" + tableMapEventData.getTable();
                if (!table.contains(value)) {
                    return;
                }
                tableMap.put(tableMapEventData.getTableId(), value);
            }
            if (data instanceof UpdateRowsEventData) { // update
                UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) data;
                String topic = tableMap.get(updateRowsEventData.getTableId());
                for (Map.Entry<Serializable[], Serializable[]> row : updateRowsEventData.getRows()) {
                    send(topic, new BinlogDto(EventType.UPDATE.getCode(), row.getValue()));
                }
            } else if (data instanceof WriteRowsEventData) { // insert
                WriteRowsEventData writeRowsEventData = (WriteRowsEventData) data;
                String topic = tableMap.get(writeRowsEventData.getTableId());
                for (Serializable[] row : writeRowsEventData.getRows()) {
                    send(topic, new BinlogDto(EventType.INSERT.getCode(), row));
                }
            } else if (data instanceof DeleteRowsEventData) { // delete
                DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) data;
                String topic = tableMap.get(deleteRowsEventData.getTableId());
                for (Serializable[] row : deleteRowsEventData.getRows()) {
                    send(topic, new BinlogDto(EventType.DELETE.getCode(), row));
                }
            }
        });
        client.connect();
    }

    private void send(String topic, BinlogDto binlogDto) {
        String msg = JSON.toJSONString(binlogDto);
        mqProducer.send(topic, msg);
    }
}
