package com.key.jorigin.mq.rocketmq.msg;

import com.key.jorigin.mq.Message;
import org.apache.commons.lang.StringUtils;

public class RocketMqMessage implements Message {

    private String keys;

    private int flag;

    private String tags;

    private Integer delayTimeLevel;


    public RocketMqMessage() {
        this.tags = "";
        this.flag = 0;
        this.keys = "";
    }

    public RocketMqMessage(String keys, String tags) {
        this.tags = StringUtils.isNotBlank(tags) ? tags : "";
        this.flag = 0;
        this.keys = StringUtils.isNotBlank(keys) ? keys : "";
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public Integer getDelayTimeLevel() {
        return delayTimeLevel;
    }

    public void setDelayTimeLevel(Integer delayTimeLevel) {
        this.delayTimeLevel = delayTimeLevel;
    }
}
