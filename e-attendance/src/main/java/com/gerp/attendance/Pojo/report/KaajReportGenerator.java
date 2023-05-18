package com.gerp.attendance.Pojo.report;

import com.gerp.attendance.Pojo.EmployeeMinimalDetailsPojo;
import com.gerp.attendance.Pojo.EmployeeDetailPojo;
import com.gerp.attendance.Pojo.KaajRequestCustomPojo;
import com.gerp.attendance.Pojo.OfficePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.KaajRequestMapper;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//import com.gerp.shared.utils.StringUtils;

@Component
public class KaajReportGenerator {

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

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
            "<title>Kaaj Report</title>\n" +
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
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Requested Date</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Approved Date</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Employee Name</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Date</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Duration</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Kaaj Type</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Location</th>\n" +
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
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">काज प्रकार</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">ठेगाना</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">स्वीकृत गर्ने व्यक्ति</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">स्थिति</th>\n" +

                "</tr>\n" +
                "</thead>\n" +
                "<tbody role=\"rowgroup\">\n";

    }

    public String generateEnglishReport(GetRowsRequest paginatedRequest) {
        paginatedRequest.setLimit(10000);
        EmployeeAttendancePage<KaajRequestCustomPojo> page = new EmployeeAttendancePage<>(paginatedRequest.getPage(), paginatedRequest.getLimit());

//        EmployeeAttendancePage<KaajRequestCustomPojo> nextpage = new EmployeeAttendancePage<>(paginatedRequest.getPage(), paginatedRequest.getLimit());

//        // if fiscal year parameter is not send default will be current fiscal year
        if(paginatedRequest.getFiscalYear()==null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        // check if its for report <value set from controller endpoint>
//        if (paginatedRequest.getForReport()) ;
//        else {
//            // check if its for approver <value set from controller endpoint>
//            if (paginatedRequest.getIsApprover())
//                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
//            else
//                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
//        }
        // check if its for report <value set from controller endpoint>
        if (paginatedRequest.getForReport()){
            paginatedRequest.setReport("report");
            if(paginatedRequest.getPisCode().equalsIgnoreCase("")){
                paginatedRequest.setPisCode(null);
            }
        }
        else {
            // check if its for approver <value set from controller endpoint>
            if (paginatedRequest.getIsApprover()) {
                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
//                if(paginatedRequest.getPisCode().equalsIgnoreCase("")){
//                    paginatedRequest.setPisCode(null);
//                }
            }
            else
                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
        }

        page = kaajRequestMapper.kaajFilter(page,
                paginatedRequest.getFiscalYear(),
                paginatedRequest.getReport(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getApproverPisCode(),
                tokenProcessorService.getOfficeCode(),
                paginatedRequest.getIsApprover(),
                paginatedRequest.getSearchField()
        );

        this.processEmployeeData(page.getRecords());

        String html = "";
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
                                "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(1)+"kaaj report of "+StringUtils.capitalize(userType)+"Employee</span><br><br>"+
                                "<div>";
            }else {
                htmlcheck =
                        htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                                "<span style=\"font-size:14px\">" + userMgmtMapper.getActiveFiscalYear(1) + "kaaj report of Employee</span><br><br>" +
                                "<div>";
            }

           htmlcheck=htmlcheck.concat(getEnglishHeader());


        int sn = 0;
        for (KaajRequestCustomPojo kaajRequestCustomPojo : page.getRecords()){
            AtomicReference<String> chekemployeeName=null;

            sn ++;
            htmlcheck = htmlcheck
                    .concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ sn +"</td>")
                    .concat("<td role=\"cell\">"+ ((kaajRequestCustomPojo.getKaajApproveDartaNo()==null || kaajRequestCustomPojo.getKaajApproveDartaNo()==0)?"-":kaajRequestCustomPojo.getKaajApproveDartaNo().toString()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getCreatedDate()==null?"-":dateConverter.convertAdToBs(kaajRequestCustomPojo.getCreatedDate().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getApproveDate()==null?"-":dateConverter.convertAdToBs(kaajRequestCustomPojo.getApproveDate().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getEmployeeMinimalDetailsPojo()==null ? "-" : this.employeeName(kaajRequestCustomPojo.getPisCodesDetail(), kaajRequestCustomPojo,String.valueOf(0)))+"</td>")
//                    .concat("<td role=\"cell\">"+ kaajRequestCustomPojo.getFromDateNp()+" - "+kaajRequestCustomPojo.getToDateNp()+"</td>")
                    .concat("<td role=\"cell\">"+ this.checkForKaajAppliedOthers(kaajRequestCustomPojo,String.valueOf(0))+"</td>")
                    .concat("<td role=\"cell\">"+ String.valueOf(kaajRequestCustomPojo.getAppliedForOthers()?this.checkDurationForKaajOthers(kaajRequestCustomPojo,String.valueOf(0)):ChronoUnit.DAYS.between(kaajRequestCustomPojo.getFromDateEn(), kaajRequestCustomPojo.getToDateEn())+1)+"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getKaajTypeName() == null ? "-" : kaajRequestCustomPojo.getKaajTypeName()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getLocation() == null ? "-" : kaajRequestCustomPojo.getLocation()) +"</td>")
                    .concat("<td role=\"cell\">"+ (StringDataUtils.getNimitConcated(kaajRequestCustomPojo.getApprovalDetail().getApproverNameEn(), kaajRequestCustomPojo.getApprovalDetail().getIsOfficeHead())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getStatus().getValueEnglish() == null ? "-" : kaajRequestCustomPojo.getStatus().getValueEnglish()) +"</td>").concat("</tr>");





        }
        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
        return htmlcheck;
    }

    public String getNimitConcated(String name  , boolean officeHead) {
        if(name == null ) {
        return  "-"; } else {
            return officeHead ? name + "(निमित्त)" : name;
        }
    }

    public String generateNepaliReport(GetRowsRequest paginatedRequest) {
        paginatedRequest.setLimit(10000);

        EmployeeAttendancePage<KaajRequestCustomPojo> page = new EmployeeAttendancePage<>(paginatedRequest.getPage(), paginatedRequest.getLimit());

//        EmployeeAttendancePage<KaajRequestCustomPojo> nextpage = new EmployeeAttendancePage<>(paginatedRequest.getPage(), paginatedRequest.getLimit());

//        // if fiscal year parameter is not send default will be current fiscal year
        if(paginatedRequest.getFiscalYear()==null || paginatedRequest.getFiscalYear().equals(0))
            paginatedRequest.setFiscalYear(userMgmtServiceData.findActiveFiscalYear().getId());

        // check if its for report <value set from controller endpoint>
//        if (paginatedRequest.getForReport()) ;
//        else {
//            // check if its for approver <value set from controller endpoint>
//            if (paginatedRequest.getIsApprover())
//                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
//            else
//                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
//        }
        if (paginatedRequest.getForReport()){
            paginatedRequest.setReport("report");
            if(paginatedRequest.getPisCode().equalsIgnoreCase("")){
                paginatedRequest.setPisCode(null);
            }
        }
        else {
            // check if its for approver <value set from controller endpoint>
            if (paginatedRequest.getIsApprover()) {
                paginatedRequest.setApproverPisCode(tokenProcessorService.getPisCode());
//                if(paginatedRequest.getPisCode().equalsIgnoreCase("")){
//                    paginatedRequest.setPisCode(null);
//                }
            }
            else
                paginatedRequest.setPisCode(tokenProcessorService.getPisCode());
        }

        page = kaajRequestMapper.kaajFilter(
                page,
                paginatedRequest.getFiscalYear(),
                paginatedRequest.getReport(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getApproverPisCode(),
                tokenProcessorService.getOfficeCode(),
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
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(0)+" को"+StringUtils.capitalize(userType)+" कर्मचारीको काज रिपोर्ट</span><br><br>"+

                            "<div>";
        }else {
            htmlcheck =
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(0)+" को कर्मचारीको काज रिपोर्ट</span><br><br>"+

                            "<div>";
        }



        htmlcheck=htmlcheck.concat(getNepaliHeader());

//        html = html.concat(getNepaliHeader(officeHierarchyPojo.getNameNp(),userMgmtMapper.getActiveFiscalYear(0)));
        String leaveName = "";

        int sn = 0;
        for (KaajRequestCustomPojo kaajRequestCustomPojo : page.getRecords()){

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
                    .concat("<td role=\"cell\">"+ ((kaajRequestCustomPojo.getKaajApproveDartaNo()==null ||kaajRequestCustomPojo.getKaajApproveDartaNo()==0)?"-":dateConverter.convertToNepali(kaajRequestCustomPojo.getKaajApproveDartaNo().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (dateConverter.convertAdToBs(kaajRequestCustomPojo.getCreatedDate().toString())==null?"-": dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(kaajRequestCustomPojo.getCreatedDate().toString()))) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getApproveDate()==null?"-":dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(kaajRequestCustomPojo.getApproveDate().toString()))) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getEmployeeMinimalDetailsPojo()==null ? "-" : this.employeeName(kaajRequestCustomPojo.getPisCodesDetail(), kaajRequestCustomPojo,String.valueOf(1)))+"</td>")
//                    .concat("<td role=\"cell\">"+ dateConverter.convertBSToDevnagari(kaajRequestCustomPojo.getFromDateNp())+" - "+dateConverter.convertBSToDevnagari(kaajRequestCustomPojo.getToDateNp())+"</td>")
                    .concat("<td role=\"cell\">"+ this.checkForKaajAppliedOthers(kaajRequestCustomPojo,String.valueOf(1))+"</td>")
                    .concat("<td role=\"cell\">"+ String.valueOf(kaajRequestCustomPojo.getAppliedForOthers()?this.checkDurationForKaajOthers(kaajRequestCustomPojo,String.valueOf(1)):dateConverter.convertBSToDevnagari(String.valueOf(ChronoUnit.DAYS.between(kaajRequestCustomPojo.getFromDateEn(), kaajRequestCustomPojo.getToDateEn())+1)))+"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getKaajTypeNameNp() == null ? "-" : kaajRequestCustomPojo.getKaajTypeNameNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getLocation() == null ? "-" : kaajRequestCustomPojo.getLocation()) +"</td>")
                    .concat("<td role=\"cell\">"+ (StringDataUtils.getNimitConcated(kaajRequestCustomPojo.getApprovalDetail().getApproverNameNp(), kaajRequestCustomPojo.getApprovalDetail().getIsOfficeHead())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getStatus().getEnum().getValueNepali() == null ? "-" : kaajRequestCustomPojo.getStatus().getEnum().getValueNepali()) +"</td>")
                    .concat("</tr>");


        }
        htmlcheck = htmlcheck.concat(pdfNepaliFooter);
        System.out.println(htmlcheck);
        return htmlcheck;
    }

    private List<KaajRequestCustomPojo> processEmployeeData(List<KaajRequestCustomPojo> records) {
        records.forEach(x -> {
            this.processEmployeeData(x);
        });
        return records;
    }

    private KaajRequestCustomPojo processEmployeeData(KaajRequestCustomPojo x) {
//        if (x.getPisCode() != null && !x.getPisCode().equals("")) {
//            EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
//            if (pis != null) {
//                x.setPisNameEn(StringUtils.capitalize(pis.getEmployeeNameEn()));
//                x.setPisNameNp(StringUtils.capitalize(pis.getEmployeeNameNp()));
//            }
//        }
        if(!x.getPisCodesDetail().isEmpty()){
            List<EmployeeMinimalDetailsPojo>employeeMinimalDetailsPojoList=new ArrayList<>();
            x.getPisCodesDetail().stream().forEach(z->{
                EmployeeMinimalDetailsPojo employeeMinimalDetailsPojos=new EmployeeMinimalDetailsPojo();
                EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(z.getPisCode());
               if(pis!=null) {
                   employeeMinimalDetailsPojos.setEmployeeNameEn(pis.getEmployeeNameEn()==null?"-":pis.getEmployeeNameEn());
                   employeeMinimalDetailsPojos.setEmployeeNameNp(pis.getEmployeeNameNp()==null?"-":pis.getEmployeeNameNp());
               }
                employeeMinimalDetailsPojoList.add(employeeMinimalDetailsPojos);
            });
            x.setEmployeeMinimalDetailsPojo(employeeMinimalDetailsPojoList);
        }
        if(x.getApprovalDetail()!=null) {
            if (x.getApprovalDetail().getApproverPisCode() != null) {
                EmployeeMinimalPojo approver = userMgmtServiceData.getEmployeeDetailMinimal(x.getApprovalDetail().getApproverPisCode());
                if (approver != null) {
                    x.getApprovalDetail().setApproverNameEn(StringUtils.capitalize(approver.getEmployeeNameEn()));
                    x.getApprovalDetail().setApproverNameNp(StringUtils.capitalize(approver.getEmployeeNameNp()));
                }

            }
        }
        return x;
    }

    private String  employeeName(List<EmployeeDetailPojo> employeeMinimalDetailsPojoList, KaajRequestCustomPojo kaajRequestCustomPojo, String type) {
        AtomicReference<String> checkEmployeeName=new AtomicReference<>();
       StringBuffer employeeList=new StringBuffer();
        AtomicInteger count= new AtomicInteger(0);
        if(!kaajRequestCustomPojo.getAppliedForOthers()) {
            employeeMinimalDetailsPojoList.stream().forEach(y -> {
                    if (type.equalsIgnoreCase("0")) {

                        checkEmployeeName.set((count.get() > 0 ? StringUtils.capitalize(y.getEmployeeNameEn()).concat(",") : y.getEmployeeNameEn()));
                    } if (type.equalsIgnoreCase("1")) {
                        checkEmployeeName.set(count.get() > 0 ? y.getEmployeeNameNp().concat(",") : y.getEmployeeNameNp());

                    }
                count.getAndIncrement();
            });
        }else {
            AtomicInteger counting= new AtomicInteger(1);
            kaajRequestCustomPojo.getKaajAppliedOthersPojo().forEach(x->{
                x.getKaajEmployeeDetail().stream().forEach(z->{
                    if(type.equalsIgnoreCase("0")) {
                        employeeList.append(StringUtils.capitalize(z.getEmployeeNameEn())).append("<br>");
//                        if(counting.intValue()>1){
//                            x.getAppliedDateList().forEach(s->{
//                                employeeList.append(("<td role=\"cell\">"+ dateConverter.convertToNepali(String.valueOf(ChronoUnit.DAYS.between(s.getFromDateEn(), s.getToDateEn())+1))+"</td>")).append("<br>");
//
//                            });
//
//
//                        }
                    }else if (type.equalsIgnoreCase("1")) {
                        employeeList.append(StringUtils.capitalize(z.getEmployeeNameNp())).append("<br>");
//                        if(counting.intValue()>1){
//                            x.getAppliedDateList().forEach(s->{
//                                employeeList.append(("<td role=\"cell\">"+ dateConverter.convertToNepali(String.valueOf(ChronoUnit.DAYS.between(s.getFromDateEn(), s.getToDateEn())+1))+"</td>")).append("<br>");
//
//                            });
//
//                        }
                    }
                });


//                counting.getAndIncrement();

            });
            checkEmployeeName.set(employeeList.toString());

        }
        return checkEmployeeName.get();
    }


//    private String  employeeName(EmployeeMinimalDetailsPojo employeeMinimalDetailsPojoList,String type) {
//        AtomicReference<String> checkEmployeeName=new AtomicReference<>();
//
//                if (type.equalsIgnoreCase("0")) {
//
//                    checkEmployeeName.set(employeeMinimalDetailsPojoList.getEmployeeNameEn());
//                } else {
//                    checkEmployeeName.set(employeeMinimalDetailsPojoList.getEmployeeNameNp());
//
//                }
//
//        return checkEmployeeName.get();
//    }

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


    private String checkForKaajAppliedOthers(KaajRequestCustomPojo kaajRequestCustomPojo,String type){
        String dateList="";
        AtomicReference<String> dateNew= new AtomicReference<>("");
        StringBuffer dateCheck=new StringBuffer();
        if(kaajRequestCustomPojo.getAppliedForOthers()){
            if(!kaajRequestCustomPojo.getKaajAppliedOthersPojo().isEmpty()){
                kaajRequestCustomPojo.getKaajAppliedOthersPojo().forEach(x->{
                    AtomicInteger count= new AtomicInteger(1);
                    x.getAppliedDateList().forEach(z->{

                       if(x.getAppliedDateList().size()> count.get()){
                           if(type.equalsIgnoreCase("0")) {
                               dateCheck.append(dateList.concat(z.getFromDateNp()).concat("-").concat(z.getToDateNp()).concat("\n"));
                           }else{
                               dateCheck.append(dateList.concat(dateConverter.convertBSToDevnagari(z.getFromDateNp())).concat("-").concat(dateConverter.convertBSToDevnagari(z.getToDateNp())).concat("\n"));

                           }
                       }else{
                           if(type.equalsIgnoreCase("0")){
                               dateCheck.append(dateList.concat(z.getFromDateNp()).concat("-").concat(z.getToDateNp()).concat("\n"));

                           }else{
                               dateCheck.append(dateList.concat(dateConverter.convertBSToDevnagari(z.getFromDateNp())).concat("-").concat(dateConverter.convertBSToDevnagari(z.getToDateNp())).concat("\n"));

                           }

                       }
                       count.getAndIncrement();

                    });

                });
            }
        }else{
            if(type.equalsIgnoreCase("0")) {
                dateCheck.append(dateList.concat(kaajRequestCustomPojo.getFromDateNp()).concat("-").concat(kaajRequestCustomPojo.getToDateNp()));
            }else{
                dateCheck.append(dateList.concat(dateConverter.convertBSToDevnagari(kaajRequestCustomPojo.getFromDateNp())).concat("-").concat(dateConverter.convertBSToDevnagari(kaajRequestCustomPojo.getToDateNp())));
            }

        }
        return dateCheck.toString();


    }

    private String checkDurationForKaajOthers(KaajRequestCustomPojo kaajRequestCustomPojo,String type){
        String dateList="";
        AtomicReference<String> dateNew= new AtomicReference<>("");
        StringBuffer dateCheck=new StringBuffer();
        if(kaajRequestCustomPojo.getAppliedForOthers()){
            if(!kaajRequestCustomPojo.getKaajAppliedOthersPojo().isEmpty()){
                kaajRequestCustomPojo.getKaajAppliedOthersPojo().forEach(x->{
                    AtomicInteger count= new AtomicInteger(1);
                    x.getAppliedDateList().forEach(z->{

                        if(x.getAppliedDateList().size()> count.get()){
                            if(type.equalsIgnoreCase("0")) {

                                dateCheck.append(dateList.concat(String.valueOf(ChronoUnit.DAYS.between(z.getFromDateEn(), z.getToDateEn()) + 1))).append("<br>");
                            }if(type.equalsIgnoreCase("1")){
                                dateCheck.append(dateList.concat(dateConverter.convertToNepali(String.valueOf(ChronoUnit.DAYS.between(z.getFromDateEn(), z.getToDateEn()) + 1)))).append("<br>");
                            }
                        }else{
                            if(type.equalsIgnoreCase("0")) {
                                dateCheck.append(dateList.concat(String.valueOf(ChronoUnit.DAYS.between(z.getFromDateEn(), z.getToDateEn()) + 1))).append("<br>");
                            }if(type.equalsIgnoreCase("1")){
                                dateCheck.append(dateList.concat(dateConverter.convertToNepali(String.valueOf(ChronoUnit.DAYS.between(z.getFromDateEn(), z.getToDateEn()) + 1)))).append("<br>");

                            }

                        }
                        count.getAndIncrement();

                    });

                });
            }
        }else{
            if(type.equalsIgnoreCase("0")){
                dateCheck.append(dateList.concat(String.valueOf(ChronoUnit.DAYS.between(kaajRequestCustomPojo.getFromDateEn(), kaajRequestCustomPojo.getToDateEn()) + 1)));
            }if(type.equalsIgnoreCase("1")){
                dateCheck.append(dateList.concat(dateConverter.convertBSToDevnagari(String.valueOf(ChronoUnit.DAYS.between(kaajRequestCustomPojo.getFromDateEn(), kaajRequestCustomPojo.getToDateEn()) + 1))));
            }

        }
        return dateCheck.toString();


    }
}