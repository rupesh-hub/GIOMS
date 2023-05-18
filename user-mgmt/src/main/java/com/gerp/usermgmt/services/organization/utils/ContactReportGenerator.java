package com.gerp.usermgmt.services.organization.utils;

import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.StringDataUtils;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.mapper.organization.OfficeMapper;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
// todo
//change all this to spring template engine


@Component
public class ContactReportGenerator {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private OfficeMapper officeMapper;
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
            "  color: white;\n" +
            "}\n" +
            "</style>";

    private static final String htmlHeader = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Contact Report</title>\n" +
            styles +
            "</head>\n" +
            "<body>";

    private static final String htmlFooter = "</body>\n" +
            "</html>";

    private static final String pdfEnglishHeader =
            htmlHeader +
            "<div>\n" +
            "<div>\n" +
            "<table role=\"table\" id=\"customers\">" +
            "<thead>\n" +
            "<tr role=\"row\">\n" +
            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">S.N</th>\n" +
            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Employee Name</th>\n" +
            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Designation</th>\n" +
            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Section/Sub Section</th>\n" +
            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Phone Number</th>\n" +
            "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">PABX Code</th>\n" +
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
            "</div>\n" +
            "</div>" + htmlFooter;




    public String generateReport(Map<String, Object> filter , int type) {
        if(type == 0){
            return generateEnglishReport(filter);
        } else {
           return generateNepaliReport(filter);
        }
    }

    String getEnglishHeader(String officeName, String officeLocation) {
       return   htmlHeader +
               "<div>\n" +
               "<div style=\"text-align:center;\">\n" +
               "<div>\n" +
               "<h3>"+ officeName +"</h1>\n" +
               "<h5><u>Contact details of working employees\n</u> <h5/>\n" +
               "</div>" +
               "<table role=\"table\" id=\"customers\">" +
               "<thead>\n" +
               "<tr role=\"row\">\n" +
               "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">S.N</th>\n" +
               "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Employee Name</th>\n" +
               "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Designation</th>\n" +
               "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Section/Sub Section</th>\n" +
               "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">Phone Number</th>\n" +
               "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">PABX Code</th>\n" +
               "</tr>\n" +
               "</thead>\n" +
               "<tbody role=\"rowgroup\">\n";
    }


//               "<h3>"+ officeAddress +"</h3>\n" +
    String getNepaliHeader(String officeName,String officeAddress) {
       return htmlHeader +
                "<div>\n" +
                "<div style=\"text-align:center;\">\n" +
                "<div>\n" +
                "<h1>"+ officeName +"</h1>\n" +
                "<h5><u>कार्यरत कर्मचारीहरुको सम्पर्क विवरण</u> <h5/>\n" +
                "</div>" +
                "<table role=\"table\" id=\"customers\">" +
                "<thead>\n" +
                "<tr role=\"row\">\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">क्र.सं</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">कर्मचारीको नाम</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">पद</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">शाखा/महाशाखा</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">मोबाइल</th>\n" +
                "<th title=\"Toggle SortBy\" role=\"columnheader\" colspan=\"1\">PABX Code</th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody role=\"rowgroup\">\n";

    }

    public String generateEnglishReport(Map<String, Object> filter) {
        List<EmployeePojo> list = employeeMapper.getEmployeeByFilterParam(filter);
        IdNamePojo o = list.get(0).getOffice();
        OfficePojo office = officeMapper.getOfficeByCode(o.getCode());


        if(list.isEmpty()){
            throw new RuntimeException("no contact found");
        }
        String html = "";
        html = html.concat(getEnglishHeader(list.get(0).getOffice().getName(),o.getOfficeAddressEn()));
        int sn = 0;
        for (EmployeePojo employeePojo : list){
            sn ++;
            html = html.concat("<tr role=\"row\">")
                    .concat("<td role=\"cell\">"+ sn +"</td>")
                    .concat("<td role=\"cell\">"+ WordUtils.capitalizeFully(employeePojo.getNameEn()) +"</td>")
            .concat("<td role=\"cell\">"+ StringDataUtils.setNAIfNull(employeePojo.getFunctionalDesignation() == null ? null : WordUtils.capitalizeFully(employeePojo.getFunctionalDesignation().getName())) +"</td>")
            .concat("<td role=\"cell\">"+ StringDataUtils.setNAIfNull(employeePojo.getSection() == null ? null : WordUtils.capitalizeFully(employeePojo.getSection().getName())) +"</td>")
            .concat("<td role=\"cell\">"+ StringDataUtils.setNAIfNull(employeePojo.getMobileNumber()) +"</td>")
            .concat("<td role=\"cell\">"+ StringDataUtils.setNAIfNull(employeePojo.getExtensionNo()) +"</td>").concat("</tr>");
        }
        html = html.concat(pdfEnglishFooter);
            return html;
    }

    public String generateNepaliReport(Map<String, Object> filter) {
        List<EmployeePojo> list = employeeMapper.getEmployeeByFilterParam(filter);
        if(list.isEmpty()){
            throw new RuntimeException("no contact found");
        }
        System.out.println("data is:");
        list.stream().forEach(e->System.out.println(e.toString()));
        String html = "";
        html = html.concat(getNepaliHeader(list.get(0).getOffice().getNameN(),list.get(0).getOffice().getOfficeAddress()));
        int sn = 0;
        for (EmployeePojo employeePojo : list){
            sn ++;
            html =
                    html.concat("<tr role=\"row\">")
                            .concat("<td role=\"cell\">"+ sn +"</td>")
                            .concat("<td role=\"cell\">"+employeePojo.getNameNp() +"</td>")
            .concat("<td role=\"cell\">"+ StringDataUtils.setNAIfNull(employeePojo.getFunctionalDesignation() == null ? null :employeePojo.getFunctionalDesignation().getNameN()) +"</td>")
             .concat("<td role=\"cell\">"+ StringDataUtils.setNAIfNull(employeePojo.getSection() == null ? null :employeePojo.getSection().getNameN()) +"</td>")
             .concat("<td role=\"cell\">"+ StringDataUtils.setNAIfNull(employeePojo.getMobileNumber()) +"</td>")
                    .concat("<td role=\"cell\">"+ StringDataUtils.setNAIfNull(employeePojo.getExtensionNo()) +"</td>").concat("</tr>");
        }
        html = html.concat(pdfNepaliFooter);
        System.out.println(html);
        return html;
    }
}
