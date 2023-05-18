package com.gerp.attendance.Pojo.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.DailyInformationPojo;
import com.gerp.attendance.Pojo.LateEmployeePojo;
import com.gerp.attendance.Pojo.OfficePojo;
import com.gerp.attendance.Pojo.shift.OfficeTimePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.mapper.KaajRequestMapper;
import com.gerp.attendance.mapper.UserMgmtMapper;
import com.gerp.attendance.service.OfficeTimeConfigService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class LateAttendanceReportGenerator {

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private OfficeTimeConfigService officeTimeConfigService;

    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    @Autowired
    private UserMgmtMapper userMgmtMapper;

    @Autowired
    private KaajRequestMapper kaajRequestMapper;

    @Autowired
    private DateConverter dateConverter;



    @Autowired
    private TokenProcessorService tokenProcessorService;

    private static final String styles = "<style>\n" +
            "#customers {\n" +
            "  font-family: Arial, Helvetica, sans-serif;\n" +
            "  border-collapse: collapse;\n" +
            "  width: 100%;\n" +
            "}\n" +
            "\n" +
            "#customers td, #customers th {\n" +
            "  border: 1px solid #ddd;\n" +
            "  padding: 8px;\n" +
            "}\n" +
            "\n" +
            "#customers tr:nth-child(even){background-color: #f2f2f2;}\n" +
            "\n" +
            "#customers tr:hover {background-color: #ddd;}\n" +
            "\n" +
            "#customers th {\n" +
            "  padding-top: 12px;\n" +
            "  padding-bottom: 12px;\n" +
            "  text-align: left;\n" +
            "  background-color: #2f6fcf;\n" +
            "  color: black;\n" +
            "}\n" +
            "</style>";

    private static final String htmlHeader = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Daily Information Report</title>\n" +
            styles +
            "</head>\n" +
            "<body>"+
            "<div>\n" +
            "<div style=\"text-align:center;\">\n" +
            "<div>\n" ;

    private static final String htmlFooter = "</body>\n" +
            "</html>";

    private static final String pdfEnglishHeader =
            htmlHeader +
                    "<div>\n" +
                    "<div>\n" +
                    "<div>\n" +
                    "<h1>Government Of Nepal</h1>\n" +
                    "<h1>Ministry Of Communication And Information Technology</h1>\n" +
                    "<h5>2078/79 Leave Report of permanent employees <h5/>\n" +
                    "</div>" +
                    "<table role=\"table\" id=\"customers\">" +
                    "<thead>\n" +
                    "<tr role=\"row\">\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">S.N</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Darta No.</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Requested Date</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Approved Date</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Employee Name</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Date</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Duration</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Kaaj Type</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Location</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Approver Status</th>\n" +


                    "</tr>\n" +
                    "</thead>\n" +
                    "<tbody role=\"rowgroup\">\n";

    private static final String pdfEnglishFooter =
            "</tbody>\n" +
                    "</table>\n" +
                    "</div>\n" +
                    "</div>" + htmlFooter;

//    private static final String pdfNepaliHeader  =
//            htmlHeader +
//            "<div>\n" +
//            "<div>\n" +
//                    "<div>\n" +
//                    "<h1>"+ +"</h1>\n" +
//                    "<h5><u>कार्यरत कर्मचारीहरुको सम्पर्क विवरण</u> <h5/>\n" +
//                    "</div>" +
//            "<table role=\"table\" id=\"customers\">" +
//            "<thead>\n" +
//            "<tr role=\"row\">\n" +
//                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">क्र.सं</th>\n" +
//            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">कर्मचारीको नाम</th>\n" +
//            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">पद</th>\n" +
//            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">सेक्सन/उप-सेक्सन</th>\n" +
//            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">मोबाइल</th>\n" +
//            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">PABX Code</th>\n" +
//            "</tr>\n" +
//            "</thead>\n" +
//            "<tbody role=\"rowgroup\">\n";

    private static final String pdfNepaliFooter = "</tr>\n" +
            "</tbody>\n" +
            "</table>\n" +
            "<br>\n" +
            "<br>\n" +
            "<br>\n" +
            "<div style=\"display:block\">" +
            "<div style=\"display:flex;justify-content: space-around; padding-top:60px\">\n" +
            "<span style=\"display: inline-block;width: 100px;justify-content:center;padding: 5px;border-top: 1px solid;text-align:center\">तयारी गर्ने</span> "
            +"<span style=\"display: inline-block;width: 100px;justify-content:center;padding: 5px;border-top: 1px solid;text-align:center\">पेश गर्ने</span> "
            +"<span style=\"display: inline-block;width: 100px;justify-content:center;padding: 5px;border-top: 1px solid;text-align:center\">प्रमाणित गर्ने</span> "+
            "</div>" +
            "</div>" +
            "</div>\n" +
            "</div>" + htmlFooter;




    public String generateReport(GetRowsRequest paginatedRequest, int type) throws ParseException {
        if(type == 0){
            return generateEnglishReport(paginatedRequest);
        }
        else
        {
            return generateNepaliReport(paginatedRequest);
        }
    }

    String getEnglishHeader() {
        return "<table role=\"table\" id=\"customers\">" +
                "<thead>\n" +
                "<tr role=\"row\">\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">PIS Code</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Employee Name</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Designation</th>\n" +
//                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Date</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Start Time</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">In Time</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Late CheckIn</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Status</th>\n" +

                "</tr>\n" +
                "</thead>\n" +
                "<tbody role=\"rowgroup\">\n";
    }


    //               "<h3>"+ officeAddress +"</h3>\n" +
    String getNepaliHeader() {
        return "<table role=\"table\" id=\"customers\">" +
                "<thead>\n" +
                "<tr role=\"row\">\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">क.सं.नं.</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">कर्मचारीको नाम</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">पद</th>\n" +
//                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">मिति</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">आउने समय</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">आएको समय</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">ढिलो आएको</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">स्थिति</th>\n" +

                "</tr>\n" +
                "</thead>\n" +
                "<tbody role=\"rowgroup\">\n";

    }

    public String generateEnglishReport(GetRowsRequest paginatedRequest) throws ParseException {
       paginatedRequest.setLimit(10000);
        Page<LateEmployeePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if(paginatedRequest.getOfficeCode()==null || paginatedRequest.getOfficeCode().trim().equals(""))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

//        paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
        if(tokenProcessorService.isGeneralUser() && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()){
            if(paginatedRequest.getSearchField()!=null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            }else{
                Map<String,Object> newPisCode=new HashMap<>();
                newPisCode.put("pisCode",tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }

        page=employeeAttendanceMapper.getAllLateAttendance(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );
        String htmlcheck=
                htmlHeader +
                        "<span style=\"font-size:14px\">Government Of Nepal<span><br>" ;

//        html.concat(htmlHeader);
//        html.concat("<h2>Government Of Nepal</h2>");

        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), String.valueOf(1),1);
        for (int i = topOfficeDetails.size()-2 ; i >= 0 ; i--){

            if(i== topOfficeDetails.size()-1){
                htmlcheck=  htmlcheck + "<span style=\"font-size:20px\">"+ topOfficeDetails.get(i) +"</span><br>" ;

            }else {
                htmlcheck= htmlcheck + "<span style=\"font-size:14px\">"+ topOfficeDetails.get(i) +"</span><br>" ;
            }
        }


        if(paginatedRequest.getSearchField().get("userType")!=null){
            String userType=userMgmtMapper.getUserType(1,paginatedRequest.getSearchField());
            htmlcheck=
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">"+"("+dateConverter.convertAdToBs(paginatedRequest.getSearchField().get("dateEn").toString())+") Late Attendance report of "+ StringUtils.capitalize(userType)+"Employee</span><br><br>"+
                            "<div>";
        }else {
            htmlcheck =
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">" + "("+dateConverter.convertAdToBs(paginatedRequest.getSearchField().get("dateEn").toString()) + ") Late Attendance report of Employee</span><br><br>" +
                            "<div>";
        }

        htmlcheck=htmlcheck.concat(getEnglishHeader());
        int sn = 0;
        OfficeTimePojo officeTimePojo= officeTimeConfigService.getOfficeTimeByCode(paginatedRequest.getOfficeCode());
        if(officeTimePojo.getAllowedLimit()> page.getRecords().stream().count()){

            Long count=0l;
            for(Long i=page.getTotal();count<=officeTimePojo.getAllowedLimit();i--){
                page.getRecords().remove(i);
                count++;
            }

        }


        for (LateEmployeePojo lateEmployeePojo : page.getRecords()){
            sn ++;
            htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getPisCode()==null?"-":lateEmployeePojo.getPisCode()) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getEmpNameEn()==null?"-":lateEmployeePojo.getEmpNameEn()) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getFdNameEn()==null?"-":lateEmployeePojo.getFdNameEn()) +"</td>")
//                    .concat("<td role=\"cell\">"+ StringUtils.setNAIfNull(dailyInformationPojo.getDateNp() == null ? "-" : dailyInformationPojo.getDateNp()) +"</td>")
//                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getDateNp()==null?"-":lateEmployeePojo.getDateNp().toString())+"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getShiftCheckin()== null ? "-" : LocalDateTime.ofInstant(new SimpleDateFormat("HH:mm").parse(lateEmployeePojo.getShiftCheckin().toString()).toInstant(), ZoneId.systemDefault()).toLocalTime()) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getShiftCheckin()== null ? "-" : LocalDateTime.ofInstant(new SimpleDateFormat("HH:mm").parse(lateEmployeePojo.getCheckIn().toString()).toInstant(), ZoneId.systemDefault()).toLocalTime()) +"</td>")
//                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getCheckIn()== null ? "-" : lateEmployeePojo.getCheckIn()) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getShiftCheckin() == null ? "-" : this.getTimeDifference(lateEmployeePojo.getShiftCheckin(),lateEmployeePojo.getCheckIn())) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getAttendanceType() == null ? "-" : lateEmployeePojo.getAttendanceType().getEnum().getValueEnglish()) +"</td>")
                    .concat("</tr>");
        }
        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
        return htmlcheck;
    }

    public String generateNepaliReport(GetRowsRequest paginatedRequest) throws ParseException {
        paginatedRequest.setLimit(10000);
        Page<LateEmployeePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());


        if(paginatedRequest.getOfficeCode()==null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        if(tokenProcessorService.isGeneralUser() && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()){
            if(paginatedRequest.getSearchField()!=null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            }else{
                Map<String,Object>newPisCode=new HashMap<>();
                newPisCode.put("pisCode",tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }

        page=employeeAttendanceMapper.getAllLateAttendance(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );

        String htmlcheck=
                htmlHeader +
                        "<span style=\"font-size:14px\">नेपाल सरकार<span><br>" ;


        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), String.valueOf(0),1);
        for (int i = topOfficeDetails.size()-1 ; i >= 0 ; i--){
            if (i == topOfficeDetails.size()-2){
                htmlcheck= htmlcheck + "<span style=\"font-size:20px\">"+ topOfficeDetails.get(i) +"</span><br>" ;
            }else {
                htmlcheck= htmlcheck + "<span style=\"font-size:14px\">"+ topOfficeDetails.get(i) +"</span><br>" ;
            }
        }

        if(paginatedRequest.getSearchField().get("userType")!=null){
            String userType=userMgmtMapper.getUserType(0,paginatedRequest.getSearchField());
            htmlcheck=
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">"+"("+dateConverter.convertToNepali(dateConverter.convertAdToBs(paginatedRequest.getSearchField().get("dateEn").toString()))+") को "+StringUtils.capitalize(userType)+" "+") कर्मचारीको ढिलो आएको रिपोर्ट</span><br><br>"+

                            "<div>";
        }else {
            htmlcheck =
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">"+"("+dateConverter.convertToNepali(dateConverter. convertAdToBs(paginatedRequest.getSearchField().get("dateEn").toString()))+") कर्मचारीको ढिलो आएको रिपोर्ट</span><br><br>"+
                            "<div>";
        }



        htmlcheck=htmlcheck.concat(getNepaliHeader());
        String leaveName = "";

        int sn = 0;
        for (LateEmployeePojo lateEmployeePojo : page.getRecords()){

            sn ++;
//            html = html.concat("<tr role=\"row\">")
//                    .concat("<td role=\"cell\">"+ sn +"</td>")
//                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getLeaveApproveDartaNo()==null?null:leaveReportDataPojo.getLeaveApproveDartaNo().toString()) +"</td>")
//                    .concat("<td role=\"cell\">"+ StringUtils.setNAIfNull(leaveReportDataPojo.getEmployeeNameNp() == null ? null : WordUtils.capitalizeFully(leaveReportDataPojo.getEmployeeNameNp())) +"</td>")
//                    .concat("<td role=\"cell\">"+ leaveReportDataPojo.getFromDateNp()+" - "+leaveReportDataPojo.getToDateNp()+"</td>")
//                    .concat("<td role=\"cell\">"+ String.valueOf(ChronoUnit.DAYS.between(leaveReportDataPojo.getFromDateEn(), leaveReportDataPojo.getToDateEn()))+"</td>")
//                    .concat("<td role=\"cell\">"+ StringUtils.setNAIfNull(leaveReportDataPojo.getLeaveNameEn()) +"</td>")
//                    .concat("<td role=\"cell\">"+ StringUtils.setNAIfNull(leaveReportDataPojo.getApprovalDetail().getApproverNameEn()) +"</td>")
//                    .concat("<td role=\"cell\">"+ StringUtils.setNAIfNull(leaveReportDataPojo.getApprovalDetail().getStatus().getValueEnglish()) +"</td>").concat("</tr>");

            htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getPisCode()==null?"-":dateConverter.convertToNepali(lateEmployeePojo.getPisCode())) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getEmpNameNp()==null?"-":lateEmployeePojo.getEmpNameNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getFdNameNp()==null?"-":lateEmployeePojo.getFdNameNp()) +"</td>")
//                    .concat("<td role=\"cell\">"+ StringUtils.setNAIfNull(dailyInformationPojo.getDateNp() == null ? "-" : dailyInformationPojo.getDateNp()) +"</td>")
//                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getDateNp()==null?"-":dateConverter.convertBSToDevnagari(lateEmployeePojo.getDateNp()))+"</td>")
//                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getShiftCheckin()== null ? "-" : dateConverter.convertToNepali(lateEmployeePojo.getShiftCheckin().toString()))+"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getShiftCheckin()== null ? "-" : dateConverter.convertBSToDevnagari(LocalDateTime.ofInstant(new SimpleDateFormat("HH:mm").parse(lateEmployeePojo.getShiftCheckin().toString()).toInstant(), ZoneId.systemDefault()).toLocalTime().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getCheckIn()== null ? "-" : dateConverter.convertBSToDevnagari(LocalDateTime.ofInstant(new SimpleDateFormat("HH:mm").parse(lateEmployeePojo.getCheckIn().toString()).toInstant(), ZoneId.systemDefault()).toLocalTime().toString())) +"</td>")
//                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getCheckIn()== null ? "-" : dateConverter.convertToNepali(String.valueOf(lateEmployeePojo.getCheckIn()))) +"</td>")
//                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getLateCheckin() == null ? "-" : dateConverter.convertToNepali(LocalTime.parse(lateEmployeePojo.getLateCheckin(), DateTimeFormatter.ofPattern("HH:mm:ss")).toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getLateCheckin() == null ? "-" : dateConverter.convertToNepali(this.getTimeDifference(lateEmployeePojo.getShiftCheckin(),lateEmployeePojo.getCheckIn()))) +"</td>")

                    .concat("<td role=\"cell\">"+ (lateEmployeePojo.getAttendanceType() == null ? "-" : lateEmployeePojo.getAttendanceType().getEnum().getValueNepali()) +"</td>")
                    .concat("</tr>");


        }
        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
        return htmlcheck;
    }

private String getTimeDifference(LocalTime checkIn,LocalTime shiftCheckin) throws ParseException {

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
    private List<String> topOfficeDetails(String officeCOde, List<String> value, String type, int count){
        OfficePojo officeDetail = userMgmtServiceData.getOfficeDetail(officeCOde);
        if (count ==1 ){
            value.add(type.equalsIgnoreCase("1")? officeDetail.getAddressEn(): officeDetail.getAddressNp());
        }
        value.add(type.equalsIgnoreCase("1")?officeDetail.getNameEn():officeDetail.getNameNp());

        if (officeDetail.getParentCode() == null || officeDetail.getParentCode().equalsIgnoreCase("8886")){
            return value;
        }
        return topOfficeDetails(officeDetail.getParentCode(),value,type,++count);
    }


}
