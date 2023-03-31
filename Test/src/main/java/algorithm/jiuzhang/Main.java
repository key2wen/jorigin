package algorithm.jiuzhang;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * 1. 写一个算法查找两个字符串中公共最大的字符串。（要求：低时间复杂度）
 * 如： abcdefabcd abefabghi 找出: efab。
 * <p>
 * <p>
 * 2. 查找一个字符串所有具有镜像(aba abba abcba abccba)性质的字符串，（要求：低时间复杂度）
 * 如: Hello woworlrow.
 * 输出：
 * 1. ll
 * 2. wow
 * 3. orlro
 * 简单起见，已经被消费的就不再参与查找。
 * <p>
 * <p>
 * 3. 简单实现一个拓扑排序，用于规划任务执行的先后顺序。
 * 如任务A依赖于任务B记成 A -> B
 * 实现： A->C B->D C->E B->C D->E F->A F->B
 * 最终算出执行顺序为 E C A D B F
 * <p>
 * <p>
 * 4. 安全位点计算：初始安全位点为 -1
 * a. 我们从kafka中拉取数据，每个数据有一个long型offset号(位点，递增）
 * b. 多线程消费，每个数据消费耗时不一定（随机时间）。
 * c. 安全位点计算，假设共有20个数据。
 * 如果已经完成 0 2 3 4 5 9 10 15，则安全位点为 0，因为 1 没有消费完。
 * 如果再完成 1，则安全位点是 5（0-5）均完成。
 * 再完成7 8 11 12 13 14 ... 19 安全位点还是5，因为6还没有完成。
 * 最后完成 6 安全位点则跳到 19，说明19之前（包括19）的所有数据已经安全消费完成，不需要再次消费。
 * <p>
 * public class SafeOffsetRegistry<E> {
 * private long safeOffset = -1;
 * <p>
 * // 递增的登录位点
 * public void register(long offset, E e) {...}
 * <p>
 * // 标记完成位点数据的消费
 * public void complete(long offset) {...safeOffset=...}
 * <p>
 * // 获取安全位点
 * public long getSafeOffset() {return safeOffset;}
 * }
 * <p>
 * <p>
 * 5. 实现一个简单的布隆过滤器，例如IP防火墙需要排除白名单IP之外的所有IP请求。
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("题1. 写一个算法查找两个字符串中公共最大的字符串。（要求：低时间复杂度)");
        subString();

        System.out.println();
        System.out.println("题2. 查找一个字符串所有镜像(aba abba abcba abccba)性质的字符串");
        huiWen();

        System.out.println();
        System.out.println("题3. 简单实现一个拓扑排序，用于规划任务执行的先后顺序。");
        sortedProcessor();

        System.out.println();
        System.out.println();
        System.out.println("题 4. 安全位点计算：初始安全位点为 -1");
        safeOffset();

        System.out.println();
        System.out.println("题5. 实现一个简单的布隆过滤器，例如IP防火墙需要排除白名单IP之外的所有IP请求。");
        bloomFilter();
    }

    private static void safeOffset() {
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

    private static void sortedProcessor() {
        Task taskA = new Task(Node.A);
        Task taskB = new Task(Node.B);
        Task taskC = new Task(Node.C);
        Task taskD = new Task(Node.D);
        Task taskE = new Task(Node.E);
        Task taskF = new Task(Node.F);
        taskA.addPreTask(taskC);
        taskB.addPreTask(taskD, taskC);
        taskC.addPreTask(taskE);
        taskD.addPreTask(taskE);
        taskF.addPreTask(taskA, taskB);
        System.out.println("Task Relation is : A->C B->D C->E B->C D->E F->A F->B");
        Stack<Task> stackTasks = new Stack<>();
        stackTasks.add(taskF); //F作为其实起始任务
        MultiProcessor.sortedRunTasks(stackTasks);
    }

    private static void subString() {
        String s1 = "abcdefabcd";
        String s2 = "abefabghi";
        System.out.println(String.format("String1 is : %s, String2 is : %s", s1, s2));
        System.out.println("MaxCommonSubString is : " + CommonSubStrUtil.getMaxSubStr(s1, s2));
    }

    private static void bloomFilter() {
        BloomFilter filter = new BloomFilter();
        List<String> initIps = Arrays.asList("127.0.0.1", "127.0.0.2", "127.0.0.3", "127.0.0.4");
        for (String ip : initIps) {
            filter.add(ip);
        }
        System.out.println("Init WhiteList IP Collection is : [127.0.0.1, 127.0.0.2, 127.0.0.3, 127.0.0.4]");
        System.out.println("Check -> 127.0.0.1  in WhiteList IP is : " + filter.check("127.0.0.1"));
        System.out.println("Check -> 127.0.0.5  in WhiteList IP is : " + filter.check("127.0.0.5"));
    }

    private static void huiWen() {
        String origin = "Hello woworlrow";
        System.out.println("Origin String is :" + origin);
        List<String> huiWenList = HuiWenUtil.getAllSub(origin);
        System.out.println("All HuiWen String is :");
        for (int i = huiWenList.size() - 1; i >= 0; i--) {
            System.out.println(huiWenList.get(i));
        }
    }
}