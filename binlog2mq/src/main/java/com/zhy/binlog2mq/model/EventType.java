package com.zhy.binlog2mq.model;

/**
 * @author zhy
 * @date 2019/11/5 16:40
 */
public enum EventType {
    INSERT(1),
    UPDATE(2),
    DELETE(3);

    private Integer code;

    EventType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
