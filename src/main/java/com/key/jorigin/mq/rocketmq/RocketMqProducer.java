package com.key.jorigin.mq.rocketmq;

import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.key.jorigin.mq.Producer;
import com.key.jorigin.mq.rocketmq.msg.RocketMqMessage;
import com.key.jorigin.mq.rocketmq.producer.RocketMQSendHandler;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * create by zwh
 */
public class RocketMqProducer implements Producer<SendResult, RocketMqMessage> {

    private String groupId;

    private String topic;

    private String namesrvAddr;

    private MqHandler mqHandler;

    private AtomicBoolean inited = new AtomicBoolean(false);

    public RocketMqProducer() {
    }

    public RocketMqProducer(String groupId, String topic, String namesrvAddr) {
        this.groupId = groupId;
        this.topic = topic;
        this.namesrvAddr = namesrvAddr;
    }

    public synchronized void init() {
        if (inited.get()) {
            return;
        }
        mqHandler = new RocketMQSendHandler(groupId, topic, namesrvAddr);
        inited.set(true);
    }

    @Override
    public SendResult sendMessage(RocketMqMessage message) {
        return mqHandler.sendMessage(message);
    }

    public SendResult sendMessage(RocketMqMessage message, MessageQueueSelector selector, Object arg) {
        return mqHandler.sendMessage(message, selector, arg);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public MqHandler getMqHandler() {
        return mqHandler;
    }

    public void setMqHandler(MqHandler mqHandler) {
        this.mqHandler = mqHandler;
    }


}
