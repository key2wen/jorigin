package com.key.jorigin.mq.rocketmq.msg;

public class StringMessage extends RocketMqMessage {

    private String body;

    public StringMessage(String body) {
        super();
        this.body = body;
    }

    public StringMessage(String keys, String body) {
        super(keys, "");
        this.body = body;
    }

    public StringMessage(String keys, String tags, String body) {
        super(keys, tags);
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("StringMessage [ keys=%s , tags=%s , body=%s]", this.getKeys(), this.getTags(), this.getBody());
    }
}
