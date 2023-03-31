package sort;

/**
 * @author 张文辉
 */
public class ChoiceHeapSort {
    /**
     * 堆排序，选择排序，每次都选择堆顶元素
     *
     * 堆排序（Heapsort）是指利用堆这种数据结构所设计的一种排序算法。
     * 堆积是一个近似完全二叉树的结构，并同时满足堆积的性质：即子结点的键值或索引总是小于（或者大于）它的父节点。
     *
     * 堆：实际上是一颗 完全二叉树 ，但是它还满足父结点大于（或小于）子结点特性。
     * 父结点大于子结点称为最大堆（或大顶堆，array[i]>=array[2i+1] && array[i]>=array[2i+2]，i从0开始），
     * 父结点小于子结点称为最小堆（或小顶堆，array[i]<=array[2i+1] && array[i]<=array[2i+2] ，i从0开始）
     *
     * 7.1 算法描述
     将初始待排序关键字序列(R1,R2….Rn)构建成大顶堆，此堆为初始的无序区；
     将堆顶元素R[1]与最后一个元素R[n]交换，此时得到新的无序区(R1,R2,……Rn-1)和新的有序区(Rn),
     且满足R[1,2…n-1]<=R[n]；
     由于交换后新的堆顶R[1]可能违反堆的性质，因此需要对当前无序区(R1,R2,……Rn-1)调整为新堆，
     然后再次将R[1]与无序区最后一个元素交换，得到新的无序区(R1,R2….Rn-2)和新的有序区(Rn-1,Rn)。
     不断重复此过程直到有序区的元素个数为n-1，则整个排序过程完成。

     最佳情况：T(n) = O(nlogn) 最差情况：T(n) = O(nlogn) 平均情况：T(n) = O(nlogn)
     *
     * @param a
     */

    /**
     * @param a
     * @param n       需要创建的堆大小，元数个数
     * @param notLeaf 非叶子节点
     */
    public static void createMaxHeap(int[] a, int n, int notLeaf) {
        int superIndex = notLeaf; // 非叶子结点
        int leafData = a[superIndex];

        int leftLeafIndex = superIndex * 2 + 1; // 左子结点

        int flag = 0; // 如果没有移动过，则不需要对数据进行调整

        while (flag == 0 && leftLeafIndex < n) {
            if ((leftLeafIndex < n - 1)
                    && (a[leftLeafIndex] < a[leftLeafIndex + 1])) {
                leftLeafIndex++; // 左子结点小于右子节点，则和右子结点比较
            }
            if (a[leftLeafIndex] < leafData) {
                flag = 1; // 非叶子结点更大，则不需要移动
            } else {
                a[superIndex] = a[leftLeafIndex]; //数据大的往上移动
                superIndex = leftLeafIndex; //下一个操作的是子节点中较大的节点
                leftLeafIndex = superIndex * 2 + 1;
            }
        }
        //最后把最早的非叶子节点（父节点的数据放入到子节点中去），superIndex在循环中已经被设置成了子节点index
        a[superIndex] = leafData;
    }

    public static void initCreateMaxHeap(int[] a) {
        /**
         * 循环所有的非叶子节点 对应的 数组index
         * 处理完所有的非叶子节点，则最大堆就构成了
         */
        for (int i = (a.length - 1) / 2 - 1; i >= 0; i--) {
            createMaxHeap(a, a.length, i);
        }
    }

    public static void sort(int[] a) {
        System.out.println("堆排序后：");
        initCreateMaxHeap(a);
        for (int currLength = a.length - 1; currLength > 0; currLength--) {
            int temp = a[currLength];
            a[currLength] = a[0];
            a[0] = temp;
            createMaxHeap(a, currLength, 0);
        }
    }

    public static void main(String[] args) {
        int[] a = {45, 23, 24, 13, 12, 35, 23, 45, 68, 10, 9, 40, 1};
        ChoiceHeapSort.sort(a);
        // ChoiceHeapSort.initCreateMaxHeap(a);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "    ");
        }
    }
}