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
        /**
         * Message:
         * flag属性:似乎没什么用，只存储后透传
         * DelayTimeLevel方法:和 MessageStoreConfig#messageDelayLevel 相关，如1代表1s，2代表5s
         * get(set)BuyerId方法:基本没用，测试类里面才用
         *
         * is(set)WaitStoreMsgOK: 表示消息是否在服务器落盘后才返回应答
         * 只有同步刷盘的时候，这个配置才work
         * 参考CommitLog#handleDiskFlush以及handleHA函数的if嵌套条件
         *
         * get(set)Keys 消息索引用，可以根据key查询消息，和Index部分相关
         *
         * putUserProperty 和 putProperty的区别: 内部API用的putProperty， 外部尽量用putUserProperty
         */
        com.alibaba.rocketmq.common.message.Message msg = new com.alibaba.rocketmq.common.message.Message(topic,
                message.getTags(),
                message.getKeys(),
                body);
        /**
         * 添加延迟消息设置
         *
         * RcoketMQ的延时等级为：1s，5s，10s，30s，1m，2m，3m，4m，5m，6m，7m，8m，9m，10m，20m，30m，1h，2h。
         * level=0，表示不延时。level=1，表示 1 级延时，对应延时 1s。level=2 表示 2 级延时，对应5s，以此类推
         */
        if (message.getDelayTimeLevel() != null) {
            msg.setDelayTimeLevel(message.getDelayTimeLevel());
        }
        return msg;
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
