package com.key.jorigin.mq.rocketmq.producer;

import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.key.jorigin.mq.rocketmq.MqHandler;
import com.key.jorigin.mq.rocketmq.msg.RocketMqMessage;
import com.key.jorigin.mq.rocketmq.msg.StringMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RocketMQSendHandler implements MqHandler {

    private static final Logger log = LoggerFactory.getLogger(RocketMQSendHandler.class);

    private String groupId;

    private String topic;

    private String namesrvAddr;

    private DefaultMQProducer producer;

    public RocketMQSendHandler(String groupId, String topic, String namesrvAddr) {
        this.groupId = groupId;
        this.topic = topic;
        this.namesrvAddr = namesrvAddr;
        this.init_producer();
    }

    private void init_producer() {
        try {
            producer = new DefaultMQProducer(this.groupId);
            producer.setNamesrvAddr(this.namesrvAddr);
            producer.start();
        } catch (Exception e) {
            log.error("RocketMQReceiveHandler init_producter error! groupId = " + groupId, e);
        }
    }

    @Override
    public SendResult sendMessage(RocketMqMessage message) {
        SendResult result = null;
        try {
            com.alibaba.rocketmq.common.message.Message msg = getMessage(message);
            result = producer.send(msg);
        } catch (Exception e) {
            log.error("RocketMQReceiveHandler sendMessage error!", e);
        }
        return result;
    }

    private com.alibaba.rocketmq.common.message.Message getMessage(RocketMqMessage message) {
        byte[] body = null;
        if (message instanceof StringMessage) {
            body = ((StringMessage) message).getBody().getBytes();
        } else {
            throw new IllegalArgumentException("message:" + message.getClass() + " not support");
        }
        return new com.alibaba.rocketmq.common.message.Message(topic,
                message.getTags(),
                message.getKeys(),
                body);
    }

    @Override
    public SendResult sendMessage(RocketMqMessage message, MessageQueueSelector selector, Object arg) {
        SendResult result = null;
        try {
            com.alibaba.rocketmq.common.message.Message msg = getMessage(message);
            result = producer.send(msg, selector, arg);
        } catch (Exception e) {
            log.error("RocketMQReceiveHandler sendMessage error!", e);
        }
        return result;
    }

    @Override
    public Set<MessageQueue> fetchSubscribeMessage() throws Exception {
        throw new CloneNotSupportedException("该接口不支持");
    }

    @Override
    public PullResult pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums)
            throws Exception {
        throw new CloneNotSupportedException("该接口不支持");
    }

}
