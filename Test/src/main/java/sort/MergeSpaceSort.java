package sort;


/**
 * @author 张文辉
 */
public class MergeSpaceSort {
    /**
     * 归并排序 : 二路归并排序,利用临时空间
     * <p>
     * <p>
     * 和选择排序一样，归并排序的性能不受输入数据的影响，但表现比选择排序好的多，因为始终都是O(n log n）的时间复杂度。代价是需要额外的内存空间。
     * 归并排序是建立在归并操作上的一种有效的排序算法。该算法是采用分治法（Divide and Conquer）的一个非常典型的应用。归并排序是一种稳定的排序方法。
     * 将已有序的子序列合并，得到完全有序的序列；即先使每个子序列有序，再使子序列段间有序。若将两个有序表合并成一个有序表，称为2-路归并。
     * 5.1 算法描述
     * 把长度为n的输入序列分成两个长度为n/2的子序列；
     * 对这两个子序列分别采用归并排序；
     * 将两个排序好的子序列合并成一个最终的排序序列。
     * <p>
     * 最佳情况：T(n) = O(n)  最差情况：T(n) = O(nlogn)  平均情况：T(n) = O(nlogn)
     *
     * @param a
     */
    public static void twoMerge(int[] a, int width, int[] tempArray) {

        int low1Index = 0, up1Index;
        int low2Index, up2Index;
        int tempIndex = 0;
        while (low1Index + width < a.length) {
            up1Index = low1Index + width - 1;
            low2Index = up1Index + 1;
            up2Index = (low2Index + width - 1) <= (a.length - 1) ? (low2Index
                    + width - 1) : (a.length - 1);
            int i, j;
            for (i = low1Index, j = low2Index; (i <= up1Index && j <= up2Index); tempIndex++) {
                if (a[i] <= a[j]) {
                    tempArray[tempIndex] = a[i];
                    i++;
                } else {
                    tempArray[tempIndex] = a[j];
                    j++;
                }
            }
            while (i <= up1Index) {
                tempArray[tempIndex++] = a[i++];
            }
            while (j <= up2Index) {
                tempArray[tempIndex++] = a[j++];
            }

            low1Index = up2Index + 1;
        }

        while (low1Index < a.length && tempIndex < tempArray.length) {
            tempArray[tempIndex++] = a[low1Index++];
        }
    }

    static public int[] sort(int[] a) {
        System.out.println("归并排序后：");
        int[] tempArray = new int[a.length];
        for (int i = 1; i < a.length; i *= 2) {
            twoMerge(a, i, tempArray);
            for (int m = 0; m < tempArray.length; m++) {
                System.out.print(tempArray[m] + "    ");
            }
            // a = Arrays.copyOf(tempArray, tempArray.length);
            for (int j = 0; j < a.length; j++) {
                a[j] = tempArray[j];
            }
            System.out.println();
        }
        return a;
    }

    static public void main(String[] args) {
        int[] a = {45, 23, 24, 13, 12, 35, 23, 45, 68, 10, 9, 40, 1};
        // a = MergeSpaceSort.sort(a);
        MergeSpaceSort.sort(a);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "    ");
        }
    }
}
