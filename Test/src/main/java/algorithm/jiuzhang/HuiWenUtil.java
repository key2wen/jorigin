package algorithm.jiuzhang;

import java.util.ArrayList;
import java.util.List;

/**
 * 题2. 查找一个字符串所有具有镜像(aba abba abcba abccba)性质的字符串，（要求：低时间复杂度）
 * 如: Hello woworlrow.
 * 输出：
 * 1. ll
 * 2. wow
 * 3. orlro
 * 简单起见，已经被消费的就不再参与查找。
 */
public class HuiWenUtil {
    /**
     * 思路：用动态规划：如果dp[i][j]是回文，则dp[i+1][j-1]也是回文
     *
     * @param origin
     * @return
     */
    public static List<String> getAllSub(String origin) {
        int len = origin.length();
        String[][] dp = new String[len][len];
        List<String> result = new ArrayList<>();

        for (int row = len - 1; row >= 0; row--) {
            for (int col = row; col < len; col++) {
                char left = origin.charAt(row);
                char right = origin.charAt(col);
                if (left == right) {
                    if (col == row) {
                        dp[row][col] = left + "";
                    } else if (col - row == 1) {
                        //当时相邻的两个字符
                        dp[row][col] = left + "" + right;
                        if (row == 0 || col == len - 1) {
                            result.add(dp[row][col]);
                            dp[row][col] = null;
                        }
                    } else if (dp[row + 1][col - 1] != null) {
                        // 不是相邻字符情况
                        dp[row][col] = left + dp[row + 1][col - 1] + right;
                        if (row == 0 || col == len - 1) {
                            result.add(dp[row][col]);
                            dp[row][col] = null;
                        }
                    }
                } else if (dp[row + 1][col - 1] != null && row != col - 2) {
                    result.add(dp[row + 1][col - 1]);
                    dp[row + 1][col - 1] = null;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String str = "Hello woworlrow";
        List<String> huiWenList = getAllSub(str);
        for (int i = huiWenList.size() - 1; i >= 0; i--) {
            System.out.println(huiWenList.get(i));
        }
    }

}
