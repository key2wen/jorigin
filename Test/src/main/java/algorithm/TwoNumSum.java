package algorithm;

import java.util.HashMap;

/**
 * NC61 两数之和：https://www.nowcoder.com/practice/20ef0972485e41019e39543e8e895b7f?tpId=117&tqId=37756&rp=1&ru=/exam/oj&qru=/exam/oj&sourceUrl=%2Fexam%2Foj%3Fpage%3D1%26tab%3D%25E7%25AE%2597%25E6%25B3%2595%25E7%25AF%2587%26topicId%3D117&difficulty=undefined&judgeStatus=undefined&tags=&title=
 * <p>
 * 描述
 * 给出一个整型数组 numbers 和一个目标值 target，请在数组中找出两个加起来等于目标值的数的下标，返回的下标按升序排列。
 * （注：返回的数组下标从1开始算起，保证target一定可以由数组里面2个数字相加得到）
 * <p>
 * 数据范围：2\leq len(numbers) \leq 10^52≤len(numbers)≤10
 * 5
 * ，-10 \leq numbers_i \leq 10^9−10≤numbers
 * i
 * ​
 * ≤10
 * 9
 * ，0 \leq target \leq 10^90≤target≤10
 * 9
 * <p>
 * 要求：空间复杂度 O(n)O(n)，时间复杂度 O(nlogn)O(nlogn)
 * <p>
 * 思路:
 * 从题中给出的有效信息：
 * <p>
 * 找出下标对应的值相加为target
 * 数组中存在唯一解
 * 故此 可以使用 直接遍历 或者 hash表 来解答
 */
public class TwoNumSum {
    /**
     * @param numbers int整型一维数组
     * @param target  int整型
     * @return int整型一维数组
     * * <p>
     * * 方法一：直接遍历
     * * 具体做法：
     * * 循环遍历数组的每一个数，如果遍历的两数之和等于target，则返回两个数的下标
     * 复杂度分析：
     * 时间复杂度：O(n^2) 遍历两次数组
     * 空间复杂度：O(1) 未申请额外空间
     */
    public static int[] twoSum(int[] numbers, int target) {
        // write code here
        int n = numbers.length;
        int[] res = {-1, -1};
        //遍历数组
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                //判断相加值是否为target
                if (numbers[i] + numbers[j] == target) {
                    res[0] = i + 1;
                    res[1] = j + 1;
                    //返回值
                    return res;
                }
            }
        }
        return res;
    }

    /**
     * @param numbers int整型一维数组
     * @param target  int整型
     * @return int整型一维数组
     * 方法二 hash表
     * 具体做法：
     * 使用Map来降低时间复杂度，遍历数组，如果没有 （target - 当前值） 就将当前数字存入哈希表，如果有，返回该数字下标即可。
     * <p>
     * 哈希表可以优化第二遍循环中对元素的检索速度，
     * 复杂度分析：
     * 时间复杂度：O(n) 一次遍历hash索引查找时间复杂度为O(1)
     * 空间复杂度：O(n) 申请了n大小的map空间
     */
    public static int[] twoSum2(int[] numbers, int target) {
        // write code here
        HashMap<Integer, Integer> map = new HashMap<>();
        //遍历数组
        for (int i = 0; i < numbers.length; i++) {
            //将不包含target - numbers[i]，装入map中，包含的话直接返回下标
            if (map.containsKey(target - numbers[i]))
                return new int[]{map.get(target - numbers[i]) + 1, i + 1};
            else
                map.put(numbers[i], i);
        }
        throw new IllegalArgumentException("No solution");
    }

    public static void main(String[] args) {
        int[] array = {2, 5, 1, 5, 3};
        invoke1(array);
        System.out.println();
        invoke2(array);
    }

    private static void invoke1(int[] array) {
        int[] res = TwoNumSum.twoSum(array, 5);
        for (int a : res) {
            System.out.print(a + ", ");
        }
    }

    private static void invoke2(int[] array) {
        int[] res = TwoNumSum.twoSum2(array, 5);
        for (int a : res) {
            System.out.print(a + ", ");
        }
    }
}