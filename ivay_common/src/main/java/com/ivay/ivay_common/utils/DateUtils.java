package com.ivay.ivay_common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    private final static String[] WEEK = {"", "周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private final static String[] WEEK_2 = {"", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    private final static String MM_DD = "MM-dd";
    private final static String YYYY_MM_DD = "yyyy-MM-dd";
    private final static String YYYYMM = "yyyyMM";
    private final static String YYYYMMDD2 = "yyMMdd";
    private final static String YYYYMMDD = "yyyyMMdd";
    private final static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    private final static String YYYYMMDDHHMMSSSS = "yyyyMMddHHmmssSS";
    private final static String YYYY_MM_DD_HH_MM2 = "yyyy/MM/dd HH:mm";
    private final static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    private final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private final static String YYYYMMDD_HH_MM_SS = "yyyyMMdd HH:mm:ss";

    // region -- 将字符串格式化成日期
    public static Date stringToDate(String dateString, String format) {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(dateString);
        } catch (Exception e) {
            date = null;
            logger.info("字符串格式化成日期出错: {}", e);
        }
        return date;
    }

    public static Date stringToDate_YYYY_MM_DD(String source) {
        return stringToDate(source, YYYY_MM_DD);
    }

    public static Date stringToDate_YYYY_MM_DD_HH_MM_SS(String dateString) {
        return stringToDate(dateString, YYYY_MM_DD_HH_MM_SS);
    }

    public static Date stringtoDate_YYYY_MM_DD_HH_MM2(String dateString) {
        return stringToDate(dateString, YYYY_MM_DD_HH_MM2);
    }

    public static Date stringToDate_YYYYMMDD_HH_MM_SS(String dateString) {
        return stringToDate(dateString, YYYYMMDD_HH_MM_SS);
    }

    public static Date stringToDate_YYYY_MM_DD_HH_MM(String dateString) {
        return stringToDate(dateString, YYYY_MM_DD_HH_MM);
    }

    public static Date stringToDate_YYYYMMDDHHMM(String dateString) {
        return stringToDate(dateString, YYYYMMDDHHMMSS);
    }

    public static Date stringToDate_YY_MM_DD_HH_MM(String dateString) {
        return stringToDate(dateString, YYYY_MM_DD_HH_MM);
    }
    // endregion end

    // region -- 日期格式化成字符串
    public static String dateToString(Date date, String format) {
        String dates = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            dates = sdf.format(date);
        } catch (Exception e) {
            dates = null;
            logger.info("日期格式化成字符串出错: {}", e);
        }
        return dates;
    }

    public static String dateToString_MM_DD(Date date) {
        return dateToString(date, MM_DD);
    }

    public static String dateToString_YYYY_MM_DD_HH_MM_SS(Date date) {
        return dateToString(date, YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateToString_YYYY_MM_DD(Date date) {
        return dateToString(date, YYYY_MM_DD);

    }

    public static String dateToString_YYYYMMDD(Date date) {
        return dateToString(date, YYYYMMDD);
    }
    // endregion

    // region -- 时间戳转换
    public static String timeMillisToString(long time, String format) {
        return dateToString(new Date(time), format);
    }

    public static String timeMillisToString_YYYY_MM_DD_HH_MM_SS(long timeMillis) {
        return timeMillisToString(timeMillis, YYYY_MM_DD_HH_MM_SS);
    }

    public static long stringToTimeMillis(String dateTime) {
        if (dateTime == null || "".equals(dateTime)) {
            return 0L;
        }
        Date date = stringToDate_YYYY_MM_DD_HH_MM_SS(dateTime);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }
    // endregion

    // region -- 当前时间
    public static String getNowDate(String format) {
        return dateToString(new Date(), format);
    }

    public static String getNowDateYYYYMMDD() {
        return dateToString(new Date(), YYYYMMDD);
    }

    public static String getNowDateYYMMDD() {
        return dateToString(new Date(), YYYYMMDD2);
    }

    public static String getNowDateYYYYMM() {
        return dateToString(new Date(), YYYYMM);
    }

    public static String getNowDateYYYY_MM_DD() {
        return dateToString_YYYY_MM_DD(new Date());
    }

    public static String getNowDateYYYY_MM_DD_HH_MM() {
        return dateToString(new Date(), YYYY_MM_DD_HH_MM);
    }

    public static String getNowDateYYYY_MM_DD_HH_MM_SS() {
        return dateToString_YYYY_MM_DD_HH_MM_SS(new Date());
    }

    public static String getNowDateYYYYMMDDHHMMSS() {
        return dateToString(new Date(), YYYYMMDDHHMMSS);
    }

    public static String getNowDateYYYYMMDDHHMMSSSS() {
        return dateToString(new Date(), YYYYMMDDHHMMSSSS);
    }
    // endregion

    public static Integer handleIntTime(Date date, String weekDayName) {
        for (int i = 0; i <= 3; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -i);
            int day = cal.get(Calendar.DAY_OF_WEEK);
            String tempWeek = WEEK[day];
            if (tempWeek.equals(weekDayName)) {
                return Integer.valueOf(dateToString(cal.getTime(), YYYYMMDD));
            }
        }
        return null;
    }

    public static String getWeekStr2(Date date, String[] week) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return week[cal.get(Calendar.DAY_OF_WEEK)];
    }

    public static String getWeekStr(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return WEEK[cal.get(Calendar.DAY_OF_WEEK)];
    }

    /**
     * 补充年份 排除1月份获取同年12月份时间&12月份获取同年1月份时间
     *
     * @param dateStr
     * @param date
     *            参考时间
     * @return
     */
    public static Date strToDateDafueYear(String dateStr, Date date) {
        Date nowDate = new Date();
        String nowYear = DateUtils.dateToString(nowDate, "yyyy");
        Date matchDate = stringToDate(nowYear + "-" + dateStr, "yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(matchDate);
        Calendar now = Calendar.getInstance();
        // 有参考值使用参考值的年份
        if (date != null) {
            now.setTime(date);
            calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
        } else {
            // 排除1月份获取同年12月份时间&12月份获取同年1月份时间
            if (calendar.getTimeInMillis() < now.getTimeInMillis() && calendar.get(Calendar.MONTH) == 0
                && now.get(Calendar.MONTH) == 11) {
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR) + 1);
            } else if (calendar.getTimeInMillis() > now.getTimeInMillis() && calendar.get(Calendar.MONTH) == 11
                && now.get(Calendar.MONTH) == 0) {
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
            }
        }
        return calendar.getTime();
    }

    public static Date strToDateDafueYear(String dateStr) {
        return strToDateDafueYear(dateStr, null);
    }

    public static Date getDateByOffsetDay(Date date, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, offset);
        return cal.getTime();
    }

    public static boolean isDateBefore(String date2, Date date1) {
        try {
            DateFormat df = DateFormat.getDateTimeInstance();
            return date1.before(df.parse(date2));
        } catch (ParseException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    /**
     * 比较两个日期的早晚关系
     *
     * @param day1
     * @param day2
     * @return 0 同一天 正数 快 负数 慢
     */
    public static int isDateAfter(Date day1, Date day2) {
        try {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(day1);
            cal2.setTime(day2);

            // 获取年份
            int y1 = cal1.get(Calendar.YEAR);
            int y2 = cal2.get(Calendar.YEAR);
            if (y1 != y2) {
                return y1 - y2;
            }

            // 获取年中第几天
            int d1 = cal1.get(Calendar.DAY_OF_YEAR);
            int d2 = cal2.get(Calendar.DAY_OF_YEAR);

            return d1 - d2;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public static boolean isIndexDCDateBefore(Date date) {
        try {
            Calendar c = Calendar.getInstance();
            GregorianCalendar ca = new GregorianCalendar();

            if (ca.get(GregorianCalendar.AM_PM) == 1)// 判断上下午时间
            {
                c.set(Calendar.HOUR, -2);
            } else {
                c.set(Calendar.HOUR, +10);
            }

            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);

            Date afterTime = c.getTime();// 当天10时后
            return afterTime.before(date);
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return false;
        }
    }

    public static boolean isIndexDCDateAfter(Date date) {
        try {
            Calendar c = Calendar.getInstance();
            GregorianCalendar ca = new GregorianCalendar();

            if (ca.get(GregorianCalendar.AM_PM) == 1)// 判断上下午时间
            {
                c.set(Calendar.HOUR, +22);
            } else {
                c.set(Calendar.HOUR, +34);
            }

            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);

            Date beforeTime = c.getTime();// 明天10时前
            return beforeTime.after(date);
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return false;
        }

    }

    public static Date GetDCMatchEndTime(Date matchTime, int aheadMilli) {
        if (matchTime == null) {
            return null;
        }
        Calendar matchCal = Calendar.getInstance();
        matchCal.setTime(matchTime);

        Calendar stopPlayTicketCal = (Calendar)matchCal.clone();
        stopPlayTicketCal.set(Calendar.HOUR_OF_DAY, 4);
        stopPlayTicketCal.set(Calendar.MINUTE, 50);
        stopPlayTicketCal.set(Calendar.SECOND, 0);

        // 欧洲杯延长销售时间 9点开始至次日凌晨6点
        Date euroCupStart = DateUtils.stringToDate_YYYY_MM_DD_HH_MM_SS("2012-06-08 9:00:00");
        Date euroCupEnd = DateUtils.stringToDate_YYYY_MM_DD_HH_MM_SS("2012-07-03 06:00:00");
        if (matchTime.getTime() > euroCupStart.getTime() && matchTime.getTime() < euroCupEnd.getTime()) {
            stopPlayTicketCal.set(Calendar.HOUR_OF_DAY, 5);
        }
        stopPlayTicketCal.add(Calendar.MILLISECOND, -aheadMilli);

        Calendar startPlayTicketCal = (Calendar)matchCal.clone();
        startPlayTicketCal.set(Calendar.HOUR_OF_DAY, 9);
        startPlayTicketCal.set(Calendar.MINUTE, 0);
        startPlayTicketCal.set(Calendar.SECOND, 0);

        Calendar weStartPlayTicketCal = (Calendar)startPlayTicketCal.clone();
        weStartPlayTicketCal.add(Calendar.MILLISECOND, +aheadMilli);
        // if(matchCal.after(stopPlayTicketCal)&&(matchCal.before(startPlayTicketCal)||matchCal.equals(startPlayTicketCal))){
        //
        // //(4:50-aheadMilli)——(9:00) 都设置成为(4:50-aheadMilli)
        // return stopPlayTicketCal.getTime();
        // }else
        // if(matchCal.after(startPlayTicketCal)&&matchCal.before(weStartPlayTicketCal)){
        // //(9:00)——(9:00+aheadMilli) 都设置 (4:50-aheadMilli)+offset
        // int offset=(int)
        // (weStartPlayTicketCal.getTimeInMillis()-matchCal.getTimeInMillis());
        // stopPlayTicketCal.add(Calendar.MILLISECOND, +offset);
        // return stopPlayTicketCal.getTime();
        if (matchCal.after(stopPlayTicketCal) && matchCal.before(weStartPlayTicketCal)) {
            // (4:50-aheadMilli)——(9:00+aheadMilli) 都设置 (4:50-aheadMilli)
            return stopPlayTicketCal.getTime();
        } else {
            matchCal.add(Calendar.MILLISECOND, -aheadMilli);
            return matchCal.getTime();
        }
    }

    /**
     * 获取距离现在的时间
     */
    public static String getMinutes(Date times) {
        long time = System.currentTimeMillis() - times.getTime();// time 单位是 毫秒
        String res = null;
        // 转化成天数
        // 先判断是不是小于 60 * 60 * 1000 也就是 小于1小时，那么显示 ： **分钟前
        if (time < 60 * 60 * 1000) {
            res = (time / 1000 / 60) + "分钟前";
        }
        // 如果大于等于1小时 小于等于一天，那么显示 ： **小时前
        else if (time >= 60 * 60 * 1000 && time < 24 * 60 * 60 * 1000) {
            res = (time / 1000 / 60 / 60) + "小时前";
        }
        // 如果大于等于1小时 小于等于一天，那么显示 ： **小时前
        else if (time >= 24 * 60 * 60 * 1000) {
            res = (time / 1000 / 60 / 60 / 24) + "天前";
        }
        // 如果时间不明确或者发帖不足一分钟 ，则不显示
        else {
            res = "";
        }
        return res;
    }

    /**
     * 取得系统当前时间前n个月的相对应的一天
     *
     * @param n
     *            int
     * @return String yyyy-mm-dd
     */
    public static String getNMonthBeforeCurrentDay(int n) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -n);
        return "" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);

    }

    public static List<String> getAllBeforeDateToAfterDateSpace(String string, String string2) {
        List<String> date = null;
        try {
            date = new ArrayList<String>();
            SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat(YYYY_MM_DD);
            Date dateTemp = SDF_YYYY_MM_DD.parse(string);
            Date dateTemp2 = SDF_YYYY_MM_DD.parse(string2);
            Calendar calendarTemp = Calendar.getInstance();
            calendarTemp.setTime(dateTemp);
            while (calendarTemp.getTime().getTime() != dateTemp2.getTime()) {
                date.add(SDF_YYYY_MM_DD.format(calendarTemp.getTime()));
                calendarTemp.add(Calendar.DAY_OF_YEAR, 1);
            }
            date.add(string2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /******* 得到昨天日期 *******/
    public static String getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateToString(cal.getTime(), YYYYMMDD);
    }

    /**
     * 获取两个日期之间间隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getTwoDay(Date startDate, Date endDate) {
        long day = 0;
        try {
            day = (endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 检查给定时间是否在指定时间区间
     *
     * @param startTime
     * @param endTime
     * @param checkTime
     * @return
     */
    public static boolean isBetween(Date startTime, Date endTime, Date checkTime) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Calendar cal3 = Calendar.getInstance();
        cal1.setTime(checkTime);
        cal2.setTime(startTime);
        cal3.setTime(endTime);
        if (cal2.before(cal1) && cal3.after(cal1)) {
            return true;
        }
        return false;
    }

    // 根据日期取得星期几
    public static String getWeek(Date date) {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 日期加减操作
     *
     * @param date
     * @return
     */
    public static Date jiaOrJian(Date date, int num) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, num);
        return cal.getTime();
    }

    public static boolean verifyDateTime(String time) {
        Date date = stringToDate_YYYY_MM_DD_HH_MM_SS(time);
        return date.getTime() <= System.currentTimeMillis();
    }

    /**
     * 获取某天的最后一秒
     *
     * @param date
     * @return
     */
    public static Date getDateEnd(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取某天的最后一秒
     *
     * @param time
     * @return
     */
    public static String getDateEnd(String time) {
        if (StringUtils.isEmpty(time)) {
            return time;
        }
        return dateToString_YYYY_MM_DD_HH_MM_SS(getDateEnd(stringToDate_YYYY_MM_DD_HH_MM_SS(time)));
    }

    /**
     * 在当前日期的基础上加上几年
     *
     * @return
     */
    public static String addYears(int year) {
        Calendar calendar = new GregorianCalendar();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        date = calendar.getTime();
        String dateStr = dateToString_YYYYMMDD(date);
        return dateStr;
    }

    public static void main(String[] args) throws Exception {
        String time = "2019-05-11 10:50:00";
        System.out.println(getDateEnd(time));
        System.out.println(getDateEnd(new Date()));
        System.out.println(addYears(5));
        Date dueTime = new Date();
        String dt = dateToString(dueTime, "dd-MM-yyyy");
        System.out.println(dt);
    }

}