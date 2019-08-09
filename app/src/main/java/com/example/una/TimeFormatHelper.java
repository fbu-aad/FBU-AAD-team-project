package com.example.una;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatHelper {
    private static final String dayMonthYearPattern = "EEE, MMM d, yyyy";
    private static final String dayMonthYearHourMinutePattern = "MM/dd/yyyy HH:mm:ss";
    private static final DateFormat dayMonthYearFormat = new SimpleDateFormat(dayMonthYearPattern);
    private static final DateFormat dayMonthYearHourMinuteFormat = new SimpleDateFormat(dayMonthYearHourMinutePattern);

    public static String getDateStringFromDate(Date date) {
        String currentTimes = dayMonthYearFormat.format(date);
        return currentTimes;
    }

    public static String getDateTimeStringFromDate(Date date) {
        String currentTimes = dayMonthYearHourMinuteFormat.format(date);
        return currentTimes;
    }

}
