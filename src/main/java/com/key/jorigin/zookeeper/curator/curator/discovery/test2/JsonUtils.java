package com.key.jorigin.zookeeper.curator.curator.discovery.test2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * ${DESCRIPTION}
 *
 */
public class JsonUtils {
    private static final Gson GSON = new GsonBuilder().create();

    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }
}