package com.gerp.templating.controller;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.templating.constant.PermissionConstants;
import com.gerp.templating.entity.*;
import com.gerp.templating.services.*;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/gerp")
public class TemplatingController extends BaseController {


	private final TemplatingServices templatingServices;

	private final RamanaService ramanaService;
	private final ReportTemplateService reportTemplateService;

	private final SaruwaService saruwaService;
	private final SaruwaLetterService saruwaLetterService;
	private final SaruwaRequestDetailService saruwaRequestDetailService;
	private  final RamanaDetailService ramanaDetailService;
	private final TippaniService tippaniService;

	private final CustomMessageSource customMessageSource;

	@Autowired
	public TemplatingController(CustomMessageSource customMessageSource,
								TemplatingServices templatingServices,
								RamanaService ramanaService,
								ReportTemplateService reportTemplateService,
								SaruwaService saruwaService,
								SaruwaLetterService saruwaLetterService,
								SaruwaRequestDetailService saruwaRequestDetailService,
								TippaniService tippaniService,
								RamanaDetailService ramanaDetailService){
		this.customMessageSource = customMessageSource;
		this.templatingServices=templatingServices;
		this.ramanaService=ramanaService;
		this.reportTemplateService=reportTemplateService;
		this.saruwaService=saruwaService;
		this.tippaniService=tippaniService;
		this.saruwaLetterService=saruwaLetterService;
		this.saruwaRequestDetailService=saruwaRequestDetailService;
		this.ramanaDetailService=ramanaDetailService;
		this.permissionName = PermissionConstants.TEMPLATING+"_"+PermissionConstants.GENERATE;

	}

	//@PreAuthorize("hasPermission(#this.this.permissionName,'create')")
	@PostMapping(value = "/general-template",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
	public ResponseEntity<?> generalTemplate(@RequestBody @Valid GeneralTemplate generalTemplate, @RequestParam Language lan) throws WriterException, IOException {
		return new ResponseEntity<>(templatingServices.getGeneralTemplate(generalTemplate,lan), HttpStatus.OK);
	}

	@PostMapping(value = "/ocr-template",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
	public ResponseEntity<?> ocrTemplate(@RequestBody @Valid GeneralTemplate generalTemplate, @RequestParam Language lan) throws WriterException, IOException {
		return new ResponseEntity<>(templatingServices.getOcrTemplate(generalTemplate,lan), HttpStatus.OK);
	}

	@PostMapping(value = "/general-template/many-to",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
	public ResponseEntity<?> generalManyToTemplate(@RequestBody @Valid GeneralTemplate generalTemplate,@RequestParam Language lan) throws WriterException, IOException {
		return new ResponseEntity<>(templatingServices.getGeneralManyToTemplate(generalTemplate,lan), HttpStatus.OK);
	}

//	@PreAuthorize("hasPermission(#this.this.permissionName,'create')")
	@PostMapping(value = "/tippani-template",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
	public ResponseEntity<?> generalTippaniTemplate(@RequestBody @Valid TippaniDetail tippaniDetail) throws IOException, WriterException {
		return new ResponseEntity<>(tippaniService.getTippani(tippaniDetail), HttpStatus.OK);
	}

//	@PreAuthorize("hasPermission(#this.this.permissionName,'create')")
	@PostMapping(value = "/tippani-header",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
	public ResponseEntity<?> generalTippaniHeader(@RequestBody @Valid TippaniDetail tippaniDetail) throws IOException, WriterException {
		return new ResponseEntity<>(tippaniService.getTippaniHeader(tippaniDetail), HttpStatus.OK);
	}

//	@PreAuthorize("hasPermission(#this.this.permissionName,'create')")
	@PostMapping("/ramana-template")
	public ResponseEntity<?> generalRamanaTemplate(@RequestBody RamanaTemplate ramanaTemplate) {
		return new ResponseEntity<>(ramanaService.getRamanaTemplate(ramanaTemplate), HttpStatus.OK);
	}

//	@PostMapping("/ramana-detail-template")
//	public ResponseEntity<?> generalRamanaDetailTemplate(@RequestBody RamanaDetail ramanaDetail) {
//		return new ResponseEntity<>(ramanaDetailService.getRamanaDetail(ramanaDetail), HttpStatus.OK);
//	}
//	@PreAuthorize("hasPermission(#this.this.permissionName,'create')")
	@PostMapping("/saruwa-template")
	public ResponseEntity<?> generalSaruwaTemplate(@RequestBody SaruwaTemplate saruwaTemplate) {
		return new ResponseEntity<>(saruwaService.getSaruwaTemplate(saruwaTemplate), HttpStatus.OK);
	}

//	@PostMapping("/saruwa-letter-template")
//	public ResponseEntity<?> generalSaruwaLetterTemplate(@RequestBody SaruwaRequestLetterTemplate saruwaRequestLetterTemplate) {
//		return new ResponseEntity<>(saruwaLetterService.getSaruwaLetterTemplate(saruwaRequestLetterTemplate), HttpStatus.OK);
//	}
//	@PreAuthorize("hasPermission(#this.this.permissionName,'create')")
	@PostMapping("/saruwa-request-detail-template")
	public ResponseEntity<?> generalSaruwaRequestDetailTemplate(@RequestBody SaruwaRequestLetterTemplate saruwaRequestLetterTemplate) {
		return new ResponseEntity<>(saruwaRequestDetailService.getSaruwaRequestDetailTemplate(saruwaRequestLetterTemplate), HttpStatus.OK);
	}


	@PostMapping("/report-template")
	public ResponseEntity<?> getLeaveReportTemplate(@RequestBody ReportTemplate reportTemplate) {
		return new ResponseEntity<>(reportTemplateService.getReportTemplate(reportTemplate), HttpStatus.OK);
	}

	@PostMapping("/report-header")
	public ResponseEntity<?> getHeaderTemplate(@RequestBody ReportTemplate reportTemplate) {
		return new ResponseEntity<>(reportTemplateService.getHeaderTemplate(reportTemplate), HttpStatus.OK);
	}

	@PostMapping(value = "/general-template-for-qr",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
	public ResponseEntity<?> generalTemplateForQr(@RequestBody @Valid GeneralTemplate generalTemplate, @RequestParam Language lan) throws WriterException, IOException {
		return new ResponseEntity<>(templatingServices.getGeneralTemplateForQr(generalTemplate,lan), HttpStatus.OK);
	}

	@PostMapping(value = "/general-template/many-to-for-qr",produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
	public ResponseEntity<?> generalManyToTemplateForQr(@RequestBody @Valid GeneralTemplate generalTemplate,@RequestParam Language lan) throws WriterException, IOException {
		return new ResponseEntity<>(templatingServices.getGeneralManyToTemplateForQr(generalTemplate,lan), HttpStatus.OK);
	}
}