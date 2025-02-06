package com.hubis.acs.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_DATE = "yyyy-MM-dd";
    public static String FORMAT_TIME = "HH:mm:ss";
    public static String FORMAT_MILITIME = "HH:mm:ss.SSS";

    public static String NONFORMAT_FULL = "yyyyMMddHHmmssSS";
    public static String NONFORMAT_NORMAL = "yyyyMMddHHmmss";
    public static String NONFORMAT_DATE = "yyyyMMdd";
    public static String NONFORMAT_DATE_HOUR = "yyyyMMddHH";
    public static String NONFORMAT_MONTH = "yyyyMM";
    public static String NONFORMAT_TIME = "HHmmss";
    public static String NONFORMAT_MILITIME = "HHmmss.SSSSSS";

    public static Date getCurrentTime() {
        return new Date();
    }

    public static Date getCurrentTimeByTimekey(String timekey) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(NONFORMAT_FULL);
            return dateFormat.parse(timekey.substring(0, 17));
        } catch (Exception e) {
        }

        return null;
    }

    public static String getCurrentTimeString() {
        return getDateTimeFormat(getCurrentTime(), FORMAT_FULL);
    }

    public static String getCurrentTimekeySECS() {
        return getDateTimeFormat(getCurrentTime(), NONFORMAT_FULL).substring(0, 16);
    }

    public static String getCurrentTimekey() {
        return getDateTimeFormat(getCurrentTime(), NONFORMAT_FULL);
    }

    public static String getCurrentTimeFormat(String pattern) {
        return getDateTimeFormat(getCurrentTime(), pattern);
    }

    public static String getTimeString(Date date) {
        if (date == null)
            return null;

        return getDateTimeFormat(date, FORMAT_FULL);
    }

    public static String getTimekey(Date date) {
        if (date == null)
            return null;

        return getDateTimeFormat(date, NONFORMAT_FULL);
    }

    public static String getTimeFormat(Date date, String pattern) {
        if (date == null)
            return null;

        return getDateTimeFormat(date, pattern);
    }

    public static String getDateTimeFormat(Date date, String pattern) {
        String res = "";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            res = dateFormat.format(date);
        } catch (Exception e) {
        }

        return res;
    }


    public static double getTimeGap(Date createTime) {
        double res = 0;

        long reqDateTime = createTime.getTime();

        Date curDate = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(NONFORMAT_NORMAL);
            curDate = dateFormat.parse(dateFormat.format(curDate));
        } catch (ParseException e) {
        }

        long curDateTime = curDate.getTime();

        res = (curDateTime - reqDateTime) / 1000.0;

        return res;
    }
}
