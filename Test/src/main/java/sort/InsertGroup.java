package sort;

/**
 * @author 张文辉 希尔排序，分组插入排序
 * 
 */
public class InsertGroup {

    /**
     * 希尔排序，即分组使用直接排序
     * 
     * @param a
     *            待排序数组
     * @param d
     *            分组的增量数组,最后的增量值必须为1
     */
    public static void sort2(int[] a, int[] d) {
        for (int increment = 0; increment < d.length; increment++) {
            for (int k = 0; k < d[increment]; k++) {
                for (int i = k; i < a.length - d[increment]; i += d[increment]) {
                    int j = i;
                    int temp = a[i + d[increment]];
                    while (j >= 0 && temp < a[j]) {
                        a[j + d[increment]] = a[j];
                        j -= d[increment];
                    }
                    a[j + d[increment]] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] a = { 45, 23, 24, 13, 12, 35, 23, 45, 68, 10, 9, 40, 1 };
        int[] d = { 6, 3, 1 };
        InsertGroup.sort2(a, d);
        System.out.println("希尔排序后：");
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "    ");
        }
    }
}
