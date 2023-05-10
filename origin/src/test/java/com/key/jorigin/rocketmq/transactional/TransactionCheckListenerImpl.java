package com.key.jorigin.rocketmq.transactional;

import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 未决事务，服务器回查客户端，broker端发起请求代码没有被调用，所以此处代码可能没用。
 */

public class TransactionCheckListenerImpl implements TransactionCheckListener {

    private AtomicInteger transactionIndex = new AtomicInteger(0);

    @Override

    public LocalTransactionState checkLocalTransactionState(MessageExt msg) {

        System.out.println("server checking TrMsg " + msg.toString());

        int value = transactionIndex.getAndIncrement();

        if ((value % 6) == 0) {

            throw new RuntimeException("Could not find db");

        } else if ((value % 5) == 0) {

            return LocalTransactionState.ROLLBACK_MESSAGE;

        } else if ((value % 4) == 0) {

            return LocalTransactionState.COMMIT_MESSAGE;

        }

        return LocalTransactionState.UNKNOW;

    }

}