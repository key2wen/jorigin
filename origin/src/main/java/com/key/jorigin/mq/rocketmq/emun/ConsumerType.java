package com.key.jorigin.mq.rocketmq.emun;


import com.key.jorigin.exception.BusinessException;

public enum ConsumerType {

    PUSH("PUSH"),
    PULL("PULL");

    private String name;


    ConsumerType(String name) {
        this.name = name;
    }

    public static ConsumerType valueOfName(String name) {
        for (ConsumerType tmp : values())
            if (tmp.name.equals(name)) {
                return tmp;
            }
        throw new BusinessException("Involid value of ConsumerType");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
