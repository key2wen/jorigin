package algorithm.migu;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NumsSumCode {

    public static void main1(String[] args) {
        List<Integer> numList = Arrays.asList(1, 2, 3, 4);
        AtomicInteger sum = new AtomicInteger();
        numList.stream().forEach(n -> sum.addAndGet(n));
        System.out.println(sum.get());
    }

    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 2, 0, -1, -2, -3);
        System.out.println(getFiveMulResult(nums));
    }

    private static int getFiveMulResult(List<Integer> nums) {

        List<Integer> positiveNums = nums.stream().filter(n -> n >= 0).sorted(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2 ? -1 : 1;
            }
        }).collect(Collectors.toList());

        List<Integer> negativeNums = nums.stream().filter(n -> n < 0).sorted().collect(Collectors.toList());
        int mulNumberSize = 5;

        if (positiveNums.size() <= 0) {
            //全是负数情况
            return getMulResult(negativeNums, mulNumberSize);
        }
        if (negativeNums.size() == 0) {
            //全是正数
            return getMulResult(positiveNums, mulNumberSize);
        }
        int max = Integer.MIN_VALUE;
        for (int positive = 1; positive <= 5; positive++) {
            int leftMaxTemp = getMulResult(positiveNums, positive);
            int rightMaxTemp = getMulResult(negativeNums, mulNumberSize - positive);
            int temp = leftMaxTemp * rightMaxTemp;
            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }

    private static int getMulResult(List<Integer> sortedNegNums, int mulNumberSize) {
        int res = 1;
        for (int i = 0; i < sortedNegNums.size(); i++) {
            if (i == mulNumberSize) {
                break;
            }
            res = res * sortedNegNums.get(i);
        }
        return res;
    }

}

class Cat {

    List<Mouse> mouses = new LinkedList<>();

    public void say() {
        System.out.println("cat say...");
        for (Mouse mouse : mouses) {
            mouse.runAway();
        }
    }

    public void registerEvent(Mouse mouse) {
        mouses.add(mouse);
    }

    public void unRegister(Mouse mouse) {
        mouses.remove(mouse);
    }
}

class Mouse {
    public void runAway() {
        System.out.println("Mouse run away...");
    }
}
