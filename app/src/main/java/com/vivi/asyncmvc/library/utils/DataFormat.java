package com.vivi.asyncmvc.library.utils;

import android.studio.util.DateUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * 数据格式化方法集合
 */
public class DataFormat {

    private static final long ONE_MINUTE = 60 * 1000;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final long ONE_WEEK = 7 * ONE_DAY;

    /**
     * 时间显示规则
     * 1、不超过1分钟的表示为：刚刚
     * 2、超过1分钟不超过1个小时的标示为：多少分钟前
     * 3、超过1个小时不超过1天的标示为：多少小时前
     * 4、超过一天不超过7天的标示为：多少天前
     * 5、超过7天的显示为年月日，如：2019-01-25
     */
    public static String formatDate(long ts) {
        long delta = System.currentTimeMillis() - ts;
        if (delta < 1 * ONE_MINUTE) {
            return "刚刚";
        }
        if (delta < ONE_HOUR) {
            return delta / ONE_MINUTE + "分钟前";
        }
        if (delta < ONE_DAY) {
            return delta / ONE_HOUR + "小时前";
        }
        if (delta < ONE_WEEK) {
            return delta / ONE_DAY + "天前";
        }
        return DateUtils.formatDate(new Date(ts));
    }

    /**
     * 将时间戳格式化为format格式
     *
     * @param timestamp
     * @param format
     * @return
     */
    public static String formatDate(long timestamp, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            return df.format(new Date(timestamp));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化99+
     *
     * @param num
     * @return
     */
    public static String formatNum99(long num) {
        return num > 99 ? "99+" : String.valueOf(num);
    }

    /**
     * @param duration
     * @return 时分秒
     */
    public static String getDurationString(int duration) {
        if (duration == 0) {
            return "";
        } else if (duration < 60) {
            return duration + "秒";
        } else if (duration < 60 * 60) {
            return duration / 60 + "分钟";
        } else {
            String s = (duration / 60 / 60) + "小时";
            if (duration / 60 % 60 > 0) {
                s += duration / 60 % 60 + "分钟";
            }
            if (duration % 60 > 0) {
                s += duration % 60 + "秒";
            }
            return s;
        }
    }

    /**
     * 人民币分转元
     * 返回形如12 or 12.1 or 12.12
     *
     * @param cent
     * @return
     */
    public static String centToYuan(long cent) {
        return round(cent / 100f, 2).stripTrailingZeros().toPlainString();
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long sizeByte) {
        if (sizeByte < 1024 * 1024 * 1024) {
            return (round(sizeByte / 1024 / 1024f, 1)) + "MB";
        } else {
            return (round(sizeByte / 1024 / 1024 / 1024f, 1)) + "GB";
        }
    }

    /**
     * 四舍五入.
     *
     * @param number  原数
     * @param decimal 保留几位小数
     * @return 四舍五入后的值
     */
    public static BigDecimal round(double number, int decimal) {
        return new BigDecimal(number).setScale(decimal, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 将map<String, String>的value进行String返回，中间逗号分隔
     *
     * @param map
     * @return
     */
    public static String getStringFromMapValue(HashMap<String, String> map) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String key : map.keySet()) {
            stringBuffer.append(map.get(key));
            stringBuffer.append(",");
        }
        if (stringBuffer.length() > 0) {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }
}
