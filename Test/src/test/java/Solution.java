import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Solution {

    public static String[] split(String a) {
        Pattern p = Pattern.compile("([a-zA-Z]+)(\\d+)");
        String[] arr = new String[2];
        Matcher m = p.matcher(a);
        if (m.find()) {
            arr[0] = m.group(1);
            arr[1] = m.group(2);
        } else {
            throw new IllegalArgumentException("±ØÐëÊÇ×ÖÄ¸+Êý×Ö×éºÏ");
        }
        return arr;
    }

    private Map<String, List<String>> map = new ConcurrentHashMap<>();

    public static void insertEle(List<String> arr, String ele) {
        synchronized (arr) {
            if (arr.size() == 0) {
                arr.add(ele);
                return;
            }
            for (int i = 0; i < arr.size(); i++) {
                Integer da = Integer.parseInt(split(arr.get(i))[1]);
                Integer de = Integer.parseInt(split(ele)[1]);
                if (de < da) {
                    arr.add(i, ele);
                    return;
                }
            }
            arr.add(ele);
        }
    }

    private int tasks;

    private ExecutorService exec = Executors.newFixedThreadPool(3);
    private CountDownLatch countDownLatch;

    private Solution(int tasks) {
        this.tasks = tasks;
        countDownLatch = new CountDownLatch(tasks);
    }

    public void groupAndSort(String[] array) {
        exec.execute(() -> {
            for (String ele : array) {
                String[] splits = split(ele);
                String alphabets = splits[0];
//            String digits = splits[1];
                map.putIfAbsent(alphabets, new ArrayList<>());
                insertEle(map.get(alphabets), ele);
            }
            countDownLatch.countDown();
        });
    }

    public List<String[]> getResult() {
        try {
            countDownLatch.await();
        } catch (Exception e) {

        }
        List<String> sortKeys = new ArrayList<>(map.keySet());
        Collections.sort(sortKeys);
        List<String[]> result = new ArrayList<>();

        for (String key : sortKeys) {
            List<String> arr = map.get(key);
            String[] r = new String[arr.size()];
            for (int i = 0; i < r.length; i++) {
                r[i] = arr.get(i);
            }
            result.add(r);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
//        String[] arr = split("AA123");
//        System.out.println(arr[0] + " " + arr[1]);
//        List<String> arr = new ArrayList<>();
//        insertEle(arr, "AA123");
//        insertEle(arr, "AA23");
//        insertEle(arr, "AA01");
//        System.out.println(arr);
        Solution s = new Solution(3);
        String[][] arrs = {{"A1", "A2", "C2"}, {"B1", "C1", "A3"}, {"B2", "C2", "B3"}};
        for (int i = 0; i < arrs.length; i++) {
            s.groupAndSort(arrs[i]);
        }

        for (String[] arr : s.getResult()) {
            for (String e : arr) {
                System.out.print(e + " ");
            }
            System.out.println();
        }
    }
}
