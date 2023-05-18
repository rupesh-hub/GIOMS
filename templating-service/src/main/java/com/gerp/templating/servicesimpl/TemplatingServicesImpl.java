package com.gerp.templating.servicesimpl;

import com.gerp.shared.exception.CustomException;
import com.gerp.shared.utils.IdEncryptor;
import com.gerp.templating.Proxy.EmployeeDetailProxy;
import com.gerp.templating.entity.*;
import com.gerp.templating.services.TemplatingServices;
import com.gerp.templating.token.TokenProcessorService;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Hashtable;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TemplatingServicesImpl implements TemplatingServices {

    @Autowired
    EmployeeDetailProxy<Object> employeeDetailProxy;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${qr.link}")
    private String qRUrl;

    @Override
    public String getGeneralTemplate(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException {

        Context context = new Context();
        context.setVariable("entity", generalTemplate);
        String qr;
        if(generalTemplate.getIsQrApp() !=null && generalTemplate.getIsQrApp().equals(Boolean.TRUE)){
            QRCodeWriter barcodeWriter = new QRCodeWriter();
            Hashtable hints = new Hashtable<>();
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            BitMatrix bitMatrix;

            String url = qRUrl;
            try {
                url = url  + IdEncryptor.encrypt(generalTemplate.getDispatchId());
            } catch (Exception e) {
                throw new CustomException("Error encrypting ");
            }
            bitMatrix = barcodeWriter.encode( url, BarcodeFormat.QR_CODE, 600, 600, hints);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", baos);
        byte[] bytes = baos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(bytes);
        qr = "data:image/png;base64," + encodedString;

        } else {
         qr = getQR(generalTemplate, lan);
    }
        context.setVariable("image_value", "" + qr);

        log.info(qr);
        checkMultiple(generalTemplate, lan.toString());

        String html = null;
        String signatureData;

        //getting signature data
        VerificationInformation verificationInformation = generalTemplate.getVerificationInformation();

        String verifiedMessage;
        String signatureBy = "";
        String signatureName = "";
        String signatureEmail = "";

        Boolean hashSubject = generalTemplate.getHasSubject() != null ? generalTemplate.getHasSubject() : Boolean.TRUE;

        System.out.println("hash subject: " + generalTemplate.getHasSubject());
        String subjectData;

        if (lan.toString().equals("NEP")) {

            context.setVariable("saadar_awagataartha", generalTemplate.getSaadar_awagataartha().size() == 0 ? "" : "सादर अवगतार्थ :");
            context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "बोधार्थ:");
            context.setVariable("bodartha_karyartha", generalTemplate.getBodartha_karyartha().size() == 0 ? "" : "बोधार्थ/कार्यार्थ:");
            log.info("header is: " + generalTemplate.getHeader());

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
            context.setVariable("section_name", generalTemplate.getSectionName());
            context.setVariable("is_section_name", generalTemplate.getIsSectionName() != null ? generalTemplate.getIsSectionName() : Boolean.FALSE);
            context.setVariable("is_group_name", generalTemplate.getIsGroupName() != null ? generalTemplate.getIsGroupName() : Boolean.FALSE);
            if(generalTemplate.getIsQrApp() !=null && generalTemplate.getIsQrApp()) context.setVariable("water_mark", generalTemplate.getMobileNumber()); else context.setVariable("water_mark", null);

            html = templateEngine.process(generalTemplate.getHeader() == null || generalTemplate.getHeader().isEmpty() ? "chalani_pattra.html" : "chalani_pattra_with_header.html", context);
            signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
            subjectData = templateEngine.process(hashSubject ? "with_subject_nep.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
            html = html.replace("signature_here", signatureData);
            html = html.replace("subject_to_replace", subjectData);
            if (generalTemplate.getHeader() != null) {
                String head = generalTemplate.getHeader();
                if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                    head = head.replace("section_header", generalTemplate.getSection_header());
                else
                    head = replaceSectionHeader(head);
                html = html.replace("header_from_programmer", head);
                html = html.replace("qr_image_value", qr);
            }

        } else if (lan.toString().equals("EN")) {

            System.out.println(generalTemplate.getBodartha().size() + "  is true " + (generalTemplate.getBodartha().size() == 0));
//			context.setVariable("saadar_awagataartha",generalTemplate.getSaadar_awagataartha().size()==0?"":"Saadar Awagataartha:");
//			context.setVariable("bodartha_option",generalTemplate.getBodartha().size()==0?"":"CC :");
//			context.setVariable("bodartha_karyartha",generalTemplate.getBodartha_karyartha().size()==0?"":"Bodartha/Karyartha:");
            context.setVariable("request", generalTemplate.getSaadar_awagataartha().size() != 0
                    || generalTemplate.getBodartha().size() != 0
                    || generalTemplate.getBodartha_karyartha().size() != 0 ? "CC:" : "");
            context.setVariable("saadar_awagataartha", generalTemplate.getBodartha().size() == 0 ? "" : "Saadar Awagataartha:");
            context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "CC :");
            context.setVariable("bodartha_karyartha", generalTemplate.getBodartha_karyartha().size() == 0 ? "" : "Bodartha/Karyartha:");

            //setting signature data
            if (verificationInformation != null) {
                if (verificationInformation.getStatus() == HttpStatus.OK) {
                    verifiedMessage = "Verified Digital Signature";
                    signatureBy = "Digitally Signed By";
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
            context.setVariable("section_name", generalTemplate.getSectionName());
            context.setVariable("is_section_name", generalTemplate.getIsSectionName() != null ? generalTemplate.getIsSectionName() : Boolean.FALSE);
            context.setVariable("is_group_name", generalTemplate.getIsGroupName() != null ? generalTemplate.getIsGroupName() : Boolean.FALSE);

            html = templateEngine.process(generalTemplate.getHeader() == null || generalTemplate.getHeader().isEmpty() ? "chalani_pattra_en.html" : "chalani_pattra_with_header_en.html", context);
            signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
            subjectData = templateEngine.process(hashSubject ? "with_subject_en.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
            html = html.replace("signature_here", signatureData);
            html = html.replace("subject_to_replace", subjectData);
            if (generalTemplate.getHeader() != null) {
                String head = generalTemplate.getHeader();
                if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                    head = head.replace("section_header", generalTemplate.getSection_header());
                else
                    head = replaceSectionHeader(head);
                html = html.replace("header_from_programmer", head);
                html = html.replace("qr_image_value", qr);
            }
        }
        return html;
    }

    private void checkMultiple(GeneralTemplate generalTemplate, String lan) {

        if (generalTemplate.getSaadar_awagataartha().size() > 1) {

            generalTemplate.getSaadar_awagataartha().stream().map(e -> {
                e.setOffice(e.getOffice() +
                        (e.getSection() != null && !e.getSection().equals("") ? ", " + e.getSection() : "") +
                        (e.getAddress() != null && !e.getAddress().equals("") ? ", " + e.getAddress() : "") +
                        (e.isExternal() ? "" : (lan.equals("NEP") ? " ।" : "")));
                e.setAddress("");
                e.setSection("");
                return e;
            }).collect(Collectors.toList());
        }

        if (generalTemplate.getBodartha().size() > 1) {

            generalTemplate.getBodartha().stream().map(e -> {
                e.setOffice(e.getOffice() +
                        (e.getSection() != null && !e.getSection().equals("") ? ", " + e.getSection() : "") +
                        (e.getAddress() != null && !e.getAddress().equals("") ? ", " + e.getAddress() : "") +
                        (e.isExternal() ? "" : (lan.equals("NEP") ? " ।" : "")));
                e.setAddress("");
                e.setSection("");
                return e;
            }).collect(Collectors.toList());
        }

        if (generalTemplate.getBodartha_karyartha().size() > 1) {

            generalTemplate.getBodartha_karyartha().stream().map(e -> {
                e.setOffice(e.getOffice() +
                        (e.getSection() != null && !e.getSection().equals("") ? ", " + e.getSection() : "") +
                        (e.getAddress() != null && !e.getAddress().equals("") ? ", " + e.getAddress() : "") +
                        (e.isExternal() ? "" : (lan.equals("NEP") ? " ।" : "")));
                e.setAddress("");
                e.setSection("");
                return e;
            }).collect(Collectors.toList());
        }

    }

    @Override
    public String getGeneralManyToTemplate(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException {
        Context context = new Context();
        context.setVariable("entity", generalTemplate);
        String qr = getQR(generalTemplate, lan);

        context.setVariable("image_value", "" + qr);
        checkMultiple(generalTemplate, lan.toString());
        String html = null;

        //getting signature data
        VerificationInformation verificationInformation = generalTemplate.getVerificationInformation();

        String verifiedMessage;
        String signatureBy = "";
        String signatureName = "";
        String signatureEmail = "";

        String subjectData;
        Boolean hashSubject = generalTemplate.getHasSubject() != null ? generalTemplate.getHasSubject() : Boolean.TRUE;

        String signatureData;
        if (lan.toString().equals("NEP")) {

            context.setVariable("saadar_awagataartha", generalTemplate.getSaadar_awagataartha().size() == 0 ? "" : "सादर अवगतार्थ :");
            context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "बोधार्थ:");
            context.setVariable("bodartha_karyartha", generalTemplate.getBodartha_karyartha().size() == 0 ? "" : "बोधार्थ/कार्यार्थ:");

//			context.setVariable("saadar_awagataartha",generalTemplate.getSaadar_awagataartha().size()==0?"":"सादर अवगतार्थ :");
//			context.setVariable("bodartha_option",generalTemplate.getBodartha().size()==0?"":"बोधार्थ:");
//			context.setVariable("bodartha_karyartha",generalTemplate.getBodartha_karyartha().size()==0?"":"बोधार्थ/कार्यार्थ:");

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

            html = templateEngine.process(generalTemplate.getHeader() == null || generalTemplate.getHeader().isEmpty() ? "chalani_with_many_to.html" : "chalani_with_many_to_header.html", context);
            signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
            subjectData = templateEngine.process(hashSubject ? "with_subject_many_nep.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
            html = html.replace("signature_here", signatureData);
            html = html.replace("subject_to_replace", subjectData);
            if (generalTemplate.getHeader() != null) {
                String head = generalTemplate.getHeader();
                if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                    head = head.replace("section_header", generalTemplate.getSection_header());
                else
                    head = replaceSectionHeader(head);
                html = html.replace("header_from_programmer", head);
                html = html.replace("qr_image_value", qr);
            }

        } else if (lan.toString().equals("EN")) {
            context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "CC :");

            //setting signature data
            if (verificationInformation != null) {
                if (verificationInformation.getStatus() == HttpStatus.OK) {
                    verifiedMessage = "Verified Digital Signature";
                    signatureBy = "Digitally Signed By";
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

            html = templateEngine.process(generalTemplate.getHeader() == null || generalTemplate.getHeader().isEmpty() ? "chalani_with_many_to_en.html" : "chalani_with_many_to_header_en.html", context);
            signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
            subjectData = templateEngine.process(hashSubject ? "with_subject_many_en.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
            html = html.replace("signature_here", signatureData);
            html = html.replace("subject_to_replace", subjectData);
            if (generalTemplate.getHeader() != null) {
                String head = generalTemplate.getHeader();
                if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                    head = head.replace("section_header", generalTemplate.getSection_header());
                else
                    head = replaceSectionHeader(head);
                html = html.replace("header_from_programmer", head);
                html = html.replace("qr_image_value", qr);
            }
        }
        return html;
    }

    @Override
    public String getOcrTemplate(GeneralTemplate generalTemplate, Language lan) throws IOException, WriterException {
        Context context = new Context();
        context.setVariable("entity", generalTemplate);
        String qr = getQR(generalTemplate, lan);
        context.setVariable("image_value", "" + qr);

        checkMultiple(generalTemplate, lan.toString());
        String html = null;

        //getting signature data
        VerificationInformation verificationInformation = generalTemplate.getVerificationInformation();

        String verifiedMessage;
        String signatureBy = "";
        String signatureName = "";
        String signatureEmail = "";

        String subjectData;
        Boolean hashSubject = generalTemplate.getHasSubject() != null ? generalTemplate.getHasSubject() : Boolean.TRUE;

        context.setVariable("saadar_awagataartha", generalTemplate.getSaadar_awagataartha().size() == 0 ? "" : "सादर अवगतार्थ :");
        context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "बोधार्थ:");
        context.setVariable("bodartha_karyartha", generalTemplate.getBodartha_karyartha().size() == 0 ? "" : "बोधार्थ/कार्यार्थ:");


        String signatureData;
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

        html = templateEngine.process("chalani_ocr.html", context);

        signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
        subjectData = templateEngine.process(hashSubject ? "with_subject_many_nep.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
        html = html.replace("subject_to_replace", subjectData);
        if (generalTemplate.getHeader() != null) {
            String head = generalTemplate.getHeader();
            if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                head = head.replace("section_header", generalTemplate.getSection_header());
            else
                head = replaceSectionHeader(head);
            html = html.replace("header_from_programmer", head);
            html = html.replace("qr_image_value", qr);
        }

        html = html.replace("signature_here", signatureData);

        final String[] finalHtml = {html};

        final String[] signatureData2 = new String[1];

        //getting signature data
        if (generalTemplate != null && generalTemplate.getOcr() != null && !generalTemplate.getOcr().isEmpty()) {

            final int[] i = {0};
            generalTemplate.getOcr().forEach(x -> {
                VerificationInformation verificationInformation2 = x.getVerificationInformation();

                String verifiedMessage2;
                String signatureBy2 = "";
                String signatureName2 = "";
                String signatureEmail2 = "";

                //setting signature data
                if (verificationInformation2 != null) {
                    if (verificationInformation2.getStatus() == HttpStatus.OK) {
                        verifiedMessage2 = "प्रमाणित डिजिटल हस्ताक्षर";
                        signatureBy2 = "डिजिटल हस्ताक्षर गर्ने व्यक्ति";
                        signatureName2 = verificationInformation2.getSignature_name();
                        signatureEmail2 = verificationInformation2.getEmail();
                    } else {
                        verifiedMessage2 = verificationInformation2.getStatus() == HttpStatus.EXPECTATION_FAILED ? "डिजीटल हस्ताक्षरमा त्रुटी देखियो" : "डिजिटल हस्ताक्षर प्रमाणीकरणको लागि सो सर्भरसंग सम्पर्क हुन सकेन";
                    }
                } else {
                    verifiedMessage2 = "डिजिटल हस्ताक्षर नगरिएको";
                }

                context.setVariable("signature_check", verifiedMessage2);
                context.setVariable("signature_by", signatureBy2);
                context.setVariable("signature_name", signatureName2);
                context.setVariable("signature_email", signatureEmail2);

                signatureData2[0] = templateEngine.process(verificationInformation2 != null && verificationInformation2.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);

                String replaceString = "sign_<span>" + i[0] + "</span>";

                System.out.println("replaceString: " + replaceString);

                finalHtml[0] = finalHtml[0].replace(replaceString, signatureData2[0]);

                i[0]++;
            });
        }


        return finalHtml[0];
    }

//	@Override
//	public String generalTemplateOne(GeneralTemplateOne generalTemplateOne) throws IOException, WriterException {
//		Context context = new Context();
//		context.setVariable("entity", generalTemplateOne);
//		context.setVariable("image_value", ""+getQROne(generalTemplateOne));
//		String html=templateEngine.process("chalani_pattra_with_header.html", context);
//		return html;
//	}

//	private String getQROne(GeneralTemplateOne generalTemplateOne) throws WriterException, IOException {
//
//
//		Object o=employeeDetailProxy.getEmployeeDetailMinimal(tokenProcessorService.getPisCode());
//
//
//		QrEntity qrEntity=new QrEntity();
//
//		qrEntity.setChalinumber(generalTemplateOne.getChali_no());
//		qrEntity.setSubject(generalTemplateOne.getSubject());
//		qrEntity.setDate(new Date().toString());
//		qrEntity.setLetter_number(generalTemplateOne.getLetter_no());
//		qrEntity.setResource_id(generalTemplateOne.getr);
//
//		EmployeeDetails employeeDetails=modelMapper.map(o, EmployeeDetails.class);
//
//		qrEntity.setNameNp(employeeDetails.getNameNp());
//		qrEntity.setOfficeNname(employeeDetails.getOffice().getNameN());
//		//qrEntity.setOfficeEname(employeeDetails.getOffice().getName());
//		//qrEntity.setNameEn(employeeDetails.getqr_link: https://gioms.gov.np/public/document/NameEn());
//
//
//		QRCodeWriter barcodeWriter = new QRCodeWriter();
//		Hashtable hints = new Hashtable<>();
//		hints.put(EncodeHintType.MARGIN, 0);
//		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//
//		BitMatrix bitMatrix = barcodeWriter.encode(new Gson().toJson(qrEntity), BarcodeFormat.QR_CODE, 200, 200,hints);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", baos);
//		byte[] bytes = baos.toByteArray();
//		String encodedString = Base64.getEncoder().encodeToString(bytes);
//		return  "data:image/png;base64,"+encodedString;
//	}


    public String getQR(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException {

        Object o = employeeDetailProxy.getEmployeeDetailMinimal(tokenProcessorService.getPisCode());
        QrEntity qrEntity = new QrEntity();


        if (generalTemplate.getResource_type().equals("C")) {

            if (generalTemplate.getSignatureDetail() != null) {
                Object object = employeeDetailProxy.getEmployeeDetailMinimal(generalTemplate.getSignatureDetail().getPisCode());
                EmployeePojo employeePojo = modelMapper.map(object, EmployeePojo.class);
                qrEntity.setSignedEmployeeName(lan == Language.EN ? employeePojo.getNameEn() : employeePojo.getNameNp());
                qrEntity.setSignedEmployeeDesignation(employeePojo.getFunctionalDesignation() != null ? lan == Language.EN ? employeePojo.getFunctionalDesignation().getName() : employeePojo.getFunctionalDesignation().getNameN() : null);
            }

            Object officeDetailObj = employeeDetailProxy.getOfficeDetail(generalTemplate.getSenderOfficeCode());

            OfficePojo office = modelMapper.map(officeDetailObj, OfficePojo.class);

            qrEntity.setOfficeName(lan == Language.EN ? office.getNameEn() : office.getNameNp());
            qrEntity.setChalaniNo(generalTemplate.getChali_no());
            qrEntity.setChalaniApprovedDate(lan == Language.EN ? generalTemplate.getDispatchDateEn() : generalTemplate.getDispatchDateNp());
            qrEntity.setSubject(generalTemplate.getSubject());
            qrEntity.setStatus(generalTemplate.getStatus());
            qrEntity.setResource_type(generalTemplate.getResource_type());
        } else {
            qrEntity.setChalaniNo(generalTemplate.getChali_no());
            qrEntity.setSubject(generalTemplate.getSubject());
            qrEntity.setDate(new Date().toString());
            qrEntity.setLetter_number(generalTemplate.getLetter_no());
            qrEntity.setResource_id(generalTemplate.getResource_id());
            qrEntity.setResource_type(generalTemplate.getResource_type());

            EmployeeDetails employeeDetails = modelMapper.map(o, EmployeeDetails.class);

            qrEntity.setNameNp(employeeDetails.getNameNp());
            qrEntity.setOfficeName(employeeDetails.getOffice().getNameN());

        }

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        Hashtable hints = new Hashtable<>();
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BitMatrix bitMatrix;

        if(!generalTemplate.getStatus().equals("A")){
            bitMatrix = barcodeWriter.encode(new Gson().toJson(qrEntity), BarcodeFormat.QR_CODE, 600, 600, hints);
        }else {
            String url = qRUrl;
            try {
                url = url  + IdEncryptor.encrypt(generalTemplate.getDispatchId());
            } catch (Exception e) {
                throw new CustomException("Error encrypting ");
            }

            bitMatrix = barcodeWriter.encode( url, BarcodeFormat.QR_CODE, 600, 600, hints);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", baos);
        byte[] bytes = baos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(bytes);
        return "data:image/png;base64," + encodedString;
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


    @Override
    public String getGeneralTemplateForQr(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException {
        Context context = new Context();
        context.setVariable("entity", generalTemplate);
        String qr;
        if(generalTemplate.getIsQrApp() !=null && generalTemplate.getIsQrApp().equals(Boolean.TRUE)){
            QRCodeWriter barcodeWriter = new QRCodeWriter();
            Hashtable hints = new Hashtable<>();
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            BitMatrix bitMatrix;

            String url = qRUrl;
            try {
                url = url  + IdEncryptor.encrypt(generalTemplate.getDispatchId());
            } catch (Exception e) {
                throw new CustomException("Error encrypting ");
            }
            bitMatrix = barcodeWriter.encode( url, BarcodeFormat.QR_CODE, 600, 600, hints);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", baos);
            byte[] bytes = baos.toByteArray();
            String encodedString = Base64.getEncoder().encodeToString(bytes);
            qr = "data:image/png;base64," + encodedString;

        } else {
            qr = getQR(generalTemplate, lan);
        }
        context.setVariable("image_value", "" + qr);

        log.info(qr);
        checkMultiple(generalTemplate, lan.toString());

        String html = null;
        String signatureData;

        //getting signature data
        VerificationInformation verificationInformation = generalTemplate.getVerificationInformation();

        String verifiedMessage;
        String signatureBy = "";
        String signatureName = "";
        String signatureEmail = "";

        Boolean hashSubject = generalTemplate.getHasSubject() != null ? generalTemplate.getHasSubject() : Boolean.TRUE;

        System.out.println("hash subject: " + generalTemplate.getHasSubject());
        String subjectData;

        if(generalTemplate.getIsQrApp() !=null && generalTemplate.getIsQrApp()) context.setVariable("water_mark", generalTemplate.getMobileNumber()); else context.setVariable("water_mark", null);

        if (lan.toString().equals("NEP")) {

            context.setVariable("saadar_awagataartha", generalTemplate.getSaadar_awagataartha().size() == 0 ? "" : "सादर अवगतार्थ :");
            context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "बोधार्थ:");
            context.setVariable("bodartha_karyartha", generalTemplate.getBodartha_karyartha().size() == 0 ? "" : "बोधार्थ/कार्यार्थ:");
            log.info("header is: " + generalTemplate.getHeader());

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
            context.setVariable("section_name", generalTemplate.getSectionName());
            context.setVariable("is_section_name", generalTemplate.getIsSectionName() != null ? generalTemplate.getIsSectionName() : Boolean.FALSE);
            context.setVariable("is_group_name", generalTemplate.getIsGroupName() != null ? generalTemplate.getIsGroupName() : Boolean.FALSE);

            html = templateEngine.process(generalTemplate.getHeader() == null || generalTemplate.getHeader().isEmpty() ? "chalani_pattra_qr.html" : "chalani_pattra_with_header_qr.html", context);
            signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
            subjectData = templateEngine.process(hashSubject ? "with_subject_nep.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
            html = html.replace("signature_here", signatureData);
            html = html.replace("subject_to_replace", subjectData);
            if (generalTemplate.getHeader() != null) {
                String head = generalTemplate.getHeader();
                if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                    head = head.replace("section_header", generalTemplate.getSection_header());
                else
                    head = replaceSectionHeader(head);
                html = html.replace("header_from_programmer", head);
                html = html.replace("qr_image_value", qr);
            }

        } else if (lan.toString().equals("EN")) {

            System.out.println(generalTemplate.getBodartha().size() + "  is true " + (generalTemplate.getBodartha().size() == 0));
//			context.setVariable("saadar_awagataartha",generalTemplate.getSaadar_awagataartha().size()==0?"":"Saadar Awagataartha:");
//			context.setVariable("bodartha_option",generalTemplate.getBodartha().size()==0?"":"CC :");
//			context.setVariable("bodartha_karyartha",generalTemplate.getBodartha_karyartha().size()==0?"":"Bodartha/Karyartha:");
            context.setVariable("request", generalTemplate.getSaadar_awagataartha().size() != 0
                    || generalTemplate.getBodartha().size() != 0
                    || generalTemplate.getBodartha_karyartha().size() != 0 ? "CC:" : "");
            context.setVariable("saadar_awagataartha", generalTemplate.getBodartha().size() == 0 ? "" : "Saadar Awagataartha:");
            context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "CC :");
            context.setVariable("bodartha_karyartha", generalTemplate.getBodartha_karyartha().size() == 0 ? "" : "Bodartha/Karyartha:");

            //setting signature data
            if (verificationInformation != null) {
                if (verificationInformation.getStatus() == HttpStatus.OK) {
                    verifiedMessage = "Verified Digital Signature";
                    signatureBy = "Digitally Signed By";
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
            context.setVariable("section_name", generalTemplate.getSectionName());
            context.setVariable("is_section_name", generalTemplate.getIsSectionName() != null ? generalTemplate.getIsSectionName() : Boolean.FALSE);
            context.setVariable("is_group_name", generalTemplate.getIsGroupName() != null ? generalTemplate.getIsGroupName() : Boolean.FALSE);

            html = templateEngine.process(generalTemplate.getHeader() == null || generalTemplate.getHeader().isEmpty() ? "chalani_pattra_en_qr.html" : "chalani_pattra_with_header_en_qr.html", context);
            signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
            subjectData = templateEngine.process(hashSubject ? "with_subject_en.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
            html = html.replace("signature_here", signatureData);
            html = html.replace("subject_to_replace", subjectData);
            if (generalTemplate.getHeader() != null) {
                String head = generalTemplate.getHeader();
                if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                    head = head.replace("section_header", generalTemplate.getSection_header());
                else
                    head = replaceSectionHeader(head);
                html = html.replace("header_from_programmer", head);
                html = html.replace("qr_image_value", qr);
            }
        }
        return html;    }

    @Override
    public String getGeneralManyToTemplateForQr(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException {
        Context context = new Context();
        context.setVariable("entity", generalTemplate);
        String qr = getQR(generalTemplate, lan);

        context.setVariable("image_value", "" + qr);
        checkMultiple(generalTemplate, lan.toString());
        String html = null;

        //getting signature data
        VerificationInformation verificationInformation = generalTemplate.getVerificationInformation();

        String verifiedMessage;
        String signatureBy = "";
        String signatureName = "";
        String signatureEmail = "";

        String subjectData;
        Boolean hashSubject = generalTemplate.getHasSubject() != null ? generalTemplate.getHasSubject() : Boolean.TRUE;

        String signatureData;
        if (lan.toString().equals("NEP")) {

            context.setVariable("saadar_awagataartha", generalTemplate.getSaadar_awagataartha().size() == 0 ? "" : "सादर अवगतार्थ :");
            context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "बोधार्थ:");
            context.setVariable("bodartha_karyartha", generalTemplate.getBodartha_karyartha().size() == 0 ? "" : "बोधार्थ/कार्यार्थ:");

//			context.setVariable("saadar_awagataartha",generalTemplate.getSaadar_awagataartha().size()==0?"":"सादर अवगतार्थ :");
//			context.setVariable("bodartha_option",generalTemplate.getBodartha().size()==0?"":"बोधार्थ:");
//			context.setVariable("bodartha_karyartha",generalTemplate.getBodartha_karyartha().size()==0?"":"बोधार्थ/कार्यार्थ:");

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

            html = templateEngine.process(generalTemplate.getHeader() == null || generalTemplate.getHeader().isEmpty() ? "chalani_with_many_to_qr.html" : "chalani_with_many_to_header_qr.html", context);
            signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
            subjectData = templateEngine.process(hashSubject ? "with_subject_many_nep.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
            html = html.replace("signature_here", signatureData);
            html = html.replace("subject_to_replace", subjectData);
            if (generalTemplate.getHeader() != null) {
                String head = generalTemplate.getHeader();
                if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                    head = head.replace("section_header", generalTemplate.getSection_header());
                else
                    head = replaceSectionHeader(head);
                html = html.replace("header_from_programmer", head);
                html = html.replace("qr_image_value", qr);
            }

        } else if (lan.toString().equals("EN")) {
            context.setVariable("bodartha_option", generalTemplate.getBodartha().size() == 0 ? "" : "CC :");

            //setting signature data
            if (verificationInformation != null) {
                if (verificationInformation.getStatus() == HttpStatus.OK) {
                    verifiedMessage = "Verified Digital Signature";
                    signatureBy = "Digitally Signed By";
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

            html = templateEngine.process(generalTemplate.getHeader() == null || generalTemplate.getHeader().isEmpty() ? "chalani_with_many_to_en_qr.html" : "chalani_with_many_to_header_en_qr.html", context);
            signatureData = templateEngine.process(verificationInformation != null && verificationInformation.getStatus() == HttpStatus.OK ? "signature.html" : "fault_sign.html", context);
            subjectData = templateEngine.process(hashSubject ? "with_subject_many_en.html" : generalTemplate.getSubject() != null && !generalTemplate.getSubject().equals("") ? "without_subject.html" : "with_blank_subject.html", context);
            html = html.replace("signature_here", signatureData);
            html = html.replace("subject_to_replace", subjectData);
            if (generalTemplate.getHeader() != null) {
                String head = generalTemplate.getHeader();
                if (generalTemplate.getSection_header() != null && !generalTemplate.getSection_header().equals(""))
                    head = head.replace("section_header", generalTemplate.getSection_header());
                else
                    head = replaceSectionHeader(head);
                html = html.replace("header_from_programmer", head);
                html = html.replace("qr_image_value", qr);
            }
        }
        return html;    }
}
