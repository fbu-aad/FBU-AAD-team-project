package com.example.una;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

public class TimeFormatHelperTest {

    @Test
    public void test_getDateStringFromDate_whenMMddYYYY() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(119 + 1900, 7, 15); // Months are 0 based
        Date date = gregorianCalendar.getTime();

        String output = TimeFormatHelper.getDateStringFromDate(date);

        Assert.assertEquals("08/15/2019", output);
    }

    @Test
    public void test_getDateTimeStringFromDate_whenMMddYYYY() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(119 + 1900, 7, 15, 13, 37, 00); // Months are 0 based
        Date date = gregorianCalendar.getTime();

        String output = TimeFormatHelper.getDateTimeStringFromDate(date);

        Assert.assertEquals("08/15/2019 13:37:00", output);
    }

}