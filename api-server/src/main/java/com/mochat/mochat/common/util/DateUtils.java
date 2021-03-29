package com.mochat.mochat.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat();

    public static final long MILLIS_DAY = 1000 * 60 * 60 * 24;

    /**
     * 2018-09-18 20:20:30
     */
    private static final String S1 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 2018-09-18 20:20:30
     */
    private static final String S2 = "yyyy-MM-dd+HH:mm:ss";

    /**
     * 2018-09-18
     */
    private static final String S3 = "yyyy-MM-dd";

    /**
     * 2018-09-18
     */
    private static final String S4 = "yyyy-MM";

    private static final String S5 = "HH:mm";

    private static final String S6 = "E";

    private static final String S7 = "yyyy/MM/dd/HHmmss";

    public static String format(String pattern, long mills) {
        FORMAT.applyPattern(pattern);
        return FORMAT.format(mills);
    }

    public static String format(String pattern, String mills) {
        long m = Long.parseLong(mills);
        return format(pattern, m);
    }

    public static String formatS1(String mills) {
        return format(S1, mills);
    }

    public static String formatS1(long mills) {
        return format(S1, mills);
    }

    public static String formatS3(long mills) {
        return format(S3, mills);
    }

    public static String formatS4(long mills) {
        return format(S4, mills);
    }

    public static String formatS5(long mills) {
        return format(S5, mills);
    }

    public static String formatS7(long mills) {
        return format(S7, mills);
    }

    public static long getMills(String pattern, String date) {
        try {
            FORMAT.applyPattern(pattern);
            return FORMAT.parse(date).getTime();
        } catch (Exception e) {

        }
        return 0;
    }

    public static long getMillsByS1(String date) {
        return getMills(S1, date);
    }

    public static long getMillsByS3(String date) {
        return getMills(S3, date);
    }

    public static long getMillsByS5(String date) {
        return getMills(S5, date);
    }

    public static String getDateOfDayStartByS3(String date) {
        date += " 00:00:00";
        return date;
    }

    public static String getDateOfDayEndByS3(String date) {
        date += " 23:59:59";
        return date;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param sDate 2021-02-24 00:00:00
     * @param eDate 2021-02-24 23:59:59
     * @return 相差天数
     */
    public static int daysBetween(String sDate, String eDate) {
        long startMillis = getMillsByS1(sDate);
        long endMillis = getMillsByS1(eDate);
        return (int) ((endMillis - startMillis) / MILLIS_DAY) + 1;
    }

    public static boolean inDateByS3(String sDate, String eDate) {
        return inDateByS3(sDate, eDate, getDateByS3());
    }

    public static boolean inDateByS3(String sDate, String eDate, String cDate) {
        long startMillis = getMillsByS3(sDate);
        long endMillis = getMillsByS3(eDate);
        long currentMillis = getMillsByS1(cDate);
        return currentMillis >= startMillis && currentMillis <= endMillis;
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 5:26 下午
     * @description 获取当天格式化后的字符串
     *
     * @return 2021-03-17
     */
    public static String getDateByS3() {
        return formatS3(System.currentTimeMillis());
    }

    /**
     * @author: yangpengwei
     * @time: 2021/3/17 5:26 下午
     * @description 获取当天格式化后的字符串
     *
     * @return 17:27
     */
    public static String getDateByS5() {
        return formatS5(System.currentTimeMillis());
    }

    public static boolean inTimeByS5(String sTime, String eTime) {
        return inDateByS3(sTime, eTime, getDateByS5());
    }

    public static boolean inTimeByS5(String sTime, String eTime, String cTime) {
        long startMillis = getMillsByS5(sTime);
        long endMillis = getMillsByS5(eTime);
        long currentMillis = getMillsByS5(cTime);
        return currentMillis >= startMillis && currentMillis <= endMillis;
    }

    public static int getDayOfWeek() {
        return getDayOfWeek(System.currentTimeMillis());
    }

    public static int getDayOfWeek(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static void main(String... args) {
        long millis = getMillsByS3("2021-03-06");

        System.out.println(format(S6, millis));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
    }

}
