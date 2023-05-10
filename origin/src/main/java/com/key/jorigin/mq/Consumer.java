package com.key.jorigin.mq;

public interface Consumer<R, M extends Message> {

    R receiveMessage(M message);

}
