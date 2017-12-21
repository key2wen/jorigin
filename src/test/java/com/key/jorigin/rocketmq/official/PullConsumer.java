package com.key.jorigin.rocketmq.official;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PullConsumer，订阅消息
 */

public class PullConsumer {

    private static final Map<MessageQueue, Long> offseTable = new HashMap<MessageQueue, Long>();

    public static void main(String[] args) throws MQClientException {

        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("please_rename_unique_group_name_5");

        consumer.setNamesrvAddr("192.168.0.104:9876");

        consumer.start();

        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("TopicTest");

        for (MessageQueue mq : mqs) {

            System.out.println("Consume from the queue: " + mq);

            SINGLE_MQ:
            while (true) {

                try {

                    PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);

                    System.out.println(pullResult);

                    putMessageQueueOffset(mq, pullResult.getNextBeginOffset());

                    switch (pullResult.getPullStatus()) {

                        case FOUND:
                            // TODO
                            break;

                        case NO_MATCHED_MSG:

                            break;

                        case NO_NEW_MSG:

                            break SINGLE_MQ;

                        case OFFSET_ILLEGAL:

                            break;

                        default:

                            break;

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        }

        consumer.shutdown();

    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {

        offseTable.put(mq, offset);

    }

    private static long getMessageQueueOffset(MessageQueue mq) {

        Long offset = offseTable.get(mq);

        if (offset != null)

            return offset;

        return 0;

    }

}