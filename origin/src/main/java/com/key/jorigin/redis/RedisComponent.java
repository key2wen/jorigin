package com.key.jorigin.redis;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.JedisCluster;
import redis.clients.util.SafeEncoder;

//import javax.annotation.PreDestroy;
import java.util.*;
import java.util.Map.Entry;

/**
 * Redis 组件<br>
 */
public class RedisComponent implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Long LOCK_EXPIRED_TIME = 5 * 1000L;// 分布式锁的失效时间：5秒

    protected JedisCluster jedisCluster;

    private Serializer serializer;

    /****************** 常用方法 *******************/

    /**
     * 获取一个锁
     *
     * @param lock
     * @return
     */
    public boolean acquireLock(String lock) {
        return acquireLock(lock, LOCK_EXPIRED_TIME);
    }

    /**
     * 获取一个锁，自旋等待参数tryTime规定时间
     *
     * @param lock
     * @param expired 锁的失效时间（毫秒）
     * @param tryTime 获取锁等待时间（毫秒）
     * @return
     */
    public boolean acquireLock(String lock, long expired, int tryTime) {
        long beginTime = System.currentTimeMillis();
        boolean lockFlag = acquireLock(lock, expired);

        while (!lockFlag) {
            lockFlag = acquireLock(lock, expired);
            if ((System.currentTimeMillis() - beginTime) >= tryTime) {
                // 等待超时，
                return lockFlag;
            }
        }
        return true;
    }

    /**
     * 获取一个锁 必须保证分布式环境的多个主机的时钟是一致的
     *
     * @param lockKey
     * @return
     * @expired 锁的失效时间（毫秒）
     */
    public boolean acquireLock(String lockKey, long expired) {
        boolean success = false;
        try {
            long value = System.currentTimeMillis() + expired + 1;
            // 通过setnx获取一个lock
            //Redis Setnx（SET if Not eXists） 命令在指定的 key 不存在时，为 key 设置指定的值。设置成功，返回 1 。 设置失败，返回 0 。
            long acquired = jedisCluster.setnx(lockKey, String.valueOf(value));
            if (acquired == 1) {
                // setnx成功，则成功获取一个锁
                success = true;
            } else {
                // setnx失败，说明锁仍然被其他对象保持，检查其是否已经超时
                long oldValue = Long.valueOf(jedisCluster.get(lockKey));
                if (oldValue < System.currentTimeMillis()) {
                    // 超时情况
                    //返回给定 key 的旧值。 当 key 没有旧值时，即 key 不存在时，返回 nil 。当 key 存在但不是字符串类型时，返回一个错误。
                    String getValue = jedisCluster.getSet(lockKey, String.valueOf(value));
                    // 获取锁成功
                    if (Long.valueOf(getValue).longValue() == oldValue) {
                        success = true;
                    } else {
                        // 已被其他进程捷足先登了
                        success = false;
                    }
                } else {
                    // 未超时，则直接返回失败
                    success = false;
                }
            }
        } catch (Throwable e) {
            logger.error("acquireLock error", e);
        }
        return success;
    }

    /**
     * 释放锁
     *
     * @param lockKey key
     */
    public void releaseLock(String lockKey) {
        try {
            long current = System.currentTimeMillis();
            // 避免删除非自己获取到的锁
            if (jedisCluster.get(lockKey) != null && current < Long.valueOf(jedisCluster.get(lockKey))) {
                jedisCluster.del(lockKey);
            }
        } catch (Throwable e) {
            logger.error("releaseLock error", e);
        }
    }

    public <T> T lpop(final String key, final Class<T> c) {
        return deserialization(jedisCluster.lpop(SafeEncoder.encode(key)), c);
    }

    public <T> T rpop(final String key, final Class<T> c) {
        return deserialization(jedisCluster.rpop(SafeEncoder.encode(key)), c);
    }

    public Long lpush(final String key, final Object value) {
        return jedisCluster.lpush(SafeEncoder.encode(key), serialization(value));
    }

    public Long rpush(final String key, final Object value) {
        return jedisCluster.rpush(SafeEncoder.encode(key), serialization(value));
    }

    /**
     * 获取 key-value 的 value
     */
    public <T> T get(final String key, final Class<T> c) {
        return deserialization(jedisCluster.get(SafeEncoder.encode(key)), c);
    }

    /**
     * 获取 key-value 的 value. <br>
     * 如果 value 是一个 list, 请使用此方法.
     */
    public <T> List<T> getList(final String key, final Class<T> c) {
        return deserializationList(jedisCluster.get(SafeEncoder.encode(key)), c);
    }

    /**
     * 缓存 key-value
     */
    public void set(final String key, final Object value) {
        jedisCluster.set(SafeEncoder.encode(key), serialization(value));
    }

    /**
     * 缓存 key-value , seconds 过期时间,单位为秒.
     */
    public void set(final String key, final Object value, final int seconds) {
        jedisCluster.setex(SafeEncoder.encode(key), seconds, serialization(value));
    }

    /**
     * 不存在的key则缓存一个key-value, 存在则不用再次缓存
     *
     * @param key
     * @param value
     */
    public Long setnx(final String key, final Object value) {
        return jedisCluster.setnx(SafeEncoder.encode(key), serialization(value));
    }

    /**
     * 不存在的key则缓存一个key-value,seconds 过期时间,单位为秒. 存在 则 不再次缓存,也不设置过期时间
     *
     * @param key
     * @param value
     */
    public Long setnx(final String key, final Object value, final int seconds) {
        byte[] byteKey = SafeEncoder.encode(key);
        Long res = jedisCluster.setnx(byteKey, serialization(value));
        if (res != null && res > 0) {
            jedisCluster.expire(byteKey, seconds);
        }
        return res;
    }

    /**
     * 获取 key mapKey mapValue 中的 mapValue 列表.
     */
    public <T> List<T> hvals(final String key, final Class<T> c) {
        Collection<byte[]> value = jedisCluster.hvals(SafeEncoder.encode(key));
        List<T> list = new ArrayList<T>(value.size());
        for (byte[] bs : value) {
            list.add(deserialization(bs, c));
        }
        return list;
    }

    /**
     * 获取 key mapKey mapValue 中指定的 mapValue.
     * <p>
     * e.g. new TypeReference<List<Object>>() {} new TypeReference<Map<String,
     * Map<String, List<Object>>>>() {}
     */
    public <T> T hget(final String key, final Object mapKey, final TypeReference<T> type) {
        byte[] bs = jedisCluster.hget(SafeEncoder.encode(key), serialization(mapKey));
        return deserialization(bs, type);
    }

    /**
     * 获取 key mapKey mapValue 中指定的 mapValue.
     */
    public <T> T hget(final String key, final Object mapKey, final Class<T> c) {
        byte[] bs = jedisCluster.hget(SafeEncoder.encode(key), serialization(mapKey));
        return deserialization(bs, c);
    }

    /**
     * 获取 key mapKey mapValue 中指定的 mapValue.<br>
     * 如果 mapValue 是一个 list, 请使用此方法.
     */
    public <T> List<T> hgetList(final String key, final Object mapKey, final Class<T> c) {
        byte[] value = jedisCluster.hget(SafeEncoder.encode(key), serialization(mapKey));
        return deserializationList(value, c);
    }

    /**
     * 缓存 key mapKey mapValue.
     */
    public void hset(final String key, final Object mapKey, final Object mapValue) {
        jedisCluster.hset(SafeEncoder.encode(key), serialization(mapKey), serialization(mapValue));
    }

    public void hset(final String key, final Object mapKey, final Object mapValue, final int second) {
        jedisCluster.hset(SafeEncoder.encode(key), serialization(mapKey), serialization(mapValue));
        jedisCluster.expire(key, second);
    }

    /**
     * 删除集合中对应的key/value
     */
    public void hdel(final String key, final Object mapKey) {
        jedisCluster.hdel(SafeEncoder.encode(key), serialization(mapKey));
    }

    /**
     * 缓存 key map<mapKey,mapValue>.
     */
    public void hmset(final String key, final Map<Object, Object> map) {
        if (MapUtils.isNotEmpty(map)) {
            Map<byte[], byte[]> m = new HashMap<byte[], byte[]>(map.size());

            Iterator<Entry<Object, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Object, Object> next = it.next();
                m.put(serialization(next.getKey()), serialization(next.getValue()));
            }
            jedisCluster.hmset(SafeEncoder.encode(key), m);
        }
    }

    /**
     * 缓存 key map<mapKey,mapValue>，expireSeconds秒时间失效
     */
    public void hmset(final String key, final Map<Object, Object> map, final int expireSeconds) {
        if (MapUtils.isNotEmpty(map)) {
            Map<byte[], byte[]> m = new HashMap<byte[], byte[]>(map.size());

            Iterator<Entry<Object, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Object, Object> next = it.next();
                m.put(serialization(next.getKey()), serialization(next.getValue()));
            }
            jedisCluster.hmset(SafeEncoder.encode(key), m);
            jedisCluster.expire(key, expireSeconds);
        }
    }

    /**
     * 删除一个 Key.
     */
    public Long del(final String key) {
        return jedisCluster.del(SafeEncoder.encode(key));
    }

    /**
     * redis zadd command.
     */
    public Long zadd(final String key, final double score, final Object member) {
        return jedisCluster.zadd(SafeEncoder.encode(key), score, serialization(member));
    }

    /**
     * redis zrange command.
     */
    public <T> List<T> zrange(final String key, final long start, final long end, final Class<T> clazz) {
        Collection<byte[]> value = jedisCluster.zrange(SafeEncoder.encode(key), start, end);

        if (CollectionUtils.isEmpty(value)) {
            return null;
        }

        List<T> list = new ArrayList<T>(value.size());
        for (byte[] b : value) {
            list.add(deserialization(b, clazz));
        }
        return list;
    }

    /**
     * redis zrangeByScore command.
     */
    public <T> List<T> zrangeByScore(final String key, final double min, final double max, final Class<T> clazz) {
        Collection<byte[]> value = jedisCluster.zrangeByScore(SafeEncoder.encode(key), min, max);

        if (CollectionUtils.isEmpty(value)) {
            return null;
        }

        List<T> list = new ArrayList<T>(value.size());
        for (byte[] b : value) {
            list.add(deserialization(b, clazz));
        }
        return list;
    }

    /**
     * redis zremrangeByScore command.
     */
    public Long zremrangeByScore(final String key, final double start, final double end) {
        return jedisCluster.zremrangeByScore(key, start, end);
    }

    public Long zremrange(final String key, final String... members) {
        return jedisCluster.zrem(key, members);
    }

    /**
     * redis incr command.
     */
    public Long incr(final String key) {
        return jedisCluster.incr(key);
    }

    /**
     * redis incrby command.
     */
    public Long incrBy(final String key, final long integer) {
        return jedisCluster.incrBy(key, integer);
    }

    /**
     * redis decr command.
     */
    public Long decr(final String key) {
        return jedisCluster.decr(key);
    }

    /**
     * redis decrby command.
     */
    public Long decrBy(final String key, final long integer) {
        return jedisCluster.decrBy(key, integer);
    }

    /**
     * redis expire command.
     */
    public Long expire(final String key, final int seconds) {
        return jedisCluster.expire(key, seconds);
    }

    /**
     * redis exists command.
     */
    public Boolean exists(final String key) {
        return jedisCluster.exists(key);
    }

    public Long sadd(final String key, final Object value) {
        return jedisCluster.sadd(SafeEncoder.encode(key), serialization(value));
    }

    public Long srem(final String key, final Object value) {
        return jedisCluster.srem(SafeEncoder.encode(key), serialization(value));
    }

    public Long ttl(final String key) {
        return jedisCluster.ttl(key);
    }

    public Set<String> smembers(final String key) {
        Set<byte[]> byteValues = jedisCluster.smembers(SafeEncoder.encode(key));
        Set<String> stringValues = new HashSet<String>();
        if (byteValues == null) {
            return stringValues;
        }
        for (byte[] byteValue : byteValues) {
            stringValues.add(deserialization(byteValue, String.class));
        }
        return stringValues;
    }

    // private method
    // -----------------------------------------------------------------------
    private byte[] serialization(Object object) {
        return serializer.serialization(object);
    }

    private <T> T deserialization(byte[] byteArray, Class<T> c) {
        return serializer.deserialization(byteArray, c);
    }

    private <T> T deserialization(byte[] byteArray, TypeReference<T> type) {
        return serializer.deserialization(byteArray, type);
    }

    private <E> List<E> deserializationList(byte[] byteArray, Class<E> elementC) {
        return serializer.deserializationList(byteArray, elementC);
    }

//    @PreDestroy
    public void destory() {
        try {
            jedisCluster.close();
        } catch (Throwable e) {
            logger.error("", e);
        }
    }

    public static interface Serializer {
        byte[] serialization(Object object);

        <T> T deserialization(byte[] byteArray, Class<T> c);

        <T> T deserialization(byte[] byteArray, TypeReference<T> type);

        <E> List<E> deserializationList(byte[] byteArray, Class<E> elementC);
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.serializer == null) {
            // 为了向下兼容默认,如果没有提供序列化器,默认使用,json序列化
            serializer = new JsonSerializer();
        }
        logger.info("RedisComponent [" + this.toString() + "] is done! serializer:" + serializer.toString());
    }

}