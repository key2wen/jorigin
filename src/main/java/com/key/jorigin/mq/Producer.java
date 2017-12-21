package com.key.jorigin.mq;

public interface Producer<R, M extends Message> {

    R sendMessage(M message);

}
