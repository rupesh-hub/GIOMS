package com.gerp.templating.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralTemplate {

	private String logo_url;

	private String header;

	private String organization;

	private String department;
	
	private String ministry;

	private String address_top;

	private String letter_date;

	private String chali_no;

	private String letter_no;

	private String request_to_office;

	private String request_to_office_address;

	private String section_header;

	private String subject;

	private String body_message;

	private String requester_name;

	private String requester_position;

	private List<SaadarAwagataartha> saadar_awagataartha;

	private List<Bodartha> bodartha;

	private List<BodarthaKaryartha> bodartha_karyartha;

	private String footer;

	private List<RequestTo> request_to_many;

	@NotNull(message = "document resource id can not be null !")
	private Long resource_id;

	@NotNull
	@Size(min = 1,max = 1)
	@Pattern(regexp = "[CD]",message = "resource type can only have C or D")
	private String resource_type;

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

	private Boolean isQrApp = false;

	private String dispatchId;

	private String mobileNumber;
}