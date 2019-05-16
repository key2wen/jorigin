package sort;

/**
 * @author 张文辉
 */
public class ChangeQuickSort {
    /**
     * 交换排序 ： 快速排序 不稳定
     *时间复杂度：
     * 最佳情况：T(n) = O(nlogn)
     * 最差情况：T(n) = O(n2)
     * 平均情况：T(n) = O(nlogn)　
     *空间复杂度：
     * O(logn)　
     *
     * <p>
     * 快速排序的基本思想：通过一趟排序将待排记录分隔成独立的两部分，
     * 其中一部分记录的关键字均比另一部分的关键字小，则可分别对这两部分记录继续进行排序，以达到整个序列有序。
     * <p>
     * 6.1 算法描述
     * 快速排序使用分治法来把一个串（list）分为两个子串（sub-lists）。具体算法描述如下：
     * 从数列中挑出一个元素，称为 “基准”（pivot）；
     * 重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。
     * 在这个分区退出之后，该基准就处于数列的中间位置。这个称为分区（partition）操作；
     * 递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序。
     *
     * @param data
     */
    public static void sort(int[] data, int low, int high) {

        int level = data[low];
        int low0 = low, high0 = high;

        while (low0 < high0) {

            while (low0 < high0 && (data[high0] >= level)) {
                high0--;
            }
            if (low0 < high0) {
                data[low0] = data[high0];
                low0++;
            }

            while (low0 < high0 && (data[low0] < level)) {
                low0++;
            }
            if (low0 < high0) {
                data[high0] = data[low0];
                high0--;
            }
        }

        data[low0] = level;

        if (low < low0)
            sort(data, low, low0 - 1);
        if (low0 < high)
            sort(data, high0 + 1, high);

    }

    public static void main(String[] args) {
        int[] a = {45, 23, 24, 13, 12, 35, 23, 45, 68, 10, 9, 40, 1};
        System.out.println("快速排序后：");
        ChangeQuickSort.sort(a, 0, a.length - 1);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "    ");
        }
    }
}