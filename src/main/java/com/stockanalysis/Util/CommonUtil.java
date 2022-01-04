package com.stockanalysis.Util;

import java.util.Calendar;
import java.util.Date;

public class CommonUtil {

    public static Date addDay(Date date, int day)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }
}
