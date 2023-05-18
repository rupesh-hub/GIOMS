package com.gerp.usermgmt.services.transfer.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.Proxy.MessagingServiceData;
import com.gerp.usermgmt.Proxy.TemplateServiceData;
import com.gerp.usermgmt.mapper.transfer.TransferMapper;
import com.gerp.usermgmt.mapper.transfer.TransferRequestMapper;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.transfer.*;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.usermgmt.pojo.transfer.*;
import com.gerp.usermgmt.pojo.transfer.document.DocumentMasterResponsePojo;
import com.gerp.usermgmt.pojo.transfer.document.DocumentResponsePojo;
import com.gerp.usermgmt.pojo.transfer.document.DocumentSavePojo;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.repo.office.OfficeRepo;
import com.gerp.usermgmt.repo.transfer.RawanaRepo;
import com.gerp.usermgmt.repo.transfer.TransferHistoryRepo;
import com.gerp.usermgmt.repo.transfer.TransferRequestRepo;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.organization.fiscalyear.FiscalYearService;
import com.gerp.usermgmt.token.TokenProcessorService;
import com.gerp.shared.utils.BASE64DecodedMultipartFile;
import com.gerp.usermgmt.util.DocumentUtil;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SaruwaServiceImpl implements SaruwaService {

    private final TransferRequestRepo transferRequestRepo;
    private final TransferRequestMapper transferRequestMapper;
    private final CustomMessageSource customMessageSource;
    private final EmployeeRepo employeeRepo;
    private final TemplateServiceData templateServiceData;
    private final MessagingServiceData messagingServiceData;
    private final TokenProcessorService tokenProcessorService;
    private final DocumentUtil documentUtil;
    private final DateConverter dateConverter;
    private final String saruwa ="SARUWA_REQUEST";
    private final String saruwaLetter ="SARUWA_LETTER";
    private final String rawanaLetter ="RAWANA_LETTER";
    private final TransferHistoryRepo transferHistoryRepo;
    private final TransferMapper transferMapper;
    private final OfficeRepo officeRepo;
    private final RawanaRepo rawanaRepo;
    private final EmployeeService employeeService;
    private final FiscalYearService fiscalYearService;




    public SaruwaServiceImpl(TransferRequestRepo transferRequestRepo, TransferRequestMapper transferRequestMapper, CustomMessageSource customMessageSource, EmployeeRepo employeeRepo, TemplateServiceData templateServiceData, MessagingServiceData messagingServiceData, TokenProcessorService tokenProcessorService, DocumentUtil documentUtil, DateConverter dateConverter, TransferHistoryRepo transferHistoryRepo, TransferMapper transferMapper, OfficeRepo officeRepo, RawanaRepo rawanaRepo, EmployeeService employeeService, FiscalYearService fiscalYearService) {
        this.transferRequestRepo = transferRequestRepo;
        this.transferRequestMapper = transferRequestMapper;
        this.customMessageSource = customMessageSource;
        this.employeeRepo = employeeRepo;
        this.templateServiceData = templateServiceData;
        this.messagingServiceData = messagingServiceData;
        this.tokenProcessorService = tokenProcessorService;
        this.documentUtil = documentUtil;
        this.dateConverter = dateConverter;
        this.transferHistoryRepo = transferHistoryRepo;
        this.transferMapper = transferMapper;
        this.officeRepo = officeRepo;
        this.rawanaRepo = rawanaRepo;
        this.employeeService = employeeService;
        this.fiscalYearService = fiscalYearService;
    }


    @SneakyThrows
    public Long saruwaRequestService(Long id){
       Long documentId = transferRequestMapper.getDocumentId(id,saruwa);
       if(documentId != null){
           return documentId;
       }
        List<TransferResponsePojo> transferResponsePojoList = transferRequestMapper.getTransferRequest(id);
        TransferResponsePojo transferResponsePojo = transferResponsePojoList.get(0);
        SaruwaRequestPojo saruwaRequestPojo = getSaruwaRequestPojo(transferResponsePojo);
        String responseHtml =templateServiceData.getSaruwaRequestTemplate(saruwaRequestPojo);
        String fileName = "saruwa-request-"+transferResponsePojo.getEmployee().getCode()+".pdf";
        FileConverterPojo fileConverterPojo = new FileConverterPojo(responseHtml);
        byte[] fileArray = messagingServiceData.getFileConverter(fileConverterPojo);
        MultipartFile multipartFile = new BASE64DecodedMultipartFile(fileArray,fileName);
        if (multipartFile.getOriginalFilename() == null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"File"));
        }
        DocumentMasterResponsePojo documentMasterResponsePojo=  processDocument(multipartFile);
        TransferRequest transferRequest = transferRequestRepo.findById(id).orElse(new TransferRequest());

        if (documentMasterResponsePojo.getDocuments().get(0)==null){
            return null;
        }
        DocumentResponsePojo documentServiceData = documentMasterResponsePojo.getDocuments().get(0);
        TransferRequestDocuments transferRequestDocuments = new TransferRequestDocuments(documentServiceData.getId(),documentServiceData.getName(),saruwa,documentServiceData.getSizeKB()+"");

        if (transferRequest.getRelatedDocumentsList() == null){
            List<TransferRequestDocuments> transferRequestDocuments1 = new ArrayList<>();
            transferRequestDocuments1.add(transferRequestDocuments);
            transferRequest.setRelatedDocumentsList(transferRequestDocuments1);
        }else {
            transferRequest.getRelatedDocumentsList().add(transferRequestDocuments);
        }
        transferRequestRepo.save(transferRequest);
        return documentServiceData.getId();
    }

    private SaruwaRequestPojo getSaruwaRequestPojo(TransferResponsePojo transferResponsePojo) {
        String name = getName(transferResponsePojo.getEmployee().getNameNp(),transferResponsePojo.getEmployee().getMiddleNameNp(),transferResponsePojo.getEmployee().getLastNameNp());
        EmployeeOtherDetailsPojo employeeOtherDetailsPojo = new EmployeeOtherDetailsPojo(name,transferResponsePojo.getEmployee().getCode(),transferResponsePojo.getEmployee().getCurrentOffice().getNameNp(),transferResponsePojo.getEmployee().getDobBs(),transferResponsePojo.getEmployee().getJoinedDateNp(),transferResponsePojo.getEmployee().getDistrict().getNameNp());
        DesignationDetailPojo designationDetailPojo = new DesignationDetailPojo(transferResponsePojo.getEmployee().getCurrentDesignation().getNameNp(),transferResponsePojo.getEmployee().getCurrentPosition().getNameNp(),transferResponsePojo.getEmployee().getCurrentService().getNameNp());
        AtomicInteger atomicInteger = new AtomicInteger();
        AtomicInteger atomicInteger1 = new AtomicInteger();
        List<SaruwaPriority> saruwaPriorities = transferResponsePojo.getNewOfficeCode().stream().map(b -> new SaruwaPriority((atomicInteger.getAndIncrement()+1) + "", b.getNameNp(), b.getDistrict().getNameNp())).collect(Collectors.toList());
        List<ExperienceDetailPojo> experienceDetailPojos = transferResponsePojo.getPreviousWorkDetail().stream().map(c -> new ExperienceDetailPojo((atomicInteger1.getAndIncrement()+1) + "", c.getOldOffice().getNameNp(), c.getOldDesignation().getNameNp(), c.getOldService().getNameNp(), c.getOldPosition().getNameNp(), c.getFromDateNp(), c.getToDateNp(), Period.between(c.getFromDateEn(), c.getToDateEn()).getMonths() + "", Period.between(c.getFromDateEn(), c.getToDateEn()).getYears() + "", "")).collect(Collectors.toList());
        String date = transferResponsePojo.getSubmittedDate().substring(0,transferResponsePojo.getSubmittedDate().indexOf(" "));
        ChecklistTemplatePojo checklistTemplatePojo  = prepareCheckList(transferResponsePojo);
        checklistTemplatePojo.setEmployeeDetail(employeeOtherDetailsPojo);
        checklistTemplatePojo.setEmployeeDesignation(transferResponsePojo.getEmployee().getCurrentDesignation().getNameNp());
        return new SaruwaRequestPojo(dateConverter.convertAdToBs(date),employeeOtherDetailsPojo,designationDetailPojo,saruwaPriorities,experienceDetailPojos,checklistTemplatePojo);
    }

    private ChecklistTemplatePojo prepareCheckList(TransferResponsePojo transferResponsePojo) {
        ChecklistTemplatePojo checklistTemplatePojo = new ChecklistTemplatePojo();
        ChecklistResponsePojo cl = transferResponsePojo.getChecklists().get(0);
        if (cl.getStatus()){
            checklistTemplatePojo.setDurationComplete(true);
            checklistTemplatePojo.setDurationInComplete(false);
        }else {
            checklistTemplatePojo.setDurationComplete(false);
            checklistTemplatePojo.setDurationInComplete(true);
        }

         cl = transferResponsePojo.getChecklists().get(1);
        if (cl.getStatus()){
            checklistTemplatePojo.setWorkFirstTime(true);
            checklistTemplatePojo.setWorkRepetition(false);
        }else {
            checklistTemplatePojo.setWorkFirstTime(false);
            checklistTemplatePojo.setWorkRepetition(true);
        }

        cl = transferResponsePojo.getChecklists().get(2);
        if (cl.getStatus()){
            checklistTemplatePojo.setNearHome(true);
            checklistTemplatePojo.setFarHome(false);
        }else {
            checklistTemplatePojo.setNearHome(false);
            checklistTemplatePojo.setFarHome(true);
        }

        cl = transferResponsePojo.getChecklists().get(3);
        if (cl.getStatus()){
            checklistTemplatePojo.setCoupleInPublicService(true);
            checklistTemplatePojo.setCoupleNotInPublicService(false);
        }else {
            checklistTemplatePojo.setCoupleInPublicService(false);
            checklistTemplatePojo.setCoupleNotInPublicService(true);
        }

        cl = transferResponsePojo.getChecklists().get(4);
        if (cl.getStatus()){
            checklistTemplatePojo.setEmployeeAboveFifty(true);
            checklistTemplatePojo.setEmployeeBelowFifty(false);
        }else {
            checklistTemplatePojo.setEmployeeAboveFifty(false);
            checklistTemplatePojo.setEmployeeBelowFifty(true);
        }

        cl = transferResponsePojo.getChecklists().get(5);
        if (cl.getStatus()){
            checklistTemplatePojo.setHasFazil(true);
            checklistTemplatePojo.setHasNotfazil(false);
        }else {
            checklistTemplatePojo.setHasFazil(false);
            checklistTemplatePojo.setHasNotfazil(true);
        }

        cl = transferResponsePojo.getChecklists().get(6);
        if (cl.getStatus()){
            checklistTemplatePojo.setHasStudyLetter(true);
            checklistTemplatePojo.setHasNotStudyLetter(false);
        }else {
            checklistTemplatePojo.setHasStudyLetter(false);
            checklistTemplatePojo.setHasNotStudyLetter(true);
        }

        cl = transferResponsePojo.getChecklists().get(7);
        if (cl.getStatus()){
            checklistTemplatePojo.setHasMinistryLetter(true);
            checklistTemplatePojo.setHasNotMinistryLetter(false);
        }else {
            checklistTemplatePojo.setHasMinistryLetter(false);
            checklistTemplatePojo.setHasNotMinistryLetter(true);
        }

        cl = transferResponsePojo.getChecklists().get(8);
        if (cl.getStatus()){
            checklistTemplatePojo.setHasPostVacant(true);
            checklistTemplatePojo.setHasNotPostVacant(false);
        }else {
            checklistTemplatePojo.setHasPostVacant(false);
            checklistTemplatePojo.setHasNotPostVacant(true);
        }

        cl = transferResponsePojo.getChecklists().get(9);
        if (cl.getStatus()){
            checklistTemplatePojo.setExperienceAboveTenYears(true);
            checklistTemplatePojo.setExperienceBelowTenYears(false);
        }else {
            checklistTemplatePojo.setExperienceAboveTenYears(false);
            checklistTemplatePojo.setExperienceBelowTenYears(true);
        }

        cl = transferResponsePojo.getChecklists().get(10);
        if (cl.getStatus()){
            checklistTemplatePojo.setHolidayBelowOneYear(true);
            checklistTemplatePojo.setHolidayForOneYear(false);
        }else {
            checklistTemplatePojo.setHolidayBelowOneYear(false);
            checklistTemplatePojo.setHolidayForOneYear(true);
        }

        checklistTemplatePojo.setAttendanceDays(transferResponsePojo.getAttendanceTotal());
        return checklistTemplatePojo;
    }

    private String getName(String firstName, String middleName, String lastName) {
        if (middleName== null && lastName== null){
            return firstName;
        }else if (middleName== null){
            return firstName + " "+ lastName ;
        }else{
            return  firstName +" "+middleName+ " "+lastName;
        }
    }

    @SneakyThrows
    public Long saruwaLetterService(Long id){
        Long documentId = transferMapper.getSaruwaLetterDocumentId(id,saruwaLetter);
        if(documentId != null){
            return documentId;
        }

        TransferHistory transferHistory = transferHistoryRepo.findById(id).orElse(new TransferHistory());
        if (!transferHistory.getApproved().equals( Status.A.getValueEnglish())){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notAprove,"Transfer"));
        }
        TransferSubmissionResponsePojo dto = transferMapper.getTransferById(id,null);
        String officeName = officeRepo.findById(tokenProcessorService.getOfficeCode()).orElse(new Office()).getNameNp();
        String name = getName(dto.getEmployeeToBeTransfer().getNameNp(),dto.getEmployeeToBeTransfer().getMiddleNameNp(),dto.getEmployeeToBeTransfer().getLastNameNp());

        EmployeeDetailsPojo employeeDetailsPojo = new EmployeeDetailsPojo(name,dto.getEmployeeToBeTransfer().getCode());
        SabikDetail sabikDetail = new SabikDetail(dto.getFromPosition().getNameNp(),dto.getFromDesignation().getNameNp());
        setService(dto.getFromService().getCode(),sabikDetail,true,null);
        SabikDetail newSabikDetail = new SabikDetail(dto.getToPosition().getNameNp(),dto.getToDesignation().getNameNp());
        setService(dto.getToService().getCode(),newSabikDetail,true,null);
        SaruwaLetterPojo saruwaLetterPojo = new SaruwaLetterPojo(officeName,transferHistory.getApprovedDateNp(),Period.between(dto.getExpectedDepartureDateEn(),transferHistory.getLastModifiedDate().toLocalDateTime().toLocalDate()).getDays()+"",sabikDetail,newSabikDetail,employeeDetailsPojo);
        String responseHtml =templateServiceData.getSaruwaTemplate(saruwaLetterPojo);

        String fileName = "saruwa-letter-"+dto.getEmployeeToBeTransfer().getCode()+".pdf";
        FileConverterPojo fileConverterPojo = new FileConverterPojo(responseHtml);
        byte[] fileArray = messagingServiceData.getFileConverter(fileConverterPojo);
        MultipartFile multipartFile = new BASE64DecodedMultipartFile(fileArray,fileName);
        if (multipartFile.getOriginalFilename() == null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"File"));
        }
        DocumentMasterResponsePojo documentMasterResponsePojo=  processDocument(multipartFile);
        if (documentMasterResponsePojo.getDocuments().get(0)==null){
            return null;
        }
        DocumentResponsePojo documentServiceData = documentMasterResponsePojo.getDocuments().get(0);
        TransferDocuments transferDocuments = new TransferDocuments(documentServiceData.getId(),documentServiceData.getName(),saruwaLetter,documentServiceData.getSizeKB()+"");

        if (transferHistory.getRelatedDocumentsList() == null){
            List<TransferDocuments> transferDocumentsList = new ArrayList<>();
            transferDocumentsList.add(transferDocuments);
            transferHistory.setRelatedDocumentsList(transferDocumentsList);
        }else {
            transferHistory.getRelatedDocumentsList().add(transferDocuments);
        }
       transferHistoryRepo.save(transferHistory);
        return documentServiceData.getId();
    }

    private void setService(String code,SabikDetail sabikDetail,boolean isSaruwa,SaruwaDetail saruwaDetail) {
        List<ServicePojo> serviceList = getServicePojos(code,new ArrayList<>());
        String mainService = "";
        StringBuilder group = new StringBuilder();

        int size = serviceList.size();
        if (size -1 > 0){
            mainService = serviceList.get(size-1).getNameNp();
            if (size -2 >0){
                for (int i = size-2 ; i>=0;i-- ){
                    group.append(serviceList.get(i).getNameNp()).append("/");
                }
            }
        }
        if (isSaruwa){
            sabikDetail.setService(mainService);
            sabikDetail.setGroup(group.toString());
        }else {
            saruwaDetail.setService(mainService);
            saruwaDetail.setGroup(group.toString());
        }
    }

    private List<ServicePojo> getServicePojos(String code,List<ServicePojo> serviceList) {

        ServicePojo service = transferMapper.getService(code);
        serviceList.add(service);
       if (service.getParentCode() == null){
           return serviceList;
       }
       getServicePojos(service.getParentCode()+"",serviceList);
       return serviceList;
    }

    private MultipartFile convertToPDFANDMultipart(String responseHtml,String fileName) throws IOException {
        String fontName1 = ResourceUtils.getFile("classpath:font/Hind-Light.ttf").getAbsolutePath();
//        String fontName2 = ResourceUtils.getFile("classpath:font/Devnew.ttf").getAbsolutePath();
//        String fontName3 = ResourceUtils.getFile("classpath:font/Poppins-Regular.ttf").getAbsolutePath();
        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider(false, false, false);
        FontProgram fontProgram = FontProgramFactory.createFont(fontName1);
//        FontProgram fontProgram2 = FontProgramFactory.createFont(fontName2);
//        FontProgram fontProgram3 = FontProgramFactory.createFont(fontName3);
        fontProvider.addFont(fontProgram);
//        fontProvider.addFont(fontProgram2);
//        fontProvider.addFont(fontProgram3);
        properties.setFontProvider(fontProvider);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(responseHtml, buffer,properties);
        byte[] pdfAsBytes = buffer.toByteArray();

        return new BASE64DecodedMultipartFile(pdfAsBytes,fileName);

    }

    @Override
    public Long generateSaruwa(Long id) {
        return saruwaRequestService(id);
    }

    @Override
    public Long generateSaruwaLetter(Long id) {
        return saruwaLetterService(id);
    }

    @Override
    public Long generateRawana(Long id) {
        Optional<RawanaDetails> rawanaDetailsOptional = rawanaRepo.findById(id);
        if (!rawanaDetailsOptional.isPresent()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"rawana"));
        }
        RawanaDetails rawanaDetails = rawanaDetailsOptional.orElse(new RawanaDetails());
        Long documentId = transferMapper.getSaruwaLetterDocumentId(rawanaDetails.getTransferHistoryId(),rawanaLetter);
        if(documentId != null){
            return documentId ;
        }

        TransferHistory transferHistory = transferHistoryRepo.findById(rawanaDetails.getTransferHistoryId()).orElse(new TransferHistory());
        if (!transferHistory.getApproved().equals( Status.A.getValueEnglish())){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notAprove,"Transfer"));
        }
        TransferSubmissionResponsePojo dto = transferMapper.getTransferById(rawanaDetails.getTransferHistoryId(),null);
        String officeName = officeRepo.findById(tokenProcessorService.getOfficeCode()).orElse(new Office()).getNameNp();
        String name = getName(dto.getEmployeeToBeTransfer().getNameNp(),dto.getEmployeeToBeTransfer().getMiddleNameNp(),dto.getEmployeeToBeTransfer().getLastNameNp());

        RawanaTemplate rawanaTemplate = getRawanaTemplate(rawanaDetails, transferHistory, dto, officeName, name);
        if (rawanaDetails.getHandover()){
            rawanaTemplate.setBarbujhanathGareko(true);
            rawanaTemplate.setBarbujhanathNagareko(false);
        }else{
            rawanaTemplate.setBarbujhanathGareko(false);
            rawanaTemplate.setBarbujhanathNagareko(true);
        }
        String html=   templateServiceData.getRawanaLetterTemplate(rawanaTemplate);
        DocumentResponsePojo documentServiceData = getDocumentResponsePojo(transferHistory, dto, html);
        transferHistoryRepo.save(transferHistory);
        return documentServiceData.getId();
    }

    private DocumentResponsePojo getDocumentResponsePojo(TransferHistory transferHistory, TransferSubmissionResponsePojo dto, String html) {
        FileConverterPojo fileConverterPojo = new FileConverterPojo(html);
        byte[] fileArray = messagingServiceData.getFileConverter(fileConverterPojo);
        String fileName = "rawana-letter-"+dto.getEmployeeToBeTransfer().getCode()+".pdf";
        MultipartFile multipartFile = new BASE64DecodedMultipartFile(fileArray,fileName);
        if (multipartFile.getOriginalFilename() == null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"File"));
        }
        DocumentMasterResponsePojo documentMasterResponsePojo=  processDocument(multipartFile);
        if (documentMasterResponsePojo.getDocuments().get(0)==null){
            throw new RuntimeException(customMessageSource.get("error.rawana"));
        }
        DocumentResponsePojo documentServiceData = documentMasterResponsePojo.getDocuments().get(0);
        TransferDocuments transferDocuments = new TransferDocuments(documentServiceData.getId(),documentServiceData.getName(),rawanaLetter,documentServiceData.getSizeKB()+"");

        if (transferHistory.getRelatedDocumentsList() == null){
            List<TransferDocuments> transferDocumentsList = new ArrayList<>();
            transferDocumentsList.add(transferDocuments);
            transferHistory.setRelatedDocumentsList(transferDocumentsList);
        }else {
            transferHistory.getRelatedDocumentsList().add(transferDocuments);
        }
        return documentServiceData;
    }

    private RawanaTemplate getRawanaTemplate(RawanaDetails rawanaDetails, TransferHistory transferHistory, TransferSubmissionResponsePojo dto, String officeName, String name) {
        EmployeeDetailsPojo employeeDetailsPojo = new EmployeeDetailsPojo(name,dto.getEmployeeToBeTransfer().getCode());
        SaruwaDetail sabikDetail = new SaruwaDetail(transferHistory.getApprovedDateNp(),dto.getFromDesignation().getNameNp(),dto.getFromPosition().getNameNp(),dto.getFromOffice().getNameNp());
        setService(dto.getFromService().getCode(),null,false,sabikDetail);

        SaruwaDetail saruwaDetail = new SaruwaDetail(transferHistory.getApprovedDateNp(),dto.getToDesignation().getNameNp(),dto.getToPosition().getNameNp(),dto.getToOffice().getNameNp());
        setService(dto.getToService().getCode(),null,false,saruwaDetail);


        EmployeeMinimalPojo employeeMinimalPojo = employeeService.getOfficeHeadEmployee(dto.getToOffice().getCode());
        OfficeDetailRawana officeDetailRawana = new OfficeDetailRawana();
        officeDetailRawana.setOfficeName(dto.getToOffice().getNameNp());
        officeDetailRawana.setOfficeAddress(dto.getToOffice().getDistrict().getNameNp() + ", नेपाल");
        if (employeeMinimalPojo != null){
            officeDetailRawana.setOfficeHead(employeeDetailsPojo.getEmployeeName());
        }

        LeaveTakenPojo leaveTakenPojo = new LeaveTakenPojo();
        AccumulatedLeavePojo accumulatedLeavePojo = new AccumulatedLeavePojo();
        convertToLeaves(transferHistory,leaveTakenPojo,accumulatedLeavePojo);


        String[] registeredDate = getStringsDate(rawanaDetails.getInsuranceRegisteredNp());
        SabikLifeInsurance sabikLifeInsurance = new SabikLifeInsurance(registeredDate[0],registeredDate[1],registeredDate[2]);

        String[] paidDate = getStringsDate(rawanaDetails.getInsurancePaidNp());
        SabikLifeInsurance sabikLifeInsuranceYearly = new SabikLifeInsurance(paidDate[0],paidDate[1],paidDate[2]);

        NewRetirementPlan newRetirementPlan = new NewRetirementPlan();

        String festival = rawanaDetails.getFestivalName() +"," + rawanaDetails.getFestivalDateNp();
        RamanaDetailLetterPojo ramanaDetailLetterPojo = new RamanaDetailLetterPojo(rawanaDetails.getDailyExpenses().toString(),rawanaDetails.getSalaryIncrementDateNp(),rawanaDetails.getEmployeeProvidentFund().toString(),rawanaDetails.getIncomeTax().toString(),sabikLifeInsurance,sabikLifeInsuranceYearly,rawanaDetails.getOldPension(),newRetirementPlan,festival,rawanaDetails.getPaternityCare(),name);
        return new RawanaTemplate(officeName,fiscalYearService.getActiveYear().getYearNp(),dateConverter.convertAdToBs(LocalDate.now().toString()),officeDetailRawana,employeeDetailsPojo,sabikDetail,saruwaDetail,leaveTakenPojo,accumulatedLeavePojo,rawanaDetails.getTransferDateNp(),rawanaDetails.getTotalAttendance().toString(),rawanaDetails.getBasicSalary().toString(),rawanaDetails.getIncreasedSalary().toString(),rawanaDetails.getLastReceivedDateNp(),rawanaDetails.getProvidentFund().toString(),rawanaDetails.getPaymentMedical().toString(),ramanaDetailLetterPojo);
    }

    private void convertToLeaves(TransferHistory transferHistory, LeaveTakenPojo leaveTakenPojo1, AccumulatedLeavePojo accumulatedLeavePojo1) {
        LocalDate joinDate = employeeRepo.findById(transferHistory.getPisCode()).orElse(new Employee()).getCurOfficeJoinDtEn();
        if (joinDate == null){
            LocalDate now = LocalDate.now();
            joinDate =now.minus(Period.ofYears(1));
        }
        List<AttendaceDetailPojo> attendaceDetailPojos = templateServiceData.getAttendance(joinDate.toString(),LocalDate.now().toString());

        for (AttendaceDetailPojo adp:attendaceDetailPojos){
            switch (adp.getNameEn().trim()){
                case "Leave without pay":
                    leaveTakenPojo1.setUnpaidLeave(adp.getLeaveTakenDays());
                    accumulatedLeavePojo1.setUnpaidLeave(adp.getTotalAllowedDays());
                    break;

                case "Home Leave":
                    leaveTakenPojo1.setHomeLeave(adp.getLeaveTakenDays());
                    accumulatedLeavePojo1.setHomeLeave(adp.getTotalAllowedDays());
                    break;

                case "Festival Leave":
                    leaveTakenPojo1.setPublicHoliday(adp.getLeaveTakenDays());
                    break;

                case "Sick leave":
                    leaveTakenPojo1.setSickLeave(adp.getLeaveTakenDays());
                    accumulatedLeavePojo1.setSickLeave(adp.getTotalAllowedDays());
                    break;

                case "Paternity leave":
                    leaveTakenPojo1.setPaternityLeave(adp.getLeaveTakenDays());
                    accumulatedLeavePojo1.setPaternityLeave(adp.getTotalAllowedDays());
                    break;

                case "Study leave":
                    leaveTakenPojo1.setStudyLeave(adp.getLeaveTakenDays());
                    accumulatedLeavePojo1.setStudyLeave(adp.getTotalAllowedDays());
                    break;

                case "Emergency leave":
                    leaveTakenPojo1.setEmergencyLeave(adp.getLeaveTakenDays());
                    accumulatedLeavePojo1.setEmergencyLeave(adp.getTotalAllowedDays());
                    break;
            }
        }
    }

    private String[] getStringsDate(String rawanaDetails) {
        return rawanaDetails.split("-");
    }

    private DocumentMasterResponsePojo processDocument(MultipartFile document) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocument(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .tags(Arrays.asList("saruwa_letter"))
                        .type("1")
                        .build(),
                document
        );
       return pojo;
    }

}
