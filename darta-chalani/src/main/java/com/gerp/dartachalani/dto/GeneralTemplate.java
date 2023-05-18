package com.gerp.dartachalani.dto;

import java.time.LocalDate;
import java.util.List;

import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralTemplate {

    private String logo_url;

    private String header;

    private String organization;

    private String department;

    private String ministry;

    private String section_header;

    private String address_top;

    private String letter_date;

    private String chali_no;

    private String letter_no;

    private String request_to_office;

    private String request_to_office_address;

    private String subject;

    private String body_message;

    private String requester_name;

    private String requester_position;

    private List<Bodartha> saadar_awagataartha;

    private List<Bodartha> bodartha;

    private List<Bodartha> bodartha_karyartha;

    private String footer;

    private List<RequestTo> request_to_many;

    private String resource_type;

    private Long resource_id;

    private List<Ocr> ocr;

    private SignaturePojo signatureDetail;

    private String senderOfficeCode;

    private String dispatchDateEn;

    private String dispatchDateNp;

    private String status;

    private VerificationInformation verificationInformation;

    private Boolean hasSubject = Boolean.TRUE;

    private String sectionName;

    private Boolean isSectionName = Boolean.FALSE;

    private Boolean isGroupName = Boolean.FALSE;

    private String sectionLetter;

    private Boolean isQrApp;

    private String dispatchId;

    private String mobileNumber;
}
