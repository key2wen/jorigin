package com.key.jorigin.redis;

import com.alibaba.fastjson.TypeReference;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class HessianSerializer implements RedisComponent.Serializer {

    private Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    public byte[] serialization(Object object) {
        if (object == null)
            return null;

        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            Hessian2Output o = new Hessian2Output(bao);
            o.writeObject(object);
            o.flush();
            return bao.toByteArray();
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialization(byte[] byteArray, Class<T> c) {
        if (byteArray == null || byteArray.length == 0)
            return null;

        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(byteArray);
            Hessian2Input i = new Hessian2Input(bai);
            return (T) i.readObject(c);
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialization(byte[] byteArray, TypeReference<T> type) {
        if (byteArray == null || byteArray.length == 0)
            return null;

        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(byteArray);
            Hessian2Input i = new Hessian2Input(bai);
            return (T) i.readObject();
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> deserializationList(byte[] byteArray, Class<E> elementC) {
        if (byteArray == null || byteArray.length == 0)
            return null;

        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(byteArray);
            Hessian2Input i = new Hessian2Input(bai);
            return (List<E>) i.readObject();
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }
    }

}