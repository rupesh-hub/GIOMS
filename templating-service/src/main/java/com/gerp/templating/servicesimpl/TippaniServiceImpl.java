package com.gerp.templating.servicesimpl;

import com.gerp.templating.Proxy.EmployeeDetailProxy;
import com.gerp.templating.entity.*;
import com.gerp.templating.services.TippaniService;
import com.gerp.templating.token.TokenProcessorService;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Hashtable;

@Service
public class TippaniServiceImpl implements TippaniService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    EmployeeDetailProxy<Object> employeeDetailProxy;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public String getTippani(TippaniDetail tippaniDetail) throws IOException, WriterException {
        Context context = new Context();
        context.setVariable("entity", tippaniDetail);
        String qr = getQR(tippaniDetail);
        context.setVariable("image_value", "" + qr);

        final String[] html = {
                templateEngine.process(tippaniDetail.getDynamicHeader() != null ? "tippani_template_with_dynamic_header" : "tippani_template.html", context)
        };

        if (tippaniDetail.getDynamicHeader() != null) {


            String finalHeader = replaceRedLine(tippaniDetail.getDynamicHeader());

//            System.out.println("header: " + tippaniDetail.getDynamicHeader());

//            System.out.println("final header: " + finalHeader);

            finalHeader = replaceSectionHeader(finalHeader);

            html[0] = html[0].replace("dynamic_header", finalHeader);
            html[0] = html[0].replace("qr_image_value", qr);
        }

        final String[] signatureData = new String[1];

        //getting signature data
        if (tippaniDetail != null && tippaniDetail.getTippaniContentList() != null && !tippaniDetail.getTippaniContentList().isEmpty()) {

            final int[] i = {0};
            tippaniDetail.getTippaniContentList().forEach(x -> {
                VerificationInformation verificationInformation = x.getVerificationInformation();

                String verifiedMessage;
                String signatureBy = "";
                String signatureName = "";
                String signatureEmail = "";

                //setting signature data
                if (verificationInformation != null) {
                    if (verificationInformation.getStatus() == HttpStatus.OK) {
                        verifiedMessage = "प्रमाणित डिजिटल हस्ताक्षर";
                        signatureBy = "डिजिटल हस्ताक्षर गर्ने व्यक्ति";
                        signatureName = verificationInformation.getSignature_name();
                        signatureEmail = verificationInformation.getEmail();
                    } else {
                        verifiedMessage = verificationInformation.getStatus() == HttpStatus.EXPECTATION_FAILED ? "डिजीटल हस्ताक्षरमा त्रुटी देखियो" : "डिजिटल हस्ताक्षर प्रमाणीकरणको लागि सो सर्भरसंग सम्पर्क हुन सकेन";
                    }
                } else {
                    verifiedMessage = "डिजिटल हस्ताक्षर नगरिएको";
                }

                context.setVariable("signature_check", verifiedMessage);
                context.setVariable("signature_by", signatureBy);
                context.setVariable("signature_name", signatureName);
                context.setVariable("signature_email", signatureEmail);

                signatureData[0] = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);

                String replaceString = "sign_<span>" + i[0] + "</span>";

//                System.out.println("replaceString: " + replaceString);

                html[0] = html[0].replace(replaceString, signatureData[0]);

                i[0]++;
            });
        }


        return html[0];
    }

    @Override
    public String getTippaniHeader(TippaniDetail tippaniDetail) throws IOException, WriterException {
        Context context = new Context();
        context.setVariable("entity", tippaniDetail);
        String qr = getQR(tippaniDetail);
        context.setVariable("image_value", "" + qr);

        String header;
        if (tippaniDetail.getDynamicHeader() != null) {
            header = tippaniDetail.getDynamicHeader();
            header = header.replace("qr_image_value", qr);
            header = replaceSectionHeader(header);
            header = replaceRedLine(header);
        } else {
            header = templateEngine.process("tippani_header.html", context);
        }

        return header;
    }

    public String getQR(TippaniDetail tippaniDetail) throws WriterException, IOException {


        Object o = employeeDetailProxy.getEmployeeDetailMinimal(tokenProcessorService.getPisCode());

        QrEntity qrEntity = new QrEntity();

        if (tippaniDetail.getApproverPisCode() != null) {
            Object object = employeeDetailProxy.getEmployeeDetailMinimal(tippaniDetail.getApproverPisCode());
            EmployeePojo employeePojo = modelMapper.map(object, EmployeePojo.class);
            qrEntity.setSignedEmployeeName(employeePojo.getNameNp());
            qrEntity.setSignedEmployeeDesignation(employeePojo.getFunctionalDesignation() != null ? employeePojo.getFunctionalDesignation().getNameN() : null);

        }

        if (tippaniDetail.getSenderOfficeCode() != null) {
            Object officeDetailObj = employeeDetailProxy.getOfficeDetail(tippaniDetail.getSenderOfficeCode());
            OfficePojo office = modelMapper.map(officeDetailObj, OfficePojo.class);
            qrEntity.setOfficeName(office.getNameNp());
        }

        qrEntity.setTippaniApprovedDate(tippaniDetail.getApprovedDate());
        qrEntity.setSubject(tippaniDetail.getSubject());
        qrEntity.setResource_id(tippaniDetail.getResource_id());
        qrEntity.setStatus(tippaniDetail.getStatus().toString());
        qrEntity.setResource_type(tippaniDetail.getResource_type());

        /*qrEntity.setChalaniNo(tippaniDetail.getTippani_no());
        qrEntity.setSubject(tippaniDetail.getSubject());
        qrEntity.setDate(new Date().toString());
        qrEntity.setResource_id(tippaniDetail.getResource_id());
        qrEntity.setResource_type(tippaniDetail.getResource_type());

        EmployeeDetails employeeDetails=modelMapper.map(o, EmployeeDetails.class);

        qrEntity.setNameNp(employeeDetails.getNameNp());
        qrEntity.setOfficeName(employeeDetails.getOffice().getNameN());*/

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        Hashtable hints = new Hashtable<>();
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix bitMatrix = barcodeWriter.encode(new Gson().toJson(qrEntity), BarcodeFormat.QR_CODE, 200, 200, hints);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", baos);
        byte[] bytes = baos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(bytes);

//        System.out.println("tipanni images :" + encodedString);
        return "data:image/png;base64," + encodedString;
    }

    public String replaceRedLine(String header) {
        return header.replace("<tr>\n" +
                "            <td colspan=\"3\">\n" +
                "            <div style=\"\n" +
                "                        height: 2px;\n" +
                "                        width: 100%;\n" +
                "                        background-color: red;\n" +
                "                        margin: 1rem 1;\n" +
                "\t\t\t\t\t    \"\n" +
                "                ></div>\n" +
                "            </td>\n" +
                "        </tr>", "");
    }

    public String replaceSectionHeader(String section) {

        String sr[] = section.split("section_header");
        if (sr.length > 1) {
            String style[] = sr[0].split("style");
            String styleLast = style[style.length - 1];
            StringBuilder sb = new StringBuilder(styleLast);
            sb.insert(styleLast.indexOf(";") + 1, " display: none;");
            style[style.length - 1] = sb.toString();
            sr[0] = String.join("style", style);
            return String.join("", sr);
        }
        return section;
    }
}
