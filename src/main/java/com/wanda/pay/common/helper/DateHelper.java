package com.wanda.pay.common.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description 时间处理工具类
 * @Author liangrun
 * @Create 2016-12-23 10:58
 * @Version 1.0
 */
public class DateHelper {
    /**
     * 日期格式
     */
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    /**
     * 时间格式
     */
    public static final String FORMAT_TIME = "HH:mm:ss";
    /**
     * 时分格式
     */
    public static final String FORMAT_HOUR_MINUTE = "HH:mm";
    /**
     * 日期时间格式
     */
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /**
     * 完整日期时间格式
     */
    public static final String FORMAT_DATETIME_FULL = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 解析日期
     *
     * @param dateStr
     * @return
     */
    public static Date parseDate(String dateStr) {
        return parse(dateStr, FORMAT_DATE);
    }

    /**
     * 解析日期时间
     *
     * @param dateStr
     * @return
     */
    public static Date parseDateTime(String dateStr) {
        return parse(dateStr, FORMAT_DATETIME);
    }

    /**
     * 解析
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date parse(String dateStr, String format) {
        DateFormat df = new SimpleDateFormat(format);
        try {
            Date date = df.parse(dateStr);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return format(date, FORMAT_DATE);
    }

    /**
     * 格式化日期时间
     *
     * @param dateTime
     * @return
     */
    public static String formatDateTime(Date dateTime) {
        return format(dateTime, FORMAT_DATETIME);
    }

    /**
     * 格式化
     *
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 格式化今天日期
     *
     * @return
     */
    public static String formatToday() {
        return formatDate(new Date());
    }

    /**
     * 格式化当前时间
     *
     * @return
     */
    public static String formatNow() {
        return formatDateTime(new Date());
    }

    /**
     * 获取延后天数, days为负数则表示提前
     *
     * @param date
     * @param days
     * @return
     */
    public static Date getAfter(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 获取某时间的0点时间
     *
     * @param date
     * @return
     */
    public static Date getFirst(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取某时间的当天最后时间
     * @param date
     * @return
     */
    public static Date getLast(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
        cal.add(Calendar.MILLISECOND, -1);
        return cal.getTime();
    }

    /**
     * 获取今天0点时间
     *
     * @return
     */
    public static Date getTodayFirst() {
        return getFirst(new Date());
    }

    /**
     * 获取今天最后时间
     *
     * @return
     */
    public static Date getTodayLast() {
        return getLast(new Date());
    }
}
