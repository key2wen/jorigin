import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * create by zwh
 * <p>
 * http://ifeve.com/guava-source-cache/
 * guava cache实现原理:
 * 类似ConcurrentHashMap 的存储结构存储数据（线程安全）
 * LRU淘汰策略 (数据超量,维护一个访问顺序双向链表,淘汰表头数据)
 * 超时清除策略 (数据超时，维护一个写入顺序双向链表，淘汰表头数据)
 * 获取/写入 数据操作时做相应的淘汰策略
 */
public class CuavaCacheTest {


    static LoadingCache<Long, Long> _sessionCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<Long, Long>() {

                public Long load(Long key) throws Exception {
                    //value
                    return new Random().nextLong();
                }
            });

    public void getCache() throws ExecutionException {
        long key = 1;
        _sessionCache.get(key);
    }



//    static LoadingCache<Long, CustomSessionEntity> _sessionCache = CacheBuilder.newBuilder()
//            .maximumSize(100)
//            .expireAfterWrite(30, TimeUnit.MINUTES)
//            .build(new CacheLoader<Long, CustomSessionEntity>() {
//
//                public CustomSessionEntity load(Long key) throws Exception {
//
//                    SdWxaCustomSession session = INSTANCE.sessionService.getByCustomerCId(key);
//                    if (session == null) {
//                        return null;
//                    }
//
//                    CustomSessionEntity sessionEntity = new CustomSessionEntity();
//                    sessionEntity.appId = session.getAppId();
//                    sessionEntity.customerCId = session.getCustomerCId();
//                    sessionEntity.fromUser = session.getFromUser();
//                    sessionEntity.id = session.getId();
//                    sessionEntity.storeId = session.getStoreId();
//                    return sessionEntity;
//                }
//            });


}
