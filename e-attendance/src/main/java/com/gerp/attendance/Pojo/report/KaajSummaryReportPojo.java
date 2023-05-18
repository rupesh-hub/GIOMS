package com.gerp.attendance.Pojo.report;

import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.KaajRequestMapper;
import com.gerp.attendance.mapper.UserMgmtMapper;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//import com.gerp.shared.utils.StringUtils;

@Component
public class KaajSummaryReportPojo {

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




    public String generateReport(GetRowsRequest paginatedRequest, int type) throws ParseException {
        Integer offset=0;
        Integer limit=0;
        if(paginatedRequest.getLimit()==0){
            limit=10;
        }else{
            limit=paginatedRequest.getLimit();
        }
        if(type == 0){
            return generateEnglishReport(paginatedRequest,limit,offset);
        }
        else
        {
            return generateNepaliReport(paginatedRequest,limit,offset);
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

    public String generateEnglishReport(GetRowsRequest paginatedRequest,Integer limit,Integer offset) throws ParseException {

        List<KaajSummaryPojo> kaajSummaryPojos=kaajRequestMapper.getKaajSummaryData(tokenProcessorService.getOfficeCode(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getFromDate()==null?null:dateConverter.convertStringToDate(paginatedRequest.getFromDate().toString(),"yyyy-MM-dd"),
                paginatedRequest.getToDate()==null?null:dateConverter.convertStringToDate(paginatedRequest.getToDate().toString(),"yyyy-MM-dd"),
                paginatedRequest.getPage()==1?offset:(limit*(paginatedRequest.getPage()-1)),
                limit);

//        this.processEmployeeData(page.getRecords());

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
                            "<span style=\"font-size:14px\">"+userMgmtMapper.getActiveFiscalYear(1)+"kaaj summary report of "+"Employee</span><br><br>"+
                            "<div>";
        }else {
            htmlcheck =
                    htmlcheck +
//                        "<h3>"+ topOfficeDetails.get(topOfficeDetails.size()-1) +"</h3>\n" +
                            "<span style=\"font-size:14px\">" + userMgmtMapper.getActiveFiscalYear(1) + "kaaj summary report of Employee</span><br><br>" +
                            "<div>";
        }

        htmlcheck=htmlcheck.concat(getEnglishHeader());


        int sn = 0;
        for (KaajSummaryPojo kaajRequestCustomPojo : kaajSummaryPojos){
            AtomicReference<String> chekemployeeName=null;

            sn ++;
            htmlcheck = htmlcheck
                    .concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ sn +"</td>")
                    .concat("<td role=\"cell\">"+ ((kaajRequestCustomPojo.getKaajApprovedDartaNo()==null || kaajRequestCustomPojo.getKaajApprovedDartaNo()==0)?"-":kaajRequestCustomPojo.getKaajApprovedDartaNo().toString()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getEmpNameEn()==null?"-":kaajRequestCustomPojo.getEmpNameEn()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getDesignationNameEn()==null?"-":kaajRequestCustomPojo.getDesignationNameEn()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getLocation()==null ? "-" : kaajRequestCustomPojo.getLocation())+"</td>")
//                    .concat("<td role=\"cell\">"+ kaajRequestCustomPojo.getFromDateNp()+" - "+kaajRequestCustomPojo.getToDateNp()+"</td>")
                    .concat("<td role=\"cell\">"+ kaajRequestCustomPojo.getAdvancedAmountTravel()==null? "-": kaajRequestCustomPojo.getAdvancedAmountTravel()+"</td>")
                    .concat("<td role=\"cell\">"+ kaajRequestCustomPojo.getFromDateNp()==null? "-": kaajRequestCustomPojo.getFromDateNp()+"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getToDateNp() == null ? "-" : kaajRequestCustomPojo.getToDateNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getTotalDays() == null ? "-" : kaajRequestCustomPojo.getTotalDays()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getRemarksRegardingTravel() == null ? "-" : kaajRequestCustomPojo.getRemarksRegardingTravel()) +"</td>").concat("</tr>");

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

    public String generateNepaliReport(GetRowsRequest paginatedRequest,Integer limit,Integer offset) throws ParseException {
        List<KaajSummaryPojo> kaajSummaryPojos=kaajRequestMapper.getKaajSummaryData(tokenProcessorService.getOfficeCode(),
                paginatedRequest.getPisCode(),
                paginatedRequest.getFromDate()==null?null:dateConverter.convertStringToDate(paginatedRequest.getFromDate().toString(),"yyyy-MM-dd"),
                paginatedRequest.getToDate()==null?null:dateConverter.convertStringToDate(paginatedRequest.getToDate().toString(),"yyyy-MM-dd"),
                paginatedRequest.getPage()==1?offset:(limit*(paginatedRequest.getPage()-1)),
                limit);

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
        for (KaajSummaryPojo kaajRequestCustomPojo : kaajSummaryPojos){

            sn ++;

            htmlcheck = htmlcheck.concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ dateConverter.convertToNepali(String.valueOf(sn)) +"</td>")
                    .concat("<td role=\"cell\">"+ ((kaajRequestCustomPojo.getKaajApprovedDartaNo()==null ||kaajRequestCustomPojo.getKaajApprovedDartaNo()==0)?"-":dateConverter.convertToNepali(kaajRequestCustomPojo.getKaajApprovedDartaNo().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getEmpNameNp()==null?"-":kaajRequestCustomPojo.getEmpNameNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getDesignationNameNp()==null?"-":kaajRequestCustomPojo.getDesignationNameNp()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getLocation()==null?"-":kaajRequestCustomPojo.getLocation()) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getAdvancedAmountTravel()==null?"-":dateConverter.convertToNepali(kaajRequestCustomPojo.getAdvancedAmountTravel())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getFromDateNp() == null ? "-" : dateConverter.convertBSToDevnagari(kaajRequestCustomPojo.getFromDateNp())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getToDateNp() == null ? "-" : dateConverter.convertBSToDevnagari(kaajRequestCustomPojo.getToDateNp())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getTotalDays() == null ? "-" : dateConverter.convertBSToDevnagari(kaajRequestCustomPojo.getTotalDays().toString())) +"</td>")
                    .concat("<td role=\"cell\">"+ (kaajRequestCustomPojo.getRemarksRegardingTravel() == null ? "-" : kaajRequestCustomPojo.getRemarksRegardingTravel()) +"</td>")
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