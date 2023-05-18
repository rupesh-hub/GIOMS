package com.gerp.attendance.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.LeaveReportDataPojo;
import com.gerp.attendance.Pojo.OfficePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.LeaveRequestMapper;
import com.gerp.attendance.mapper.UserMgmtMapper;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.StringDataUtils;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class LeaveReportGenerator {

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private UserMgmtMapper userMgmtMapper;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

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
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Employee Name</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Date</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Duration</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Leave Name</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Approver</th>\n" +
                    "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Status</th>\n" +
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
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">S.N</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Darta No.</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">RequestedDate</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">ApprovedDate</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Employee Name</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Date</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Duration</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Leave Name</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Approver</th>\n" +
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
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">क्र.सं.</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">दर्ता नम्बर</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">अनुरोध मिति</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">स्वीकृत मिति</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">कर्मचारीको नाम</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">मिति</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">अवधि</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">विदाको नाम</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">स्वीकृत गर्ने व्यक्ति</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">स्थिति</th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody role=\"rowgroup\">\n";

    }

    public String generateEnglishReport(GetRowsRequest paginatedRequest) {
        paginatedRequest.setLimit(10000);

        Page<LeaveReportDataPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
//        Page<LeaveReportDataPojo> nextpage = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

//        // if fiscal year parameter is not send default will be current fiscal year
        if(paginatedRequest.getFiscalYear()==null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if(paginatedRequest.getOfficeCode()==null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        // check if its for report <value set from controller endpoint>
//        if (paginatedRequest.getForReport()) ;
//        else {
//            // check if its for approver <value set from controller endpoint>
//            if (paginatedRequest.getIsApprover())
//                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
//            else
//                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
//        }

//        page = leaveRequestMapper.filterData(
//                page,
//                paginatedRequest.getFiscalYear(),
//                paginatedRequest.getForReport(),
//                paginatedRequest.getIsApprover(),
//                paginatedRequest.getPisCode(),
//                paginatedRequest.getApproverPisCode(),
//                tokenProcessorService.getOfficeCode(),
//                paginatedRequest.getSearchField()
//        );
        if (paginatedRequest.getForReport()){
            paginatedRequest.setReport("report");
        }
        else {
            // check if its for approver <value set from controller endpoint>
            if (paginatedRequest.getIsApprover())
                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
            else
                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
        }

        page = leaveRequestMapper.filterDataPaginatedLeave(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getYear(),
                paginatedRequest.getReport(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getApproverPisCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getIsApprover(),
                paginatedRequest.getSearchField()
        );
        this.processEmployeeData(page.getRecords());
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
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(1)+"Leave report of "+StringUtils.capitalize(userType)+"Employee</span><br><br>"+
                            "<div>";
        }else {
            htmlcheck =
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">" + userMgmtMapper.getActiveFiscalYear(1) + "Leave report of Employee</span><br><br>" +
                            "<div>";
        }

        htmlcheck=htmlcheck.concat(getEnglishHeader());
        int sn = 0;
        for (LeaveReportDataPojo leaveReportDataPojo : page.getRecords()){
            String leaveName = "";
            if(leaveReportDataPojo.getIsHoliday()){
                leaveName = leaveReportDataPojo.getHolidayNameEn();
            }else
                leaveName = leaveReportDataPojo.getLeaveNameEn();
            sn ++;

            htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ sn +"</td>")
                    .concat("<td role=\"cell\">"+ ((leaveReportDataPojo.getLeaveApproveDartaNo()==null||leaveReportDataPojo.getLeaveApproveDartaNo()==0)?"-":leaveReportDataPojo.getLeaveApproveDartaNo().toString()) +"</td>")
                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getAppliedDateEn()==null?"-":leaveReportDataPojo.getAppliedDateEn().toString()) +"</td>")
                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getApprovedDate()==null?"-":leaveReportDataPojo.getApprovedDate().toString()) +"</td>")
                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getEmployeeNameEn() == null ? "-" : leaveReportDataPojo.getEmployeeNameEn()) +"</td>")
                    .concat("<td role=\"cell\">"+ leaveReportDataPojo.getFromDateNp()+" - "+leaveReportDataPojo.getToDateNp()+"</td>")
                    .concat("<td role=\"cell\">"+ String.valueOf(ChronoUnit.DAYS.between(leaveReportDataPojo.getFromDateEn(), leaveReportDataPojo.getToDateEn())+1)+"</td>")
                    .concat("<td role=\"cell\">"+ (leaveName==null?"-":leaveName) +"</td>")
                    .concat("<td role=\"cell\">"+ (StringDataUtils.getNimitConcated(leaveReportDataPojo.getApprovalDetail().getApproverNameEn(), leaveReportDataPojo.getApprovalDetail().getIsOfficeHead())) +"</td>")
                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getStatus().getEnum().getValueEnglish() == null ? "-" : leaveReportDataPojo.getStatus().getEnum().getValueEnglish()) +"</td>")
                    .concat("</tr>");

            if(leaveReportDataPojo.getTravelDays()!=null){
                sn++;
                htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                        .concat("<td role=\"cell\">"+ sn +"</td>")
                        .concat("<td role=\"cell\">"+ ((leaveReportDataPojo.getLeaveApproveDartaNo()==null || leaveReportDataPojo.getLeaveApproveDartaNo()==0)?"-":leaveReportDataPojo.getLeaveApproveDartaNo().toString()) +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getAppliedDateEn()==null?"-":leaveReportDataPojo.getAppliedDateEn().toString()) +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getApprovedDate()==null?"-":leaveReportDataPojo.getApprovedDate().toString()) +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getEmployeeNameEn() == null ? "-" : leaveReportDataPojo.getEmployeeNameEn()) +"</td>")
                        .concat("<td role=\"cell\">"+ dateConverter.convertAdToBs(leaveReportDataPojo.getFromTravelDays().toString())+" - "+dateConverter.convertAdToBs(leaveReportDataPojo.getToTravelDays().toString())+"</td>")
                        .concat("<td role=\"cell\">"+ String.valueOf(leaveReportDataPojo.getTravelDays())+"</td>")
                        .concat("<td role=\"cell\">"+ ("Baato Myaad") +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getApprovalDetail().getApproverNameEn() == null ? "-" : leaveReportDataPojo.getApprovalDetail().getApproverNameEn()) +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getStatus().getEnum().getValueEnglish() == null ? "-" : leaveReportDataPojo.getStatus().getEnum().getValueEnglish()) +"</td>")
                        .concat("</tr>");


            }


        }
        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
        return htmlcheck;
    }

    public String generateNepaliReport(GetRowsRequest paginatedRequest) {
        paginatedRequest.setLimit(10000);

        Page<LeaveReportDataPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
//        Page<LeaveReportDataPojo> nextpage = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

//        // if fiscal year parameter is not send default will be current fiscal year
        if(paginatedRequest.getFiscalYear()==null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        if(paginatedRequest.getOfficeCode()==null || paginatedRequest.getOfficeCode().equals(0))
            paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        // check if its for report <value set from controller endpoint>
//        if (paginatedRequest.getForReport()) ;
//        else {
//            // check if its for approver <value set from controller endpoint>
//            if (paginatedRequest.getIsApprover())
//                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
//            else
//                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
//        }

//        page = leaveRequestMapper.filterData(
//                page,
//                paginatedRequest.getFiscalYear(),
//                paginatedRequest.getForReport(),
//                paginatedRequest.getIsApprover(),
//                paginatedRequest.getPisCode(),
//                paginatedRequest.getApproverPisCode(),
//                tokenProcessorService.getOfficeCode(),
//                paginatedRequest.getSearchField()
//        );

        if (paginatedRequest.getForReport()){
            paginatedRequest.setReport("report");
        }
        else {
            // check if its for approver <value set from controller endpoint>
            if (paginatedRequest.getIsApprover())
                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
            else
                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
        }

        page = leaveRequestMapper.filterDataPaginatedLeave(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getYear(),
                paginatedRequest.getReport(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getApproverPisCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getIsApprover(),
                paginatedRequest.getSearchField()
        );
        this.processEmployeeData(page.getRecords());
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
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(0)+"को "+StringUtils.capitalize(userType)+"कर्मचारी को विदा रिपोर्ट</span><br><br>"+

                            "<div>";
        }else {
            htmlcheck =
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(0)+"को कर्मचारी को विदा रिपोर्ट</span><br><br>"+

                            "<div>";
        }



        htmlcheck=htmlcheck.concat(getNepaliHeader());
        String leaveName = "";

        int sn = 0;
        for (LeaveReportDataPojo leaveReportDataPojo : page.getRecords()){
            if(leaveReportDataPojo.getIsHoliday()){
                leaveName = leaveReportDataPojo.getHolidayNameNp();
            }else
                leaveName = leaveReportDataPojo.getLeaveNameNp();
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
                    .concat("<td role=\"cell\">"+ dateConverter.convertToNepali(String.valueOf(sn)) +"</td>")
                    .concat("<td role=\"cell\">"+ ((leaveReportDataPojo.getLeaveApproveDartaNo()==null || leaveReportDataPojo.getLeaveApproveDartaNo()==0)?"-":dateConverter.convertToNepali(leaveReportDataPojo.getLeaveApproveDartaNo().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getAppliedDateEn()==null?"-":dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(leaveReportDataPojo.getAppliedDateEn().toString()))) +"</td>")
                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getApprovedDate()==null?"-":dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(leaveReportDataPojo.getApprovedDate().toString()))) +"</td>")
                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getEmployeeNameNp() == null ? null : leaveReportDataPojo.getEmployeeNameNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ dateConverter.convertBSToDevnagari(leaveReportDataPojo.getFromDateNp())+" - "+dateConverter.convertBSToDevnagari(leaveReportDataPojo.getToDateNp())+"</td>")
                    .concat("<td role=\"cell\">"+ dateConverter.convertToNepali(String.valueOf(ChronoUnit.DAYS.between(leaveReportDataPojo.getFromDateEn(), leaveReportDataPojo.getToDateEn())+1))+"</td>")
                    .concat("<td role=\"cell\">"+ (leaveName==null?"-":leaveName) +"</td>")
                    .concat("<td role=\"cell\">"+ (StringDataUtils.getNimitConcated(leaveReportDataPojo.getApprovalDetail().getApproverNameNp(), leaveReportDataPojo.getApprovalDetail().getIsOfficeHead())) +"</td>")
                    .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getStatus().getEnum().getValueNepali() == null ? "-" : leaveReportDataPojo.getStatus().getEnum().getValueNepali()) +"</td>")
                    .concat("</tr>");
            if(leaveReportDataPojo.getTravelDays()!=null){
                sn++;
                String fromTravelDaysNp=dateConverter.convertAdToBs(leaveReportDataPojo.getFromTravelDays().toString());
                String toTravelDaysNp=dateConverter.convertAdToBs(leaveReportDataPojo.getToTravelDays().toString());

                htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                        .concat("<td role=\"cell\">"+ dateConverter.convertToNepali(String.valueOf(sn)) +"</td>")
                        .concat("<td role=\"cell\">"+ ((leaveReportDataPojo.getLeaveApproveDartaNo()==null || leaveReportDataPojo.getLeaveApproveDartaNo()==0)?"-":dateConverter.convertToNepali(leaveReportDataPojo.getLeaveApproveDartaNo().toString())) +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getAppliedDateEn()==null?"-":dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(leaveReportDataPojo.getAppliedDateEn().toString()))) +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getApprovedDate()==null?"-":dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(leaveReportDataPojo.getApprovedDate().toString()))) +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getEmployeeNameNp() == null ? null : leaveReportDataPojo.getEmployeeNameNp()) +"</td>")
                        .concat("<td role=\"cell\">"+ dateConverter.convertBSToDevnagari(fromTravelDaysNp)+" - "+dateConverter.convertBSToDevnagari(toTravelDaysNp)+"</td>")
                        .concat("<td role=\"cell\">"+ dateConverter.convertToNepali(leaveReportDataPojo.getTravelDays().toString())+"</td>")
                        .concat("<td role=\"cell\">"+ ("बाटो म्याद") +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getApprovalDetail().getApproverNameNp() == null ? "-" : leaveReportDataPojo.getApprovalDetail().getApproverNameNp()) +"</td>")
                        .concat("<td role=\"cell\">"+ (leaveReportDataPojo.getStatus().getEnum().getValueNepali() == null ? "-" : leaveReportDataPojo.getStatus().getEnum().getValueNepali()) +"</td>")
                        .concat("</tr>");

            }


        }
        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
        System.out.println(htmlcheck);
        return htmlcheck;
    }

    private List<LeaveReportDataPojo> processEmployeeData(List<LeaveReportDataPojo> records) {
        records.forEach(x -> {
            if (x.getPisCode() != null && !x.getPisCode().equals("")) {
                EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
                if (pis != null) {
                    x.setEmployeeNameEn(StringUtils.capitalize(pis.getEmployeeNameEn()));
                    x.setEmployeeNameNp(StringUtils.capitalize(pis.getEmployeeNameNp()));
                }
            }
            if(x.getApprovalDetail()!=null) {
                if (x.getApprovalDetail().getApproverPisCode() != null && !x.getApprovalDetail().getApproverPisCode().equals("")) {
                    EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getApprovalDetail().getApproverPisCode());
                    if (minimalPojo != null) {
                        x.getApprovalDetail().setApproverNameEn(StringUtils.capitalize(minimalPojo.getEmployeeNameEn()));
                        x.getApprovalDetail().setApproverNameNp(StringUtils.capitalize(minimalPojo.getEmployeeNameNp()));
                        x.getApprovalDetail().setDesignationEn(StringUtils.capitalize(minimalPojo.getFunctionalDesignation().getName()));
                        x.getApprovalDetail().setDesignationNp(StringUtils.capitalize(minimalPojo.getFunctionalDesignation().getNameN()));
                    }

                }
            }
        });
        return records;
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