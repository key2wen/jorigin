package com.key.jorigin.mq.rocketmq;


import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.key.jorigin.mq.rocketmq.msg.RocketMqMessage;

import java.util.Set;

public interface MqHandler {

    /**
     * 发送消息
     */
    SendResult sendMessage(RocketMqMessage message);

    /**
     * 发送消息
     * 可选择消息队列
     */
    SendResult sendMessage(RocketMqMessage message, MessageQueueSelector selector, Object arg);

    /**
     * 拉取消息
     *
     * @return
     */
    Set<MessageQueue> fetchSubscribeMessage() throws Exception;

    /**
     * pull message
     *
     * @param mq
     * @param subExpression
     * @param offset
     * @param maxNums       一次返回的消息maxNums
     * @return
     * @throws Exception
     */
    PullResult pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums) throws Exception;

}
