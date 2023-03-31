package algorithm.jiuzhang;

import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 题 4. 安全位点计算：初始安全位点为 -1
 * a. 我们从kafka中拉取数据，每个数据有一个long型offset号(位点，递增）
 * b. 多线程消费，每个数据消费耗时不一定（随机时间）。
 * c. 安全位点计算，假设共有20个数据。
 * 如果已经完成 0 2 3 4 5 9 10 15，则安全位点为 0，因为 1 没有消费完。
 * 如果再完成 1，则安全位点是 5（0-5）均完成。
 * 再完成7 8 11 12 13 14 ... 19 安全位点还是5，因为6还没有完成。
 * 最后完成 6 安全位点则跳到 19，说明19之前（包括19）的所有数据已经安全消费完成，不需要再次消费。
 *
 * @param <E>
 */
public class SafeOffsetRegistry<E> {
    private volatile long safeOffset = -1;
    private BitSet bitSet = new BitSet(Integer.MAX_VALUE);
    private Map<Long, E> consumerOffsetDataMap = new ConcurrentHashMap<>();

    // 递增的登录位点
    public void register(long offset, E e) {
        //将当前位点的数据暂存在Map,方便快速获取数据
        consumerOffsetDataMap.put(offset, e);
    }

    /**
     * 标记完成位点数据的消费
     * 思路：用一个bitset来存储消费位点到对应的bit位， 当 safeOffset 到当前 offset 位全为true,则表示中间已全部消费过了，
     * 那么新的safeOffset就是 当前offset.
     *
     * @param offset
     */
    public void complete(long offset) {
        //当前位点完成后，将对应位点在bitset中的位设为 1（true）
        bitSet.set((int) offset, true);
        for (int start = (int) safeOffset + 1; start <= offset; start++) {
            if (bitSet.get(start) == false) {
                return;
            }
            safeOffset = offset;
        }
    }

    // 获取安全位点
    public long getSafeOffset() {
        return safeOffset;
    }

    public static void main(String[] args) {
        SafeOffsetRegistry<Object> safeOffsetRegistry = new SafeOffsetRegistry<>();
        long[] consumeOffsetArray = new long[]{0, 2, 3, 4, 5, 9, 10, 15};
        StringBuilder consumeOffsetInfo = new StringBuilder();
        for (long offset : consumeOffsetArray) {
            safeOffsetRegistry.complete(offset);
            consumeOffsetInfo.append(offset).append(",");
        }
        System.out.println("Consume Offset have: " + consumeOffsetInfo);
        System.out.println("SafeOffset is : " + safeOffsetRegistry.getSafeOffset());
    }
}


