package sort;

/**
 * @author 张文辉
 */
public class InsertDirect {
    /**
     * 直接插入排序： 先插入后移动
     *
     * @param a
     */
    public static void sort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int temp = 0, index = 0;
            for (int j = 0; j < i; j++) {
                if (a[i] < a[j]) {
                    temp = a[j];
                    a[j] = a[i];
                    index = j + 1;
                    break;
                }
            }
            if (index > 0) {
                for (int m = i; m > index; m--) {
                    a[m] = a[m - 1];
                }
                a[index] = temp;
            }
        }
    }

    /**
     * 直接插入排序 先移动后插入
     *
     * @param a
     */
    public static void sort2(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int j = i;
            int temp = a[i + 1];
            while (j >= 0 && temp < a[j]) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = temp;
        }
    }

    public static void main(String[] args) {
        int[] a = {45, 23, 24, 13, 12, 35, 23, 45, 68, 10, 9, 40, 1};
        InsertDirect.sort(a);
        System.out.println("直接插入排序后：");
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "    ");
        }
        InsertDirect.sort2(a);
        System.out.println("直接插入排序后：");
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "    ");
        }
    }
}