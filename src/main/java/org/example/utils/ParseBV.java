package org.example.utils;

public class ParseBV {
    /**
     * 从 B 站视频链接中提取 BV 号
     * @param url 视频链接
     * @return 提取到的 BV 号
     * @throws IllegalArgumentException 如果链接格式不正确
     */
    public static String extractBVIdFromUrl(String url) {
        // 使用正则表达式匹配 BV 号
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(BV[0-9A-Za-z]{10})");
        java.util.regex.Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("无效的 B 站视频链接: " + url);
        }
    }
}
