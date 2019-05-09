import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;
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
public class LinkedListTest {


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


    @Test
    public void main() {

        //按访问顺序排序：
        LinkedHashMap<String, Object> map = new LinkedHashMap(16,
                0.75f, true);

        map.put("1", 11);
        map.put("2", 21);
        map.put("3", 31);

        //访问后，会将结点放到tail队尾
        map.get("3");
        map.get("1");
        map.get("2");
        map.get("1");

        Collection collection = map.keySet();
        //按访问顺序排序输出：（从head队头开始遍历）
        //3  2  1
        collection.forEach(c ->
                System.out.print(c + "  ")
        );

        map.values();
    }

    @Test
    public void main2() {

        //按写入顺序排序（默认排序方式）
        LinkedHashMap<String, Object> map = new LinkedHashMap(16,
                0.75f, false);

        //写入后，会将结点放到tail队尾
        map.put("1", 11);
        map.put("2", 21);
        map.put("3", 31);

        //访问后，不影响排序
        map.get("3");
        map.get("1");
        map.get("2");
        map.get("1");

        Collection collection = map.keySet();
        //按写入顺序排序输出：（从head队头开始遍历）
        //1  2  3
        collection.forEach(c ->
                System.out.print(c + "  ")
        );

        map.values();
    }

}
