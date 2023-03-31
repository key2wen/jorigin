package hello;

import java.util.*;

public class Test {

    /**
     * int[] nums,  int target
     * 1,2,3,5,7       3   -》 1，2，3， ｜ 1，5
     * 不能重复
     * 2，2，2，2，2     6
     */
    public List<List<Integer>> sumResult(int[] numbers, int target) {
        Arrays.sort(numbers);
        Map<Integer, Integer> numberMap = new HashMap<>();
        List<List<Integer>> result = new ArrayList<>();
        for (int n : numbers) {
            Integer count = numberMap.get(n);
            if (count == null) {
                count = 0;
            }
            numberMap.put(n, ++count);
        }
        int sum = 0;
        for (int n : numbers) {
            List<Integer> singleResult = new ArrayList<>();
            if (n > target) {
                break;
            }
            sum = sum + n;
            int remain = sum - n;
            Integer count = numberMap.get(remain);
            if (count != null) {
                singleResult.addAll(Arrays.asList(n, remain));
                continue;
            }
        }

        return null;
    }

    public static void main(String[] args) {

    }

}
