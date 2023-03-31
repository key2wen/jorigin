package sort;

/**
 * @author 张文辉
 *
 * 代码思想参考 《数据结构》
 * https://blog.csdn.net/wen523686945/article/details/38663081?spm=1001.2014.3001.5502
 * https://blog.csdn.net/hellozhxy/article/details/79911867 算法效果
 *
 */
public class ChangeBubbleSort {
    /**
     *  交换排序 ： 冒泡排序， 稳定的排序
     *  空间复杂度：o(1), 不暂用额外内存
     *  时间复杂度：平均 t(n^2), 最好 t(n), 最坏 t(n^2)
     * @param data
     */
    public static void sort(int[] data) {
        boolean flag = true;

        for(int i = 0; i < data.length && flag; i++){
            flag = false;

            for(int j = 0; j < data.length - 1 - i; j++){

                if(data[j] > data[j+1]){
                    int temp = data[j+1];
                    data[j+1] = data[j];
                    data[j] = temp;
                    flag = true;
                }
            }
        }
        System.out.println("冒泡排序后：");
    }

    public static void main(String[] args) {
        int[] a = { 45, 23, 24, 13, 12, 35, 23, 45, 68, 10, 9, 40, 1 };
        ChangeBubbleSort.sort(a);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "    ");
        }
    }
}