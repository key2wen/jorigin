package com.key.jorigin.spring.es.json;

import com.alibaba.fastjson.JSON;
import org.springframework.data.elasticsearch.core.EntityMapper;

import java.io.IOException;


public class FastjsonEntityMapper implements EntityMapper {

    @Override
    public String mapToString(Object object) throws IOException {
        return JSON.toJSONString(object);
    }

    @Override
    public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
        return JSON.parseObject(source, clazz);
    }
}
