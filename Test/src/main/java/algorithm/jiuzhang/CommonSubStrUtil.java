package algorithm.jiuzhang;

/**
 * 题1. 写一个算法查找两个字符串中公共最大的字符串。（要求：低时间复杂度）
 * 如： abcdefabcd abefabghi 找出: efab。
 */
public class CommonSubStrUtil {

    /**
     * 题1. 写一个算法查找两个字符串中公共最大的字符串。（要求：低时间复杂度）
     * 如： abcdefabcd abefabghi 找出: efab。
     * <p>
     * 思路：
     * 1.将两个字符串转成数组ch1[len1],ch2[len2]，
     * 2.构造一个临时二维数组temp[len1+1][len2+1]，
     * 3.遍历ch1和ch2，当ch1[i]=ch2[j], 在对应的temp[i][j]中填充值为1，那么最大的子串就是最大的连续为1的对角线；
     * 4.为了方便计算对角线的长度，可以用temp[i+1][j+1] = temp[i][j] + 1,先计算好长度放在下一个对角线的位置，方便直接获取；依次比较取最大值即可
     * 时间复杂度 o(n^2)
     *
     * @param s1 第一个字符串
     * @param s2 第二个字符串
     * @return 最大公共子串
     */
    public static String getMaxSubStr(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return null;
        }
        char[] ch1 = s1.toCharArray();
        char[] ch2 = s2.toCharArray();
        int[][] temp = new int[ch1.length + 1][ch2.length + 1];
        int maxLength = 0;
        int startIndex = 0;
        for (int i = 0; i < ch1.length; i++) {
            for (int j = 0; j < ch2.length; j++) {
                if (ch1[i] == ch2[j]) {
                    temp[i + 1][j + 1] = temp[i][j] + 1;
                    if (temp[i + 1][j + 1] > maxLength) {
                        maxLength = temp[i + 1][j + 1];
                        startIndex = i - maxLength;
                    }
                }
            }
        }
        return s1.substring(startIndex + 1, startIndex + 1 + maxLength);
    }

    public static void main(String[] args) {
        String s1 = "abcdefabcd";
        String s2 = "abefabghi";
        System.out.println("MaxCommonSubString is : " + getMaxSubStr(s1, s2));
    }


}