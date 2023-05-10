package com.key.jorigin.mq.rocketmq.consumer.pull;

import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.PullStatus;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.key.jorigin.mq.rocketmq.consumer.RocketMQReceiveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * pull 消息 需要自己处理消息的消费
 */
public class PullMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(PullMessageHandler.class);

    private static final int MAX_NUMS = 32;//一次拉取的最多消息总数

    private static final long TIME_ONE_HOUR = 60 * 60 * 1000;

    private static final long TIME_RECONSUMER = 60 * 1000;

    private static final long TIME_REPULL = 30 * 1000;

    private long spaceTime;

    private static final Map<MessageQueue, Long> offseTable = new HashMap<MessageQueue, Long>();

    public static final ExecutorService EXECUTOR_SERVICE = Executors
            .newCachedThreadPool();

    public RocketMQReceiveHandler rocketMQReceiveHandler;

    public void init() throws Exception {

        Set<MessageQueue> messageQueues = rocketMQReceiveHandler.fetchSubscribeMessage();
        final Semaphore SEMAPHORE = new Semaphore(messageQueues.size());

        for (MessageQueue messageQueue : messageQueues) {
            EXECUTOR_SERVICE.execute(new PullMessageTask(messageQueue, SEMAPHORE, "thread_mq_" + messageQueue.getQueueId()));
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                EXECUTOR_SERVICE.shutdown();
            }
        });

    }


    /**
     * 每个队列的消息拉取线程
     */
    private class PullMessageTask implements Runnable {

        private MessageQueue messageQueue;//当前队列

        private String taskName;

        private Semaphore semaphore;

        public PullMessageTask(MessageQueue messageQueue, Semaphore semaphore, String taskName) {
            this.messageQueue = messageQueue;
            this.semaphore = semaphore;
            this.taskName = taskName;
        }

        @Override
        public void run() {
            SINGLE_MQ:
            while (true) {
                logger.debug("pullMessageTask running topic = " + messageQueue.getTopic() + " , thread[" + Thread.currentThread().getId() + "] = " + taskName + " , broker = " + messageQueue.getBrokerName() + " , queueId = " + messageQueue.getQueueId());
                try {
                    semaphore.acquire();

                    long offset = getMessageQueueOffset(messageQueue);
                    PullResult pullResult =
                            rocketMQReceiveHandler.pullBlockIfNotFound(messageQueue, null, offset < 0 ? 0 : offset, MAX_NUMS);

                    if (pullResult.getPullStatus() == PullStatus.FOUND) {
                        logger.debug("pullBlockIfNotFound status = " + pullResult.getPullStatus() + " , offset = " + pullResult.getNextBeginOffset());
                        consumerMessageList(pullResult.getMsgFoundList());
                    } else if (pullResult.getPullStatus() == PullStatus.NO_NEW_MSG) {
                        logger.debug("pullBlockIfNotFound-#no new message,quit while#");
//                        break SINGLE_MQ;
                    } else if (pullResult.getPullStatus() == PullStatus.NO_MATCHED_MSG) {
                        logger.debug("pullBlockIfNotFound-#NO_MATCHED_MSG#");
                    } else if (pullResult.getPullStatus() == PullStatus.OFFSET_ILLEGAL) {
                        logger.debug("pullBlockIfNotFound-#OFFSET_ILLEGAL#");
                    } else {
                        logger.debug("pullBlockIfNotFound-#other#");
                    }

                    //update offset
                    putMessageQueueOffset(messageQueue, pullResult.getNextBeginOffset());
                    //sleep spaceTime
                    if (spaceTime > 0) {
                        spaceTime = spaceTime > TIME_ONE_HOUR ? TIME_ONE_HOUR : spaceTime;
//                        spaceTime = spaceTime < TIME_REPULL ? TIME_REPULL : spaceTime;
                        Thread.sleep(spaceTime);
                    }
                } catch (Exception e) {
                    logger.debug("pullMessageTask error! ", e);
                } finally {
                    semaphore.release();
                }
            }
        }

        public void consumerMessageList(List<MessageExt> list) throws Exception {
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            for (MessageExt msg : list) {
                ConsumeConcurrentlyStatus status = consumeMessage(msg);
                if (status == null) {
                    logger.debug("status is null! id = " + msg.getMsgId());
                    //todo
                } else if (status == ConsumeConcurrentlyStatus.RECONSUME_LATER) {
                    logger.debug("RECONSUME_LATER message id = " + msg.getMsgId());
                    //todo
                } else if (status == ConsumeConcurrentlyStatus.CONSUME_SUCCESS) {
                    logger.debug("consumeMessage success! msgId = " + msg.getMsgId() + " , status = " + status);
                }
            }
        }

        /**
         * update queue offset
         *
         * @param mq
         * @param offset
         */
        private void putMessageQueueOffset(MessageQueue mq, long offset) throws Exception {
            offseTable.put(mq, offset);
            rocketMQReceiveHandler.getPullConsumer().updateConsumeOffset(mq, offset);
            logger.debug("update offset queue = " + messageQueue.getQueueId() + " , offset = " + offset);
        }

        public ConsumeConcurrentlyStatus consumeMessage(MessageExt msg) {
            try {
                return ((PullMessageListener) rocketMQReceiveHandler.getMessageListener()).consumeMessage(msg);
            } catch (Exception e) {
                logger.error("consumeMessage error! id = " + msg.getMsgId(), e);
            }
            return null;
        }

        private long getMessageQueueOffset(MessageQueue mq) throws Exception {
            Long offset = offseTable.get(mq);
            logger.debug("#offseTable get queue = " + mq.getQueueId() + " , offset = " + offset + "#");
            if (offset != null) {
                logger.debug("getMessageQueueOffset = " + offset);
                return offset;
            }
            return rocketMQReceiveHandler.getPullConsumer().fetchConsumeOffset(mq, false);
        }

        public MessageQueue getMessageQueue() {
            return messageQueue;
        }

        public void setMessageQueue(MessageQueue messageQueue) {
            this.messageQueue = messageQueue;
        }

    }

    public long getSpaceTime() {
        return spaceTime;
    }

    public void setSpaceTime(long spaceTime) {
        this.spaceTime = spaceTime;
    }

    public RocketMQReceiveHandler getRocketMQReceiveHandler() {
        return rocketMQReceiveHandler;
    }

    public void setRocketMQReceiveHandler(RocketMQReceiveHandler rocketMQReceiveHandler) {
        this.rocketMQReceiveHandler = rocketMQReceiveHandler;
    }
}
