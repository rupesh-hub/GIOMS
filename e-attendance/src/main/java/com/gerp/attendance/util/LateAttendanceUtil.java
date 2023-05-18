package com.gerp.attendance.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

@Component
public class LateAttendanceUtil {
    public String getTimeDifference(LocalTime checkIn, LocalTime shiftCheckin) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date1 = format.parse(checkIn.toString());
        Date date2 = format.parse(shiftCheckin.toString());

        long diff = date2.getTime() - date1.getTime();
        long diffMinutes = (diff /1000) /(60);
        long diffHours = diffMinutes / (60);
        long min = diffMinutes %(60);
        String minutes="0";
        String hours ="0";
        if(String.valueOf(min).length()==1){
            minutes=minutes.concat(String.valueOf(min));
        }else{
            minutes=String.valueOf(min);
        }
        if(String.valueOf(diffHours).length()==1){
            hours=hours.concat(String.valueOf(diffHours));
        }else{
            hours=String.valueOf(diffHours);
        }
        String time=hours.concat(":").concat(minutes);
        return time;

    }
}
