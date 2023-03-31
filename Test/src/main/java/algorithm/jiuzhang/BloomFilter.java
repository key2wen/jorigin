package algorithm.jiuzhang;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * 题5. 实现一个简单的布隆过滤器，例如IP防火墙需要排除白名单IP之外的所有IP请求。
 */
public class BloomFilter {
    public static final int SIZE = 1024 * 1024 * 8; //总容量
    public static final int HASH_NUM = 4; //hash函数的个数
    private static BitSet filterBitSet = new BitSet(SIZE);

    public void add(String ip) {
        for (int i = 0; i < HASH_NUM; i++) {
            int hashcode = getHash(ip, i);
            filterBitSet.set(hashcode, true);
        }
    }

    private int getHash(String ip, int number) {
        return Math.abs((ip + number).hashCode() % SIZE);
    }

    public boolean check(String ip) {
        for (int i = 0; i < HASH_NUM; i++) {
            int hashcode = getHash(ip, i);
            if (filterBitSet.get(hashcode) == false) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        BloomFilter filter = new BloomFilter();
        List<String> initIps = Arrays.asList("127.0.0.1", "127.0.0.2", "127.0.0.3", "127.0.0.4");
        for (String ip : initIps) {
            filter.add(ip);
        }
        System.out.println("Init WhiteList IP Collection is : [127.0.0.1, 127.0.0.2, 127.0.0.3, 127.0.0.4]");
        System.out.println("Check -> 127.0.0.1  in WhiteList IP is : " + filter.check("127.0.0.1"));
        System.out.println("Check -> 127.0.0.5  in WhiteList IP is : " + filter.check("127.0.0.5"));
    }
}