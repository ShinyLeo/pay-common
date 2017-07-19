package com.wanda.pay.common.helper;

import java.util.Date;
import java.util.Random;

/**
 * Created by robin on 16/12/22.
 */
public class StringHelper {
    /**
     * 生成时间戳 + 固定位数的字符串
     *
     * @param appendLength 固定位数,正整数
     * @return
     */
    public static String randomTimestampStr(int appendLength) {
        return randomTimestampStr(DateHelper.format(new Date(), "yyyyMMddHHmmssSSS"), appendLength);
    }

    /**
     * 生成时间戳 + 固定位数的字符串
     * @param timestamp
     * @param appendLength 固定位数
     * @return
     */
    public static String randomTimestampStr(long timestamp, int appendLength) {
        return randomTimestampStr(timestamp + "", appendLength);
    }

    public static String randomTimestampStr(String timestamp, int appendLength) {
        if (appendLength < 1) {
            throw new IllegalArgumentException("appendLength must be positive integer.");
        }
        return String.format("%s%0" + appendLength + "d", timestamp, new Random().nextInt(NumberHelper.pow(10, appendLength)));
    }
}
