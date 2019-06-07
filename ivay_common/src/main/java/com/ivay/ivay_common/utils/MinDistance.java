package com.ivay.ivay_common.utils;

public class MinDistance {


    /**
     * 忽略大小写比较
     *
     * @param word1
     * @param word2
     * @return
     */
    public static int minDistance(String word1, String word2) {
        word1 = StringUtil.replaceBlank(word1).toUpperCase();
        word2 = StringUtil.replaceBlank(word2).toUpperCase();
        //dp[i][j]表示源串A位置i到目标串B位置j处最低需要操作的次数
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 0; i < word1.length() + 1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j < word2.length() + 1; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i < word1.length() + 1; i++) {
            for (int j = 1; j < word2.length() + 1; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {  // 第i个字符是字符串下标为i-1第哪个
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = (Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1])) + 1;
                }
            }
        }
        return dp[word1.length()][word2.length()];
    }
}