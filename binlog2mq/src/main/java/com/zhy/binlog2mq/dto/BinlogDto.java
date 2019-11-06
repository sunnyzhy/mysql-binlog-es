package com.zhy.binlog2mq.dto;

import lombok.Data;

/**
 * @author zhy
 * @date 2019/11/5 16:19
 */
@Data
public class BinlogDto {
    private Integer event;
    private Object data;

    public BinlogDto(Integer event, Object data) {
        this.event = event;
        this.data = data;
    }
}
