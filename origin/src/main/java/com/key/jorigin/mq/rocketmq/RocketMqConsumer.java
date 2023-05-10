package com.key.jorigin.mq.rocketmq;

import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.listener.MessageListener;
import com.alibaba.rocketmq.client.hook.ConsumeMessageHook;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.key.jorigin.mq.Consumer;
import com.key.jorigin.mq.rocketmq.consumer.RocketMQReceiveHandler;
import com.key.jorigin.mq.rocketmq.consumer.pull.PullMessageHandler;
import com.key.jorigin.mq.rocketmq.emun.ConsumerType;
import com.key.jorigin.mq.rocketmq.msg.RocketMqMessage;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class RocketMqConsumer implements Consumer<Void, RocketMqMessage> {

    private String groupId;

    private String topic;

    private String tag;

    private String ctype;//消费类型

    private String namesrvAddr;

    private MessageListener messageListener;

    /**
     * 消息消费方法Hook
     */
    private ConsumeMessageHook consumeMessageHook;

    /**
     * Minimum consumer thread number
     */
    private int consumeThreadMin;
    /**
     * Max consumer thread number
     */
    private int consumeThreadMax;

    private MqHandler mqHandler;

    private PullMessageHandler pullMessageHandler;

    private AtomicBoolean inited = new AtomicBoolean(false);

    public RocketMqConsumer() {
    }

    public RocketMqConsumer(String groupId, String topic, String tag, String ctype, String namesrvAddr, MessageListener messageListener) {
        this.groupId = groupId;
        this.topic = topic;
        this.tag = tag;
        this.ctype = ctype;
        this.namesrvAddr = namesrvAddr;
        this.messageListener = messageListener;
    }

    public Set<MessageQueue> fetchSubscribeMessage() throws Exception {
        return mqHandler.fetchSubscribeMessage();
    }

    public PullResult pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums) throws Exception {
        return mqHandler.pullBlockIfNotFound(mq, subExpression, offset, maxNums);
    }


    public synchronized void init() throws Exception {
        if (inited.get()) {
            return;
        }
        ConsumerType consumerType = ConsumerType.valueOfName(ctype);
        mqHandler = new RocketMQReceiveHandler(groupId, topic, tag, consumerType, messageListener, namesrvAddr,
                consumeMessageHook, consumeThreadMin, consumeThreadMax);

        //拉模式，自定义处理消息
        if (consumerType == ConsumerType.PULL) {
            pullMessageHandler = new PullMessageHandler();
            pullMessageHandler.setRocketMQReceiveHandler((RocketMQReceiveHandler) mqHandler);
            pullMessageHandler.init();
        }

        inited.set(true);
    }

    @Override
    public Void receiveMessage(RocketMqMessage message) {
        return null;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public ConsumeMessageHook getConsumeMessageHook() {
        return consumeMessageHook;
    }

    public void setConsumeMessageHook(ConsumeMessageHook consumeMessageHook) {
        this.consumeMessageHook = consumeMessageHook;
    }

    public int getConsumeThreadMin() {
        return consumeThreadMin;
    }

    public void setConsumeThreadMin(int consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public int getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public void setConsumeThreadMax(int consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }


}
