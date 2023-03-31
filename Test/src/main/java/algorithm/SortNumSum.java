package algorithm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 题目1.1【用连续自然数之和来表达整数】分值100 【用连续自然数之和来表达整数】一个整数可以由连续的自然数之和来表示。给定一个整数，计算该整数有几种连续自然数之和的表达式，且打印出每种表达式。
 * 输入描述:
 * 一个目标整数T (1 <=T<= 1000)
 * 输出描述:
 * 示例1:
 * 该整数的所有表达式和表达式的个数。如果有多种表达式，输出要求为: 1. 自然数个数最少的表达式优先输出
 * 2.每个表达式中按自然数递增的顺序输出，具体的格式参见样例。在每个测试数据结束时，输出一行”Result:X”，其中X是最终的表达式个数。
 * 输入
 * 9
 * 输出
 * 9=9 9=4+5 9=2+3+4
 * Result:3
 */
public class SortNumSum {

    @Test
    public void test() {
        for (int i = 1; i < 50; i++) {
            SortNumSum.main(null);
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入数字：");
        int sum = scanner.nextInt();
        int count = 1;
        System.out.println("输出：");
        //num = 1时：
        System.out.println(sum + "=" + sum);
        for (int num = 2; num < sum; num++) {
            //偶数: sum = num/2 * (2k + 1) = num * k + num/2
            if (num % 2 == 0) {
                if (handleDouble(sum, num)) {
                    count++;
                }
            } else {
                //奇数:sum = num * k
                if (handleTuple(sum, num)) {
                    count++;
                }
            }
        }
        System.out.println("Result:" + count);
    }

    private static boolean handleTuple(int sum, int num) {
        //因为k>=2, 则 sum >= 2 * num
        if ((sum % num == 0) && (sum >= 2 * num)) {
            int k = sum / num;
            //第一个数需要大于0
            if (num < 2 * k) {
                List<Integer> kList = new ArrayList<>();
                for (int i = 1; i <= (num / 2); i++) {
                    kList.add(k - ((num / 2 + 1 - i) * 1));
                }
                kList.add(k);
                for (int i = (num / 2 + 2); i <= num; i++) {
                    kList.add(k + ((i - ((num / 2) + 1)) * 1));
                }
                StringBuilder sb = new StringBuilder();
                kList.forEach(o -> sb.append(o).append("+"));
                System.out.println(sum + "=" + sb.deleteCharAt(sb.length() - 1));
                return true;
            }
        }
        return false;
    }

    private static boolean handleDouble(int sum, int num) {
        //因为sum = num * k + num/2 , k >=1, 能推出：3 * num <= 2* sum
        if (3 * num <= (sum * 2)) {
            if ((sum - (num / 2)) % num == 0) {
                int k = (sum - (num / 2)) / num;
                //这里k所在数 能推出第一个数为：k - (num/2 - 1) * 1 > 0 (第一个数大于0)
                if (num < (2 * k + 2)) {
                    List<Integer> kList = new ArrayList<>();
                    for (int i = 1; i < num / 2; i++) {
                        kList.add(k - ((num / 2 - i) * 1));
                    }
                    kList.add(k);
                    for (int i = (num / 2 + 1); i <= num; i++) {
                        kList.add(k + ((i - (num / 2)) * 1));
                    }
                    StringBuilder sb = new StringBuilder();
                    kList.forEach(o -> sb.append(o).append("+"));
                    System.out.println(sum + "=" + sb.deleteCharAt(sb.length() - 1));
                    return true;
                }
            }
        }
        return false;
    }
}
