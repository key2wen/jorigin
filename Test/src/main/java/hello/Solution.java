package hello;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * 输入为一个长度为20亿的int数组和k（k通常较小），请设计一个函数，输出这个输入数组中前k大的元素。
 */
public class Solution {
    public static int[] rank(Integer[] input, int k) {
        if (k <= 0) {
            return null;
        }
        TreeSet<Integer> treeSet = new TreeSet(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 < o2 ? 1 : -1;
            }
        });
        List<Integer> inp = (List<Integer>) Arrays.asList(input);
        treeSet.addAll(inp);
        int[] result = new int[k];
        Iterator<Integer> iterator = treeSet.iterator();
        int i = 0;
        while (i < k && iterator.hasNext()) {
            int res = iterator.next();
            result[i] = res;
            i++;
        }
        return result;
    }

    public static void main(String[] args) {
        Integer[] input = {1, 3, 4, 2, 6, 5};
        int[] result = Solution.rank(input, 3);
        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i]);
        }
    }
}