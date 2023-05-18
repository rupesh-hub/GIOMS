package com.gerp.shared.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;


public class  DateTimeUtils {

    public DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getTimeDifference(LocalTime checkIn, LocalTime shiftCheckin) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        Date date1 = format.parse(checkIn.toString());
        Date date2 = format.parse(shiftCheckin.toString());
        return milliSecondToHour(date2.getTime()-date1.getTime());
    }

    public static String milliSecondToHour(long millis){
        int sec  = (int)(millis/ 1000) % 60 ;
        int min  = (int)((millis/ (1000*60)) % 60);
        int hr   = (int)((millis/ (1000*60*60)) % 24);

        String hours = (hr < 10) ? "0" + hr : String.valueOf(hr);
        String minutes = (min < 10) ? "0" + min : String.valueOf(min);
        String seconds = (sec < 10) ? "0" + sec : String.valueOf(sec);

        return hours + ":" + minutes + ":" + seconds + ".";
    }


}
