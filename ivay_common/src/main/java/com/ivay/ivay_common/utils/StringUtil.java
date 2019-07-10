package com.ivay.ivay_common.utils;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * 字符串转化工具类
 *
 * @author xx
 */
public class StringUtil {

    /**
     * 字符串转为驼峰
     *
     * @param str
     * @return
     */
    public static String str2hump(String str) {
        StringBuffer buffer = new StringBuffer();
        if (str != null && str.length() > 0) {
            if (str.contains("_")) {
                String[] chars = str.split("_");
                int size = chars.length;
                if (size > 0) {
                    List<String> list = Lists.newArrayList();
                    for (String s : chars) {
                        if (s != null && s.trim().length() > 0) {
                            list.add(s);
                        }
                    }

                    size = list.size();
                    if (size > 0) {
                        buffer.append(list.get(0));
                        for (int i = 1; i < size; i++) {
                            String s = list.get(i);
                            buffer.append(s.substring(0, 1).toUpperCase());
                            if (s.length() > 1) {
                                buffer.append(s.substring(1));
                            }
                        }
                    }
                }
            } else {
                buffer.append(str);
            }
        }

        return buffer.toString();
    }

    /**
     * 不够位数的在前面补0，保留num的长度位数字
     *
     * @param code
     * @return
     */
    public static String autoGenericCode(String code, int num) {
        String result = "";
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        result = String.format("%0" + num + "d", Integer.parseInt(code));

        return result;
    }

    /**
     * 判断字符串是否是数字
     *
     * @param str
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static final char[] BCD_LOOKUP =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 将字节数组转换为16进制字符串的形式.
     */
    public static final String bytesToHexStr(byte[] bcd) {
        StringBuffer s = new StringBuffer(bcd.length * 2);

        for (int i = 0; i < bcd.length; i++) {
            s.append(BCD_LOOKUP[(bcd[i] >>> 4) & 0x0f]);
            s.append(BCD_LOOKUP[bcd[i] & 0x0f]);
        }

        return s.toString();
    }

    /**
     * 将16进制字符串还原为字节数组.
     */
    public static final byte[] hexStrToBytes(String s) {

        byte[] bytes;

        bytes = new byte[s.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

    public static String utfToString(byte[] data) {

        String str = null;

        try {
            str = new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return str;

    }

    /**
     * 判断字符串中是否含有特殊字符
     *
     * @return
     */
    public static boolean hasSpecialChar(String str) {
        String specialCharEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(specialCharEx);
        Matcher m = p.matcher(str);
        return m.find(); // true表示含有特殊字符
    }

    // 只含数字或字母
    private static final String CHARACTER_REGEX = "\\W{1,}|_{1,}";

    // 只含数字/字母/下划线
    private static final String PASSWORD_REGEX = "^[0-9a-zA-Z_]{1,}$";

    public static Boolean isCharacter(String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        Pattern p = Pattern.compile(CHARACTER_REGEX);
        // 获取 matcher 对象
        Matcher m = p.matcher(input);
        return !m.find();
    }

    /**
     * 利用{1}, {2}方式格式化字符串
     *
     * @param str
     * @param attributes
     * @return
     */
    public static String replaceStringInParentheses(String str, Map<String, Object> attributes) {
        if (!StringUtils.isEmpty(str)) {
            int start = str.indexOf("{");
            int end = str.indexOf("}");
            while (start > -1 && end > start) {
                String s = str.substring(start, end + 1);
                str = str.replace(s, attributes.get(s.substring(1, s.length() - 1)).toString());
                start = str.indexOf("{", end - 1);
                end = str.indexOf("}", start + 1);
            }
        }
        return str;
    }

    /**
     * 密码只能设置数字/字母和下划线
     */
    public static boolean valiPassword(String input) {
        boolean flag = false;
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        Pattern p = Pattern.compile(PASSWORD_REGEX);
        Matcher m = p.matcher(input);
        flag = m.find();
        return flag;
    }

    private static final Pattern PATTERN_ACCENTED_CHARACTERS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * 越南语转英文大写字母
     *
     * @param vi
     * @return
     */
    public static String vietnameseToUpperEnglish(String vi) {
        if (vi == null) {
            return null;
        }
        vi = vi.trim().toUpperCase();
        if (StringUtils.isEmpty(vi)) {
            return "";
        }
        // 去掉重音符号
        String nfdNormalizedString = Normalizer.normalize(vi, Normalizer.Form.NFD);
        return PATTERN_ACCENTED_CHARACTERS.matcher(nfdNormalizedString).replaceAll("").replaceAll("Đ", "D")
            .replaceAll("đ", "d");
    }

    /**
     * 越南语转英文字母
     *
     * @param vi
     * @return
     */
    public static String vietnameseToEnglish(String vi) {
        if (vi == null) {
            return null;
        }
        if (StringUtils.isEmpty(vi)) {
            return "";
        }
        // 去掉重音符号
        String nfdNormalizedString = Normalizer.normalize(vi, Normalizer.Form.NFD);
        return PATTERN_ACCENTED_CHARACTERS.matcher(nfdNormalizedString).replaceAll("").replaceAll("Đ", "D")
            .replaceAll("đ", "d");
    }

    private static final Pattern PATTERN_BLANK_CHARACTERS = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * 去除所有的空白字符
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = null;
        if (str == null) {
            return dest;
        } else {
            Matcher m = PATTERN_BLANK_CHARACTERS.matcher(str);
            dest = m.replaceAll("");
            return dest;
        }
    }

    /**
     * 电话号码脱敏
     *
     * @param phone
     * @return
     */
    public static String phoneDesensitization(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return "";
        }
        return phone.replaceAll("(\\d{3})(.+)(\\d{4})", "$1***$3");
    }

    public static void main(String[] args) {
        System.out.println(
            vietnameseToEnglish("[OVAY] mã xác thực: {0}, mã xác thực có hiệu lực trong 10 phút,, không được để lộ"));
    }

}
