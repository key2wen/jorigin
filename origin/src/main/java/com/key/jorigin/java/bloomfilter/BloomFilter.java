package com.key.jorigin.java.bloomfilter;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//https://www.cnblogs.com/zhxshseu/p/5289871.html

/**
 * 1. Bloom-Filter算法简介
 * Bloom-Filter，即布隆过滤器，1970年由Bloom中提出。它可以用于检索一个元素是否在一个集合中。
 * Bloom Filter（BF）是一种空间效率很高的随机数据结构，它利用位数组很简洁地表示一个集合，并能判断一个元素是否属于这个集合。它是一个判断元素是否存在集合的快速的概率算法。Bloom Filter有可能会出现错误判断，但不会漏掉判断。也就是Bloom Filter判断元素不再集合，那肯定不在。如果判断元素存在集合中，有一定的概率判断错误。因此，Bloom Filter”不适合那些“零错误的应用场合。而在能容忍低错误率的应用场合下，Bloom Filter比其他常见的算法（如hash，折半查找）极大节省了空间。
 * <p>
 * 它的优点是空间效率和查询时间都远远超过一般的算法，缺点是有一定的误识别率和删除困难。
 * <p>
 * Bloom Filter的详细介绍：Bloom Filter
 * <p>
 * 2、 Bloom-Filter的基本思想       Bloom-Filter算法的核心思想就是利用多个不同的Hash函数来解决“冲突”。
 * 计算某元素x是否在一个集合中，首先能想到的方法就是将所有的已知元素保存起来构成一个集合R，然后用元素x跟这些R中的元素一一比较来判断是否存在于集合R中；我们可以采用链表等数据结构来实现。但是，随着集合R中元素的增加，其占用的内存将越来越大。试想，如果有几千万个不同网页需要下载，所需的内存将足以占用掉整个进程的内存地址空间。即使用MD5，UUID这些方法将URL转成固定的短小的字符串，内存占用也是相当巨大的。
 * <p>
 * 于是，我们会想到用Hash table的数据结构，运用一个足够好的Hash函数将一个URL映射到二进制位数组（位图数组）中的某一位。如果该位已经被置为1，那么表示该URL已经存在。
 * <p>
 * Hash存在一个冲突（碰撞）的问题，用同一个Hash得到的两个URL的值有可能相同。为了减少冲突，我们可以多引入几个Hash，如果通过其中的一个Hash值我们得出某元素不在集合中，那么该元素肯定不在集合中。只有在所有的Hash函数告诉我们该元素在集合中时，才能确定该元素存在于集合中。这便是Bloom-Filter的基本思想。
 * 原理要点：一是位数组， 而是k个独立hash函数。
 * <p>
 * 1）位数组：
 * <p>
 * 假设Bloom Filter使用一个m比特的数组来保存信息，初始状态时，Bloom Filter是一个包含m位的位数组，每一位都置为0，即BF整个数组的元素都设置为0。
 * 2）添加元素，k个独立hash函数
 * <p>
 * 为了表达S={x1, x2,…,xn}这样一个n个元素的集合，Bloom Filter使用k个相互独立的哈希函数（Hash Function），它们分别将集合中的每个元素映射到{1,…,m}的范围中。
 * <p>
 * 当我们往Bloom Filter中增加任意一个元素x时候，我们使用k个哈希函数得到k个哈希值，然后将数组中对应的比特位设置为1。即第i个哈希函数映射的位置hashi(x)就会被置为1（1≤i≤k）。
 * <p>
 * 注意，如果一个位置多次被置为1，那么只有第一次会起作用，后面几次将没有任何效果。在下图中，k=3，且有两个哈希函数选中同一个位置（从左边数第五位，即第二个“1“处）。
 * 3）判断元素是否存在集合
 * <p>
 * 在判断y是否属于这个集合时，我们只需要对y使用k个哈希函数得到k个哈希值，如果所有hashi(y)的位置都是1（1≤i≤k），即k个位置都被设置为1了，那么我们就认为y是集合中的元素，否则就认为y不是集合中的元素。下图中y1就不是集合中的元素（因为y1有一处指向了“0”位）。y2或者属于这个集合，或者刚好是一个false positive。
 * Bloom Filter的缺点：
 * <p>
 * 1）Bloom Filter无法从Bloom Filter集合中删除一个元素。因为该元素对应的位会牵动到其他的元素。所以一个简单的改进就是 counting Bloom filter，用一个counter数组代替位数组，就可以支持删除了。 此外，Bloom Filter的hash函数选择会影响算法的效果。
 * <p>
 * 2）还有一个比较重要的问题，如何根据输入元素个数n，确定位数组m的大小及hash函数个数，即hash函数选择会影响算法的效果。当hash函数个数k=(ln2)*(m/n)时错误率最小。在错误率不大于E的情况 下，m至少要等于n*lg(1/E) 才能表示任意n个元素的集合。但m还应该更大些，因为还要保证bit数组里至少一半为0，则m应 该>=nlg(1/E)*lge ，大概就是nlg(1/E)1.44倍(lg表示以2为底的对数)。
 * <p>
 * 举个例子我们假设错误率为0.01，则此时m应大概是n的13倍。这样k大概是8个。
 * <p>
 * 注意：
 * <p>
 * 这里m与n的单位不同，m是bit为单位，而n则是以元素个数为单位(准确的说是不同元素的个数)。通常单个元素的长度都是有很多bit的。所以使用bloom filter内存上通常都是节省的。
 * <p>
 * 一般BF可以与一些key-value的数据库一起使用，来加快查询。由于BF所用的空间非常小，所有BF可以常驻内存。这样子的话，对于大部分不存在的元素，我们只需要访问内存中的BF就可以判断出来了，只有一小部分，我们需要访问在硬盘上的key-value数据库。从而大大地提高了效率。
 */
public class BloomFilter {
    public static final int NUM_SLOTS = 1024 * 1024 * 8;
    public static final int NUM_HASH = 8;

    //java中可以被称为Number的有byte，short，int，long，float，double和char，我们在使用这些
    /**
     * 一、BigInteger介绍
     * 如果在操作的时候一个整型数据已经超过了整数的最大类型长度 long 的话，则此数据就无法装入，所以，此时要使用 BigInteger 类进行操作。这些大数都会以字符串的形式传入。
     * <p>
     * BigInteger 相比 Integer 的确可以用 big 来形容。它是用于科学计算，Integer 只能容纳一个 int，所以，最大值也就是 2 的 31 次访减去 1，十进制为 2147483647。但是，如果需要计算更大的数，31 位显然是不够用的，那么，此时 BigInteger 就能满足我们的需求了。
     * <p>
     * BigInteger 能够容纳的位数那可就大了，我简单试了一下，上千位没有任何问题。除了容量大之外，BigInteger 还封装了一些常见的操作，比如 ±
     * 的基本操作，还有绝对值，相反数，最大公约数，是否是质数等等的运算。
     */
    private BigInteger bitmap = new BigInteger("0"); //值为0,也就是是说每一位初始值都是0，

    public static void main(String[] args) {
        //测试代码
        BloomFilter bf = new BloomFilter();
        ArrayList<String> contents = new ArrayList<>();
        contents.add("sldkjelsjf");
        contents.add("ggl;ker;gekr");
        contents.add("wieoneomfwe");
        contents.add("sldkjelsvrnlkjf");
        contents.add("ksldkflefwefwefe");

        for (int i = 0; i < contents.size(); i++) {
            bf.addElement(contents.get(i));
        }
        System.out.println(bf.check("sldkjelsvrnlkjf"));
        System.out.println(bf.check("sldkjelsvrnkjf"));
    }

    private int getHash(String message, int n) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            message = message + String.valueOf(n);
            byte[] bytes = message.getBytes();
            md5.update(bytes);
            BigInteger bi = new BigInteger(md5.digest());

            return Math.abs(bi.intValue()) % NUM_SLOTS;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BloomFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public void addElement(String message) {
        for (int i = 0; i < NUM_HASH; i++) {
            int hashcode = getHash(message, i);
            if (!bitmap.testBit(hashcode)) {
                bitmap = bitmap.or(new BigInteger("1").shiftLeft(hashcode));
            }
        }

    }

    public boolean check(String message) {
        for (int i = 0; i < NUM_HASH; i++) {
            int hashcode = getHash(message, i);
            if (!this.bitmap.testBit(hashcode)) {
                return false;
            }
        }
        return true;
    }
}