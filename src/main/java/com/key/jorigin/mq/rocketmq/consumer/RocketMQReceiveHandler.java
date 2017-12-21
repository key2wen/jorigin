package com.key.jorigin.mq.rocketmq.consumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.listener.MessageListener;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.hook.ConsumeMessageHook;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.key.jorigin.exception.BusinessException;
import com.key.jorigin.mq.rocketmq.MqHandler;
import com.key.jorigin.mq.rocketmq.emun.ConsumerType;
import com.key.jorigin.mq.rocketmq.msg.RocketMqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RocketMQReceiveHandler implements MqHandler {

    private static final Logger log = LoggerFactory.getLogger(RocketMQReceiveHandler.class);

    private String groupId;

    private String topic;

    private String tag;

    private String namesrvAddr;

    private ConsumerType consumerType;//消费类型

    private MessageListener messageListener;

    private DefaultMQPullConsumer pullConsumer;

    private DefaultMQPushConsumer pushConsumer;

    private ConsumeMessageHook consumeMessageHook;

    /**
     * Minimum consumer thread number
     */
    private int consumeThreadMin;
    /**
     * Max consumer thread number
     */
    private int consumeThreadMax;

    public RocketMQReceiveHandler(String groupId, String topic, String tag, ConsumerType ctype, MessageListener messageListener, String namesrvAddr,
                                  ConsumeMessageHook consumeMessageHook, int consumeThreadMin, int consumeThreadMax) {
        this.groupId = groupId;
        this.topic = topic;
        this.tag = tag;
        this.namesrvAddr = namesrvAddr;
        this.messageListener = messageListener;
        this.consumeMessageHook = consumeMessageHook;
        this.consumerType = ctype;
        this.consumeThreadMin = consumeThreadMin;
        this.consumeThreadMax = consumeThreadMax;
        this.init();
    }


    private void init() {
        if (ConsumerType.PUSH == this.consumerType) {
            init_pushConsumer();
        } else if (ConsumerType.PULL == this.consumerType) {
            init_pullConsumer();
        }
        log.info("handler client start..");
    }


    private void init_pullConsumer() {
        try {
            pullConsumer = new DefaultMQPullConsumer(this.groupId);
            pullConsumer.setNamesrvAddr(this.namesrvAddr);
            pullConsumer.start();
        } catch (Exception e) {
            log.error("RocketMQReceiveHandler init_pullConsumer error! groupId = " + groupId + " , topic = " + topic, e);
        }
    }

    /**
     * 默认：一个新的订阅组第一次启动从队列的最前位置开始消费
     */
    private void init_pushConsumer() {
        try {
            pushConsumer =
                    new DefaultMQPushConsumer(this.groupId);
            pushConsumer.setNamesrvAddr(this.namesrvAddr);
            pushConsumer.subscribe(topic,
                    tag);
            pushConsumer.setConsumeFromWhere(
                    ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            if (this.consumeMessageHook != null) {
                pushConsumer.getDefaultMQPushConsumerImpl().registerConsumeMessageHook(this.consumeMessageHook);
            }
            if (this.consumeThreadMin > 0) {
                pushConsumer.setConsumeThreadMin(consumeThreadMin);
            }
            if (this.consumeThreadMax > 0) {
                pushConsumer.setConsumeThreadMax(consumeThreadMax);
            }
            if (messageListener instanceof MessageListenerConcurrently) {
                pushConsumer.registerMessageListener((MessageListenerConcurrently) messageListener);
            } else if (messageListener instanceof MessageListenerOrderly) {
                pushConsumer.registerMessageListener((MessageListenerOrderly) messageListener);
            } else {
                throw new MQClientException(0, "Unsupported message listener class, " + messageListener.getClass().getCanonicalName());
            }
            pushConsumer.start();
        } catch (Exception e) {
            log.error("RocketMQReceiveHandler init_pushConsumerr error! groupId = " + groupId + " , topic = " + topic + " , tag = " + tag, e);
        }
    }

    @Override
    public SendResult sendMessage(RocketMqMessage message) {
        throw new BusinessException("该接口不支持");
    }


    @Override
    public SendResult sendMessage(RocketMqMessage message, MessageQueueSelector selector, Object arg) {
        throw new BusinessException("该接口不支持");
    }

    @Override
    public Set<MessageQueue> fetchSubscribeMessage() throws MQClientException {
        return pullConsumer.fetchSubscribeMessageQueues(this.topic);
    }

    @Override
    public PullResult pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return pullConsumer.pullBlockIfNotFound(mq, subExpression, offset, maxNums);
    }


    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public DefaultMQPullConsumer getPullConsumer() {
        return pullConsumer;
    }

    public void setPullConsumer(DefaultMQPullConsumer pullConsumer) {
        this.pullConsumer = pullConsumer;
    }

    public DefaultMQPushConsumer getPushConsumer() {
        return pushConsumer;
    }

    public void setPushConsumer(DefaultMQPushConsumer pushConsumer) {
        this.pushConsumer = pushConsumer;
    }

    public ConsumeMessageHook getConsumeMessageHook() {
        return consumeMessageHook;
    }

    public void setConsumeMessageHook(ConsumeMessageHook consumeMessageHook) {
        this.consumeMessageHook = consumeMessageHook;
    }
}
