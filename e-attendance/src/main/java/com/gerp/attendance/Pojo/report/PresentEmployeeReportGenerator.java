package com.gerp.attendance.Pojo.report;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.DailyInformationPojo;
import com.gerp.attendance.Pojo.OfficePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.mapper.KaajRequestMapper;
import com.gerp.attendance.mapper.UserMgmtMapper;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PresentEmployeeReportGenerator {
    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

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




    public String generateReport(GetRowsRequest paginatedRequest, int type) {
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
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Date</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Section</th>\n" +
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
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">क.सं. नं.</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">कर्मचारीको नाम</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">पद</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">मिति</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">महाशाखा/शाखा/इकाइ</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">स्थिति</th>\n" +

                "</tr>\n" +
                "</thead>\n" +
                "<tbody role=\"rowgroup\">\n";

    }

    public String generateEnglishReport(GetRowsRequest paginatedRequest) {
        paginatedRequest.setLimit(10000);

        Page<DailyInformationPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if(paginatedRequest.getDate()==null || paginatedRequest.getDate().equals(0))
            paginatedRequest.setDate(LocalDate.now());

        if(paginatedRequest.getFiscalYear()==null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if(paginatedRequest.getOfficeCode()==null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

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

        page = employeeAttendanceMapper.getPresentData(
                page,
                paginatedRequest.getFiscalYear().toString(),
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getDate(),
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
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(1)+"Present report of "+ StringUtils.capitalize(userType)+"Employee</span><br><br>"+
                            "<div>";
        }else {
            htmlcheck =
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">" + userMgmtMapper.getActiveFiscalYear(1) + "Present report of Employee</span><br><br>" +
                            "<div>";
        }

        htmlcheck=htmlcheck.concat(getEnglishHeader());
        int sn = 0;
        for (DailyInformationPojo dailyInformationPojo : page.getRecords()){

            sn ++;
            htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getPisCode()==null?"-":dailyInformationPojo.getPisCode()) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getEmpNameEn()==null?"-":dailyInformationPojo.getEmpNameEn()) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getFdNameEn()==null?"-":dailyInformationPojo.getFdNameEn()) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getDateNp() == null ? "-" : dailyInformationPojo.getDateNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getSectionNameEn()==null?"-":dailyInformationPojo.getSectionNameEn())+"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getStatus() == null ? "-" : dailyInformationPojo.getStatus().getEnum().getValueEnglish()) +"</td>")
                    .concat("</tr>");
        }
        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
        return htmlcheck;
    }

    public String generateNepaliReport(GetRowsRequest paginatedRequest) {
        paginatedRequest.setLimit(10000);

        Page<DailyInformationPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        if(paginatedRequest.getDate()==null || paginatedRequest.getDate().equals(0))
            paginatedRequest.setDate(LocalDate.now());

        if(paginatedRequest.getFiscalYear()==null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

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

        page = employeeAttendanceMapper.getPresentData(
                page,
                paginatedRequest.getFiscalYear().toString(),
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getDate(),
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
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(0)+"को"+StringUtils.capitalize(userType)+" कर्मचारीको उपस्थित रिपोर्ट</span><br><br>"+

                            "<div>";
        }else {
            htmlcheck =
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(0)+" को कर्मचारीको उपस्थित रिपोर्ट</span><br><br>"+

                            "<div>";
        }



        htmlcheck=htmlcheck.concat(getNepaliHeader());
        String leaveName = "";

        int sn = 0;
        for (DailyInformationPojo dailyInformationPojo : page.getRecords()){

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
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getPisCode()==null?"-":dateConverter.convertToNepali(dailyInformationPojo.getPisCode())) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getEmpNameNp()==null?"-":dailyInformationPojo.getEmpNameNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getFdNameNp()==null?"-":dailyInformationPojo.getFdNameNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getDateEn().toString() == null ? "-" : dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(dailyInformationPojo.getDateEn().toString()))) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getSectionNameNp()==null?"-":dailyInformationPojo.getSectionNameNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (dailyInformationPojo.getStatus() == null ? "-" : dailyInformationPojo.getStatus().getEnum().getValueNepali()) +"</td>")
                    .concat("</tr>");


        }

        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
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
}
