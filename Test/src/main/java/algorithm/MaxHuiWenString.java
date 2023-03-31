package algorithm;

/**
 * 5. 最长回文子串
 * 给你一个字符串 s，找到 s 中最长的回文子串。
 * <p>
 * 如果字符串的反序与原始字符串相同，则该字符串称为回文字符串。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：s = "babad"
 * 输出："bab"
 * 解释："aba" 同样是符合题意的答案。
 * 示例 2：
 * <p>
 * 输入：s = "cbbd"
 * 输出："bb"
 * 提示：
 * <p>
 * 1 <= s.length <= 1000
 * s 仅由数字和英文字母组成
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode.cn/problems/longest-palindromic-substring
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class MaxHuiWenString {

    public static String longestPalindrome(String origin) {
        int length = origin.length();
        if (length == 1) {
            return origin;
        }
        if (length == 2) {
            if (origin.charAt(0) == origin.charAt(1)) {
                return origin;
            } else {
                return origin.substring(0, 1);
            }
        }
        String reverse = new StringBuilder(origin).reverse().toString();
        int maxLen = 0; //最大回文长度
        int step = 1;
        String common = null;
        for (int i = 0; i < origin.length(); i++) {
            String sub;
            for (int j = i + step; j <= origin.length(); j++) {
                sub = origin.substring(i, j);
                int idx = reverse.indexOf(sub);
                if ((idx >= 0) && (maxLen < sub.length()) && (idx + j == length)) {
                    maxLen = sub.length();
                    common = sub;
                }
            }
            step = maxLen + 1;
        }
        return common;
    }

    public static void main(String[] args) {
        String origin = "a";
        String common = longestPalindrome(origin);
        System.out.println(common);
        origin = "aaa";
        common = longestPalindrome(origin);
        System.out.println(common);
        origin = "aaabxgobaaa";
        common = longestPalindrome(origin);
        System.out.println(common);
    }

}
