package com.gerp.attendance.util;

import com.gerp.attendance.Pojo.DailyLogPojo;
import com.gerp.attendance.Pojo.KaajRequestCustomPojo;
import com.gerp.attendance.Pojo.LeaveRequestLatestPojo;
import com.gerp.attendance.mapper.HolidayMapper;
import com.gerp.attendance.mapper.LeavePolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ContentGenerator {

    public static final String LEAVE_CONSTANT = "LR";
    public static final String DAILY_LOG = "DL";
    public static final String KAAJ_REQUEST = "KR";



    static String getLeaveContent(LeaveRequestLatestPojo leaveRequest) {
        String content = ContentGenerator.LEAVE_CONSTANT
                + leaveRequest.getPisCode()
                + leaveRequest.getFromDateEn()
                + leaveRequest.getToDateEn()
                + leaveRequest.getLeavePolicyId()
                + leaveRequest.getDescription();
            if (leaveRequest.getDocument() != null) {
                content = content.concat(leaveRequest.getDocument().getName());
            }
            return content.replaceAll("\\s","");
    }

    static String getDailyLogContent(DailyLogPojo dailyLogPojo) {
        String content = ContentGenerator.DAILY_LOG
                + (dailyLogPojo.getPisCode()==null?" ":dailyLogPojo.getPisCode())
                + (dailyLogPojo.getTimeFrom()==null?" ":dailyLogPojo.getTimeFrom())
                + (dailyLogPojo.getTimeTo()==null?" ":dailyLogPojo.getTimeTo())
                + (dailyLogPojo.getRemarks()==null?" ":dailyLogPojo.getRemarks());
         return content.replaceAll("\\s","");
    }


    static String getKaajRequestContent(KaajRequestCustomPojo kaajRequestCustomPojo) {
        String content = ContentGenerator.KAAJ_REQUEST
                + (kaajRequestCustomPojo.getKaajTypeId()==null?" ":kaajRequestCustomPojo.getKaajTypeId())
                + (kaajRequestCustomPojo.getDurationType()==null?" ":kaajRequestCustomPojo.getDurationType())
                + (kaajRequestCustomPojo.getAdvanceAmountTravel()==null?" ":kaajRequestCustomPojo.getAdvanceAmountTravel())
                + (kaajRequestCustomPojo.getFiscalYearCode()==null?" ":kaajRequestCustomPojo.getFiscalYearCode())
                + (kaajRequestCustomPojo.getLocation()==null?" ":kaajRequestCustomPojo.getLocation())
                + (kaajRequestCustomPojo.getRemarkRegardingTravel()==null?" ":kaajRequestCustomPojo.getRemarkRegardingTravel())
                + (kaajRequestCustomPojo.getCountryId()==null? " ":kaajRequestCustomPojo.getCountryId().toString());
        if (kaajRequestCustomPojo.getAppliedForOthers()) {
            content = content.concat("true").concat(kaajRequestCustomPojo.getAppliedPisCode());
            final String[] finalContent1 = {new String()};
            kaajRequestCustomPojo.getKaajAppliedOthersPojo().stream().forEach(z->{
                z.getAppliedDateList().stream().forEach(s->{
                  finalContent1[0] = finalContent1[0].concat(s.getFromDateEn().toString())
                    .concat(s.getToDateEn().toString());
                });

            });
//            dateList.add(dateList.toString());
            content=content.concat(finalContent1[0]);

        }else{
            content=content.concat("false")
            .concat(kaajRequestCustomPojo.getPisCode())
            .concat(kaajRequestCustomPojo.getFromDateEn().toString())
            .concat(kaajRequestCustomPojo.getToDateEn().toString());
        }
        if (!kaajRequestCustomPojo.getDocument().isEmpty()) {
            final String[] finalContent = {new String()};
            if(!kaajRequestCustomPojo.getDocument().isEmpty()){
                kaajRequestCustomPojo.getDocument().forEach(x->{
                    finalContent[0] = finalContent[0].concat(x.getName()==null?null:x.getName());

                });
                content=content.concat(finalContent[0]);
            }

        }
        if (!kaajRequestCustomPojo.getReferenceDocument().isEmpty()) {
            final String[] finalContent = {new String()};
            kaajRequestCustomPojo.getReferenceDocument().forEach(x->{
                if(x.getId()!=null) {
                    finalContent[0] = finalContent[0].concat(x.getName());
                }
            });
            content=content.concat(finalContent[0]);
        }
        return content.replaceAll("\\s","");
    }


}
