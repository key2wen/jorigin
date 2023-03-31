package algorithm;

//import javafx.util.Pair;
//

import java.util.Scanner;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 最大子串: 如 2, 9, 3, 1, -1, -5, -6, 8, ..，最大子串为：2+9+3+1=15
 * 以第n个节点结尾的最大子串未：Max[n]
 * 1. Max[n] = data[n] + （Max[n-1] > 0 ? Max[n-1] : 0）
 * 2. Max[0] = data[0]
 */
public class MaxSumValSubList {

    public static Pair<Integer, Integer> maxSubListValue(int[] data) {

        int maxSubValue[] = new int[data.length];
        //Max[0]
        maxSubValue[0] = data[0];

        int maxN = 0;
        int value = maxSubValue[0];

        for (int i = 1; i < data.length; i++) {
            //Max[i]:
            maxSubValue[i] = data[i] + (maxSubValue[i - 1] > 0 ? maxSubValue[i - 1] : 0);
            if (maxSubValue[i] > value) {
                value = maxSubValue[i];
                maxN = i;
            }
        }

        for (int i = 0; i < maxSubValue.length; i++) {
            System.out.print(maxSubValue[i] + " ");
        }
        System.out.println();

//        return new Pair<>(maxN, value);
        return Pair.of(maxN, value);
    }

    public static Pair<Integer, Integer> maxSubListValue2(int[] data) {

        //节省内存
//        int maxSubValue[] = new int[data.length];
        //Max[0]

        int maxN = 0;
        int value = data[0];

        for (int i = 1; i < data.length; i++) {
            //Max[i]:
            data[i] = data[i] + (data[i - 1] > 0 ? data[i - 1] : 0);
            if (data[i] > value) {
                value = data[i];
                maxN = i;
            }
        }

        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.println();

//        return new Pair<>(maxN, value);
        return Pair.of(maxN, value);
    }

    //把空间也去掉了
    public static Pair<Integer, Integer> maxSubListValue3() {

        //节省内存
//        int maxSubValue[] = new int[data.length];
        //Max[0]
        Scanner scanner = new Scanner(System.in);
        int length = 5;

        System.out.println("输入第一个元素：");
        int value = scanner.nextInt();
        int maxN = 0;
        int preIndexVal = value;

        for (int i = 1; i < length; i++) {

            System.out.println("输入第" + (i + 1) + "个元素：");
            int inputValue = scanner.nextInt();

            //Max[i]:
            preIndexVal = inputValue + (preIndexVal > 0 ? preIndexVal : 0);

            if (preIndexVal > value) {
                value = preIndexVal;
                maxN = i;
            }
        }

        System.out.println();

//        return new Pair<>(maxN, value);
        return Pair.of(maxN, value);
    }


    public static void main(String[] args) {
        int data[] = {1, 3, -1, 3, -2};

        System.out.println("method1：");
        Pair<Integer, Integer> pair = maxSubListValue(data);
        System.out.println(pair);

        System.out.println("method21：");
        Pair<Integer, Integer> pair2 = maxSubListValue2(data);
        System.out.println(pair2);

        System.out.println("method3：");
        Pair<Integer, Integer> pair3 = maxSubListValue3();
        System.out.println(pair3);
        /**
         * 运气结果：
         * method1：
         1 4 3 6 4
         3=6
         method2：
         1 4 3 6 4
         3=6
         method3：
         输入第一个元素：
         1
         输入第2个元素：
         3
         输入第3个元素：
         -1
         输入第4个元素：
         3
         输入第5个元素：
         -2

         3=6

         */
    }
}
