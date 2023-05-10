package com.key.jorigin.mq.rocketmq.consumer.pull;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListener;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * 继承该MessageListener接口，是为了和 Push的监听类保持同一类型
 */
public interface PullMessageListener extends MessageListener {

    ConsumeConcurrentlyStatus consumeMessage(final MessageExt msg);
}
