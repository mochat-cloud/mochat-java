package com.mochat.mochat.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaojinjian
 * @ClassName PinyinUtil.java
 * @Description 拼音字母操作帮助类
 * @createTime 2020/12/16 14:28
 */
public  class PinyinUtil {
    /**
     * 将汉字转换为全拼
     *
     * @param text 文本
     * @param separator 分隔符
     * @return {@link String}
     */
    public static String getPinyin(String text, String separator) {
        char[] chars = text.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        // 设置大小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 设置声调表示方法
        format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        // 设置字母u表示方法
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String[] s;
        String rs = StringUtils.EMPTY;
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                // 判断是否为汉字字符
                if (String.valueOf(chars[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    s = PinyinHelper.toHanyuPinyinStringArray(chars[i], format);
                    if (s != null) {
                        sb.append(s[0]).append(separator);
                        continue;
                    }
                }
                sb.append(String.valueOf(chars[i]));
                if ((i + 1 >= chars.length) || String.valueOf(chars[i + 1]).matches("[\\u4E00-\\u9FA5]+")) {
                    sb.append(separator);
                }
            }
            rs = sb.substring(0, sb.length() - 1);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return rs;
    }
}
