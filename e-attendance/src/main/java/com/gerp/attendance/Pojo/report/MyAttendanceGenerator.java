package com.gerp.attendance.Pojo.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.DailyInformationPojo;
import com.gerp.attendance.Pojo.KaajRequestCustomPojo;
import com.gerp.attendance.Pojo.OfficeHierarchyPojo;
import com.gerp.attendance.Pojo.OfficePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.mapper.KaajRequestMapper;
import com.gerp.attendance.mapper.UserMgmtMapper;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Component
public class MyAttendanceGenerator {

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    @Autowired
    private UserMgmtMapper userMgmtMapper;

    @Autowired
    private CustomMessageSource customMessageSource;

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
            "<title>Leave Report</title>\n" +
            styles +
            "</head>\n" +
            "<body>"+
            "<div>\n" +
            "<div style=\"text-align:center;\">\n" +
            "<div>\n" ;

    private static final String htmlFooter = "</body>\n" +
            "</html>";

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

//    private static final String pdfNepaliFooter = "</tr>\n" +
//            "</tbody>\n" +
//            "</table>\n" +
//            "</div>\n" +
//            "</div>" + htmlFooter;




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
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">S.N</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Date</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Check In</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Check Out</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Late Check In(HH:MM)</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Early Check Out(HH:MM)</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Extra Time(HH:MM)</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Attendance Type</th>\n" +

                "</tr>\n" +
                "</thead>\n" +
                "<tbody role=\"rowgroup\">\n";
    }


    //               "<h3>"+ officeAddress +"</h3>\n" +
    String getNepaliHeader() {
        return "<table role=\"table\" id=\"customers\">" +
                "<thead>\n" +
                "<tr role=\"row\">\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">क्र.सं.</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">मिति</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">चेक इन</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">चेक आउट</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">ढिलो चेकइन(घ:मि)</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">प्रारम्भिक चेकआउट(घ:मि)</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">अतिरिक्त समय(घ:मि)</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">हाजिरी प्रकार</th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody role=\"rowgroup\">\n";

    }

    public String generateEnglishReport(GetRowsRequest paginatedRequest) {
        paginatedRequest.setLimit(100000);
        EmployeeAttendancePage<EmployeeAttendanceReportDataPojo> page = new EmployeeAttendancePage(paginatedRequest.getPage(), paginatedRequest.getLimit());


//        if(paginatedRequest.getPisCode()==null || paginatedRequest.getPisCode().trim().equals(""))
//                throw new RuntimeException(customMessageSource.get("notnull",customMessageSource.get("pisempcode")));

        if(paginatedRequest.getPisCode()==null || paginatedRequest.getPisCode().trim().equals(""))
            paginatedRequest.setPisCode(tokenProcessorService.getPisCode());

        if(paginatedRequest.getFromDate() == null || paginatedRequest.getToDate() == null)
            throw new RuntimeException(customMessageSource.get("notnull","Date"));

        page.setOptimizeCountSql(false);

        page = employeeAttendanceMapper.filterData(
                page,
                paginatedRequest.getPisCode(),
                AttendanceStatus.DEVICE.toString(),
                AttendanceStatus.MA.toString(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );

        EmployeeAttendanceTotalSum sum = employeeAttendanceMapper.getSumForFilter(
                paginatedRequest.getPisCode(),
                AttendanceStatus.DEVICE.toString(),
                AttendanceStatus.MA.toString(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        if(sum!=null) {
            page.setTotalExtraTime(sum.getTotalExtraTime());
            page.setTotalLateCheckin(sum.getTotalLateCheckin());
            page.setTotalEarlyCheckout(sum.getTotalEarlyCheckout());
        }else{
            page.setTotalExtraTime(LocalTime.parse("00:00"));
            page.setTotalLateCheckin(LocalTime.parse("00:00"));
            page.setTotalEarlyCheckout(LocalTime.parse("00:00"));
        }




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


            htmlcheck=
                    htmlcheck +
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getDateWiseFiscalYear(1,paginatedRequest.getDate(),paginatedRequest.getDate())+"Attendance report of "+this.processEmployeeData(paginatedRequest.getPisCode(),String.valueOf(0))+"</span><br><br>"+
                            "<div>";


        htmlcheck=htmlcheck.concat(getEnglishHeader());
        int sn = 0;
        for (EmployeeAttendanceReportDataPojo employeeAttendance : page.getRecords()){

            sn ++;
            htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ (sn) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getDates()==null?"-":employeeAttendance.getDateNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getCheckin()==null?"-": employeeAttendance.getCheckin().toString()) +"</td>")
//                    .concat("<td role=\"cell\">"+ (employeeAttendance.getCheckin()==null?"-":employeeAttendance.getCheckin().toString()) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getCheckout()==null?"-":employeeAttendance.getCheckout().toString())+"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getLateCheckin()== null ? "-" : employeeAttendance.getLateCheckin()) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getEarlyCheckout()== null ? "-" : employeeAttendance.getEarlyCheckout()) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getExtraTime() == null ? "-" : employeeAttendance.getExtraTime().toString()) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getAttendanceType() == null ? "-" : employeeAttendance.getAttendanceType().getEnum().getValueEnglish()) +"</td>")
                    .concat("</tr>");
        }
        htmlcheck = htmlcheck.concat(pdfEnglishFooter);
        return htmlcheck;
    }

    public String generateNepaliReport(GetRowsRequest paginatedRequest) throws ParseException {
        paginatedRequest.setLimit(100000);
        EmployeeAttendancePage<EmployeeAttendanceReportDataPojo> page = new EmployeeAttendancePage(paginatedRequest.getPage(), paginatedRequest.getLimit());

//        if(paginatedRequest.getPisCode()==null || paginatedRequest.getPisCode().trim().equals(""))
//                throw new RuntimeException(customMessageSource.get("notnull",customMessageSource.get("pisempcode")));

        if(paginatedRequest.getPisCode()==null || paginatedRequest.getPisCode().trim().equals(""))
            paginatedRequest.setPisCode(tokenProcessorService.getPisCode());

        if(paginatedRequest.getFromDate() == null || paginatedRequest.getToDate() == null)
            throw new RuntimeException(customMessageSource.get("notnull","Date"));
        page.setOptimizeCountSql(false);

        page = employeeAttendanceMapper.filterData(
                page,
                paginatedRequest.getPisCode(),
                AttendanceStatus.DEVICE.toString(),
                AttendanceStatus.MA.toString(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getSearchField()
        );

        EmployeeAttendanceTotalSum sum = employeeAttendanceMapper.getSumForFilter(
                paginatedRequest.getPisCode(),
                AttendanceStatus.DEVICE.toString(),
                AttendanceStatus.MA.toString(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        if(sum!=null) {
            page.setTotalExtraTime(sum.getTotalExtraTime());
            page.setTotalLateCheckin(sum.getTotalLateCheckin());
            page.setTotalEarlyCheckout(sum.getTotalEarlyCheckout());
        }else{
            page.setTotalExtraTime(LocalTime.parse("00:00"));
            page.setTotalLateCheckin(LocalTime.parse("00:00"));
            page.setTotalEarlyCheckout(LocalTime.parse("00:00"));
        }

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


        htmlcheck=
                htmlcheck +
                        "<span style=\"font-size:14px\">"+this.processEmployeeData(paginatedRequest.getPisCode(),String.valueOf(1))+"("+dateConverter.convertToNepali(paginatedRequest.getPisCode())+")को आ.ब("+userMgmtMapper.getActiveFiscalYear(0)+") को हाजिरी रिपोर्ट"+"</span><br><br>"+
                        "<div>";




        htmlcheck=htmlcheck.concat(getNepaliHeader());
        String leaveName = "";

        int sn = 0;
        for (EmployeeAttendanceReportDataPojo employeeAttendance : page.getRecords()){

            sn ++;

            htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ dateConverter.convertToNepali(String.valueOf(sn)) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getDateNp()==null?"-":dateConverter.convertBSToDevnagari(employeeAttendance.getDateNp())) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getCheckin()== null ? "-" : dateConverter.convertToNepali(LocalDateTime.ofInstant(new SimpleDateFormat("HH:mm").parse(employeeAttendance.getCheckin().toString()).toInstant(), ZoneId.systemDefault()).toLocalTime().toString())) +"</td>")
//                    .concat("<td role=\"cell\">"+ (employeeAttendance.getCheckin()==null?"-":dateConverter.convertToNepali(employeeAttendance.getCheckin().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getCheckout()== null ? "-" : dateConverter.convertToNepali(LocalDateTime.ofInstant(new SimpleDateFormat("HH:mm").parse(employeeAttendance.getCheckout().toString()).toInstant(), ZoneId.systemDefault()).toLocalTime().toString())) +"</td>")
//                    .concat("<td role=\"cell\">"+ (employeeAttendance.getCheckout()==null?"-":dateConverter.convertToNepali(employeeAttendance.getCheckout().toString()))+"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getLateCheckin() == null ? "-" : dateConverter.convertToNepali(dateConverter.getTimeDifference(employeeAttendance.getShiftCheckin(),employeeAttendance.getCheckin()))) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getEarlyCheckout() == null ? "-" : dateConverter.convertToNepali(dateConverter.getTimeDifference(employeeAttendance.getCheckout(),employeeAttendance.getShiftCheckout()))) +"</td>")
//                    .concat("<td role=\"cell\">"+ (employeeAttendance.getLateCheckin()== null ? "-" : dateConverter.convertToNepali(employeeAttendance.getLateCheckin().toString())) +"</td>")
//                    .concat("<td role=\"cell\">"+ (employeeAttendance.getEarlyCheckout()== null ? "-" : dateConverter.convertToNepali(employeeAttendance.getEarlyCheckout().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getExtraTime() == null ? "-" : dateConverter.convertToNepali(employeeAttendance.getExtraTime().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (employeeAttendance.getAttendanceType() == null ? "-" : employeeAttendance.getAttendanceType().getEnum().getValueNepali()) +"</td>")
                    .concat("</tr>");


        }
        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
        System.out.println(htmlcheck);
        return htmlcheck;
    }


    private List<String> topOfficeDetails(String officeCOde, List<String> value, String type,int count){
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

    private String processEmployeeData(String pisCode,String type) {

            EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(pisCode);
            if (pis != null) {
                if(type.equalsIgnoreCase("0"))
                return StringUtils.capitalize(pis.getEmployeeNameEn());
                else
                return  StringUtils.capitalize(pis.getEmployeeNameNp());
            }
            return "employee";
        }

}