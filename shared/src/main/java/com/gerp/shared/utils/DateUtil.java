package com.gerp.shared.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@Component
public class DateUtil {

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static String days(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String dayOfWeek = getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
        return dayOfWeek;
    }

    private static String getDayOfWeek(int value){
        String day = "";
        switch(value){
            case 1:
                day="Sunday";
                break;
            case 2:
                day="Monday";
                break;
            case 3:
                day="Tuesday";
                break;
            case 4:
                day="Wednesday";
                break;
            case 5:
                day="Thursday";
                break;
            case 6:
                day="Friday";
                break;
            case 7:
                day="Saturday";
                break;
        }
        return day;
    }

    public static boolean isNotNullAndEmpty(String date) {
        return date != null && !date.trim().equals("");
    }
}
