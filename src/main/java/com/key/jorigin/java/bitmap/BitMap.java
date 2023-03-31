package com.key.jorigin.java.bitmap;

import java.util.BitSet;

/**
 * Bitmap/Bitset:  http://www.cnblogs.com/binyue/p/5224987.html
 * <p>
 * 比如有一堆数字，需要存储，source=[3,5,6,9] 用int就需要4*4个字节。java.util.BitSet可以存true/false。
 * 如果用java.util.BitSet，则会少很多，其原理是：
 * 1，先找出数据中最大值maxvalue=9
 * 2，声明一个BitSet bs,它的size是maxvalue+1=10
 * 3，遍历数据source，bs[source[i]]设置成true.
 * 最后的值是：
 * (0为false;1为true)
 * bs [0,0,0,1,0,1,1,0,0,1]
 * 3, 5,6, 9
 * 这样一个本来要int型需要占4字节共32位的数字现在只用了1位，这样就省下了很大空间。
 */
public class BitMap {

    static BitSet bitSet = new BitSet(0);

    public static void main(String[] args) {
        bitSet.set(1);
    }

}
