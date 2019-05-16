package sort;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author 张文辉
 * <p>
 *
 */
public class RadixSort {
    /**
     * 基数排序
     *
     * @param a
     * @param count 最大数值的位数 比如 123，则count为3
     * @param scale 进制数(十进制、二进制等)
     *              <p>
     *              <p>
     *              基数排序也是非比较的排序算法，对每一位进行排序，从最低位开始排序，复杂度为O(kn),为数组长度，k为数组中的数的最大的位数；
     *              基数排序是按照低位先排序，然后收集；再按照高位排序，然后再收集；依次类推，直到最高位。有时候有些属性是有优先级顺序的，先按低优先级排序，再按高优先级排序。最后的次序就是高优先级高的在前，高优先级相同的低优先级高的在前。基数排序基于分别排序，分别收集，所以是稳定的。
     *              10.1 算法描述
     *              取得数组中的最大数，并取得位数；
     *              arr为原始数组，从最低位开始取每个位组成radix数组；
     *              对radix进行计数排序（利用计数排序适用于小范围数的特点）；
     *              <p>
     *              <p>
     *              10.4 算法分析
     *              最佳情况：T(n) = O(n * k)   最差情况：T(n) = O(n * k)   平均情况：T(n) = O(n * k)
     *              基数排序有两种方法：
     *              MSD 从高位开始进行排序 LSD 从低位开始进行排序
     *              基数排序 vs 计数排序 vs 桶排序
     *              这三种排序算法都利用了桶的概念，但对桶的使用方法上有明显差异：
     *              基数排序：根据键值的每位数字来分配桶
     *              计数排序：每个桶只存储单一键值
     *              桶排序：每个桶存储一定范围的数值
     */
    public static void radixSort(int[] a, int count, int scale) {
        Queue[] qList = new Queue[scale];
        for (int i = 1; i <= count; i++) {
            for (int j = 0; j < a.length; j++) {
                int value = (a[j] / ((int) Math.pow(scale, i - 1)))
                        - (scale * (a[j] / (int) Math.pow(scale, i)));
                if (qList[value] == null) {
                    qList[value] = new LinkedBlockingQueue<Integer>();
                }
                qList[value].add(a[j]);
            }
            int tempIndex = 0;
            for (int q = 0; q < qList.length; q++) {
                if (qList[q] == null)
                    continue;
                while (!qList[q].isEmpty()) {
                    a[tempIndex] = (Integer) qList[q].poll();
                    tempIndex++;
                }
            }
        }
        for (Queue q : qList) {
            q = null;
        }
        qList = null;
    }

    static public void main(String[] args) {
        int[] a = {45, 1024, 24, 13, 12, 35, 23, 45, 68, 10, 9, 404, 132, 10};
        // a = MergeSpaceSort.sort(a);
        RadixSort.radixSort(a, 4, 10);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "    ");
        }
    }
}