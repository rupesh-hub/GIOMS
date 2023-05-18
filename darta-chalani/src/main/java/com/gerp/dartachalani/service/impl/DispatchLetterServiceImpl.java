package com.gerp.dartachalani.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.Proxy.*;
import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.document.DocumentMasterResponsePojo;
import com.gerp.dartachalani.dto.document.DocumentResponsePojo;
import com.gerp.dartachalani.dto.document.DocumentSavePojo;
import com.gerp.dartachalani.dto.document.SysDocumentsPojo;
import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.dartachalani.dto.enums.SearchRecommentionTypeEnum;
import com.gerp.dartachalani.dto.group.OfficeGroupPojo;
import com.gerp.dartachalani.mapper.*;
import com.gerp.dartachalani.model.DelegationTableMapper;
import com.gerp.dartachalani.model.dispatch.*;
import com.gerp.dartachalani.model.draft.share.DraftShare;
import com.gerp.dartachalani.model.draft.share.DraftShareLog;
import com.gerp.dartachalani.model.external.ExternalRecords;
import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.dartachalani.model.memo.MemoReference;
import com.gerp.dartachalani.model.signature.SignatureType;
import com.gerp.dartachalani.model.signature.SignatureVerificationLog;
import com.gerp.dartachalani.repo.*;
import com.gerp.dartachalani.repo.signature.SignatureVerificationLogRepository;
import com.gerp.dartachalani.service.DispatchLetterService;
import com.gerp.dartachalani.service.ReceivedLetterService;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.dartachalani.service.digitalSignature.VerifySignatureService;
import com.gerp.dartachalani.service.rabbitmq.RabbitMQService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.dartachalani.utils.DartaChalaniConstants;
import com.gerp.dartachalani.utils.DocumentUtil;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.BodarthaEnum;
import com.gerp.shared.enums.Status;
import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Service
@Transactional
@Slf4j
public class DispatchLetterServiceImpl extends GenericServiceImpl<DispatchLetter, Long> implements DispatchLetterService {

    private final DispatchLetterRepo dispatchLetterRepo;
    private final DispatchForwardRepo dispatchForwardRepo;
    private final DispatchLetterReceiverInternalRepo dispatchLetterReceiverInternalRepo;
    private final DispatchLetterReceiverExternalRepo dispatchLetterReceiverExternalRepo;
    private final DispatchDocumentDetailsRepo dispatchDocumentDetailsRepo;
    private final DispatchLetterUpdateHistoryRepo dispatchLetterUpdateHistoryRepo;
    private final DispatchLetterMapper dispatchLetterMapper;
    private final CustomMessageSource customMessageSource;
    private final DmsService dmsService;
    private final ReceivedLetterService receivedLetterService;
    private final UserMgmtServiceData userMgmtServiceData;
    private final InitialServiceImpl initialService;
    private final DispatchLetterReviewRepo dispatchLetterReviewRepo;
    private final DispatchLetterReviewDetailRepo dispatchLetterReviewDetail;
    private final DispatchLetterReceiveInternalDetailRepo receiveInternalDetailRepo;
    private final MemoRepo memoRepo;
    private final MemoReferenceRepo memoReferenceRepo;
    private final MemoMapper memoMapper;
    private final ReceivedLetterMapper receivedLetterMapper;
    private final MemoReferenceMapper memoReferenceMapper;
    private final ExternalRecordsRepo externalRecordsRepo;
    private final SignatureDataRepo signatureDataRepo;
    private final SignatureVerificationLogRepository signatureVerificationLogRepository;
    private final DraftShareRepo draftShareRepo;
    private final DraftShareLogRepo draftShareLogRepo;
    private final ConvertHtlToFileProxy convertHtlToFileProxy;
    private final DocumentServiceData documentServiceData;
    private final DispatchPdfDataRepo dispatchPdfDataRepo;
    private final VerifySignatureService verifySignatureService;
    private final FooterDataMapper footerDataMapper;
    private final DelegationTableMapperRepo delegationTableMapperRepo;
    private final String MODULE_KEY = PermissionConstants.DISPATCH_LETTER_MODULE_NAME;
    private final String MODULE_APPROVAL_KEY = PermissionConstants.DISPATCH_APPROVAL;
    private final String DELEGATED_NEP = DartaChalaniConstants.DELEGATED_NEP;
    private final String DELEGATED_EN = DartaChalaniConstants.DELEGATED_EN;
    private final String ADDITIONAL_RESPONSIBILITY_NEP = DartaChalaniConstants.ADDITIONAL_RESPONSIBILITY_NEP;
    private final String ADDITIONAL_RESPONSIBILITY_EN = DartaChalaniConstants.ADDITIONAL_RESPONSIBILITY_EN;
    private final String TRANSFER_FROM_PIS_CODE = DartaChalaniConstants.TRANSFER_FROM_PIS_CODE;
    private final String TRANSFER_FROM_SECTION_CODE = DartaChalaniConstants.TRANSFER_FROM_SECTION_CODE;
    @Autowired
    private DocumentUtil documentUtil;
    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private DateConverter dateConverter;
    @Autowired
    private LetterTemplateProxy letterTemplateProxy;
    @Autowired
    private RabbitMQService notificationService;

    public DispatchLetterServiceImpl(DispatchLetterRepo dispatchLetterRepo,
                                     DispatchForwardRepo dispatchForwardRepo,
                                     DispatchLetterReceiverInternalRepo dispatchLetterReceiverInternalRepo,
                                     DispatchLetterReceiverExternalRepo dispatchLetterReceiverExternalRepo,
                                     DispatchLetterMapper dispatchLetterMapper,
                                     CustomMessageSource customMessageSource,
                                     DmsService dmsService,
                                     ReceivedLetterService receivedLetterService,
                                     UserMgmtServiceData userMgmtServiceData,
                                     InitialServiceImpl initialService,
                                     DispatchLetterReviewRepo dispatchLetterReviewRepo,
                                     DispatchLetterReviewDetailRepo dispatchLetterReviewDetail,
                                     DispatchLetterReceiveInternalDetailRepo receiveInternalDetailRepo,
                                     DispatchDocumentDetailsRepo dispatchDocumentDetailsRepo,
                                     MemoRepo memoRepo,
                                     MemoReferenceRepo memoReferenceRepo,
                                     ReceivedLetterMapper receivedLetterMapper,
                                     MemoMapper memoMapper,
                                     MemoReferenceMapper memoReferenceMapper,
                                     ExternalRecordsRepo externalRecordsRepo,
                                     SignatureDataRepo signatureDataRepo,
                                     ConvertHtlToFileProxy convertHtlToFileProxy,
                                     DocumentServiceData documentServiceData,
                                     DispatchPdfDataRepo dispatchPdfDataRepo,
                                     VerifySignatureService verifySignatureService,
                                     DispatchLetterUpdateHistoryRepo dispatchLetterUpdateHistoryRepo,
                                     FooterDataMapper footerDataMapper,
                                     DelegationTableMapperRepo delegationTableMapperRepo,
                                     SignatureVerificationLogRepository signatureVerificationLogRepository,
                                     DraftShareRepo draftShareRepo,
                                     DraftShareLogRepo draftShareLogRepo) {
        super(dispatchLetterRepo);
        this.dispatchLetterRepo = dispatchLetterRepo;
        this.dispatchForwardRepo = dispatchForwardRepo;
        this.dispatchLetterReceiverInternalRepo = dispatchLetterReceiverInternalRepo;
        this.dispatchLetterReceiverExternalRepo = dispatchLetterReceiverExternalRepo;
        this.dispatchLetterMapper = dispatchLetterMapper;
        this.customMessageSource = customMessageSource;
        this.dmsService = dmsService;
        this.receivedLetterService = receivedLetterService;
        this.userMgmtServiceData = userMgmtServiceData;
        this.initialService = initialService;
        this.dispatchLetterReviewRepo = dispatchLetterReviewRepo;
        this.dispatchDocumentDetailsRepo = dispatchDocumentDetailsRepo;
        this.dispatchLetterReviewDetail = dispatchLetterReviewDetail;
        this.receiveInternalDetailRepo = receiveInternalDetailRepo;
        this.memoRepo = memoRepo;
        this.memoReferenceRepo = memoReferenceRepo;
        this.receivedLetterMapper = receivedLetterMapper;
        this.memoMapper = memoMapper;
        this.memoReferenceMapper = memoReferenceMapper;
        this.externalRecordsRepo = externalRecordsRepo;
        this.signatureDataRepo = signatureDataRepo;
        this.convertHtlToFileProxy = convertHtlToFileProxy;
        this.documentServiceData = documentServiceData;
        this.dispatchPdfDataRepo = dispatchPdfDataRepo;
        this.verifySignatureService = verifySignatureService;
        this.dispatchLetterUpdateHistoryRepo = dispatchLetterUpdateHistoryRepo;
        this.footerDataMapper = footerDataMapper;
        this.delegationTableMapperRepo = delegationTableMapperRepo;
        this.signatureVerificationLogRepository = signatureVerificationLogRepository;
        this.draftShareRepo = draftShareRepo;
        this.draftShareLogRepo = draftShareLogRepo;
    }

    @Override
    public DispatchLetter findById(Long uuid) {
        DispatchLetter dispatchLetter = super.findById(uuid);
        if (dispatchLetter == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("dispatch.letter")));

        return dispatchLetter;
    }

    @Override
    public DispatchLetter saveLetter(DispatchLetterRequestPojo dispatchLetterRequestPojo) {

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo == null)
            throw new RuntimeException("Employee detail not found with id : " + tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ");

        Boolean isAlreadyApproved = Boolean.FALSE;
        if (dispatchLetterRequestPojo.getIsAlreadyApproved() != null && dispatchLetterRequestPojo.getIsAlreadyApproved()) {
            isAlreadyApproved = Boolean.TRUE;
        }

        DispatchLetter dispatchLetter = new DispatchLetter().builder()
                .letterPriority(dispatchLetterRequestPojo.getLetterPriority())
                .letterPrivacy(dispatchLetterRequestPojo.getLetterPrivacy())
                .content(dispatchLetterRequestPojo.getContent())
                .senderPisCode(tokenProcessorService.getPisCode())
                .fiscalYearCode(userMgmtServiceData.findActiveFiscalYear().getCode())
                .signee(dispatchLetterRequestPojo.getSignee())
                .senderOfficeCode(tokenProcessorService.getOfficeCode())
                .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() != null ? employeePojo.getFunctionalDesignationCode() : null : null)
                .subject(dispatchLetterRequestPojo.getSubject())
                .receiverType(dispatchLetterRequestPojo.getReceiverType())
                .isDraft(isAlreadyApproved ? Boolean.FALSE : dispatchLetterRequestPojo.getIsDraft())
                .dispatchLetterReceiverInternals(this.dispatchLetterInternal(dispatchLetterRequestPojo))
                .dispatchLetterReceiverExternals(this.dispatchLetterReceiverExternal(dispatchLetterRequestPojo))
                .singular(dispatchLetterRequestPojo.getSingular())
                .include(false)
                .isEnglish(dispatchLetterRequestPojo.getIsEnglish())
                .isAd(dispatchLetterRequestPojo.getIsAd())
                .hasSubject(dispatchLetterRequestPojo.getHasSubject())
                .isArchive(Boolean.FALSE)
                .isAlreadyApproved(isAlreadyApproved)
                .build();

        if (dispatchLetterRequestPojo.getDocuments() != null && !dispatchLetterRequestPojo.getDocuments().isEmpty())
            this.processDocument(dispatchLetterRequestPojo.getDocuments(), dispatchLetter, employeePojo, userMgmtServiceData.findActiveFiscalYear().getCode());

        DispatchLetter saved = dispatchLetterRepo.save(dispatchLetter);

//        OfficeTemplatePojo templateHeader = userMgmtServiceData.getOfficeTemplate(tokenProcessorService.getOfficeCode(), "H");
//
//        OfficeTemplatePojo templateFooter = userMgmtServiceData.getOfficeTemplate(tokenProcessorService.getOfficeCode(), "F");
//
//        if (Boolean.TRUE.equals(dispatchLetter.getIsEnglish())
//                && templateHeader != null
//                && (templateHeader.getTemplateEn() == null || !templateHeader.getTemplateEn().isEmpty()))
//            throw new CustomException("English header does not exist");
//        else if (templateHeader != null
//                && (templateHeader.getTemplateNp() == null || !templateHeader.getTemplateNp().isEmpty()))
//            throw new CustomException("Nepali header does not exist");
//
//        if (Boolean.TRUE.equals(dispatchLetter.getIsEnglish())
//                && templateFooter != null
//                && (templateFooter.getTemplateEn() == null || !templateFooter.getTemplateEn().isEmpty()))
//            throw new CustomException("English footer does not exist");
//        else if (templateFooter != null
//                && (templateFooter.getTemplateNp() == null || !templateFooter.getTemplateNp().isEmpty()))
//            throw new CustomException("Nepali footer does not exist");

        if (dispatchLetterRequestPojo.getCcReceiver() != null) {
            List<DispatchLetterReceiverInternal> internalCc = new ArrayList<>();
            dispatchLetterRequestPojo.getCcReceiver().forEach(
                    x -> {
                        DispatchLetterReceiverInternal dispatchLetterReceiverInternal = new DispatchLetterReceiverInternal().builder()
                                .receiverOfficeCode(x.getReceiverOfficeCode() == null ? tokenProcessorService.getOfficeCode() : x.getReceiverOfficeCode())
                                .receiverSectionId(x.getReceiverSectionId())
                                .receiverSectionName(x.getReceiverSectionName())
                                .toCC(true)
                                .within_organization(x.getWithin_organization() != null ? x.getWithin_organization() : x.getReceiverPisCode() != null ? Boolean.TRUE : Boolean.FALSE)
                                .receiverPisCode(x.getReceiverPisCode())
                                .receiverDesignationCode(x.getReceiverDesignationCode())
                                .completion_status(Status.P)
                                .senderPisCode(tokenProcessorService.getPisCode())
                                .bodarthaType(x.getBodarthaType())
                                .dispatchLetter(dispatchLetter)
                                .orderNumber(x.getOrder())
                                .remarks(x.getRemarks())
                                .isSectionName(x.getIsSectionName())
                                .salutation(x.getSalutation())
                                .build();
                        internalCc.add(dispatchLetterReceiverInternal);
                    });
            dispatchLetterReceiverInternalRepo.saveAll(internalCc);
        }

        if (dispatchLetterRequestPojo.getCcExternal() != null) {
            List<DispatchLetterReceiverExternal> externalCc = new ArrayList<>();
            dispatchLetterRequestPojo.getCcExternal().forEach(
                    x -> {
                        DispatchLetterReceiverExternal dispatchLetterReceiverExternal = new DispatchLetterReceiverExternal().builder()
                                .receiverAddress(x.getReceiverAddress())
                                .receiverEmail(x.getReceiverEmail())
                                .receiverName(x.getReceiverName())
                                .receiverOfficeSectionSubSection(x.getReceiverOfficeSectionSubSection())
                                .receiverPhoneNumber(x.getReceiverPhoneNumber())
                                .dispatch_letter_type(x.getDispatch_letter_type())
                                .toCc(true)
                                .senderOfficeCode(tokenProcessorService.getOfficeCode())
                                .senderPisCode(tokenProcessorService.getPisCode())
                                .bodarthaType(x.getBodarthaType())
                                .dispatchLetter(dispatchLetter)
                                .orderNumber(x.getOrder())
                                .remarks(x.getRemarks())
                                .build();
                        externalCc.add(dispatchLetterReceiverExternal);
                    });
            dispatchLetterReceiverExternalRepo.saveAll(externalCc);
        }

        SignatureData signatureData = new SignatureData().builder()
                .dispatchLetter(dispatchLetter)
                .pisCode(tokenProcessorService.getPisCode())
                .officeCode(tokenProcessorService.getOfficeCode())
                .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                .signatureIsActive(dispatchLetterRequestPojo.getSignatureIsActive())
                .signature(dispatchLetterRequestPojo.getSignatureIsActive() != null ? dispatchLetterRequestPojo.getSignatureIsActive() ? dispatchLetterRequestPojo.getSignature() : null : null)
                .hashContent(dispatchLetterRequestPojo.getHashContent())
                .includeSectionId(dispatchLetterRequestPojo.getIncludeSectionId())
                .includedSectionId(dispatchLetterRequestPojo.getIncludedSectionId())
                .includeSectionInLetter(dispatchLetterRequestPojo.getIncludeSectionInLetter())
                .build();

        signatureDataRepo.save(signatureData);

        if (dispatchLetterRequestPojo.getTaskId() != null || dispatchLetterRequestPojo.getProjectId() != null) {
            ExternalRecords records = new ExternalRecords().builder()
                    .dispatchLetter(dispatchLetter)
                    .projectId(dispatchLetterRequestPojo.getProjectId())
                    .taskId(dispatchLetterRequestPojo.getTaskId())
                    .build();
            externalRecordsRepo.save(records);
        }

        if (dispatchLetterRequestPojo.getSystemDocuments() != null && !dispatchLetterRequestPojo.getSystemDocuments().isEmpty()) {
            List<DispatchLetterDocumentDetails> sysDocs = new ArrayList<>();
            for (SysDocumentsPojo documentPojo : dispatchLetterRequestPojo.getSystemDocuments()) {
                DispatchLetterDocumentDetails systemDoc = new DispatchLetterDocumentDetails().builder()
                        .documentId(documentPojo.getDocumentId())
                        .documentName(documentPojo.getDocumentName())
                        .include(true)
                        .dispatchLetter(dispatchLetter)
                        .build();
                sysDocs.add(systemDoc);
            }
            dispatchDocumentDetailsRepo.saveAll(sysDocs);
        }

        if (dispatchLetterRequestPojo.getReferenceMemoId() != null && !dispatchLetterRequestPojo.getReferenceMemoId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : dispatchLetterRequestPojo.getReferenceMemoId()) {
                Memo memo1 = memoRepo.findById(id).orElseThrow(() -> new RuntimeException("No Memo Found"));
                MemoReference memoReference = new MemoReference().builder()
                        .dispatchLetter(dispatchLetter)
                        .referencedMemoId(id)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isEditable(true)
                        .include(true)
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (dispatchLetterRequestPojo.getReceivedReferenceId() != null && !dispatchLetterRequestPojo.getReceivedReferenceId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : dispatchLetterRequestPojo.getReceivedReferenceId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .dispatchLetter(dispatchLetter)
                        .dartaReferenceId(id)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .include(true)
                        .isEditable(true)
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (dispatchLetterRequestPojo.getChalaniReferenceId() != null && !dispatchLetterRequestPojo.getChalaniReferenceId().isEmpty()) {
            List<MemoReference> chalaniReferences = new ArrayList<>();

            for (Long id : dispatchLetterRequestPojo.getChalaniReferenceId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .dispatchLetter(dispatchLetter)
                        .chalaniReferenceId(id)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .include(true)
                        .isEditable(true)
                        .isAttach(Boolean.TRUE)
                        .build();
                chalaniReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(chalaniReferences);
        }

        return dispatchLetter;
    }

    private void processDocument(List<MultipartFile> documents, DispatchLetter dispatchLetter, EmployeePojo employeePojo, String activeFiscalYear) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .tag("darta_chalani")
                        .subModuleName("chalani")
                        .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                        .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                        .fiscalYearCode(activeFiscalYear)
                        .type("1")
                        .build(),
                documents
        );

        if (pojo == null)
            throw new RuntimeException("कागच पत्र सेब हुन सकेन");

        if (pojo != null) {
            dispatchLetter.setDispatchLetterDocumentDetails(
                    pojo.getDocuments().stream().map(
                            x -> new DispatchLetterDocumentDetails().builder()
                                    .documentId(x.getId())
                                    .documentName(x.getName())
                                    .sizeKB(x.getSizeKB())
                                    .include(true)
                                    .build()
                    ).collect(Collectors.toList())
            );
        }

    }

    private void updateDocument(List<MultipartFile> documents, DispatchLetter dispatchLetter, EmployeePojo employeePojo, String activeFiscalYear) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .tag("darta_chalani")
                        .subModuleName("chalani")
                        .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                        .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                        .fiscalYearCode(activeFiscalYear)
                        .type("1")
                        .build(),
                documents
        );

        if (pojo == null)
            throw new RuntimeException("कागच पत्र सेब हुन सकेन");

        if (pojo != null) {
            dispatchLetter.setDispatchLetterDocumentDetails(
                    pojo.getDocuments().stream().map(
                            x -> new DispatchLetterDocumentDetails().builder()
                                    .documentId(x.getId())
                                    .documentName(x.getName())
                                    .include(true)
                                    .build()
                    ).collect(Collectors.toList())
            );
        }

    }

    private void deleteDocuments(List<Long> documentsToRemove) {
        if (documentsToRemove != null && !documentsToRemove.isEmpty()) {
            for (Long id : documentsToRemove) {
                dispatchDocumentDetailsRepo.softDeleteDoc(id);
            }
        }
    }

    @Override
    public DispatchLetter update(DispatchLetterRequestPojo dispatchLetterRequestPojo) {

        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user section
        checkUserSection(employeePojo);

        String tokenUserSectionCode = employeePojo.getSectionId();

        Boolean isAlreadyApproved = Boolean.FALSE;
        if (dispatchLetterRequestPojo.getIsAlreadyApproved() != null && dispatchLetterRequestPojo.getIsAlreadyApproved()) {
            isAlreadyApproved = Boolean.TRUE;
        }

        DispatchLetter dispatchLetter = this.findById(dispatchLetterRequestPojo.getId());
        SignatureData signatureData = signatureDataRepo.getByDispatchId(dispatchLetter.getId());

        String sectionCode = employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null;
        String designationCode = employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null;
        DispatchLetterUpdateHistory updateHistory = new DispatchLetterUpdateHistory().builder()
                .dispatchLetter(dispatchLetter)
                .pisCode(tokenPisCode)
                .sectionCode(sectionCode)
                .designationCode(designationCode)
                .subject(dispatchLetter.getSubject())
                .content(dispatchLetter.getContent())
                .signatureIsActive(signatureData.getSignatureIsActive())
                .signatureData(signatureData.getSignature())
                .hashContent(signatureData.getHashContent())
                .build();
        updateHistory = dispatchLetterUpdateHistoryRepo.save(updateHistory);

        dispatchLetterReviewRepo.setReviewContentId(updateHistory.getId(), dispatchLetter.getId());

        dispatchLetter.setLetterPriority(dispatchLetterRequestPojo.getLetterPriority());
        dispatchLetter.setLetterPrivacy(dispatchLetterRequestPojo.getLetterPrivacy());
        dispatchLetter.setContent(dispatchLetterRequestPojo.getContent());
        dispatchLetter.setSignee(dispatchLetterRequestPojo.getSignee());
        dispatchLetter.setSubject(dispatchLetterRequestPojo.getSubject());
        dispatchLetter.setIsDraft(isAlreadyApproved ? Boolean.FALSE : dispatchLetterRequestPojo.getIsDraft());
        dispatchLetter.setStatus(Status.P);
        dispatchLetter.setSingular(dispatchLetterRequestPojo.getSingular());
        dispatchLetter.setIsEnglish(dispatchLetterRequestPojo.getIsEnglish());
        dispatchLetter.setIsAd(dispatchLetterRequestPojo.getIsAd());
        dispatchLetter.setRemarks(dispatchLetterRequestPojo.getRemarks());
        dispatchLetter.setReceiverType(dispatchLetterRequestPojo.getReceiverType());
        dispatchLetter.getDispatchLetterReceiverInternals().clear();
        dispatchLetter.getDispatchLetterReceiverExternals().clear();
        dispatchLetter.setHasSubject(dispatchLetterRequestPojo.getHasSubject());
        dispatchLetter.setIsAlreadyApproved(isAlreadyApproved);
        if ((dispatchLetterRequestPojo.getToReceivers() != null && !dispatchLetterRequestPojo.getToReceivers().isEmpty()) || (dispatchLetterRequestPojo.getCcReceiver() != null && !dispatchLetterRequestPojo.getCcReceiver().isEmpty())) {
            dispatchLetter.getDispatchLetterReceiverInternals().addAll(this.dispatchLetterInternal(dispatchLetterRequestPojo));
        }
        if (dispatchLetterRequestPojo.getDispatchLetterReceiverExternals() != null && !dispatchLetterRequestPojo.getDispatchLetterReceiverExternals().isEmpty()) {
            dispatchLetter.getDispatchLetterReceiverExternals().addAll(this.dispatchLetterReceiverExternal(dispatchLetterRequestPojo));
        }

        dispatchLetterRepo.save(dispatchLetter);

        if (signatureData != null) {
            signatureData.setSignatureIsActive(dispatchLetterRequestPojo.getSignatureIsActive());
            signatureData.setSignature(dispatchLetterRequestPojo.getSignatureIsActive() != null ? dispatchLetterRequestPojo.getSignatureIsActive() ? dispatchLetterRequestPojo.getSignature() : null : null);
            signatureData.setIncludeSectionId(dispatchLetterRequestPojo.getIncludeSectionId());
            signatureData.setIncludedSectionId(dispatchLetterRequestPojo.getIncludedSectionId());
            signatureData.setHashContent(dispatchLetterRequestPojo.getHashContent());
            signatureData.setIncludeSectionInLetter(dispatchLetterRequestPojo.getIncludeSectionInLetter());
            signatureDataRepo.save(signatureData);
        }

        if (dispatchLetterRequestPojo.getDocuments() != null && !dispatchLetterRequestPojo.getDocuments().isEmpty()) {
            this.updateDocument(dispatchLetterRequestPojo.getDocuments(), dispatchLetter, employeePojo, userMgmtServiceData.findActiveFiscalYear().getCode());
        }

        if (dispatchLetterRequestPojo.getDocumentsToRemove() != null && !dispatchLetterRequestPojo.getDocumentsToRemove().isEmpty())
            this.deleteDocuments(dispatchLetterRequestPojo.getDocumentsToRemove());

        if (dispatchLetterRequestPojo.getSystemDocuments() != null && !dispatchLetterRequestPojo.getSystemDocuments().isEmpty()) {
            List<DispatchLetterDocumentDetails> sysDocs = new ArrayList<>();
            for (SysDocumentsPojo documentPojo : dispatchLetterRequestPojo.getSystemDocuments()) {
                DispatchLetterDocumentDetails systemDoc = new DispatchLetterDocumentDetails().builder()
                        .documentId(documentPojo.getDocumentId())
                        .documentName(documentPojo.getDocumentName())
                        .include(true)
                        .dispatchLetter(dispatchLetter)
                        .build();
                sysDocs.add(systemDoc);
            }
            dispatchDocumentDetailsRepo.saveAll(sysDocs);
        }

        if (dispatchLetterRequestPojo.getReferenceMemoId() != null && !dispatchLetterRequestPojo.getReferenceMemoId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : dispatchLetterRequestPojo.getReferenceMemoId()) {
                Memo memo1 = memoRepo.findById(id).orElseThrow(() -> new RuntimeException("No Memo Found"));
                MemoReference memoReference = new MemoReference().builder()
                        .dispatchLetter(dispatchLetter)
                        .referencedMemoId(id)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenPisCode)
                        .include(true)
                        .isEditable(true)
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (dispatchLetterRequestPojo.getReceivedReferenceId() != null && !dispatchLetterRequestPojo.getReceivedReferenceId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : dispatchLetterRequestPojo.getReceivedReferenceId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .dispatchLetter(dispatchLetter)
                        .dartaReferenceId(id)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenPisCode)
                        .include(true)
                        .isEditable(true)
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (dispatchLetterRequestPojo.getChalaniReferenceId() != null && !dispatchLetterRequestPojo.getChalaniReferenceId().isEmpty()) {
            List<MemoReference> chalaniReferences = new ArrayList<>();

            for (Long id : dispatchLetterRequestPojo.getChalaniReferenceId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .dispatchLetter(dispatchLetter)
                        .chalaniReferenceId(id)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenPisCode)
                        .include(true)
                        .isEditable(true)
                        .isAttach(Boolean.TRUE)
                        .build();
                chalaniReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(chalaniReferences);
        }

        if (dispatchLetterRequestPojo.getReferencesToRemove() != null && !dispatchLetterRequestPojo.getReferencesToRemove().isEmpty()) {
            dispatchLetterRequestPojo.getReferencesToRemove().forEach(id -> {
                memoReferenceRepo.softDeleteReference( id);
            });
        }

        Optional<DraftShare> draft =
                draftShareRepo.getByReceiverPisCodeAndReceiverSectionCodeAndDispatchIdAndIsActiveAndLetterType(tokenPisCode, tokenUserSectionCode, dispatchLetterRequestPojo.getId(), Boolean.TRUE, DcTablesEnum.DISPATCH);

        if (draft.isPresent()) {
            DraftShare draftShare = draft.get();
                draftShare.setStatus(dispatchLetterRequestPojo.getIsReadyToSubmit() ? Status.FN : Status.IP);
                draftShareRepo.save(draftShare);

            DraftShareLog draftShareLog = DraftShareLog.builder()
                    .fromStatus(draftShare.getStatus())
                    .toStatus(dispatchLetterRequestPojo.getIsReadyToSubmit() ? Status.FN : Status.IP)
                    .pisCode(tokenPisCode)
                    .sectionCode(tokenUserSectionCode)
                    .draftShareId(draftShare.getId())
                    .build();

            draftShareLogRepo.save(draftShareLog);
        }

        DraftShare senderDraftShare = draftShareRepo.getSenderDispatchDraftShare(tokenPisCode, tokenUserSectionCode, dispatchLetterRequestPojo.getId());
        if (senderDraftShare != null) {

            DraftShareLog draftShareLog = DraftShareLog.builder()
                    .fromStatus(Status.P)
                    .toStatus(dispatchLetterRequestPojo.getIsReadyToSubmit() ? Status.FN : Status.IP)
                    .pisCode(tokenPisCode)
                    .sectionCode(tokenUserSectionCode)
                    .draftShareId(senderDraftShare.getId())
                    .build();

            draftShareLogRepo.save(draftShareLog);
        }
        return dispatchLetter;
    }

    @Override
    public DispatchLetterDTO    getDispatchLetterById(Long id, String type) {

        //for work on transferred employee chalani
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user is involved in any section or not
        checkUserSection(tokenUser);

        String tokenUserSection = tokenUser.getSectionId();

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        DispatchLetterDTO dispatchLetterResponsePojo = dispatchLetterMapper.getDispatchLetterDetailById(id);

        if (!dispatchLetterResponsePojo.getIsDraft()) {
            if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
                List<String> involvedOffices = dispatchLetterMapper.getInvolvedOffices(id);
                log.info("Involved offices in dispatch letter: " + involvedOffices);
                if (!involvedOffices.contains(tokenProcessorService.getOfficeCode()))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
            } else {
                String strPisCodes = this.convertListToString(listPisCodes);
                log.info("dispatch letter id: " + id + " and section: " + tokenUser.getSectionId());
                System.out.println("previousPisCodes: " + strPisCodes);
                if (!dispatchLetterMapper.checkInvolvedChalani(id, tokenUser.getSectionId(), strPisCodes))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
            }
        } else {
            if (!draftShareRepo.checkPermissionDispatchShare(id, tokenPisCode, tokenUserSection)
                    && !(dispatchLetterResponsePojo.getSenderPisCode().equals(tokenPisCode) && dispatchLetterResponsePojo.getSenderSectionCode().equals(tokenUserSection)))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
        }

        dispatchLetterResponsePojo.setPreviousPisCodes(listPisCodes);

        //get office head pis code for delegation detail
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
        String officeHeadPisCode = "";
        if (officeHeadPojo != null && officeHeadPojo.getPisCode() != null)
            officeHeadPisCode = officeHeadPojo.getPisCode();

        List<DispatchLetterCommentsPojo> dispatchLetterCommentsPojos = dispatchLetterMapper.getAllCommentsOfDispatchLetter(id);
        if (dispatchLetterCommentsPojos != null && !dispatchLetterCommentsPojos.isEmpty()) {
            dispatchLetterCommentsPojos.forEach(x -> {
                x.setDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getDate().toString())));
            });
        }

        dispatchLetterResponsePojo.setAllComments(dispatchLetterCommentsPojos);

        Boolean isHashed = dispatchLetterResponsePojo != null ? dispatchLetterResponsePojo.getHash_content() == null ? Boolean.FALSE : Boolean.TRUE : Boolean.FALSE;
        dispatchLetterResponsePojo.setDispatchNo(dispatchLetterResponsePojo.getDispatchNo());
        EmployeeMinimalPojo employeeMinimal = userMgmtServiceData.getEmployeeDetailMinimal(dispatchLetterResponsePojo.getSenderPisCode());
        if (employeeMinimal != null) {
            dispatchLetterResponsePojo.setSenderName(employeeMinimal.getEmployeeNameEn());
            dispatchLetterResponsePojo.setSenderNameNp(employeeMinimal.getEmployeeNameNp());
        }

        if (dispatchLetterResponsePojo.getDelegatedId() != null) {
            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(dispatchLetterResponsePojo.getDelegatedId());

//            if (dispatchLetterResponsePojo.getSenderPisCode().equals(officeHeadPisCode))
//                dispatchLetterResponsePojo.setIsDelegated(Boolean.TRUE);

            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                dispatchLetterResponsePojo.setSenderName(delegationResponsePojo.getToEmployee().getNameEn());
                dispatchLetterResponsePojo.setSenderNameNp(delegationResponsePojo.getToEmployee().getNameNp());

                //check the delegated user is reassignment and set value accordingly
                if (delegationResponsePojo.getIsReassignment() != null
                        && delegationResponsePojo.getIsReassignment()) {
                    dispatchLetterResponsePojo.setIsReassignment(Boolean.TRUE);
                    if (delegationResponsePojo.getFromSection() != null)
                        dispatchLetterResponsePojo.setReassignmentSection(delegationResponsePojo.getFromSection());
                } else {
                    dispatchLetterResponsePojo.setIsDelegated(Boolean.TRUE);
                }
                dispatchLetterResponsePojo.setSignedPis(delegationResponsePojo.getToEmployee().getCode());
            }
        }

        if (type != null && type.equals("receiver") && !dispatchLetterResponsePojo.getDocuments().isEmpty()) {
            dispatchLetterResponsePojo.setDocuments(this.getActiveDocuments(dispatchLetterResponsePojo.getDocuments()));
        }

        if (dispatchLetterResponsePojo.getRemarksPisCode() != null && !dispatchLetterResponsePojo.getRemarksPisCode().isEmpty()) {
            DelegationTableMapper delegationTableMapper = delegationTableMapperRepo.getByDispatchId(dispatchLetterResponsePojo.getDispatchId());
            dispatchLetterResponsePojo.setIsRemarksUserDelegated(false);
            if (delegationTableMapper != null
                    && delegationTableMapper.getDelegatedId() != null
                    && delegationTableMapper.getStatusTo() != null
                    && delegationTableMapper.getStatusTo().equals(Status.A)) {
                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(delegationTableMapper.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null && delegationResponsePojo.getToEmployee().getCode() != null) {

//                    if (dispatchLetterResponsePojo.getRemarksPisCode().equals(officeHeadPisCode))
//                        dispatchLetterResponsePojo.setIsRemarksUserDelegated(Boolean.TRUE);
                    //check the delegated user is reassignment and set value accordingly
                    if (delegationResponsePojo.getIsReassignment() != null
                            && delegationResponsePojo.getIsReassignment()) {
                        dispatchLetterResponsePojo.setIsRemarksUserReassignment(Boolean.TRUE);
                        if (delegationResponsePojo.getFromSection() != null)
                            dispatchLetterResponsePojo.setRemarksUserReassignmentSection(delegationResponsePojo.getFromSection());
                    } else {
                        dispatchLetterResponsePojo.setIsRemarksUserDelegated(Boolean.TRUE);
                        dispatchLetterResponsePojo.setRemarksUserDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                        dispatchLetterResponsePojo.setRemarksUserDesignationNameEn(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameEn() : "");
                    }

                    dispatchLetterResponsePojo.setRemarksUserDetails(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                }
            } else {
                EmployeeMinimalPojo remarksUserMinimal = userMgmtServiceData.getEmployeeDetailMinimal(dispatchLetterResponsePojo.getRemarksPisCode());
                dispatchLetterResponsePojo.setRemarksUserDetails(remarksUserMinimal);
                if (dispatchLetterResponsePojo.getRemarksDesignationCode() != null) {
                    DetailPojo designationPojo = userMgmtServiceData.getDesignationDetailByCode(dispatchLetterResponsePojo.getRemarksDesignationCode());
                    if (designationPojo != null) {
                        dispatchLetterResponsePojo.setRemarksUserDesignationNameNp(designationPojo.getNameNp());
                        dispatchLetterResponsePojo.setRemarksUserDesignationNameEn(designationPojo.getNameEn());
                    }
                }
            }
        }

        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getSignatureIsActive() != null && dispatchLetterResponsePojo.getSignature().getSignatureIsActive()) {
            Boolean isHashedSignatureData = dispatchLetterResponsePojo.getSignature().getHashContent() == null ? Boolean.FALSE : Boolean.TRUE;

            VerificationInformation verification = verifySignatureService.verify(dispatchLetterResponsePojo.getContent(), dispatchLetterResponsePojo.getSignature().getSignatureData(), isHashedSignatureData);
            dispatchLetterResponsePojo.setVerificationInformation(verification);

            if (verification != null) {

                Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                //check for alternate result ie. if the letter is verified or not verified record already exists or not
                Boolean result = signatureVerificationLogRepository.
                        existsDispatchLog(
                                SignatureType.DISPATCH.toString(),
                                dispatchLetterResponsePojo.getDispatchId());

                if (result == null || result != isVerified)
                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                            .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                            .signatureType(SignatureType.DISPATCH)
                            .isVerified(isVerified)
                            .signatureBy(dispatchLetterResponsePojo.getSignedPis() == null ? dispatchLetterResponsePojo.getSignature().getPisCode() : dispatchLetterResponsePojo.getSignedPis())
                            .individualStatus(dispatchLetterResponsePojo.getStatus())
                            .build());
            }
        }

        if (dispatchLetterResponsePojo.getSignature() != null &&
                dispatchLetterResponsePojo.getSignature().getDesignationCode() != null &&
                !dispatchLetterResponsePojo.getSignature().getDesignationCode().isEmpty()) {
            dispatchLetterResponsePojo.getSignature().setDesignationPojo(userMgmtServiceData.getDesignationDetail(dispatchLetterResponsePojo.getSignature().getDesignationCode()));
        }


        if (dispatchLetterResponsePojo.getRemarksSignatureIsActive() != null && dispatchLetterResponsePojo.getRemarksSignatureIsActive()) {
            String newCont = dispatchLetterResponsePojo.getContent() + " " + dispatchLetterResponsePojo.getRemarks();
            VerificationInformation verification = verifySignatureService.verify(newCont, dispatchLetterResponsePojo.getRemarksSignatureData(), isHashed);
            dispatchLetterResponsePojo.setRemarksVerificationInformation(verification);

            if (verification != null) {

                Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                //check for alternate result ie. if the letter is verified or not verified record already exists or not
                Boolean result = signatureVerificationLogRepository.
                        existsDispatchLog(
                                SignatureType.DISPATCH.toString(),
                                dispatchLetterResponsePojo.getDispatchId());

                if (result == null || result != isVerified)
                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                            .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                            .signatureType(SignatureType.DISPATCH)
                            .isVerified(isVerified)
                            .signatureBy(dispatchLetterResponsePojo.getRemarksUserDetails().getPisCode())
                            .individualStatus(dispatchLetterResponsePojo.getStatus())
                            .build());
            }

        }

        boolean involve = false;

        if (type != null && type.equals("receiver") && dispatchLetterResponsePojo.getInclude() != null && !dispatchLetterResponsePojo.getInclude())
            involve = true;


        if (!involve) {
            List<GenericReferenceDto> memoReferences = memoReferenceMapper.getChalaniMemoReference(dispatchLetterResponsePojo.getDispatchId());

            if (memoReferences != null && !memoReferences.isEmpty()) {
                List<MemoResponsePojo> memoResponsePojos = new ArrayList<>();
                for (GenericReferenceDto tippani : memoReferences) {
                    if (tippani != null && tippani.getId() != null) {

                        if (type != null && type.equals("receiver") && tippani.getInclude() != null && !tippani.getInclude())
                            continue;

                        MemoResponsePojo letter = memoMapper.getMemoById(tippani.getId());
                        letter.setReferenceId(tippani.getReferenceId());
                        letter.setReferenceIsEditable(tippani.getReferenceIsEditable());
                        letter.setReferenceEmployee(userMgmtServiceData.getEmployeeDetailMinimal(tippani.getPisCode()));
                        memoResponsePojos.add(letter);
                    }
                }
                if (!memoResponsePojos.isEmpty()) {
                    for (MemoResponsePojo data : memoResponsePojos) {
                        data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                        data.setCreatedTimeNp(dateConverter.convertBSToDevnagari(data.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
                        data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()));
                    }
                }
                dispatchLetterResponsePojo.setMemoReferences(memoResponsePojos);
            }

            List<GenericReferenceDto> dartaReference = memoReferenceMapper.getChalaniDartaReference(dispatchLetterResponsePojo.getDispatchId());

            if (dartaReference != null && !dartaReference.isEmpty()) {
                List<ReceivedLetterResponsePojo> receivedLetterResponsePojos = new ArrayList<>();
                for (GenericReferenceDto darta : dartaReference) {
                    if (darta != null && darta.getId() != null) {

                        if (type != null && type.equals("receiver") && darta.getInclude() != null && !darta.getInclude())
                            continue;

                        ReceivedLetterResponsePojo letter = receivedLetterMapper.getReceivedLetter(darta.getId());
                        letter.setReferenceId(darta.getReferenceId());
                        letter.setReferenceIsEditable(darta.getReferenceIsEditable());
                        letter.setReferenceCreator(userMgmtServiceData.getEmployeeDetailMinimal(darta.getPisCode()));
                        receivedLetterResponsePojos.add(letter);
                    }
                }
                if (!receivedLetterResponsePojos.isEmpty()) {
                    for (ReceivedLetterResponsePojo data : receivedLetterResponsePojos) {
                        data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()));
                        data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                        data.setCreatedTimeNp(dateConverter.convertBSToDevnagari(data.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
                    }
                }
                dispatchLetterResponsePojo.setDartaReferences(receivedLetterResponsePojos);
            }

            List<GenericReferenceDto> chalaniReference = memoReferenceMapper.getChalaniChalaniReference(dispatchLetterResponsePojo.getDispatchId());

            if (chalaniReference != null && !chalaniReference.isEmpty()) {
                List<DispatchLetterDTO> dispatchLetterDTOS = new ArrayList<>();
                for (GenericReferenceDto chalani : chalaniReference) {
                    if (chalani != null && chalani.getId() != null) {

                        if (type != null && type.equals("receiver") && chalani.getInclude() != null && !chalani.getInclude())
                            continue;

                        DispatchLetterDTO letter = dispatchLetterMapper.getDispatchLetterDetailById(chalani.getId());
                        letter.setReferenceId(chalani.getReferenceId());
                        letter.setReferenceIsEditable(chalani.getReferenceIsEditable());
                        letter.setReferenceCreator(userMgmtServiceData.getEmployeeDetailMinimal(chalani.getPisCode()));
                        dispatchLetterDTOS.add(letter);
                    }
                }
                if (!dispatchLetterDTOS.isEmpty()) {
                    for (DispatchLetterDTO data : dispatchLetterDTOS) {
                        EmployeeMinimalPojo employeeMinimal1 = userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode());
                        if (employeeMinimal != null) {
                            data.setSenderName(employeeMinimal1.getEmployeeNameEn());
                            data.setSenderNameNp(employeeMinimal1.getEmployeeNameNp());
                        }
                    }
                }
                dispatchLetterResponsePojo.setChalaniReference(dispatchLetterDTOS);
            }
        }

        DispatchLetterReview dispatchActive = dispatchLetterReviewRepo.getDispatchByActiveStatus(dispatchLetterResponsePojo.getDispatchId());

        if (dispatchActive != null) {
            if (dispatchActive.getReceiverPisCode().equals(tokenProcessorService.getPisCode()) && dispatchActive.getActive()) {
                dispatchLetterResponsePojo.setEditable(true);
            }
        }

        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getPisCode() != null) {
            String reviewContent;
            DispatchLetterReview dispatchLetterReview = dispatchLetterReviewRepo.findLatestByUser(dispatchLetterResponsePojo.getSignature().getPisCode(), dispatchLetterResponsePojo.getDispatchId());

            //condition when chalani creator signature is replaced by chalani approver
            if (dispatchLetterResponsePojo.getSignature() != null
                    && dispatchLetterResponsePojo.getSignature().getPisCode() != null
                    && dispatchLetterResponsePojo.getRemarksPisCode() != null
                    && dispatchLetterResponsePojo.getSignature().getPisCode().equals(dispatchLetterResponsePojo.getRemarksPisCode())
                    && dispatchLetterResponsePojo.getRemarksSignatureIsActive() != null
                    && dispatchLetterResponsePojo.getRemarksSignatureIsActive()) {
                reviewContent = dispatchLetterResponsePojo.getContent() + " " + dispatchLetterResponsePojo.getRemarks();
                VerificationInformation verification = verifySignatureService.verify(reviewContent, dispatchLetterResponsePojo.getRemarksSignatureData(), isHashed);
                dispatchLetterResponsePojo.setActiveSignatureData(verification);

                //set signature to approver log for already approved letter
                if (dispatchLetterResponsePojo.getIsAlreadyApproved() != null && dispatchLetterResponsePojo.getIsAlreadyApproved())
                    dispatchLetterResponsePojo.setRemarksVerificationInformation(verification);

                if (verification != null) {

                    Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                    //check for alternate result ie. if the letter is verified or not verified record already exists or not
                    Boolean result = signatureVerificationLogRepository.
                            existsDispatchLog(
                                    SignatureType.DISPATCH.toString(),
                                    dispatchLetterResponsePojo.getDispatchId());

                    if (result == null || result != isVerified)
                        signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                                .signatureType(SignatureType.DISPATCH)
                                .isVerified(isVerified)
                                .signatureBy(dispatchLetterResponsePojo.getRemarksUserDetails().getPisCode())
                                .individualStatus(dispatchLetterResponsePojo.getStatus())
                                .build());
                }

            }

            //condition when chalani creator signature is not changed
            if (dispatchLetterReview != null
                    && dispatchLetterResponsePojo.getSignature() != null
                    && dispatchLetterResponsePojo.getSignature().getPisCode() != null
                    && dispatchLetterReview.getSenderPisCode() != null
                    && dispatchLetterReview.getSenderPisCode().equals(dispatchLetterResponsePojo.getSignature().getPisCode())
                    && dispatchLetterReview.getRemarksSignatureIsActive() != null
                    && dispatchLetterReview.getRemarksSignatureIsActive()) {
                reviewContent = dispatchLetterResponsePojo.getContent() + " " + dispatchLetterReview.getRemarks();
                Boolean isHashedReview = dispatchLetterReview.getHashContent() == null ? Boolean.FALSE : Boolean.TRUE;

                VerificationInformation verification = verifySignatureService.verify(reviewContent, dispatchLetterReview.getRemarksSignature(), isHashedReview);
                dispatchLetterResponsePojo.setActiveSignatureData(verification);

                //set signature to approver log for already approved letter
                if (dispatchLetterResponsePojo.getIsAlreadyApproved() != null && dispatchLetterResponsePojo.getIsAlreadyApproved())
                    dispatchLetterResponsePojo.setRemarksVerificationInformation(verification);

                if (verification != null) {

                    Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                    //check for alternate result ie. if the letter is verified or not verified record already exists or not
                    Boolean result = signatureVerificationLogRepository.
                            existsDispatchLog(
                                    SignatureType.DISPATCH.toString(),
                                    dispatchLetterResponsePojo.getDispatchId());

                    if (result == null || result != isVerified)
                        signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                                .signatureType(SignatureType.DISPATCH)
                                .isVerified(isVerified)
                                .signatureBy(dispatchLetterResponsePojo.getSignedPis() == null ? dispatchLetterResponsePojo.getSignature().getPisCode() : dispatchLetterResponsePojo.getSignedPis())
                                .individualStatus(dispatchLetterResponsePojo.getStatus())
                                .build());
                }

            }
        }

        if (dispatchLetterResponsePojo.getIsAlreadyApproved())
            dispatchLetterResponsePojo.setApproval(new ArrayList<>());
        if (dispatchLetterResponsePojo.getApproval() != null && !dispatchLetterResponsePojo.getApproval().isEmpty()) {
            String finalOfficeHeadPisCode = officeHeadPisCode;
            dispatchLetterResponsePojo.getApproval().forEach(x -> {
                x.setIsDelegated(false);
                if (x.getReceiverPisCode() != null) {
                    EmployeeMinimalPojo receiver = userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode());
                    if (receiver != null) {
                        x.setReceiverNameNp(receiver.getEmployeeNameNp());
                        x.setReceiverName(receiver.getEmployeeNameEn());
                    }

                    if (x.getIsSeen() != null && !x.getIsSeen()) {
                        if (tokenProcessorService.getPisCode().equals(x.getReceiverPisCode())) {
                            notificationService.notificationProducer(
                                    NotificationPojo.builder()
                                            .moduleId(dispatchLetterResponsePojo.getDispatchId())
                                            .module(MODULE_APPROVAL_KEY)
                                            .sender(tokenProcessorService.getPisCode())
                                            .receiver(x.getSenderPisCode())
                                            .subject(customMessageSource.getNepali("manual.received"))
                                            .detail(customMessageSource.getNepali("chalani.view", receiver != null ? receiver.getEmployeeNameNp() : "", dispatchLetterResponsePojo.getSubject()))
                                            .pushNotification(true)
                                            .received(false)
                                            .build()
                            );
                            dispatchLetterReviewRepo.setSeen(dispatchLetterResponsePojo.getDispatchId(), tokenProcessorService.getPisCode());
                        }
                    }
                }

                EmployeeMinimalPojo sender = null;
                if (x.getSenderPisCode() != null)
                    sender = userMgmtServiceData.getEmployeeDetailMinimal(x.getSenderPisCode());

                if (x.getDelegatedId() != null) {

//                    if (x.getSenderPisCode().equals(finalOfficeHeadPisCode))
//                        x.setIsDelegated(true);

                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
//                        EmployeeMinimalPojo delegatedSender = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode());

                        //check the delegated user is reassignment and set value accordingly
                        if (delegationResponsePojo.getIsReassignment() != null
                                && delegationResponsePojo.getIsReassignment()) {
                            x.setIsReassignment(Boolean.TRUE);
                            if (delegationResponsePojo.getFromSection() != null)
                                x.setReassignmentSection(delegationResponsePojo.getFromSection());
                        } else {
                            x.setIsDelegated(true);
                        }

                        x.setSenderName(delegationResponsePojo.getToEmployee().getNameEn());
                        x.setSenderNameNp(delegationResponsePojo.getToEmployee().getNameNp());
                        if (sender != null)
                            x.setSenderDesignationNameNp(sender.getFunctionalDesignation() != null ? sender.getFunctionalDesignation().getNameN() : null);
                        x.setSignedPis(delegationResponsePojo.getToEmployee().getCode());
                    }

                } else {
                    if (sender != null) {
                        x.setSenderName(sender.getEmployeeNameEn());
                        x.setSenderNameNp(sender.getEmployeeNameNp());
                        x.setSenderDesignationNameNp(sender.getFunctionalDesignation() != null ? sender.getFunctionalDesignation().getNameN() : null);
                        x.setSignedPis(sender.getPisCode());
                    }
                }

                if (x.getSignatureIsActive() != null && x.getSignatureIsActive()) {
                    String newContent;
                    Boolean isHashedApproval = x.getHash_content() == null ? Boolean.FALSE : Boolean.TRUE;
                    if (x.getContentLogId() == null) {
                        newContent = dispatchLetterResponsePojo.getContent() + " " + x.getRemarks();

                        VerificationInformation verification = verifySignatureService.verify(newContent, x.getSignatureData(), isHashedApproval);
                        x.setSignatureVerification(verification);

                        if (verification != null) {

                            Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                            //check for alternate result ie. if the letter is verified or not verified record already exists or not
                            Boolean result = signatureVerificationLogRepository.
                                    existsDispatchReviewLog(
                                            SignatureType.DISPATCH_ACTIVITY.toString(),
                                            dispatchLetterResponsePojo.getDispatchId(),
                                            x.getApprovalId());

                            if (result == null || result != isVerified)
                                signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                        .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                                        .dispatchReviewId(x.getApprovalId())
                                        .signatureType(SignatureType.DISPATCH_ACTIVITY)
                                        .isVerified(isVerified)
                                        .signatureBy(x.getSignedPis())
                                        .individualStatus(x.getApprovalStatus().toString())
                                        .build());
                        }
                    } else {
                        Optional<DispatchLetterUpdateHistory> logContent = dispatchLetterUpdateHistoryRepo.findById(x.getContentLogId());
                        if (logContent.isPresent()) {
                            newContent = logContent.get().getContent() + " " + x.getRemarks();

                            VerificationInformation verification = verifySignatureService.verify(newContent, x.getSignatureData(), isHashedApproval);
                            x.setSignatureVerification(verification);
                            if (verification != null) {

                                Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                                //check for alternate result ie. if the letter is verified or not verified record already exists or not
                                Boolean result = signatureVerificationLogRepository.
                                        existsDispatchReviewLog(
                                                SignatureType.DISPATCH_ACTIVITY.toString(),
                                                dispatchLetterResponsePojo.getDispatchId(),
                                                x.getApprovalId());

                                if (result == null || result != isVerified)
                                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                            .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                                            .dispatchReviewId(x.getApprovalId())
                                            .signatureType(SignatureType.DISPATCH_ACTIVITY)
                                            .isVerified(isVerified)
                                            .signatureBy(x.getSignedPis())
                                            .individualStatus(x.getApprovalStatus().toString())
                                            .build());
                            }
                        }
                    }
                }

            });
        }

        this.getATemplate(dispatchLetterResponsePojo, dispatchLetterResponsePojo.getSingular(), type);

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (dispatchLetterResponsePojo.getSenderPisCode().equals(tokenProcessorService.getPisCode())
                && employeePojo != null
                && dispatchLetterResponsePojo.getSenderSectionCode() != null
                && dispatchLetterResponsePojo.getSenderSectionCode().equals(employeePojo.getSectionId()))
            dispatchLetterResponsePojo.setReferenceIsEditable(true);
        else
            dispatchLetterResponsePojo.setReferenceIsEditable(false);

        return dispatchLetterResponsePojo;

    }

    private List<DocumentResponsePojo> getActiveDocuments(List<DocumentResponsePojo> document) {
        List<DocumentResponsePojo> newPojo = new ArrayList<>();
        for (DocumentResponsePojo data : document) {
            if (data.getIsActive() != null && data.getIsActive() && data.getInclude() != null && data.getInclude())
                newPojo.add(data);
        }
        return newPojo;
    }

    private DispatchLetterDTO getATemplate(DispatchLetterDTO dispatchLetterResponsePojo, boolean isSingular, String type) {

        //get office head pis code
        String officeHeadPisCode = "";

        // logic for internal letter
        if (dispatchLetterResponsePojo.getDispatchLetterInternal() != null && !dispatchLetterResponsePojo.getDispatchLetterInternal().isEmpty()) {
            dispatchLetterResponsePojo.getDispatchLetterInternal().forEach(x -> {
                Boolean isSectionName = x.getIsSectionName();
                if (x.getWithinOrganization() != null ? x.getWithinOrganization() : x.getInternalReceiverPiscode() != null ? Boolean.TRUE : Boolean.FALSE) {
                    EmployeePojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetail(x.getInternalReceiverPiscode());

                    SectionPojo userSection = null;
                    if (x.getInternalReceiverSectionId() != null) {
                        try {
                            userSection = userMgmtServiceData.getSectionDetail(Long.parseLong(x.getInternalReceiverSectionId()));
                        } catch (NumberFormatException e) {

                        }
                    }
                    if (employeeMinimalPojo != null) {
                        x.setEmployeeName(employeeMinimalPojo.getNameEn() != null ? getStringPascalCase(employeeMinimalPojo.getNameEn()) : "");
                        x.setEmployeeNameNp(employeeMinimalPojo.getNameNp() != null ? employeeMinimalPojo.getNameNp() : "");
                        x.setSectionName(userSection != null ? userSection.getNameEn() != null ? getStringPascalCase(userSection.getNameEn()) : "" : "");
                        x.setSectionNameNp(userSection != null ? userSection.getNameNp() != null ? userSection.getNameNp() : "" : "");
                        x.setDesignationName(employeeMinimalPojo.getFunctionalDesignation() != null ? employeeMinimalPojo.getFunctionalDesignation().getName() != null ? getStringPascalCase(employeeMinimalPojo.getFunctionalDesignation().getName()) : "" : "");
                        x.setDesignationNameNp(employeeMinimalPojo.getFunctionalDesignation() != null ? employeeMinimalPojo.getFunctionalDesignation().getNameN() != null ? employeeMinimalPojo.getFunctionalDesignation().getNameN() : "" : "");
                    } else {
                        x.setEmployeeName("");
                        x.setEmployeeNameNp("");
                        x.setSectionName("");
                        x.setSectionNameNp("");
                        x.setDesignationName("");
                        x.setDesignationNameNp("");
                    }
                } else if (x.getWithinOrganization() != null ? !x.getWithinOrganization() : x.getInternalReceiverPiscode() == null ? Boolean.TRUE : Boolean.FALSE) {
                    OfficePojo officePojo = userMgmtServiceData.getOfficeDetail(x.getInternalReceiverOfficeCode());

                    if (officePojo != null) {
                        x.setEmployeeName(officePojo.getNameEn() != null ? getStringPascalCase(officePojo.getNameEn()) : "");
                        x.setEmployeeNameNp(officePojo.getNameNp() != null ? officePojo.getNameNp() : "");
                        x.setDesignationName(officePojo.getAddressEn() != null ? officePojo.getAddressEn() : "");
                        x.setDesignationNameNp(officePojo.getAddressNp() != null ? officePojo.getAddressNp() : "");
                    } else {
                        x.setEmployeeName("");
                        x.setEmployeeNameNp("");
                        x.setDesignationName("");
                        x.setDesignationNameNp("");
                    }

                    if (x.getInternalReceiverSectionId() != null && !x.getInternalReceiverSectionId().isEmpty()) {
                        SectionPojo sectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(x.getInternalReceiverSectionId()));
                        if (sectionPojo != null) {
                            x.setSectionName(sectionPojo.getNameEn() != null ? getStringPascalCase(sectionPojo.getNameEn()) : "");
                            x.setSectionNameNp(sectionPojo.getNameNp() != null ? sectionPojo.getNameNp() : "");
                        } else {
                            x.setSectionName("");
                            x.setSectionNameNp("");
                        }
                    } else {
                        x.setSectionName(x.getInternalReceiverSectionName() != null ? getStringPascalCase(x.getInternalReceiverSectionName()) : "");
                        x.setSectionNameNp(x.getInternalReceiverSectionName() != null ? x.getInternalReceiverSectionName() : "");
                    }

                }
            });
        }

        // logic for external letter
        if (dispatchLetterResponsePojo.getDispatchLetterExternal() != null && !dispatchLetterResponsePojo.getDispatchLetterExternal().isEmpty()) {
            dispatchLetterResponsePojo.getDispatchLetterExternal().forEach(x -> {
                x.setEmployeeName(x.getReceiverName() != null ? x.getReceiverName() : "");
                x.setEmployeeNameNp(x.getReceiverName() != null ? x.getReceiverName() : "");
            });
        }

        // sender detail
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(dispatchLetterResponsePojo.getSenderPisCode());
        String toEmployee = null;
        EmployeePojo requester;
        DesignationPojo requesterDesignation = null;
        boolean isOfficeHead = false;
        DetailPojo requesterSection = null;

        // check if  office head signature
        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getPisCode() != null) {
            OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
            if (officeHeadPojo != null && officeHeadPojo.getPisCode() != null && officeHeadPojo.getPisCode().equals(dispatchLetterResponsePojo.getSignature().getPisCode()))
                isOfficeHead = true;
        }

        DetailPojo reassignmentSection = null;
        Boolean isReassignment = Boolean.FALSE;
        Boolean isDelegated = Boolean.FALSE;
        DelegationResponsePojo delegationResponsePojo;
        // check if its delegated
        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getDelegatedId() != null) {
            delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(dispatchLetterResponsePojo.getSignature().getDelegatedId());
            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                toEmployee = delegationResponsePojo.getToEmployee().getCode();

//                if (delegationResponsePojo != null
//                        && delegationResponsePojo.getFromEmployee() != null
//                        && delegationResponsePojo.getFromEmployee().getCode() != null
//                        && delegationResponsePojo.getFromEmployee().getCode().equals(officeHeadPisCode))
//                    isDelegated = Boolean.TRUE;

                // here need to task
                if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment()) {
                    isReassignment = Boolean.TRUE;
                    if (delegationResponsePojo.getFromSection() != null) {
                        reassignmentSection = delegationResponsePojo.getFromSection();
                    }

                } else {
                    isDelegated = Boolean.TRUE;
                }
                if (delegationResponsePojo.getFromSection() != null) {
                    requesterSection = delegationResponsePojo.getFromSection();
                }
            }
        }

        if (toEmployee != null) {
            requester = userMgmtServiceData.getEmployeeDetail(toEmployee);

            if (isOfficeHead && requester != null) {
                requester.setNameNp(requester.getNameNp());
            } else if (requester != null && requester.getFunctionalDesignation() != null) {

                //logic for delegation info
                requesterDesignation = new DesignationPojo();
                requesterDesignation.setNameEn(
                        requester.getFunctionalDesignation().getName() != null ?
                                isDelegated ?
                                        DELEGATED_EN + ", " + requester.getFunctionalDesignation().getName() :
                                        isReassignment ?
                                                ADDITIONAL_RESPONSIBILITY_EN + ", " + reassignmentSection.getNameEn() + ", " + requester.getFunctionalDesignation().getName() :
                                                requester.getFunctionalDesignation().getName() :
                                "");
                requesterDesignation.setNameNp(requester.getFunctionalDesignation().getNameN() != null ?
                        isDelegated ?
                                DELEGATED_NEP + ", " + requester.getFunctionalDesignation().getNameN() :
                                isReassignment ?
                                        ADDITIONAL_RESPONSIBILITY_NEP + ", " + reassignmentSection.getNameNp() + ", " + requester.getFunctionalDesignation().getNameN() :
                                        requester.getFunctionalDesignation().getNameN() :
                        "");
            }
        } else {
            requester = userMgmtServiceData.getEmployeeDetail(dispatchLetterResponsePojo.getSignature() != null ? dispatchLetterResponsePojo.getSignature().getPisCode() : null);
            if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getDesignationCode() != null)
                requesterDesignation = userMgmtServiceData.getDesignationDetail(dispatchLetterResponsePojo.getSignature().getDesignationCode());
            if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getSectionCode() != null) {
                SectionPojo sectionPojoDetail = userMgmtServiceData.getSectionDetail(Long.parseLong(dispatchLetterResponsePojo.getSignature().getSectionCode()));
                if (sectionPojoDetail != null && requesterSection == null) {
                    requesterSection = new DetailPojo();
                    requesterSection.setNameEn(sectionPojoDetail.getNameEn());
                    requesterSection.setNameNp(sectionPojoDetail.getNameNp());
                }
            }
        }

        String footer = "";
//        FooterDataDto footerData = footerDataMapper.getFooterByOfficeCode(tokenProcessorService.getOfficeCode());
//        if (footerData != null)
//            footer = footerData.getFooter();

//        DesignationPojo requesterDesignation = null;
        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getDesignationCode() != null && requesterDesignation == null)
            requesterDesignation = userMgmtServiceData.getDesignationDetail(dispatchLetterResponsePojo.getSignature().getDesignationCode());

        SectionPojo sectionPojo = null;
        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getIncludeSectionId() != null && dispatchLetterResponsePojo.getSignature().getIncludeSectionId() && dispatchLetterResponsePojo.getSignature().getIncludedSectionId() != null)
            sectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(dispatchLetterResponsePojo.getSignature().getIncludedSectionId()));

        String img = "data:image/png;base64,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";

        OfficePojo officeDetail = userMgmtServiceData.getOfficeDetail(dispatchLetterResponsePojo.getSenderOfficeCode());
        OfficePojo parentOffice = userMgmtServiceData.getOfficeDetail(officeDetail != null ? officeDetail.getParentCode() : null);

        if (parentOffice != null && parentOffice.getCode().equals("8886"))
            parentOffice = null;

        boolean isEnglish = dispatchLetterResponsePojo.getIsEnglish() != null ? dispatchLetterResponsePojo.getIsEnglish() : false;
        boolean isAd = dispatchLetterResponsePojo.getIsAd() != null ? dispatchLetterResponsePojo.getIsAd() : false;
        boolean includeSection = dispatchLetterResponsePojo.getIncludeSection() != null ? dispatchLetterResponsePojo.getIncludeSection() : true;
        String lang = "NEP";
        String header = null;
        boolean useDynamicHeader = false;
        OfficeTemplatePojo dynamicTemplateHeader = null;

        if (isEnglish) {
            lang = "EN";
        }

        // get template as per saved if its added
        if (dispatchLetterResponsePojo.getTemplateHeaderId() != null) {
//            todo add validation
            dynamicTemplateHeader = userMgmtServiceData.getOfficeTemplateById(dispatchLetterResponsePojo.getTemplateHeaderId());
        } else {
            // get active template
            if (Boolean.TRUE.equals(dispatchLetterResponsePojo.getIsDraft())) {
                dynamicTemplateHeader = userMgmtServiceData.getOfficeTemplate(tokenProcessorService.getOfficeCode(), "H");
            }
        }


        // remove this and add dynamic header as compulsory
        if (dynamicTemplateHeader != null) {
            useDynamicHeader = true;
            if (isEnglish && dynamicTemplateHeader.getTemplateEn() != null &&
                    !dynamicTemplateHeader.getTemplateEn().isEmpty())
                header = dynamicTemplateHeader.getTemplateEn();
            else if (!isEnglish && dynamicTemplateHeader.getTemplateNp() != null
                    && !dynamicTemplateHeader.getTemplateNp().isEmpty())
                header = dynamicTemplateHeader.getTemplateNp();
        }

        OfficeTemplatePojo dynamicTemplateFooter;
        if (dispatchLetterResponsePojo.getTemplateFooterId() != null) {
            dynamicTemplateFooter = userMgmtServiceData.getOfficeTemplateById(dispatchLetterResponsePojo.getTemplateFooterId());
        } else {
            dynamicTemplateFooter = userMgmtServiceData.getOfficeTemplate(tokenProcessorService.getOfficeCode(), "F");
        }
        if (dynamicTemplateFooter != null) {
            if (isEnglish && dynamicTemplateFooter.getTemplateEn() != null &&
                    !dynamicTemplateFooter.getTemplateEn().isEmpty())
                footer = dynamicTemplateFooter.getTemplateEn();
            else if (!isEnglish && dynamicTemplateFooter.getTemplateNp() != null
                    && !dynamicTemplateFooter.getTemplateNp().isEmpty())
                footer = dynamicTemplateFooter.getTemplateNp();
        }

        List<Bodartha> bodartha = new ArrayList<>();
        List<Bodartha> karyartha = new ArrayList<>();
        List<Bodartha> sadarBodartha = new ArrayList<>();
        List<RequestTo> requestTos = new ArrayList<>();
        List<String> templates = new ArrayList<>();
        final RequestTo[] req = {null};
        final String shree = "श्री ";

        AtomicReference<Boolean> isCC = new AtomicReference<>();
        AtomicReference<Boolean> isExternalAndReceiver = new AtomicReference<>();
        isCC.set(Boolean.FALSE);
        isExternalAndReceiver.set(Boolean.FALSE);
        if (dispatchLetterResponsePojo.getDispatchLetterInternal() != null && !dispatchLetterResponsePojo.getDispatchLetterInternal().isEmpty()) {
            dispatchLetterResponsePojo.getDispatchLetterInternal().forEach(x -> {

                Boolean isGroupName = x.getIsGroupName() != null ? x.getIsGroupName() : Boolean.FALSE;
                String groupNameEn = "";
                String groupNameNp = "";
                if (isGroupName && x.getGroupId() != null) {
                    OfficeGroupPojo officeGroupPojo = userMgmtServiceData.getOfficeGroupById(x.getGroupId());
                    groupNameEn = officeGroupPojo.getNameEn();
                    groupNameNp = officeGroupPojo.getNameNp();
                }
                boolean isSaluted = false;
                String designationEn = "";
                String designationNp = "";

                if (x.getSalutation() != null)
                    isSaluted = true;

                Boolean isSectionName = x.getIsSectionName() != null ? x.getIsSectionName() : Boolean.FALSE;
                if (x.getInternalReceiver() != null && x.getInternalReceiver()) {

                    if (x.getDesignationName() != null)
                        designationEn = ", " + getStringPascalCase(x.getDesignationName());
                    if (x.getDesignationNameNp() != null)
                        designationNp = ", " + x.getDesignationNameNp();

//                    EmployeePojo receiverDetail = userMgmtServiceData.getEmployeeDetail(x.getInternalReceiverPiscode());

                    SectionPojo receiverSectionPojo = null;
                    if (x.getInternalReceiverSectionId() != null) {
                        receiverSectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(x.getInternalReceiverSectionId()));
                    }

                    RequestTo requestTo = new RequestTo().builder()
                            .office(isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() + designationEn : isGroupName ? groupNameNp : isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() + designationNp)
                            .sectionName(isSectionName ? receiverSectionPojo != null ? isEnglish ? receiverSectionPojo.getNameEn() : "श्री " + receiverSectionPojo.getNameNp() : null : null)
                            .isSectionName(isSectionName)
                            .isGroupName(isGroupName)
                            .groupId(x.getGroupId())
                            .order(x.getOrder()).build();


                    if (isSaluted || isGroupName || isSectionName) {
                        requestTo.setAddress("");
                    } else {
                        requestTo.setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                    }
                    requestTos.add(requestTo);

                    if (req[0] == null)
                        req[0] = requestTo;

                    if (x.getInternalReceiverPiscode() != null && x.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())) {
                        req[0] = new RequestTo().builder()
                                .office(isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() + designationEn : isGroupName ? groupNameNp : isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() + designationNp)
                                .sectionName(isSectionName ? receiverSectionPojo != null ? isEnglish ? receiverSectionPojo.getNameEn() : "श्री " + receiverSectionPojo.getNameNp() : null : null)
                                .isSectionName(isSectionName)
                                .isGroupName(isGroupName)
                                .build();

                        if (isSaluted || isGroupName) {
                            req[0].setAddress("");
                        } else {
                            req[0].setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                        }
                    }

                } else if (x.getInternalReceiverCc() != null && x.getInternalReceiverCc() && (x.getBodarthaType() == null || x.getBodarthaType().equals(BodarthaEnum.B))) {

                    Bodartha bIn = new Bodartha().builder()
                            .office(isSectionName ? isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? shree + " " + x.getSectionNameNp() : "" : isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() != null ? x.getEmployeeName() : "" : isGroupName ? groupNameNp : x.getEmployeeNameNp() != null ? isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() : "")
                            .remarks(x.getRemarks())
                            .order(x.getOrder()).build();

                    if (isSaluted || isGroupName || isSectionName) {
                        bIn.setSection("");
                        bIn.setAddress("");
                    } else {
                        bIn.setSection(isEnglish ? x.getDesignationName() != null ? x.getDesignationName() : "" : x.getDesignationNameNp() != null ? x.getDesignationNameNp() : "");
                        bIn.setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                    }
                    bodartha.add(bIn);

                } else if (x.getInternalReceiverCc() != null && x.getInternalReceiverCc() && x.getBodarthaType().equals(BodarthaEnum.K)) {

                    Bodartha kar = new Bodartha().builder()
                            .office(isSectionName ? isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? shree + " " + x.getSectionNameNp() : "" : isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() != null ? x.getEmployeeName() : "" : isGroupName ? groupNameNp : x.getEmployeeNameNp() != null ? isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() : "")
                            .remarks(x.getRemarks())
                            .order(x.getOrder()).build();

                    if (isSaluted || isGroupName || isSectionName) {
                        kar.setSection("");
                        kar.setAddress("");
                    } else {
                        kar.setSection(isEnglish ? x.getDesignationName() != null ? x.getDesignationName() : "" : x.getDesignationNameNp() != null ? x.getDesignationNameNp() : "");
                        kar.setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                    }
                    karyartha.add(kar);

                } else if (x.getInternalReceiverCc() != null && x.getInternalReceiverCc() && x.getBodarthaType().equals(BodarthaEnum.S)) {

                    Bodartha sad = new Bodartha().builder()
                            .office(isSectionName ? isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? shree + " " + x.getSectionNameNp() : "" : isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() != null ? x.getEmployeeName() : "" : isGroupName ? groupNameNp : x.getEmployeeNameNp() != null ? isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() : "")
                            .remarks(x.getRemarks())
                            .order(x.getOrder()).build();

                    if (isSaluted || isGroupName || isSectionName) {
                        sad.setSection("");
                        sad.setAddress("");
                    } else {
                        sad.setSection(isEnglish ? x.getDesignationName() != null ? x.getDesignationName() : "" : x.getDesignationNameNp() != null ? x.getDesignationNameNp() : "");
                        sad.setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                    }
                    sadarBodartha.add(sad);
                }

                if (x.getInternalReceiverCc() != null && x.getInternalReceiverCc() && x.getInternalReceiverPiscode() != null && x.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())) {
                    isCC.set(Boolean.TRUE);
                }

                if (x.getInternalReceiverPiscode() != null && x.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode()) && req[0] == null) {
                        isExternalAndReceiver.set(Boolean.TRUE);
                }
            });
        }

        if (dispatchLetterResponsePojo.getDispatchLetterExternal() != null && !dispatchLetterResponsePojo.getDispatchLetterExternal().isEmpty()) {
            dispatchLetterResponsePojo.getDispatchLetterExternal().forEach(x -> {

                String externalSection = "";
                if (x.getReceiverOfficeSectionSubSection() != null)
                    externalSection = " ," + x.getReceiverOfficeSectionSubSection();

                if (x.getIsCc() != null && !x.getIsCc()) {

                    RequestTo to = new RequestTo().builder()
                            .office(x.getReceiverName() != null ? x.getReceiverName() + externalSection : "")
                            .isSectionName(Boolean.FALSE)
                            .isGroupName(Boolean.FALSE)
                            .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                            .order(x.getOrder()).build();

                    requestTos.add(to);

                } else if (x.getIsCc() != null && x.getIsCc() && (x.getBodarthaType() == null || x.getBodarthaType().equals(BodarthaEnum.B))) {

                    Bodartha bd = new Bodartha().builder()
                            .office(x.getReceiverName() != null ? x.getReceiverName() : "")
                            .remarks(x.getRemarks())
                            .isExternal(true)
                            .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                            .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                            .order(x.getOrder()).build();

                    bodartha.add(bd);

                } else if (x.getIsCc() != null && x.getIsCc() && x.getBodarthaType().equals(BodarthaEnum.K)) {

                    Bodartha karEx = new Bodartha().builder()
                            .office(x.getReceiverName() != null ? x.getReceiverName() : "")
                            .remarks(x.getRemarks())
                            .isExternal(true)
                            .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                            .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                            .order(x.getOrder()).build();

                    karyartha.add(karEx);

                } else if (x.getIsCc() != null && x.getIsCc() && x.getBodarthaType().equals(BodarthaEnum.S)) {

                    Bodartha sadEx = new Bodartha().builder()
                            .office(x.getReceiverName() != null ? x.getReceiverName() : "")
                            .remarks(x.getRemarks())
                            .isExternal(true)
                            .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                            .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                            .order(x.getOrder()).build();

                    sadarBodartha.add(sadEx);

                }
            });
        }

        String chalaniDate = isEnglish ? isAd ? dispatchLetterResponsePojo.getDispatchDate() != null ? dispatchLetterResponsePojo.getDispatchDate().toString()
                : ""
                : dispatchLetterResponsePojo.getDispatchDateNp() != null ? dispatchLetterResponsePojo.getDispatchDateNp()
                : ""
                : isAd ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDate().toString())
                : ""
                : dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp())
                : "";

        String chalaniNo = dispatchLetterResponsePojo.getDispatchNo();

        if (isEnglish && dispatchLetterResponsePojo.getReferenceCode() != null) {
            chalaniNo = dispatchLetterResponsePojo.getDispatchNo() + "-" + dispatchLetterResponsePojo.getReferenceCode().substring(1);
        }

        requestTos.sort(Comparator.comparing(RequestTo::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        bodartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        karyartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        sadarBodartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        Boolean includeSectionInLetter = dispatchLetterResponsePojo.getSignature() != null ?
                dispatchLetterResponsePojo.getSignature().getIncludeSectionInLetter() != null ?
                        dispatchLetterResponsePojo.getSignature().getIncludeSectionInLetter()
                        : Boolean.FALSE : Boolean.FALSE;

        VerificationInformation verificationInformation = dispatchLetterResponsePojo.getActiveSignatureData();
        if (type != null && type.equals("receiver") && req[0] != null && !isCC.get()) {
            GeneralTemplate internalTemplate = new GeneralTemplate().builder()
                    .header(useDynamicHeader ? header : null)
                    .logo_url(img)
                    .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                    .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                    .department(isEnglish ? parentOffice != null ? getStringPascalCase(parentOffice.getNameEn()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                    .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                    .letter_date(chalaniDate)
                    .section_header(includeSection ? sectionPojo != null ? isEnglish ? getStringPascalCase(sectionPojo.getNameEn()) : sectionPojo.getNameNp() : null : null)
                    .chali_no(chalaniNo)
                    .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                    .request_to_office(req[0].getOffice() != null ? req[0].getOffice() : "")
                    .request_to_office_address(req[0].getAddress() != null ? req[0].getAddress() : "")
                    .body_message(dispatchLetterResponsePojo.getContent())
                    .subject(dispatchLetterResponsePojo.getSubject())
                    .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? employeePojo.getNameEn() : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                    .requester_position(isEnglish ? requesterDesignation != null ? getStringPascalCase(requesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : requesterDesignation != null ? requesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                    .bodartha(bodartha)
                    .bodartha_karyartha(karyartha)
                    .saadar_awagataartha(sadarBodartha)
                    .resource_type("C")
                    .resource_id(dispatchLetterResponsePojo.getDispatchId())
                    .footer(footer)
                    .signatureDetail(dispatchLetterResponsePojo.getSignature())
                    .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                    .dispatchDateEn(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertAdToBs(dispatchLetterResponsePojo.getDispatchDate().toString()) : null : null)
                    .dispatchDateNp(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp()) : null : null)
                    .status(dispatchLetterResponsePojo.getStatus())
                    .verificationInformation(verificationInformation)
                    .hasSubject(dispatchLetterResponsePojo.getHasSubject())
                    .sectionName(req[0].getSectionName())
                    .isSectionName(req[0].getIsSectionName() != null ? req[0].getIsSectionName() : Boolean.FALSE)
                    .isGroupName(req[0].getIsGroupName() != null ? req[0].getIsGroupName() : Boolean.FALSE)
                    .sectionLetter(includeSectionInLetter && requesterSection != null ? isEnglish ? requesterSection.getNameEn() : requesterSection.getNameNp() : null)
                    .build();

            System.out.println("gson1: " + new Gson().toJson(internalTemplate));
            // template generate
            internalTemplate.setDispatchId(dispatchLetterResponsePojo.getDispatchId().toString());
            templates.add(letterTemplateProxy.getGeneralTemplate(internalTemplate, lang));
        } else if (type == null || (type != null && !type.equals("receiver")) || isCC.get() || isExternalAndReceiver.get()) {
            if (!isSingular) {
                String finalChalaniNo = chalaniNo;
                OfficePojo finalParentOffice = parentOffice;
                String finalLang = lang;
                DesignationPojo finalRequesterDesignation = requesterDesignation;
                SectionPojo finalSectionPojo = sectionPojo;
                String finalFooter = footer;
                String finalHeader = header;
                boolean finalUseDynamicHeader = useDynamicHeader;
                DetailPojo finalSection = requesterSection;

                List<RequestTo> requestToListWithUniqueSectionName = removeSameSectionName(requestTos);

                requestToListWithUniqueSectionName.forEach(x -> {
                    GeneralTemplate internalTemplate = new GeneralTemplate().builder()
                            .header(finalUseDynamicHeader ? finalHeader : null)
                            .logo_url(img)
                            .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                            .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                            .department(isEnglish ? finalParentOffice != null ? getStringPascalCase(finalParentOffice.getNameEn()) : "" : finalParentOffice != null ? finalParentOffice.getNameNp() : "")
                            .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                            .letter_date(chalaniDate)
                            .section_header(includeSection ? finalSectionPojo != null ? isEnglish ? getStringPascalCase(finalSectionPojo.getNameEn()) : finalSectionPojo.getNameNp() : null : null)
                            .chali_no(finalChalaniNo)
                            .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                            .request_to_office(x.getOffice())
                            .request_to_office_address(x.getAddress())
                            .body_message(dispatchLetterResponsePojo.getContent())
                            .subject(dispatchLetterResponsePojo.getSubject())
                            .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getNameEn()) : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                            .requester_position(isEnglish ? finalRequesterDesignation != null ? getStringPascalCase(finalRequesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : finalRequesterDesignation != null ? finalRequesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                            .bodartha(bodartha)
                            .bodartha_karyartha(karyartha)
                            .saadar_awagataartha(sadarBodartha)
                            .resource_type("C")
                            .resource_id(dispatchLetterResponsePojo.getDispatchId())
                            .footer(finalFooter)
                            .signatureDetail(dispatchLetterResponsePojo.getSignature())
                            .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                            .dispatchDateEn(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertAdToBs(dispatchLetterResponsePojo.getDispatchDate().toString()) : null : null)
                            .dispatchDateNp(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp()) : null : null)
                            .status(dispatchLetterResponsePojo.getStatus())
                            .verificationInformation(verificationInformation)
                            .hasSubject(dispatchLetterResponsePojo.getHasSubject())
                            .sectionName(x.getSectionName())
                            .isSectionName(x.getIsSectionName() != null ? x.getIsSectionName() : Boolean.FALSE)
                            .isGroupName(x.getIsGroupName() != null ? x.getIsGroupName() : Boolean.FALSE)
                            .sectionLetter(includeSectionInLetter && finalSection != null ? isEnglish ? finalSection.getNameEn() : finalSection.getNameNp() : null)
                            .build();

                    System.out.println("gson2: " + new Gson().toJson(internalTemplate));
                    internalTemplate.setDispatchId(dispatchLetterResponsePojo.getDispatchId().toString());
                    templates.add(letterTemplateProxy.getGeneralTemplate(internalTemplate, finalLang));
                });
            } else {
                GeneralTemplate internalTemplate = new GeneralTemplate().builder()
                        .header(useDynamicHeader ? header : null)
                        .logo_url(img)
                        .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                        .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                        .department(isEnglish ? parentOffice != null ? getStringPascalCase(parentOffice.getNameEn()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                        .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                        .letter_date(chalaniDate)
                        .section_header(includeSection ? sectionPojo != null ? isEnglish ? getStringPascalCase(sectionPojo.getNameEn()) : sectionPojo.getNameNp() : null : null)
                        .chali_no(chalaniNo)
                        .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                        .request_to_many(removeSameSectionName(requestTos))
                        .body_message(dispatchLetterResponsePojo.getContent())
                        .subject(dispatchLetterResponsePojo.getSubject())
                        .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getNameEn()) : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                        .requester_position(isEnglish ? requesterDesignation != null ? getStringPascalCase(requesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : requesterDesignation != null ? requesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                        .bodartha(bodartha)
                        .bodartha_karyartha(karyartha)
                        .saadar_awagataartha(sadarBodartha)
                        .resource_type("C")
                        .resource_id(dispatchLetterResponsePojo.getDispatchId())
                        .footer(footer)
                        .signatureDetail(dispatchLetterResponsePojo.getSignature())
                        .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                        .dispatchDateEn(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertAdToBs(dispatchLetterResponsePojo.getDispatchDate().toString()) : null : null)
                        .dispatchDateNp(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp()) : null : null)
                        .status(dispatchLetterResponsePojo.getStatus())
                        .verificationInformation(verificationInformation)
                        .hasSubject(dispatchLetterResponsePojo.getHasSubject())
                        .sectionLetter(includeSectionInLetter && requesterSection != null ? isEnglish ? requesterSection.getNameEn() : requesterSection.getNameNp() : null)
                        .build();

                System.out.println("gson3: " + new Gson().toJson(internalTemplate));
                dispatchLetterResponsePojo.setIsTableFormat(useDynamicHeader ? Boolean.TRUE : isEnglish ? Boolean.TRUE : Boolean.FALSE);
                internalTemplate.setDispatchId(dispatchLetterResponsePojo.getDispatchId().toString());
                templates.add(letterTemplateProxy.getGeneralMultipleTemplate(internalTemplate, lang));
            }
        } /*else if (type != null && type.equals("receiver") && req[0] == null) {
            GeneralTemplate internalTemplate = new GeneralTemplate().builder()
                    .header(useDynamicHeader ? header : null)
                    .logo_url(img)
                    .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                    .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                    .department(isEnglish ? parentOffice != null ? getStringPascalCase(parentOffice.getNameEn()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                    .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                    .letter_date(chalaniDate)
                    .section_header(includeSection ? sectionPojo != null ? isEnglish ? getStringPascalCase(sectionPojo.getNameEn()) : sectionPojo.getNameNp() : null : null)
                    .chali_no(chalaniNo)
                    .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                    .request_to_many(requestTos)
                    .body_message(dispatchLetterResponsePojo.getContent())
                    .subject(dispatchLetterResponsePojo.getSubject())
                    .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? employeePojo.getNameEn() : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                    .requester_position(isEnglish ? requesterDesignation != null ? getStringPascalCase(requesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : requesterDesignation != null ? requesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                    .bodartha(bodartha)
                    .bodartha_karyartha(karyartha)
                    .saadar_awagataartha(sadarBodartha)
                    .resource_type("C")
                    .resource_id(dispatchLetterResponsePojo.getDispatchId())
                    .footer(footer)
                    .signatureDetail(dispatchLetterResponsePojo.getSignature())
                    .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                    .dispatchDateEn(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertAdToBs(dispatchLetterResponsePojo.getDispatchDate().toString()) : null : null)
                    .dispatchDateNp(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp()) : null : null)
                    .status(dispatchLetterResponsePojo.getStatus())
                    .build();

            dispatchLetterResponsePojo.setIsTableFormat(useDynamicHeader ? Boolean.TRUE : isEnglish ? Boolean.TRUE : Boolean.FALSE);
            templates.add(letterTemplateProxy.getGeneralMultipleTemplate(internalTemplate, lang));
        }*/

        dispatchLetterResponsePojo.setTemplates(templates);

        if (dispatchLetterResponsePojo.getStatus() != null && dispatchLetterResponsePojo.getStatus().equals("A")) {
            List<Ocr> ocrList = new ArrayList<>();
            DesignationPojo designationPojo = null;
            if (dispatchLetterResponsePojo.getApproval() != null && !dispatchLetterResponsePojo.getApproval().isEmpty()) {
                List<DispatchLetterApprovalPojo> newApprovalList = new ArrayList<>(dispatchLetterResponsePojo.getApproval());
                newApprovalList.sort(Comparator.comparing(DispatchLetterApprovalPojo::getApprovalId, Comparator.nullsLast(Comparator.naturalOrder())));
                for (DispatchLetterApprovalPojo approvalPojo : newApprovalList) {
                    DetailPojo senderSection = null;
                    if (approvalPojo.getSenderPisCode() != null) {

                        if (approvalPojo.getDelegatedId() != null) {
                            DelegationResponsePojo delegationResponsePojoSignature = userMgmtServiceData.getDelegationDetailsById(dispatchLetterResponsePojo.getSignature().getDelegatedId());
                            if (delegationResponsePojoSignature != null && delegationResponsePojoSignature.getToEmployee() != null) {

//                                if (delegationResponsePojoSignature != null
//                                        && delegationResponsePojoSignature.getFromEmployee() != null
//                                        && delegationResponsePojoSignature.getFromEmployee().getCode() != null
//                                        && delegationResponsePojoSignature.getFromEmployee().getCode().equals(officeHeadPisCode))
//                                    approvalPojo.setIsDelegated(Boolean.TRUE);

                                // here need to task
                                if (delegationResponsePojoSignature.getIsReassignment() != null && delegationResponsePojoSignature.getIsReassignment()) {
                                    approvalPojo.setIsReassignment(Boolean.TRUE);
                                    if (delegationResponsePojoSignature.getFromSection() != null)
                                        approvalPojo.setReassignmentSection(delegationResponsePojoSignature.getFromSection());
                                } else {
                                    approvalPojo.setIsDelegated(Boolean.TRUE);
                                }
                                if (delegationResponsePojoSignature.getFromSection() != null)
                                    senderSection = delegationResponsePojoSignature.getFromSection();
                            }
                        } else {
                            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(approvalPojo.getSenderPisCode());
                            if (employeeMinimalPojo != null) {
                                approvalPojo.setSenderName(getStringPascalCase(employeeMinimalPojo.getEmployeeNameEn()));
                                approvalPojo.setSenderNameNp(employeeMinimalPojo.getEmployeeNameNp());
                            }

                            if (approvalPojo.getSenderSectionCode() != null) {
                                SectionPojo sectionPojoDetail = userMgmtServiceData.getSectionDetail(Long.parseLong(approvalPojo.getSenderSectionCode()));
                                if (sectionPojoDetail != null && senderSection == null) {
                                    senderSection = new DetailPojo();
                                    senderSection.setNameEn(sectionPojoDetail.getNameEn());
                                    senderSection.setNameNp(sectionPojoDetail.getNameNp());
                                }
                            }
                        }
                    }
                    if (approvalPojo.getSenderDesignationCode() != null) {
                        designationPojo = userMgmtServiceData.getDesignationDetail(approvalPojo.getSenderDesignationCode());
                    }
                    Ocr ocr = new Ocr().builder()
                            .review(approvalPojo.getRemarks())
                            .requester_name(isEnglish ? getStringPascalCase(approvalPojo.getSenderName()) : approvalPojo.getSenderNameNp())
                            .requester_position(designationPojo != null ? isEnglish ? getStringPascalCase(designationPojo.getNameEn()) : designationPojo.getNameNp() : "")
                            .verificationInformation(approvalPojo.getSignatureVerification())
                            .sectionLetter(includeSectionInLetter && senderSection != null ? isEnglish ? senderSection.getNameEn() : senderSection.getNameNp() : null)
                            .build();
                    ocrList.add(ocr);

                    if (approvalPojo.getReverted() != null && approvalPojo.getReverted())
                        ocrList.clear();
                }
            }

            if (dispatchLetterResponsePojo.getRemarks() != null && dispatchLetterResponsePojo.getRemarksPisCode() != null && dispatchLetterResponsePojo.getRemarksDesignationCode() != null) {
                //EmployeeMinimalPojo remarksUser = userMgmtServiceData.getEmployeeDetailMinimal(dispatchLetterResponsePojo.getRemarksPisCode());
                //DesignationPojo remarksDesignation = userMgmtServiceData.getDesignationDetail(dispatchLetterResponsePojo.getRemarksDesignationCode());

                Ocr remarksOcr = new Ocr();
                remarksOcr.setReview(dispatchLetterResponsePojo.getRemarks());

                if (dispatchLetterResponsePojo.getRemarksUserDetails() != null)
                    remarksOcr.setRequester_name(isEnglish ?
                            dispatchLetterResponsePojo.getIsRemarksUserDelegated() ?
                                    DELEGATED_EN + ", " + getStringPascalCase(dispatchLetterResponsePojo.getRemarksUserDetails().getEmployeeNameEn())
                                    : getStringPascalCase(dispatchLetterResponsePojo.getRemarksUserDetails().getEmployeeNameEn())
                            : dispatchLetterResponsePojo.getIsRemarksUserDelegated() ?
                            DELEGATED_NEP + ", " + dispatchLetterResponsePojo.getRemarksUserDetails().getEmployeeNameNp()
                            : dispatchLetterResponsePojo.getRemarksUserDetails().getEmployeeNameNp());

                remarksOcr.setRequester_position(isEnglish ? getStringPascalCase(dispatchLetterResponsePojo.getRemarksUserDesignationNameEn()) : dispatchLetterResponsePojo.getRemarksUserDesignationNameNp());

                remarksOcr.setVerificationInformation(dispatchLetterResponsePojo.getRemarksVerificationInformation());

                DetailPojo detailSection = null;
                if (dispatchLetterResponsePojo.getRemarksSectionCode() != null) {
                    SectionPojo sectionPojoDetail = userMgmtServiceData.getSectionDetail(Long.parseLong(dispatchLetterResponsePojo.getRemarksSectionCode()));
                    if (sectionPojoDetail != null) {
                        detailSection = new DetailPojo();
                        detailSection.setNameEn(sectionPojoDetail.getNameEn());
                        detailSection.setNameNp(sectionPojoDetail.getNameNp());
                    }
                }

                remarksOcr.setSectionLetter(includeSectionInLetter && detailSection != null ? isEnglish ? detailSection.getNameEn() : detailSection.getNameNp() : null);

                ocrList.add(remarksOcr);
            }

            GeneralTemplate ocTemplate = new GeneralTemplate().builder()
                    .header(useDynamicHeader ? header : null)
                    .logo_url(img)
                    .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                    .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                    .department(isEnglish ? parentOffice != null ? getStringPascalCase(parentOffice.getNameEn()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                    .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                    .letter_date(chalaniDate)
                    .section_header(includeSection ? sectionPojo != null ? isEnglish ? getStringPascalCase(sectionPojo.getNameEn()) : sectionPojo.getNameNp() : null : null)
                    .chali_no(chalaniNo)
                    .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                    .request_to_many(requestTos)
                    .ocr(ocrList)
                    .body_message(dispatchLetterResponsePojo.getContent())
                    .subject(dispatchLetterResponsePojo.getSubject())
                    .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getNameEn()) : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                    .requester_position(isEnglish ? requesterDesignation != null ? getStringPascalCase(requesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : requesterDesignation != null ? requesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                    .bodartha(bodartha)
                    .bodartha_karyartha(karyartha)
                    .saadar_awagataartha(sadarBodartha)
                    .resource_type("C")
                    .resource_id(dispatchLetterResponsePojo.getDispatchId())
                    .footer(footer)
                    .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                    .verificationInformation(dispatchLetterResponsePojo.getActiveSignatureData())
                    .hasSubject(dispatchLetterResponsePojo.getHasSubject())
                    .sectionLetter(includeSectionInLetter ? isEnglish ? requesterSection.getNameEn() : requesterSection.getNameNp() : null)
                    .build();

            System.out.println("Oc: " + new Gson().toJson(ocTemplate));

            dispatchLetterResponsePojo.setOcTemplate(letterTemplateProxy.getOcTemplate(ocTemplate, lang));
        }

        return dispatchLetterResponsePojo;
    }

    private List<RequestTo> removeSameSectionName(List<RequestTo> requestTos) {
        List<RequestTo> requestToWithUniqueSection = new ArrayList<>(requestTos);
        List<String> sectionNameList = new ArrayList<>();
        List<Integer> groupIds = new ArrayList<>();
        requestTos.stream().forEach(x -> {

            if (x.getIsSectionName() != null && x.getIsSectionName()) {
                if (!sectionNameList.contains(x.getSectionName())) {
                    sectionNameList.add(x.getSectionName());
                } else {
                    requestToWithUniqueSection.remove(x);
                }
            } else if (x.getIsGroupName() != null && x.getIsGroupName()) {

                if (!groupIds.contains(x.getGroupId())) {
                    groupIds.add(x.getGroupId());
                } else {
                    requestToWithUniqueSection.remove(x);
                }
            }

        });

        return requestToWithUniqueSection;
    }

    public String getStringPascalCase(String name) {

        if (name == null || name.length() == 0)
            return name;

        String lowerCaseString = name.toLowerCase();
        return Arrays.stream(lowerCaseString.split(" "))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));

    }

    @Override
    public DispatchLetterForward dispatch(DispatchForwardRequestPojo dispatchForwardRequestPojo) {
        DispatchLetterForward dispatchLetterForward = new DispatchLetterForward();
        dispatchLetterForward.setDescription(dispatchForwardRequestPojo.getDescription());
//        dispatchLetterForward.setDispatchLetter(dispatchLetterRepo.getOne(dispatchForwardRequestPojo.getDispatchLetterId()));
        return dispatchForwardRepo.save(dispatchLetterForward);
    }

    //    @Override
//    public List<DispatchedLetterReceiver> forward(DispatchReceiverRequestPojo dispatchReceiverRequestPojo) {
//        List<DispatchedLetterReceiver> dispatchedLetterReceive = new ArrayList<>();
//
//        for (String receiverOfficeId : dispatchReceiverRequestPojo.getReceiverOfficeCode()) {
//            DispatchedLetterReceiver dispatchedLetterReceiver = new DispatchedLetterReceiver();
//            dispatchedLetterReceiver.setDescription(dispatchReceiverRequestPojo.getDescription());
//            dispatchedLetterReceiver.setReceiverOfficeCode(receiverOfficeId);
//            dispatchedLetterReceiver.setDispatchLetterForward(dispatchForwardRepo.getOne(dispatchReceiverRequestPojo.getDispatchLetterForwardId()));
//            dispatchedLetterReceive.add(dispatchedLetterReceiver);
//        }
//        return dispatchReceiverRepo.saveAll(dispatchedLetterReceive);
//    }

    @Override
    public List<DispatchLetterReceiverExternal> dispatchLetterReceiverExternal(DispatchLetterRequestPojo dispatchLetterRequestPojo) {
        List<DispatchLetterReceiverExternal> dispatchLetterReceiverExternals = new ArrayList<>();
        if (dispatchLetterRequestPojo.getDispatchLetterReceiverExternals() != null && !dispatchLetterRequestPojo.getDispatchLetterReceiverExternals().isEmpty()) {
            dispatchLetterRequestPojo.getDispatchLetterReceiverExternals().forEach(
                    x -> {
                        DispatchLetterReceiverExternal dispatchLetterReceiverExternal = new DispatchLetterReceiverExternal().builder()
                                .receiverAddress(x.getReceiverAddress())
                                .receiverEmail(x.getReceiverEmail())
                                .receiverName(x.getReceiverName())
                                .receiverOfficeSectionSubSection(x.getReceiverOfficeSectionSubSection())
                                .receiverPhoneNumber(x.getReceiverPhoneNumber())
                                .dispatch_letter_type(x.getDispatch_letter_type())
                                .toCc(x.isToCc())
                                .orderNumber(x.getOrder())
                                .bodarthaType(x.isToCc() ? x.getBodarthaType() : null)
                                .senderOfficeCode(tokenProcessorService.getOfficeCode())
                                .senderPisCode(tokenProcessorService.getPisCode())
                                .salutation(x.getSalutation())
                                .isGroupName(x.getIsGroupName())
                                .groupId(x.getGroupId())
                                .remarks(x.getRemarks())
                                .build();

                        dispatchLetterReceiverExternals.add(dispatchLetterReceiverExternal);

                    }

            );
        }
        return dispatchLetterReceiverExternals;
    }

    @Override
    public List<DispatchLetterReceiverInternal> dispatchLetterInternal(DispatchLetterRequestPojo dispatchLetterRequestPojo) {
        List<DispatchLetterReceiverInternal> dispatchLetters = new ArrayList<>();
        String officeCode = tokenProcessorService.getOfficeCode();

        if (dispatchLetterRequestPojo.getToReceivers() != null && !dispatchLetterRequestPojo.getToReceivers().isEmpty()) {
            dispatchLetterRequestPojo.getToReceivers().forEach(
                    x -> {
                        DispatchLetterReceiverInternal dispatchLetterReceiverInternal = new DispatchLetterReceiverInternal().builder()
                                .receiverOfficeCode(x.getReceiverOfficeCode() != null ? x.getReceiverOfficeCode() : officeCode)
                                .receiverPisCode(x.getReceiverPisCode())
                                .receiverSectionId(x.getReceiverSectionId())
                                .receiverSectionName(x.getReceiverSectionName())
                                .receiverDesignationCode(x.getReceiverDesignationCode())
                                .toReceiver(true)
                                .completion_status(Status.P)
                                .orderNumber(x.getOrder())
                                .within_organization(x.getWithin_organization())
                                .salutation(x.getSalutation())
                                .senderPisCode(tokenProcessorService.getPisCode())
                                .isGroupName(x.getIsGroupName())
                                .groupId(x.getGroupId())
                                .isSectionName(x.getIsSectionName())
                                .build();

                        dispatchLetters.add(dispatchLetterReceiverInternal);
                    }
            );
        }

        if (dispatchLetterRequestPojo.getIsEdit() != null && dispatchLetterRequestPojo.getIsEdit()) {
            if (dispatchLetterRequestPojo.getCcReceiver() != null && !dispatchLetterRequestPojo.getCcReceiver().isEmpty()) {
                dispatchLetterRequestPojo.getCcReceiver().forEach(
                        x -> {
                            DispatchLetterReceiverInternal dispatchLetterReceiverInternal = new DispatchLetterReceiverInternal().builder()
                                    .receiverOfficeCode(x.getReceiverOfficeCode() == null ? officeCode : x.getReceiverOfficeCode())
                                    .receiverSectionId(x.getReceiverSectionId())
                                    .receiverSectionName(x.getReceiverSectionName())
                                    .toCC(true)
                                    .within_organization(x.getWithin_organization())
                                    .receiverPisCode(x.getReceiverPisCode())
                                    .receiverDesignationCode(x.getReceiverDesignationCode())
                                    .completion_status(Status.P)
                                    .bodarthaType(x.getBodarthaType())
                                    .senderPisCode(tokenProcessorService.getPisCode())
                                    .orderNumber(x.getOrder())
                                    .salutation(x.getSalutation())
                                    .isGroupName(x.getIsGroupName())
                                    .groupId(x.getGroupId())
                                    .remarks(x.getRemarks())
                                    .isSectionName(x.getIsSectionName())
                                    .build();
                            dispatchLetters.add(dispatchLetterReceiverInternal);

                        });
            }
        }
        return dispatchLetters;
    }

    @Override
    public List<DispatchedResponsePojo> getAllDispatched() {
        List<DispatchedResponsePojo> dispatchedResponsePojos = dispatchLetterMapper.getDispatchedLetter();
        dispatchedResponsePojos.forEach(x -> {
            if (x.getStatus() == null || x.getStatus().equals("R")) {
                x.setEditable(true);
            }
            x.setEditable(false);
        });

        return dispatchedResponsePojos;
    }

    @Override
    public Page<DispatchedResponsePojo> getAllInternalLetters(GetRowsRequest request) {
        String internalPisCode = tokenProcessorService.getPisCode();

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(internalPisCode);

        if (employeePojo != null && employeePojo.getSection() == null)
            throw new RuntimeException("प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ");

        //for work on transferred employee chalani
        Set<String> listPisCodes = new HashSet<>();

        listPisCodes.add(internalPisCode);
        if (employeePojo.getSectionId() != null) {
            if (this.getPreviousPisCode(internalPisCode, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(internalPisCode, employeePojo.getSectionId()));
        }

        Page<DispatchedResponsePojo> dispatchedResponsePojos = new Page(request.getPage(), request.getLimit());
        dispatchedResponsePojos = dispatchLetterMapper.getInternalLetters(dispatchedResponsePojos, internalPisCode, request.getSearchField(), employeePojo.getSectionId(), listPisCodes);
        dispatchedResponsePojos.getRecords().forEach(x -> {
            if (x.getStatus() == null || x.getStatus().equals("R")) {
                x.setEditable(true);
            }
            x.setEditable(false);

            List<DispatchLetterInternalDTO> internalDTOS = dispatchLetterMapper.getInternalsByDispatchId(x.getId());
            if (internalDTOS != null && !internalDTOS.isEmpty()) {
                for (DispatchLetterInternalDTO internalDTO : internalDTOS) {
                    if (internalDTO.getWithinOrganization() != null ? internalDTO.getWithinOrganization() : internalDTO.getInternalReceiverPiscode() != null ? Boolean.TRUE : Boolean.FALSE) {
                        if (internalDTO.getInternalReceiverPiscode() != null) {
                            EmployeePojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetail(internalDTO.getInternalReceiverPiscode());
                            if (employeeMinimalPojo != null) {
                                internalDTO.setEmployeeName(employeeMinimalPojo.getNameEn());
                                internalDTO.setEmployeeNameNp(employeeMinimalPojo.getNameNp());
                                internalDTO.setSectionName(employeeMinimalPojo.getSection() != null ? employeeMinimalPojo.getSection().getName() : null);
                                internalDTO.setSectionNameNp(employeeMinimalPojo.getSection() != null ? employeeMinimalPojo.getSection().getNameN() : null);
                                if (employeeMinimalPojo.getSectionId() != null
                                        && internalDTO.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())
                                        && internalDTO.getInternalReceiverSectionId() != null
                                        && internalDTO.getInternalReceiverSectionId().equals(employeeMinimalPojo.getSectionId())) {

                                    if (internalDTO.getInternalReceiverCc() != null && internalDTO.getInternalReceiverCc())
                                        x.setIsCC(internalDTO.getInternalReceiverCc());

                                    if (internalDTO.getInternalReceiver() != null && internalDTO.getInternalReceiver())
                                        x.setIsCC(!internalDTO.getInternalReceiver());

                                    if (Boolean.TRUE.equals(internalDTO.getIsImportant()))
                                        x.setIsImportant(internalDTO.getIsImportant());
                                }
                            }
                        }
                    } else {
                        if (internalDTO.getInternalReceiverOfficeCode() != null) {
                            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(internalDTO.getInternalReceiverOfficeCode());
                            if (officePojo != null) {
                                internalDTO.setEmployeeName(officePojo.getNameEn());
                                internalDTO.setEmployeeNameNp(officePojo.getNameNp());
                            }
                        }
                    }
                }
                x.setDispatchLetterReceiverInternalPojoList(internalDTOS);
            }
            SignatureData signatureData = signatureDataRepo.getByDispatchId(x.getId());
            if (signatureData != null)
                x.setSignatureUser(userMgmtServiceData.getEmployeeDetailMinimal(signatureData.getPisCode()));

        });

        return dispatchedResponsePojos;
    }

    @Override
    public void deleteDispatchedLetter(Long id) {

        String token = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(token);
        checkUserSection(employeePojo);

        String tokenUserSection = employeePojo.getSectionId();

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(token);
        if (employeePojo.getSectionId() != null) {
            if (getPreviousPisCode(token, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(getPreviousPisCode(token, employeePojo.getSectionId()));
        }

        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(id).get();
        if (dispatchLetter.getIsDraft()
                && ((listPisCodes.contains(dispatchLetter.getSenderPisCode())
                && employeePojo.getSectionId().equals(dispatchLetter.getSenderSectionCode())) || draftShareRepo.checkPermissionDispatchShare(id, token, tokenUserSection))) {
            dispatchLetterRepo.softDelete(dispatchLetter.getId());
        } else {
            throw new RuntimeException("Can not Delete");
        }
    }

    @Override
    public void archiveDispatchedLetter(Long id) {
        String token = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(token);
        checkUserSection(employeePojo);

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(token);
        if (employeePojo.getSectionId() != null) {
            if (getPreviousPisCode(token, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(getPreviousPisCode(token, employeePojo.getSectionId()));
        }

        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(id).get();
        if ((dispatchLetterMapper.getActive(id, listPisCodes, employeePojo.getSectionId())
                && listPisCodes.contains(dispatchLetter.getSenderPisCode()) && employeePojo.getSectionId().equals(dispatchLetter.getSenderSectionCode()))) {
            dispatchLetterRepo.archive(dispatchLetter.getId());
        } else {
            throw new RuntimeException("Can not Archive");
        }
    }

    @Override
    public void restoreDispatchedLetter(Long id) {
        String token = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(token);
        checkUserSection(employeePojo);

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(token);
        if (employeePojo.getSectionId() != null) {
            if (getPreviousPisCode(token, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(getPreviousPisCode(token, employeePojo.getSectionId()));
        }

        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(id).get();
        if (Boolean.TRUE.equals(dispatchLetter.getIsArchive())
                && listPisCodes.contains(dispatchLetter.getSenderPisCode())
                && employeePojo.getSectionId().equals(dispatchLetter.getSenderSectionCode())) {
            dispatchLetterRepo.restore(dispatchLetter.getId());
        } else {
            throw new RuntimeException("Can not Restore");
        }
    }

    @Override
    public DispatchLetter saveDispatch(DispatchLetterRequestPojo dispatchLetterRequestPojo) {
        DispatchLetter dispatchLetter = new DispatchLetter().builder()
                .dispatchDateNp("2020-02-03")
                .subject(dispatchLetterRequestPojo.getSubject())
                .senderPisCode(dispatchLetterRequestPojo.getSenderPisCode())
                .build();
        dispatchLetterRepo.save(dispatchLetter);
        return dispatchLetter;
    }


    @Override
    public ArrayList<DispatchLetterResponsePojo> getAllDraftDispatch() {
        String pisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        return dispatchLetterMapper.getAllDraft(pisCode, employeePojo.getSection().getId().toString());
    }

    @Override
    public ArrayList<DispatchLetterResponsePojo> getAllDispatch() {
        String pisCode = tokenProcessorService.getPisCode();
        String officeCode = tokenProcessorService.getOfficeCode();
        OfficeHeadPojo office = userMgmtServiceData.getOfficeHeadDetail(officeCode);
        String officeHeadPisCode = office.getPisCode();
        ArrayList<DispatchLetterResponsePojo> responsePojos = dispatchLetterMapper.getAllDispatch(pisCode, officeCode, officeHeadPisCode);
        responsePojos.forEach(x -> {
            if (x.getStatus() == null || x.getStatus().equals("R")) {
                x.setEditable(true);
            } else {
                x.setEditable(false);
            }
        });
        return responsePojos;
    }

    @Override
    public Long forwardDispatchLetterWithinOrganization(DispatchLetterReceiverInternalPojo dispatchLetterReceiverInternalPojo) {
        String tokenPisCode = tokenProcessorService.getPisCode();
        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(dispatchLetterReceiverInternalPojo.getDispatchLetterId()).get();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user is involved in section or not
        checkUserSection(employeePojo);

        Set<String> listPisCodes = new HashSet<>();

        listPisCodes.add(tokenPisCode);
        if (this.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()) != null)
            listPisCodes.addAll(this.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()));

        String senderSectionCode = employeePojo.getSectionId();

        //DispatchLetterReceiverInternal activeLetter = dispatchLetterMapper.findActive(dispatchLetter.getId(), tokenPisCode, senderSectionCode);

        List<DispatchLetterReceiverInternal> activeLetters = dispatchLetterMapper.findActiveLetters(dispatchLetter.getId(), listPisCodes, senderSectionCode);

        if (activeLetters != null && !activeLetters.isEmpty()) {
            activeLetters.forEach(activeLetter -> {
                activeLetter.setDispatchLetter(dispatchLetter);
                activeLetter.setActive(false);
                activeLetter.setWithin_organization(Boolean.TRUE);
                dispatchLetterReceiverInternalRepo.save(activeLetter);
            });
        }

        //  DispatchLetterReceiverInternal activeLetterCC = dispatchLetterMapper.findActiveCC(dispatchLetter.getId(), tokenPisCode, senderSectionCode);
        List<DispatchLetterReceiverInternal> activeLettersCC = dispatchLetterMapper.findActiveCCLetters(dispatchLetter.getId(), listPisCodes, senderSectionCode);

        if (activeLettersCC != null && !activeLettersCC.isEmpty()) {
            activeLettersCC.forEach(activeLetterCC -> {
                activeLetterCC.setDispatchLetter(dispatchLetter);
                activeLetterCC.setActive(false);
                activeLetterCC.setWithin_organization(Boolean.TRUE);
                dispatchLetterReceiverInternalRepo.save(activeLetterCC);
            });
        }

        if (dispatchLetterMapper.checkIsReceiver(dispatchLetter.getId(), senderSectionCode, listPisCodes)) {
            log.info("pis code: " + tokenPisCode + " does not get this letter : " + dispatchLetter.getId() + " as receiver so that it can not be forwarded");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid User");
        }

        dispatchLetterReceiverInternalPojo.getReceiverDetails().stream().forEach(x -> {

            EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());

            DispatchLetterReceiverInternal activeLetterUser = dispatchLetterMapper.findActive(dispatchLetter.getId(), x.getReceiverPisCode(), x.getReceiverSectionId());
            if (activeLetterUser != null) {
                throw new RuntimeException(receipient != null ? receipient.getNameNp() + " संग यो पत्र पहिले नै भएकोले उक्त पत्र फेरि पठाउन निषेध गरिएको छ|" : "Invalid");
            }

            DispatchLetterReceiverInternal dlrInternal = null;
            if (dispatchLetterReceiverInternalPojo.getId() != null) {
                dlrInternal = dispatchLetterReceiverInternalRepo.findById(dispatchLetterReceiverInternalPojo.getId()).get();
            } else {
                dlrInternal = new DispatchLetterReceiverInternal();
            }

            dlrInternal.setCompletion_status(dispatchLetterReceiverInternalPojo.getCompletionStatus());
            dlrInternal.setDescription(dispatchLetterReceiverInternalPojo.getDescription());
            dlrInternal.setDispatchLetter(dispatchLetter);
            dlrInternal.setReceiverSectionId(x.getReceiverSectionId());
            dlrInternal.setReceiverPisCode(x.getReceiverPisCode());
            dlrInternal.setReceiverOfficeCode(x.getReceiverOfficeCode());
            dlrInternal.setSenderPisCode(tokenProcessorService.getPisCode());
            dlrInternal.setSenderSectionId(senderSectionCode);
            dlrInternal.setToCC(x.isToCC());
            dlrInternal.setWithin_organization(true);
            dispatchLetterReceiverInternalRepo.saveAndFlush(dlrInternal);

        });

        return dispatchLetter.getId();
    }

    @Override
    public String approveDispatchLetter(UpdateDispatchLetterPojo updateDispatchLetterPojo) {

        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user is involved in section or not
        checkUserSection(employeePojo);

        //check the login user is approver for this dispatch_letter
        isApprover(tokenPisCode, employeePojo.getSectionId(), updateDispatchLetterPojo.getDispatchLetterId());

        List<NotificationPojo> notificationPojoList = new ArrayList<>();
        Optional<DispatchLetter> dispatchLetter = dispatchLetterRepo.findDispatchLetterById(updateDispatchLetterPojo.getDispatchLetterId());
        if (!dispatchLetter.isPresent()) {
            throw new RuntimeException("Letter Not Found for Approval");
        }
//        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());

        DispatchLetter letter = dispatchLetter.get();
        EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(letter.getSenderPisCode());

        if (letter.getStatus().equals(Status.A)) {
            throw new RuntimeException("Letter Already Approved");
        }

        letter.setRemarks(updateDispatchLetterPojo.getRemarks());
        letter.setStatus(updateDispatchLetterPojo.getStatus());
        letter.setRemarksSignatureIsActive(updateDispatchLetterPojo.getRemarksSignatureIsActive());
        letter.setRemarksSignature(updateDispatchLetterPojo.getRemarksSignature());
        letter.setHashContent(updateDispatchLetterPojo.getHashContent());
        letter.setRemarksPisCode(tokenProcessorService.getPisCode());
        letter.setRemarksSectionCode(employeePojo != null && employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null);
        letter.setRemarksDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null);

        boolean isEnglish = letter.getIsEnglish() != null && letter.getIsEnglish();

        if (updateDispatchLetterPojo.getStatus().equals(Status.A)) {
            letter.setDispatchNo(
                    isEnglish ?
                            letter.getIsEnglish() ?
                                    initialService.getChalaniNumber(employeeMinimalPojo.getSectionCode()) :
                                    dateConverter.convertBSToDevnagari(initialService.getChalaniNumber(employeeMinimalPojo.getSectionCode())) :
                            dateConverter.convertBSToDevnagari(initialService.getChalaniNumber(employeeMinimalPojo.getSectionCode())));
            letter.setDispatchDateEn(LocalDate.now());
            letter.setDispatchDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
            IdNamePojo idNamePojo = userMgmtServiceData.findActiveFiscalYear();
            letter.setReferenceCode(isEnglish ? idNamePojo.getName() :
                    idNamePojo.getNameN());
            letter.setInclude(updateDispatchLetterPojo.getInclude());
            letter.setLastModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        }

        dispatchLetterRepo.saveAndFlush(letter);

        if (tokenProcessorService.getDelegatedId() != null) {
            DelegationTableMapper delegationTableMapper = DelegationTableMapper.builder()
                    .tableName(DcTablesEnum.DISPATCH)
                    .dispatchLetter(letter)
                    .statusFrom(Status.P)
                    .statusTo(updateDispatchLetterPojo.getStatus())
                    .build();

            delegationTableMapperRepo.save(delegationTableMapper);
        }

        if (updateDispatchLetterPojo.getStatus().equals(Status.A)) {
            DispatchLetterDTO newDispatchLetter = dispatchLetterMapper.getDispatchLetterDetailById(letter.getId());
            if (newDispatchLetter.getDocuments() != null && !newDispatchLetter.getDocuments().isEmpty()) {
                List<DocumentResponsePojo> newDocuments = new ArrayList<>();
                for (DocumentResponsePojo documentDetails : newDispatchLetter.getDocuments()) {
                    if (documentDetails.getInclude())
                        newDocuments.add(documentDetails);
                }
                newDispatchLetter.setDocuments(newDocuments);
            }

            List<DispatchLetterInternalDTO> newLetters = new ArrayList<>();


            if (newDispatchLetter.getDispatchLetterInternal() != null && !newDispatchLetter.getDispatchLetterInternal().isEmpty()) {
                for (DispatchLetterInternalDTO internals : newDispatchLetter.getDispatchLetterInternal()) {
                    if (internals.getWithinOrganization() != null ? !Boolean.TRUE.equals(internals.getWithinOrganization()) : internals.getInternalReceiverPiscode() == null ? Boolean.TRUE : Boolean.FALSE) {
                        newLetters.add(internals);
                    } else {

                        notificationPojoList.add(NotificationPojo.builder()
                                .moduleId(letter.getId())
                                .module(MODULE_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(internals.getInternalReceiverPiscode())
                                .subject(customMessageSource.getNepali("manual.received"))
                                .detail(customMessageSource.getNepali("chalani.approve", employeePojo.getNameNp(), letter.getSubject()))
                                .pushNotification(true)
                                .received(false)
                                .build()
                        );
                    }
                }
                newDispatchLetter.setDispatchLetterInternal(newLetters);
            }

            if (newDispatchLetter.getDispatchLetterInternal() != null && !newDispatchLetter.getDispatchLetterInternal().isEmpty()) {
                receivedLetterService.saveReceivedLetter(newDispatchLetter);
            }

            //send notification to chalani creator when chalani get approved
            notificationPojoList.add(
                    NotificationPojo.builder()
                            .moduleId(letter.getId())
                            .module(MODULE_APPROVAL_KEY)
                            .sender(tokenProcessorService.getPisCode())
                            .receiver(letter.getSenderPisCode())
                            .subject(customMessageSource.getNepali("manual.received"))
                            .detail(customMessageSource.getNepali("chalani.approve", employeePojo.getNameNp(), letter.getSubject()))
                            .pushNotification(true)
                            .received(false)
                            .build()
            );

            //get list of employee who involved in chalani approval flow
            List<String> middleApproverPisCodes = dispatchLetterReviewRepo.getMiddleReviewerPisCode(letter.getId(), tokenProcessorService.getPisCode());
            if (!middleApproverPisCodes.isEmpty()) {
                middleApproverPisCodes.stream().forEach(notificationReceiver -> {

                            //send notification to involved employe in chalani approval flow
                            notificationPojoList.add(
                                    NotificationPojo.builder()
                                            .moduleId(letter.getId())
                                            .module(MODULE_APPROVAL_KEY)
                                            .sender(tokenProcessorService.getPisCode())
                                            .receiver(notificationReceiver)
                                            .subject(customMessageSource.getNepali("manual.received"))
                                            .detail(customMessageSource.getNepali("chalani.approve", employeePojo.getNameNp(), letter.getSubject()))
                                            .pushNotification(true)
                                            .received(false)
                                            .build()
                            );
                        }
                );
            }

        } else {
            //send notification to chalani creator when chalani is rejected
            notificationPojoList.add(
                    NotificationPojo.builder()
                            .moduleId(letter.getId())
                            .module(MODULE_APPROVAL_KEY)
                            .sender(tokenProcessorService.getPisCode())
                            .receiver(letter.getSenderPisCode())
                            .subject(customMessageSource.getNepali("manual.received"))
                            .detail(customMessageSource.getNepali("chalani.reject", employeePojo.getNameNp(), letter.getSubject()))
                            .pushNotification(true)
                            .received(false)
                            .build()
            );
        }

        if (updateDispatchLetterPojo.getMySignature() != null && updateDispatchLetterPojo.getMySignature())
            this.changeSignatureUser(letter.getId(), tokenProcessorService.getPisCode(), updateDispatchLetterPojo.getSignatureIsActive(), updateDispatchLetterPojo.getSignature(), updateDispatchLetterPojo.getHashContent());


        SignatureData signatureData = signatureDataRepo.getByDispatchId(letter.getId());
        if (signatureData != null && updateDispatchLetterPojo.getIncludeSectionId() != null) {
            signatureData.setIncludeSectionId(updateDispatchLetterPojo.getIncludeSectionId());
            signatureData.setIncludedSectionId(updateDispatchLetterPojo.getIncludedSectionId());
            signatureDataRepo.save(signatureData);
        }

        DispatchLetterReview dispatchActive = dispatchLetterReviewRepo.getDispatchByActiveStatus(letter.getId());
        if (dispatchActive != null) {
            dispatchActive.setActive(false);
            dispatchActive.setStatus(Status.F);
            dispatchLetterReviewRepo.saveAndFlush(dispatchActive);
        }

        if (updateDispatchLetterPojo.getChalaniIds() != null && !updateDispatchLetterPojo.getChalaniIds().isEmpty()) {
            for (Long chalaniId : updateDispatchLetterPojo.getChalaniIds()) {
                memoReferenceRepo.chalaniInclude(letter.getId(), chalaniId);
            }
        }

        if (updateDispatchLetterPojo.getTippaniIds() != null && !updateDispatchLetterPojo.getTippaniIds().isEmpty()) {
            for (Long tippaniId : updateDispatchLetterPojo.getTippaniIds()) {
                memoReferenceRepo.tippaniInclude(letter.getId(), tippaniId);
            }
        }

        if (updateDispatchLetterPojo.getDartaIds() != null && !updateDispatchLetterPojo.getDartaIds().isEmpty()) {
            for (Long dartaId : updateDispatchLetterPojo.getDartaIds()) {
                memoReferenceRepo.dartaInclude(letter.getId(), dartaId);
            }
        }

        if (updateDispatchLetterPojo.getDocumentIds() != null && !updateDispatchLetterPojo.getDocumentIds().isEmpty()) {
            for (Long documentId : updateDispatchLetterPojo.getDocumentIds()) {
                dispatchDocumentDetailsRepo.documentInclude(letter.getId(), documentId);
            }
        }

        if (!notificationPojoList.isEmpty()) {
            notificationPojoList.forEach(x -> {
                notificationService.notificationProducer(x);
            });
        }

        return letter.getDispatchNo();
    }

    public String setAlreadyApprovedLetter(UpdateDispatchLetterPojo updateDispatchLetterPojo) {

        String tokenPisCode =  tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user is involved in section or not
        checkUserSection(employeePojo);

        List<NotificationPojo> notificationPojoList = new ArrayList<>();
        Optional<DispatchLetter> dispatchLetter = dispatchLetterRepo.findDispatchLetterById(updateDispatchLetterPojo.getDispatchLetterId());
        if (!dispatchLetter.isPresent()) {
            throw new RuntimeException("Letter Not Found for Approval");
        }
//        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());

        DispatchLetter letter = dispatchLetter.get();
        EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(letter.getSenderPisCode());

        letter.setRemarks(updateDispatchLetterPojo.getRemarks());
        letter.setStatus(updateDispatchLetterPojo.getStatus());
        letter.setRemarksSignatureIsActive(Boolean.FALSE);
        letter.setRemarksSignature(null);
        letter.setHashContent(null);
        letter.setRemarksPisCode(tokenPisCode);
        letter.setRemarksSectionCode(employeePojo != null && employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null);
        letter.setRemarksDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null);

        boolean isEnglish = letter.getIsEnglish() != null && letter.getIsEnglish();

        if (updateDispatchLetterPojo.getStatus().equals(Status.A)) {
            letter.setDispatchNo(
                    isEnglish ?
                            letter.getIsEnglish() ?
                                    initialService.getChalaniNumber(employeeMinimalPojo.getSectionCode()) :
                                    dateConverter.convertBSToDevnagari(initialService.getChalaniNumber(employeeMinimalPojo.getSectionCode())) :
                            dateConverter.convertBSToDevnagari(initialService.getChalaniNumber(employeeMinimalPojo.getSectionCode())));
            letter.setDispatchDateEn(LocalDate.now());
            letter.setDispatchDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
            letter.setReferenceCode(isEnglish ? userMgmtServiceData.findActiveFiscalYear().getName() :
                    userMgmtServiceData.findActiveFiscalYear().getNameN());
            letter.setLastModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        }

        dispatchLetterRepo.saveAndFlush(letter);

        if (updateDispatchLetterPojo.getStatus().equals(Status.A)) {
            DispatchLetterDTO newDispatchLetter = dispatchLetterMapper.getDispatchLetterDetailById(letter.getId());
            if (newDispatchLetter.getDocuments() != null && !newDispatchLetter.getDocuments().isEmpty()) {
                List<DocumentResponsePojo> newDocuments = new ArrayList<>();
                for (DocumentResponsePojo documentDetails : newDispatchLetter.getDocuments()) {
                    if (documentDetails.getInclude())
                        newDocuments.add(documentDetails);
                }
                newDispatchLetter.setDocuments(newDocuments);
            }

            List<DispatchLetterInternalDTO> newLetters = new ArrayList<>();

            if (newDispatchLetter.getDispatchLetterInternal() != null && !newDispatchLetter.getDispatchLetterInternal().isEmpty()) {
                for (DispatchLetterInternalDTO internals : newDispatchLetter.getDispatchLetterInternal()) {
                    if (internals.getWithinOrganization() != null ? !Boolean.TRUE.equals(internals.getWithinOrganization()) : internals.getInternalReceiverPiscode() == null ? Boolean.TRUE : Boolean.FALSE) {
                        newLetters.add(internals);
                    } else {

                        notificationPojoList.add(NotificationPojo.builder()
                                .moduleId(letter.getId())
                                .module(MODULE_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(internals.getInternalReceiverPiscode())
                                .subject(customMessageSource.getNepali("manual.received"))
                                .detail(customMessageSource.getNepali("chalani.approve", employeePojo.getNameNp(), letter.getSubject()))
                                .pushNotification(true)
                                .received(false)
                                .build()
                        );
                    }
                }
                newDispatchLetter.setDispatchLetterInternal(newLetters);
            }

            if (newDispatchLetter.getDispatchLetterInternal() != null && !newDispatchLetter.getDispatchLetterInternal().isEmpty()) {
                receivedLetterService.saveReceivedLetter(newDispatchLetter);
            }
        }

        DispatchLetterReview dispatchActive = dispatchLetterReviewRepo.getDispatchByActiveStatus(letter.getId());
        if (dispatchActive != null) {
            dispatchActive.setActive(false);
            dispatchActive.setStatus(Status.F);
            dispatchLetterReviewRepo.saveAndFlush(dispatchActive);
        }

        if (!notificationPojoList.isEmpty()) {
            notificationPojoList.forEach(x -> {
                notificationService.notificationProducer(x);
            });
        }

        return letter.getDispatchNo();
    }

    @Override
    public List<DispatchedResponsePojo> getAllForwardedLetters() {
        String pisCode = tokenProcessorService.getPisCode();
        List<DispatchedResponsePojo> dispatchResponse = dispatchLetterMapper.getAllForwardedLetters(pisCode);

        dispatchResponse.stream().forEach(x -> {
            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode());
            if (employeeMinimalPojo != null) {
                x.setReceiverName(employeeMinimalPojo.getEmployeeNameEn());
                x.setReceiverNameNp(employeeMinimalPojo.getEmployeeNameNp());
            }
        });

        return dispatchResponse;
    }

    @Override
    public List<DispatchLetterInternalDTO> getForwardedLetterDetail(Long dispatchLetterId) {
        String pisCode = tokenProcessorService.getPisCode();
        List<DispatchLetterInternalDTO> dispatchLetterDetails = dispatchLetterMapper.getForwardedLetterDetail(pisCode, dispatchLetterId);
        dispatchLetterDetails.forEach(x -> {
            if (x.getInternalReceiverPiscode() != null) {
                EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getInternalReceiverPiscode());
                if (employeeMinimalPojo != null) {
                    x.setEmployeeName(employeeMinimalPojo.getEmployeeNameEn());
                    x.setEmployeeNameNp(employeeMinimalPojo.getEmployeeNameNp());

                }
            }
        });

        dispatchLetterDetails.forEach(x -> {
            x.setCreatedDateEn(x.getCreatedDate() != null ? dateConverter.convertAdToBs(x.getCreatedDate().toString()) : null);
            x.setCreatedDateNp(x.getCreatedDate() != null ? dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getCreatedDate().toString())) : null);


            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getSenderPisCode());
            if (x.getDelegatedId() != null) {
                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                    EmployeeMinimalPojo userDetail = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode());
                    x.setSendersName(userDetail.getEmployeeNameEn());
                    x.setSendersNameNp(userDetail.getEmployeeNameNp());

                    //set designation of delegated user
                    x.setDesignationName(employeeMinimalPojo != null && employeeMinimalPojo.getFunctionalDesignation() != null ? employeeMinimalPojo.getFunctionalDesignation().getName() : null);
                    x.setDesignationNameNp(employeeMinimalPojo != null && employeeMinimalPojo.getFunctionalDesignation() != null ? employeeMinimalPojo.getFunctionalDesignation().getNameN() : null);
                }
            } else {
                if (employeeMinimalPojo != null) {
                    x.setSendersName(employeeMinimalPojo.getEmployeeNameEn());
                    x.setSendersNameNp(employeeMinimalPojo.getEmployeeNameNp());
                }
            }
        });

        return dispatchLetterDetails;
    }

    @Override
    public List<DispatchedResponsePojo> getInProgressDispatchLetters() {
        String pisCode = tokenProcessorService.getPisCode();
        List<DispatchedResponsePojo> dispatchedResponsePojos = dispatchLetterMapper.getInprogressDispatchLetters(pisCode);
        dispatchedResponsePojos.stream().forEach(x -> {
            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode());
            if (employeeMinimalPojo != null) {
                x.setReceiverName(employeeMinimalPojo.getEmployeeNameEn());
                x.setReceiverNameNp(employeeMinimalPojo.getEmployeeNameNp());
            }
        });

        return dispatchedResponsePojos;
    }

    @Override
    public List<DispatchedResponsePojo> getFinalizedDispatchLetters() {
        String pisCode = tokenProcessorService.getPisCode();
        List<DispatchedResponsePojo> dispatchedResponsePojos = dispatchLetterMapper.getFinalizedDispatchLetter(pisCode);
        dispatchedResponsePojos.stream().forEach(x -> {
            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode());
            if (employeeMinimalPojo != null) {
                x.setReceiverName(employeeMinimalPojo.getEmployeeNameEn());
                x.setReceiverNameNp(employeeMinimalPojo.getEmployeeNameNp());
            }
        });

        return dispatchedResponsePojos;
    }

    @Override
    public Long updateForwardedDispatchLetter(UpdateDispatchLetterPojo updateDispatchLetterPojo) {
        String pisCode = tokenProcessorService.getPisCode();
        List<DispatchLetterReceiverInternal> dispatchLetterReceiverInternalList = dispatchLetterReceiverInternalRepo.getDispatchLetterByPisCodeAndDispatchId(pisCode, updateDispatchLetterPojo.getDispatchLetterId());
        if (dispatchLetterReceiverInternalList.size() <= 0) {
            throw new RuntimeException("Data not Found");
        }

        dispatchLetterReceiverInternalList.stream().forEach(dispatchLetterReceiverInternal -> {
            dispatchLetterReceiverInternal.setCompletion_status(updateDispatchLetterPojo.getStatus());
            //dispatchLetterReceiverInternal.setDescription(updateDispatchLetterPojo.getDescription());
            dispatchLetterReceiverInternalRepo.save(dispatchLetterReceiverInternal);

            if (!updateDispatchLetterPojo.getStatus().equals("FN")) {
                DispatchLetterReceiveInternalDetail receiveInternalDetail = new DispatchLetterReceiveInternalDetail();
                receiveInternalDetail.setDescription(updateDispatchLetterPojo.getDescription());
                receiveInternalDetail.setStaus(updateDispatchLetterPojo.getStatus());
                receiveInternalDetail.setWithinOrganization(dispatchLetterReceiverInternal);
                receiveInternalDetail.setDate(LocalDate.now());
                receiveInternalDetail.setDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
                receiveInternalDetailRepo.save(receiveInternalDetail);
            }
        });
        return dispatchLetterReceiverInternalList.get(0).getId();
    }

    public Long getActiveTemplateHeaderFooter(TemplateType type) {
        OfficeTemplatePojo template = userMgmtServiceData.getOfficeTemplate(tokenProcessorService.getOfficeCode(), type.toString());
        if (type.equals(TemplateType.F) && template == null) {
            return null;
        }
        if (template == null) {
            throw new ServiceValidationException(customMessageSource.get("empty.request", "office.template"));
        }
        return template.getId();
    }

    @Override
    @Transactional
    public Long sendForReview(DispatchLetterReviewPojo dispatchLetterReviewPojo) {

        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo senderPojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        OfficeHeadPojo office = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
        DispatchLetterReview dispatchActive = dispatchLetterReviewRepo.getDispatchByActiveStatus(dispatchLetterReviewPojo.getDispatchLetterId());
        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(dispatchLetterReviewPojo.getDispatchLetterId()).orElseThrow(() -> new ServiceValidationException("crud.not_exits"));

        Boolean isAlreadyApproved = dispatchLetter.getIsAlreadyApproved() != null ? dispatchLetter.getIsAlreadyApproved() : Boolean.FALSE;

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(isAlreadyApproved ? tokenPisCode : dispatchLetterReviewPojo.getReceiverPisCode());

        //check sender section
        checkUserSection(senderPojo);

        //check receiver section
        checkUserSection(employeePojo);

        String tokenUserSectionCode = senderPojo.getSectionId();

        if (!dispatchLetter.getIsDraft()) {
            //check for dispatch letter creator or get letter for review
            checkSendForReview(tokenPisCode, senderPojo.getSectionId(), dispatchLetterReviewPojo.getDispatchLetterId());
        } else {
            if (!draftShareRepo.checkPermissionDispatchShare(dispatchLetterReviewPojo.getDispatchLetterId(), tokenPisCode, tokenUserSectionCode)
                    && !(dispatchLetter.getSenderPisCode().equals(tokenPisCode) && dispatchLetter.getSenderSectionCode().equals(tokenUserSectionCode)))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid User");
        }

        Long id = null;
        if (dispatchLetterReviewPojo.getId() != null) {
            id = dispatchLetterReviewPojo.getId();
        }

        //validation for empty receiver
        if (dispatchLetter.getDispatchLetterReceiverExternals().size() == 0
                && dispatchLetter.getDispatchLetterReceiverInternals().size() == 0)
            throw new RuntimeException("Receivers not found for this Letter.");

        // setting template header only for initial forward
        if (dispatchLetter.getTemplateHeaderId() == null) {
            dispatchLetter.setTemplateHeaderId(this.getActiveTemplateHeaderFooter(TemplateType.H));
        }

        // setting template footer only for initial forward
        if (dispatchLetter.getTemplateFooterId() == null) {
            dispatchLetter.setTemplateFooterId(this.getActiveTemplateHeaderFooter(TemplateType.F));
        }

//
//        if (senderPojo == null)
//            throw new RuntimeException("Employee detail not found with id: " + tokenProcessorService.getPisCode());
//
//        if (employeePojo == null)
//            throw new RuntimeException("Employee detail not found with pis: " + dispatchLetterReviewPojo.getReceiverPisCode());
//
//        if (employeePojo.getSection() == null)
//            throw new RuntimeException("Employee section not found");

        DispatchLetterReview dispatchLetterReview = DispatchLetterReview.builder()
                .id(id)
                .receivedDate(LocalDate.now())
                .receivedDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()))
                .receiverOfficeCode(isAlreadyApproved ? tokenProcessorService.getOfficeCode() : dispatchLetterReviewPojo.getReceiverOfficeCode())
                .receiverPisCode(isAlreadyApproved ? tokenPisCode : dispatchLetterReviewPojo.getReceiverPisCode())
                .receiverSectionCode(isAlreadyApproved ? tokenUserSectionCode : dispatchLetterReviewPojo.getReceiverSectionCode())
                .receiverDesignationCode(employeePojo.getFunctionalDesignationCode())
                .senderPisCode(tokenProcessorService.getPisCode())
                .senderDesignationCode(senderPojo != null ? senderPojo.getFunctionalDesignationCode() : "")
                .senderSectionCode(senderPojo != null ? senderPojo.getSection() != null ? senderPojo.getSection().getId().toString() : "" : "")
                .remarks(dispatchLetterReviewPojo.getRemarks())
                .remarksSignatureIsActive(dispatchLetterReviewPojo.getRemarksSignatureIsActive())
                .remarksSignature(dispatchLetterReviewPojo.getRemarksSignature())
                .hashContent(dispatchLetterReviewPojo.getHashContent())
                .status(Status.valueOf(dispatchLetterReviewPojo.getStatus()))
                .subject(dispatchLetterReviewPojo.getSubject())
                .dispatchLetter(dispatchLetter)
                .isSeen(false)
                .reverted(false)
                .build();
        DispatchLetterReview saved = dispatchLetterReviewRepo.saveAndFlush(dispatchLetterReview);
        DispatchLetterReviewDetail ds = new DispatchLetterReviewDetail();
        ds.setDescription(null);
        ds.setStaus("Received");
        ds.setDispatchLetterReview(dispatchLetterReview);
        dispatchLetterReviewDetail.save(ds);

        if (dispatchLetter.getIsDraft() && !dispatchLetter.getSenderPisCode().equals(tokenPisCode)) {
            dispatchLetterRepo.updateDispatchLetterForDraftShare(tokenPisCode, tokenUserSectionCode, employeePojo.getFunctionalDesignationCode(), dispatchLetter.getId());
        }

        //disable draft
        dispatchLetterRepo.setIsDraft(dispatchLetter.getId());

        if (dispatchActive != null) {
            dispatchActive.setActive(false);
            dispatchActive.setStatus(Status.F);
            dispatchLetterReviewRepo.saveAndFlush(dispatchActive);
        }

        if (!isAlreadyApproved && office != null && dispatchLetterReviewPojo.getReceiverPisCode() != null && dispatchLetterReviewPojo.getReceiverPisCode().equals(office.getPisCode())) {
            dispatchLetter.setStatus(Status.P);
            dispatchLetterRepo.save(dispatchLetter);
        }

        if (dispatchLetterReviewPojo.getMySignature() != null && dispatchLetterReviewPojo.getMySignature())
            this.changeSignatureUser(dispatchLetter.getId(), tokenProcessorService.getPisCode(), dispatchLetterReviewPojo.getSignatureIsActive(), dispatchLetterReviewPojo.getSignature(), dispatchLetterReviewPojo.getHashContent());

        SignatureData signatureData = signatureDataRepo.getByDispatchId(dispatchLetter.getId());
        if (signatureData != null && dispatchLetterReviewPojo.getIncludeSectionId() != null) {
            signatureData.setIncludeSectionId(dispatchLetterReviewPojo.getIncludeSectionId());
            signatureData.setIncludedSectionId(dispatchLetterReviewPojo.getIncludedSectionId());
            signatureDataRepo.save(signatureData);
        }

        if (isAlreadyApproved) {
            UpdateDispatchLetterPojo updateDispatchLetterPojo = new UpdateDispatchLetterPojo();
            updateDispatchLetterPojo.setDispatchLetterId(dispatchLetterReviewPojo.getDispatchLetterId());
            updateDispatchLetterPojo.setRemarks(dispatchLetterReviewPojo.getRemarks());
            updateDispatchLetterPojo.setStatus(Status.A);
            setAlreadyApprovedLetter(updateDispatchLetterPojo);
        } else {
            notificationService.notificationProducer(
                    NotificationPojo.builder()
                            .moduleId(dispatchLetter.getId())
                            .module(MODULE_APPROVAL_KEY)
                            .sender(tokenProcessorService.getPisCode())
                            .receiver(dispatchLetterReviewPojo.getReceiverPisCode())
                            .subject(customMessageSource.getNepali("manual.received"))
                            .detail(customMessageSource.getNepali("chalani.forward", senderPojo.getNameNp(), dispatchLetter.getSubject()))
                            .pushNotification(true)
                            .received(true)
                            .build()
            );
        }

        return dispatchLetterReview.getId();
    }

    @Override
    public List<DispatchedResponsePojo> getReviewedDispatchLetters() {
        String pisCode = tokenProcessorService.getPisCode();
        List<DispatchedResponsePojo> dispatchedResponsePojos = dispatchLetterMapper.getReviewedDispatchLetters(pisCode);

        dispatchedResponsePojos.stream().forEach(x -> {
            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode());
            if (employeeMinimalPojo != null) {
                x.setReceiverName(employeeMinimalPojo.getEmployeeNameEn());
                x.setReceiverNameNp(employeeMinimalPojo.getEmployeeNameNp());
            }
        });

        return dispatchedResponsePojos;
    }

    @Override
    public Long updateForReview(UpdateDispatchLetterPojo updateDispatchLetterPojo) {
        String pisCode = tokenProcessorService.getPisCode();

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(pisCode);

        checkUserSection(employeePojo);

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(pisCode);

        if (this.getPreviousPisCode(pisCode, employeePojo.getSectionId()) != null) {
            listPisCodes.addAll(this.getPreviousPisCode(pisCode , employeePojo.getSectionId()));
        }

        DispatchLetterReview dispatchLetterReview = dispatchLetterReviewRepo.getDispatchLetterByPisCodeAndDispatchLetter(listPisCodes, updateDispatchLetterPojo.getDispatchLetterId());
        dispatchLetterReview.setStatus(updateDispatchLetterPojo.getStatus());
        dispatchLetterReview.setRemarks(updateDispatchLetterPojo.getRemarks());
        dispatchLetterReviewRepo.save(dispatchLetterReview);
        DispatchLetterReviewDetail ds = new DispatchLetterReviewDetail();
        ds.setDispatchLetterReview(dispatchLetterReview);
        ds.setStaus(updateDispatchLetterPojo.getStatus().getValueEnglish());
        ds.setDescription(updateDispatchLetterPojo.getRemarks());
        dispatchLetterReviewDetail.save(ds);
        return dispatchLetterReview.getId();

    }

    @Override
    public Long backForReview(UpdateDispatchLetterPojo updateDispatchLetterPojo) {
        String pisCode = tokenProcessorService.getPisCode();

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        System.out.println("sectionId: "+employeePojo.getSectionId());

        //check login user is involved in any section or not
        checkUserSection(employeePojo);

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(pisCode);

        if (this.getPreviousPisCode(pisCode, employeePojo.getSectionId()) != null) {
            listPisCodes.addAll(this.getPreviousPisCode(pisCode , employeePojo.getSectionId()));
        }

        //check login user is approver for this dispatch letter
        isApprover(pisCode, employeePojo.getSectionId(), updateDispatchLetterPojo.getDispatchLetterId());

        DispatchLetterReview dispatchLetterReview = dispatchLetterReviewRepo.getDispatchLetterByPisCodeAndDispatchLetter(listPisCodes, updateDispatchLetterPojo.getDispatchLetterId());
        dispatchLetterReview.setStatus(updateDispatchLetterPojo.getStatus());
        dispatchLetterReview.setActive(false);
        dispatchLetterReviewRepo.save(dispatchLetterReview);
        DispatchLetterReviewDetail ds = new DispatchLetterReviewDetail();
        ds.setDispatchLetterReview(dispatchLetterReview);
        ds.setStaus("BackforReview");
        ds.setDescription(updateDispatchLetterPojo.getRemarks());
        dispatchLetterReviewDetail.save(ds);

        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(dispatchLetterReview.getDispatchLetter().getId()).get();

        EmployeePojo receiverPojo = userMgmtServiceData.getEmployeeDetail(dispatchLetter.getSenderPisCode());

        DispatchLetterReview newDispatchLetterReview = new DispatchLetterReview().builder()
                .status(Status.P)
                .senderPisCode(tokenProcessorService.getPisCode())
                .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                .remarks(updateDispatchLetterPojo.getRemarks())
                .remarksSignatureIsActive(updateDispatchLetterPojo.getRemarksSignatureIsActive())
                .remarksSignature(updateDispatchLetterPojo.getRemarksSignature())
                .hashContent(updateDispatchLetterPojo.getHashContent())
                .receiverPisCode(dispatchLetter.getSenderPisCode())
                .receiverOfficeCode(dispatchLetter.getSenderOfficeCode())
                .receiverSectionCode(dispatchLetter.getSenderSectionCode())
                .receiverDesignationCode(receiverPojo != null ? receiverPojo.getFunctionalDesignationCode() : null)
                .receivedDate(LocalDate.now())
                .receivedDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()))
                .isSeen(false)
                .reverted(true)
                .dispatchLetter(dispatchLetter)
                .build();
        dispatchLetterReviewRepo.save(newDispatchLetterReview);

        notificationService.notificationProducer(
                NotificationPojo.builder()
                        .moduleId(dispatchLetter.getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(tokenProcessorService.getPisCode())
                        .receiver(dispatchLetter.getSenderPisCode())
                        .subject(customMessageSource.getNepali("manual.received"))
                        .detail(customMessageSource.getNepali("chalani.forward", employeePojo != null ? employeePojo.getNameNp() : "", dispatchLetter.getSubject()))
                        .pushNotification(true)
                        .received(false)
                        .build()
        );

        return dispatchLetterReview.getId();
    }

    @Override
    public Page<DispatchLetterResponsePojo> filterData(GetRowsRequest paginatedRequest) {
        Page<DispatchLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        Set<String> listPisCodes = new HashSet<>();

        if (paginatedRequest.getPisCode() != null)
            paginatedRequest.setPisCode(paginatedRequest.getPisCode().equals(tokenProcessorService.getPisCode()) ? paginatedRequest.getPisCode() : null);
        if (paginatedRequest.getSearchField().get("sectionCode") != null) {
            if (this.getPreviousPisCode(tokenProcessorService.getPisCode(), paginatedRequest.getSearchField().get("sectionCode").toString()) != null) {
                listPisCodes.addAll(this.getPreviousPisCode(tokenProcessorService.getPisCode(), paginatedRequest.getSearchField().get("sectionCode").toString()));
            }
        }

        if (paginatedRequest.getSearchField().get("isApprover") != null) {
            if (paginatedRequest.getSearchField().get("isApprover").toString().equalsIgnoreCase("true")) {
                if (paginatedRequest.getSearchField().get("pisCode") != null && paginatedRequest.getSearchField().get("appSectionCode") != null) {
                    if (this.getPreviousPisCode(paginatedRequest.getSearchField().get("pisCode").toString(), paginatedRequest.getSearchField().get("appSectionCode").toString()) != null)
                        listPisCodes.addAll(this.getPreviousPisCode(paginatedRequest.getSearchField().get("pisCode").toString(), paginatedRequest.getSearchField().get("appSectionCode").toString()));
                }
            }

        }
        if (paginatedRequest.getSearchField().get("receiverPisCode") != null) {
            if (paginatedRequest.getSearchField().get("receiverSectionCode") != null) {
                if (this.getPreviousPisCode(paginatedRequest.getSearchField().get("receiverPisCode").toString(), paginatedRequest.getSearchField().get("receiverSectionCode").toString()) != null)
                    listPisCodes.addAll(this.getPreviousPisCode(paginatedRequest.getSearchField().get("receiverPisCode").toString(), paginatedRequest.getSearchField().get("receiverSectionCode").toString()));
            }
        }
        if (paginatedRequest.getSearchField().get("dispatchReceiverPisCode") != null) {
            if (paginatedRequest.getSearchField().get("dispatchReceiverSectionCode") != null) {
                if (this.getPreviousPisCode(paginatedRequest.getSearchField().get("dispatchReceiverPisCode").toString(), paginatedRequest.getSearchField().get("dispatchReceiverSectionCode").toString()) != null)
                    listPisCodes.addAll(this.getPreviousPisCode(paginatedRequest.getSearchField().get("dispatchReceiverPisCode").toString(), paginatedRequest.getSearchField().get("dispatchReceiverSectionCode").toString()));
            }
        }

        if (paginatedRequest.getSearchField().get("senderPisCode") != null) {
            if (paginatedRequest.getSearchField().get("senderSectionCode") != null) {
                if (this.getPreviousPisCode(paginatedRequest.getSearchField().get("senderPisCode").toString(), paginatedRequest.getSearchField().get("senderSectionCode").toString()) != null)
                    listPisCodes.addAll(this.getPreviousPisCode(paginatedRequest.getSearchField().get("senderPisCode").toString(), paginatedRequest.getSearchField().get("senderSectionCode").toString()));
            }
        }

        if (paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE) != null) {
            if (paginatedRequest.getSearchField().get(TRANSFER_FROM_SECTION_CODE) != null) {
                paginatedRequest.getSearchField().put("receiverPisCode", paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE).toString());
                paginatedRequest.getSearchField().put("receiverSectionCode", paginatedRequest.getSearchField().get(TRANSFER_FROM_SECTION_CODE).toString());
                if (this.getPreviousPisCode(paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE).toString(), paginatedRequest.getSearchField().get(TRANSFER_FROM_SECTION_CODE).toString()) != null) {
                    listPisCodes.addAll(this.getPreviousPisCode(paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE).toString(), paginatedRequest.getSearchField().get(TRANSFER_FROM_SECTION_CODE).toString()));
                }
            }
        }

        page = dispatchLetterMapper.filterData(
                page,
                paginatedRequest.getPisCode(),
                tokenProcessorService.getOfficeCode(),
                listPisCodes,
                paginatedRequest.getSearchField()
        );

        //add current employee pis code
        listPisCodes.add(tokenProcessorService.getPisCode());
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            for (DispatchLetterResponsePojo data : page.getRecords()) {

                //this flag is only for chalani creator
                if (data.getIsImportant() != null && data.getIsImportant() && !listPisCodes.contains(data.getSenderPisCode()))
                    data.setIsImportant(Boolean.FALSE);

                //flag for draft share
                if (data.isDraft())
                    data.setDraftShareCount(draftShareRepo.dispatchDraftShareCount(data.getId()));

                if (data.getDelegatedId() != null) {
                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(data.getDelegatedId());
                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                        data.setCreator(userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode()));
                } else
                    data.setCreator(userMgmtServiceData.getEmployeeDetail(data.getSenderPisCode()));

                DispatchLetterReview dispatchActive = dispatchLetterReviewRepo.getDispatchByActiveStatus(data.getId());

                if (dispatchActive != null) {
                    if (dispatchActive.getReceiverPisCode().equals(tokenProcessorService.getPisCode()) && dispatchActive.getActive()) {
                        data.setEditable(true);
                    }
                }

                if (data.getApproval() == null || data.getApproval().isEmpty())
                    data.setEditable(true);

                List<DispatchLetterApprovalPojo> approvalPojos = dispatchLetterMapper.getApprovalList(data.getId());
                if (approvalPojos != null && !approvalPojos.isEmpty()) {
                    data.setApproval(approvalPojos);
                    data.getApproval().forEach(
                            x -> {
                                if (x.getReceiverPisCode() != null) {

                                    EmployeePojo empDetail = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());

                                    if (empDetail != null && empDetail.getSectionId() != null
                                            && x.getReceiverPisCode().equals(tokenProcessorService.getPisCode())
                                            && x.getReceiverSectionCode() != null
                                            && x.getReceiverSectionCode().equals(empDetail.getSectionId())) {
                                        data.setApprovalIsActive(x.getIsActive());
                                        data.setApprovalStatus(x.getApprovalStatus());

                                        if (Boolean.TRUE.equals(x.getIsImportant()))
                                            data.setIsImportant(true);
                                    }
                                }
                            }
                    );
                }

                List<DispatchLetterInternalDTO> internalDTOS = dispatchLetterMapper.getInternalsByDispatchId(data.getId());
                if (internalDTOS != null && !internalDTOS.isEmpty()) {
                    for (DispatchLetterInternalDTO internalDTO : internalDTOS) {
                        if (internalDTO.getWithinOrganization() != null ? internalDTO.getWithinOrganization() : internalDTO.getInternalReceiverPiscode() != null ? Boolean.TRUE : Boolean.FALSE) {
                            if (internalDTO.getInternalReceiverPiscode() != null) {
                                EmployeePojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetail(internalDTO.getInternalReceiverPiscode());
                                SectionPojo sectionPojo = null;
                                if (internalDTO.getInternalReceiverSectionId() != null) {
                                    sectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(internalDTO.getInternalReceiverSectionId()));
                                }
                                if (employeeMinimalPojo != null) {
                                    internalDTO.setEmployeeName(employeeMinimalPojo.getNameEn());
                                    internalDTO.setEmployeeNameNp(employeeMinimalPojo.getNameNp());

                                    internalDTO.setSectionName(sectionPojo != null ? sectionPojo.getNameEn() : null);
                                    internalDTO.setSectionNameNp(sectionPojo != null ? sectionPojo.getNameNp() : null);
                                    if (employeeMinimalPojo.getSectionId() != null
                                            && internalDTO.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())
                                            && internalDTO.getInternalReceiverSectionId() != null
                                            && internalDTO.getInternalReceiverSectionId().equals(employeeMinimalPojo.getSectionId())) {
                                        if (Boolean.TRUE.equals(internalDTO.getIsImportant()))
                                            data.setIsImportant(internalDTO.getIsImportant());
                                    }
                                }
                            }
                        } else if (internalDTO.getWithinOrganization() != null ? !internalDTO.getWithinOrganization() : internalDTO.getInternalReceiverPiscode() == null ? Boolean.TRUE : Boolean.FALSE) {
                            if (internalDTO.getInternalReceiverOfficeCode() != null) {
                                OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(internalDTO.getInternalReceiverOfficeCode());
                                if (officePojo != null) {
                                    internalDTO.setEmployeeName(officePojo.getNameEn());
                                    internalDTO.setEmployeeNameNp(officePojo.getNameNp());
                                }
                            }
                        }
                    }
                    data.setDispatchLetterInternal(internalDTOS);
                }

                List<DispatchLetterExternalDTO> externalDTOS = dispatchLetterMapper.getExternalsByDispatchId(data.getId());
                if (externalDTOS != null && !externalDTOS.isEmpty()) {
                    data.setDispatchLetterExternal(externalDTOS);
                }

                data.setPreviousPisCodes(listPisCodes);
            }
        }

        return page;
    }

    @Override
    public Page<DispatchLetterResponsePojo> getArchiveChalani(GetRowsRequest paginatedRequest) {
        Page<DispatchLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        Set<String> listPisCodes = new HashSet<>();

        String pisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(pisCode);

        checkUserSection(employeePojo);

        String sectionCode = employeePojo.getSectionId();

        listPisCodes.add(pisCode);
        if (this.getPreviousPisCode(pisCode, sectionCode) != null)
            listPisCodes.addAll(this.getPreviousPisCode(pisCode, sectionCode));

        page = dispatchLetterMapper.getArchiveChalani(
                page,
                tokenProcessorService.getOfficeCode(),
                listPisCodes,
                sectionCode,
                paginatedRequest.getSearchField()
        );

        //add current employee pis code
        listPisCodes.add(tokenProcessorService.getPisCode());
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            for (DispatchLetterResponsePojo data : page.getRecords()) {

                //this flag is only for chalani creator
                if (data.getIsImportant() != null && data.getIsImportant() && !listPisCodes.contains(data.getSenderPisCode()))
                    data.setIsImportant(Boolean.FALSE);

                if (data.getDelegatedId() != null) {
                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(data.getDelegatedId());
                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                        data.setCreator(userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode()));
                } else
                    data.setCreator(userMgmtServiceData.getEmployeeDetail(data.getSenderPisCode()));

                DispatchLetterReview dispatchActive = dispatchLetterReviewRepo.getDispatchByActiveStatus(data.getId());

                if (dispatchActive != null) {
                    if (dispatchActive.getReceiverPisCode().equals(tokenProcessorService.getPisCode()) && dispatchActive.getActive()) {
                        data.setEditable(true);
                    }
                }

                if (data.getApproval() == null || data.getApproval().isEmpty())
                    data.setEditable(true);

                List<DispatchLetterApprovalPojo> approvalPojos = dispatchLetterMapper.getApprovalList(data.getId());
                if (approvalPojos != null && !approvalPojos.isEmpty()) {
                    data.setApproval(approvalPojos);
                    data.getApproval().forEach(
                            x -> {
                                EmployeePojo approvalUser = null;
                                if (x.getReceiverPisCode() != null) {
                                    if (x.getDelegatedId() != null) {
                                        DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                                        if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                            approvalUser = userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode());
                                    } else
                                        approvalUser = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());
                                    EmployeePojo empDetail = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());

                                    if (empDetail != null && empDetail.getSectionId() != null
                                            && x.getReceiverPisCode().equals(tokenProcessorService.getPisCode())
                                            && x.getReceiverSectionCode() != null
                                            && x.getReceiverSectionCode().equals(empDetail.getSectionId())) {
                                        data.setApprovalIsActive(x.getIsActive());
                                        data.setApprovalStatus(x.getApprovalStatus());

                                        if (Boolean.TRUE.equals(x.getIsImportant()))
                                            data.setIsImportant(true);
                                    }
                                }
                            }
                    );
                }

                List<DispatchLetterInternalDTO> internalDTOS = dispatchLetterMapper.getInternalsByDispatchId(data.getId());
                if (internalDTOS != null && !internalDTOS.isEmpty()) {
                    for (DispatchLetterInternalDTO internalDTO : internalDTOS) {
                        if (internalDTO.getWithinOrganization() != null ? internalDTO.getWithinOrganization() : internalDTO.getInternalReceiverPiscode() != null ? Boolean.TRUE : Boolean.FALSE) {
                            if (internalDTO.getInternalReceiverPiscode() != null) {
                                EmployeePojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetail(internalDTO.getInternalReceiverPiscode());
                                if (employeeMinimalPojo != null) {
                                    internalDTO.setEmployeeName(employeeMinimalPojo.getNameEn());
                                    internalDTO.setEmployeeNameNp(employeeMinimalPojo.getNameNp());
                                    internalDTO.setSectionName(employeeMinimalPojo.getSection() != null ? employeeMinimalPojo.getSection().getName() : null);
                                    internalDTO.setSectionNameNp(employeeMinimalPojo.getSection() != null ? employeeMinimalPojo.getSection().getNameN() : null);
                                    if (employeeMinimalPojo.getSectionId() != null
                                            && internalDTO.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())
                                            && internalDTO.getInternalReceiverSectionId() != null
                                            && internalDTO.getInternalReceiverSectionId().equals(employeeMinimalPojo.getSectionId())) {
                                        if (Boolean.TRUE.equals(internalDTO.getIsImportant()))
                                            data.setIsImportant(internalDTO.getIsImportant());
                                    }
                                }
                            }
                        } else if (internalDTO.getWithinOrganization() != null ? !internalDTO.getWithinOrganization() : internalDTO.getInternalReceiverPiscode() == null ? Boolean.TRUE : Boolean.FALSE) {
                            if (internalDTO.getInternalReceiverOfficeCode() != null) {
                                OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(internalDTO.getInternalReceiverOfficeCode());
                                if (officePojo != null) {
                                    internalDTO.setEmployeeName(officePojo.getNameEn());
                                    internalDTO.setEmployeeNameNp(officePojo.getNameNp());
                                }
                            }
                        }
                    }
                    data.setDispatchLetterInternal(internalDTOS);
                }

                List<DispatchLetterExternalDTO> externalDTOS = dispatchLetterMapper.getExternalsByDispatchId(data.getId());
                if (externalDTOS != null && !externalDTOS.isEmpty()) {
                    data.setDispatchLetterExternal(externalDTOS);
                }

                data.setPreviousPisCodes(listPisCodes);
            }
        }

        return page;
    }

    public List<String> getPreviousPisCode(String pisCode, String SectionId) {
        if (userMgmtServiceData.getPreviousEmployeeDetail(pisCode, Long.parseLong(SectionId)) != null) {
            return (userMgmtServiceData.getPreviousEmployeeDetail(pisCode, Long.parseLong(SectionId)));
        }
        return null;
    }

    @Override
    public List<DispatchLetterExternalDTO> getExternalUsers() {
        return dispatchLetterMapper.getExternalsByOfficeCode(tokenProcessorService.getOfficeCode());
    }

    @Override
    public Long savePdf(SaveDispatchPdfPojo data) {
        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(data.getDispatchId()).get();
        List<DispatchPdfData> pdfData = new ArrayList<>();

        data.getPdf().forEach(x -> {
            DispatchPdfData dispatchPdfData = new DispatchPdfData().builder()
                    .dispatchLetter(dispatchLetter)
                    .pdf(x).build();
            pdfData.add(dispatchPdfData);
        });

        if (!pdfData.isEmpty())
            dispatchPdfDataRepo.saveAll(pdfData);

        return dispatchLetter.getId();
    }

    @Override
    public DispatchLetter contentUpdate(DispatchLetterRequestPojo data) {
        DispatchLetter dispatchLetter = this.findById(data.getId());

//        EmployeePojo tokenUserDetails = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
//
//        String sectionCode = tokenUserDetails != null ? tokenUserDetails.getSection() != null ? tokenUserDetails.getSection().getId().toString() : null : null;
//        String designationCode = tokenUserDetails != null ? tokenUserDetails.getFunctionalDesignationCode() : null;
//
//        DispatchLetterUpdateHistory updateHistory = new DispatchLetterUpdateHistory().builder()
//                .dispatchLetter(dispatchLetter)
//                .pisCode(tokenProcessorService.getPisCode())
//                .sectionCode(sectionCode)
//                .designationCode(designationCode)
//                .oldSubject(dispatchLetter.getSubject())
//                .oldContent(dispatchLetter.getContent())
//                .newSubject(data.getSubject())
//                .newContent(data.getContent())
//                .build();
//
//        dispatchLetter.setSubject(data.getSubject());
//        dispatchLetter.setContent(data.getContent());
//        dispatchLetterRepo.save(dispatchLetter);
//        dispatchLetterUpdateHistoryRepo.save(updateHistory);

//        if (data.getIsContentChanged()) {
//            SignatureData signatureData = signatureDataRepo.getByDispatchId(dispatchLetter.getId());
//            if (signatureData != null) {
//                signatureData.setSignatureIsActive(data.getSignatureIsActive());
//                signatureData.setSignature(data.getSignatureIsActive() != null ? data.getSignatureIsActive() ? data.getSignature() : null : null);
//                signatureDataRepo.save(signatureData);
//            }
//        }

        return dispatchLetter;
    }

    @Override
    public Long setImportantFlag(Long id, boolean value, String type) {
        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(id).orElseThrow(() -> new CustomException("Letter Not Found"));
        String pisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(pisCode);

        List<DispatchLetterReview> letterReviews = new ArrayList<>();

        if (employeePojo != null && employeePojo.getSectionId() != null && type == null) {

            String sectionCode = employeePojo.getSectionId();

//            if (dispatchLetter.getIsImportant() != null && dispatchLetter.getIsImportant() && !value) {
//                dispatchLetter.setIsImportant(false);
//                dispatchLetter.setLastModifiedDateImp(new Timestamp(new Date().getTime()));
//                dispatchLetterRepo.save(dispatchLetter);
//            } else

            Set<String> listPisCodes = new HashSet<>();
            listPisCodes.add(pisCode);
            if (this.getPreviousPisCode(pisCode, sectionCode) != null)
                listPisCodes.addAll(this.getPreviousPisCode(pisCode, sectionCode));

            if (listPisCodes.contains(dispatchLetter.getSenderPisCode())
                    && dispatchLetter.getSenderSectionCode().equals(sectionCode)) {
                dispatchLetter.setIsImportant(dispatchLetter.getIsImportant() != null ? !dispatchLetter.getIsImportant() : Boolean.TRUE);
                dispatchLetter.setLastModifiedDateImp(new Timestamp(new Date().getTime()));
                dispatchLetterRepo.save(dispatchLetter);
            }

            letterReviews = dispatchLetterReviewRepo.getAllDispatchLetterReviewByPisCodeAndSectionCode(tokenProcessorService.getPisCode(), dispatchLetter.getId(), employeePojo.getSectionId());
        }

        if (type != null && type.equals("internal")) {
            if (employeePojo != null
                    && employeePojo.getSectionId() != null) {
                DispatchLetterReceiverInternal dispatchLetterReceiverInternal = dispatchLetterReceiverInternalRepo.getInternalByReceiver(tokenProcessorService.getPisCode(), employeePojo.getSectionId(), dispatchLetter.getId());
                if (dispatchLetterReceiverInternal != null) {
                    dispatchLetterReceiverInternal.setIsImportant(value);
                    dispatchLetterReceiverInternal.setLastModifiedDate(dispatchLetterReceiverInternal.getLastModifiedDate());
                    dispatchLetterReceiverInternalRepo.save(dispatchLetterReceiverInternal);
                }
            }
        } else if (letterReviews != null && !letterReviews.isEmpty()) {
            letterReviews.forEach(
                    x -> x.setIsImportant(value)
            );
            dispatchLetterReviewRepo.saveAll(letterReviews);
        }

        return dispatchLetter.getId();
    }

    private MultiValueMap<String, Object> convertToMultiValueMap(MultipartFile file, String fileName) {
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        if (documentUtil.checkEmpty(file)) {
            Resource resource = documentUtil.getUserFileResource(documentUtil.getRootLocation().toString(), file, null);
            body.add("temp_file", resource);
        }
        return body;
    }

    private void changeSignatureUser(Long id, String pisCode, Boolean signatureIsActive, String signature, String hashContent) {
        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(id).get();
        SignatureData update = signatureDataRepo.getByDispatchId(dispatchLetter.getId());
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(pisCode);

        if (employeePojo == null)
            throw new RuntimeException("Employee Does not Exist");

        SignatureData signatureData = new SignatureData();
        signatureData.setPisCode(employeePojo.getPisCode());
        signatureData.setSectionCode(employeePojo.getSection().getId().toString());
        signatureData.setDesignationCode(employeePojo.getFunctionalDesignationCode());
        signatureData.setDispatchLetter(dispatchLetter);
        signatureData.setSignatureIsActive(signatureIsActive);
        signatureData.setSignature(signatureIsActive != null ? signatureIsActive ? signature : null : null);
        signatureData.setHashContent(hashContent);

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, signatureData);
        } catch (Exception e) {
            throw new RuntimeException("Does not exist");
        }

        signatureDataRepo.save(update);

    }

    @Override
    public Map<String, Object> getSearchRecommendation() {
        Map<String, Object> response = new HashMap<>();

        String pisCode = tokenProcessorService.getPisCode();

        String sectionId = userMgmtServiceData.getEmployeeDetail(pisCode).getSectionId();

        List<SearchRecommendationDto> searchRecommendation = dispatchLetterMapper.getSearchRecommendation(pisCode, sectionId);

        // extracting unique data for every column returned from the above query
        List<Map<String, Object>> uniqueReceiverNames = searchRecommendation.stream()
                .filter(obj -> obj.getReceiverName() != null && !obj.getReceiverName().isEmpty() && !obj.getReceiverName().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> {
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put("name", obj.getReceiverName());
                    tempMap.put("type", SearchRecommentionTypeEnum.EXTERNAL);
                    return tempMap;
                })
                .distinct()
                .collect(Collectors.toList());

        List<String> uniqueReceiverPisCode = searchRecommendation.stream()
                .filter(obj -> obj.getReceiverPisCode() != null && !obj.getReceiverPisCode().isEmpty() && !obj.getReceiverPisCode().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> obj.getReceiverPisCode())
                .distinct()
                .collect(Collectors.toList());

        List<String> uniqueReceiverOfficeCode = searchRecommendation.stream()
                .filter(obj -> obj.getReceiverOfficeCode() != null && !obj.getReceiverOfficeCode().isEmpty() && !obj.getReceiverOfficeCode().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> obj.getReceiverOfficeCode())
                .distinct()
                .collect(Collectors.toList());

        List<String> uniqueSenderDetails = searchRecommendation.stream()
                .filter(obj -> obj.getSenderPisCode() != null && !obj.getSenderPisCode().isEmpty() && !obj.getSenderPisCode().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> obj.getSenderPisCode())
                .distinct()
                .collect(Collectors.toList());

        // building response body
        List<Map<String, Object>> senderDetails = uniqueSenderDetails.stream().map(obj -> {
            Map<String, Object> tempSenderDetail = new HashMap<>();
            EmployeePojo senderDetail = userMgmtServiceData.getEmployeeDetail(obj);
            if (senderDetail == null) {
                return null;
            }
            tempSenderDetail.put("nameEn", senderDetail.getNameEn());
            tempSenderDetail.put("nameNp", senderDetail.getNameNp());
            tempSenderDetail.put("sectionId", senderDetail.getSectionId());
            tempSenderDetail.put("officeCode", senderDetail.getOffice().getCode());
            tempSenderDetail.put("pisCode", obj);
            tempSenderDetail.put("type", SearchRecommentionTypeEnum.SENDER);
            return tempSenderDetail;
        })
                .collect(Collectors.toList());

        List<Map<String, Object>> employeeDetails = uniqueReceiverPisCode.stream().map(obj -> {

            Map<String, Object> tempEmployeeDetail = new HashMap<>();
            EmployeePojo employeeDetail1 = userMgmtServiceData.getEmployeeDetail(obj);
            if (employeeDetail1 == null) {
                return null;
            }
            tempEmployeeDetail.put("nameEn", employeeDetail1.getNameEn());
            tempEmployeeDetail.put("nameNp", employeeDetail1.getNameNp());
            tempEmployeeDetail.put("sectionId", employeeDetail1.getSectionId());
            tempEmployeeDetail.put("officeCode", employeeDetail1.getOffice().getCode());
            tempEmployeeDetail.put("pisCode", obj);
            tempEmployeeDetail.put("type", SearchRecommentionTypeEnum.WITHIN);
            return tempEmployeeDetail;
        })
                .collect(Collectors.toList());

        List<Map<String, Object>> receiverOfficeDetails = uniqueReceiverOfficeCode.stream().map(obj -> {
            Map<String, Object> tempReceiverOfficeDetails = new HashMap<>();
            OfficeMinimalPojo officeDetailMinimal = userMgmtServiceData.getOfficeDetailMinimal(obj);
            if (officeDetailMinimal == null) {
                return null;
            }
            tempReceiverOfficeDetails.put("nameEn", officeDetailMinimal.getNameEn());
            tempReceiverOfficeDetails.put("nameNp", officeDetailMinimal.getNameNp());
            tempReceiverOfficeDetails.put("receiverOfficeCode", obj);
            tempReceiverOfficeDetails.put("type", SearchRecommentionTypeEnum.OFFICE);
            return tempReceiverOfficeDetails;
        })
                .collect(Collectors.toList());

        response.put("receiverDetails", uniqueReceiverNames);
        response.put("receiverEmployeeDetails", employeeDetails);
        response.put("receiverOfficeDetails", receiverOfficeDetails);
        response.put("senderDetails", senderDetails);
        return response;
    }

    @Override
    public List<Map<String, Object>> getInternalLetterSender() {

        List<String> senderPisCodes = dispatchLetterMapper.getInterLetterSenderList(tokenProcessorService.getPisCode());

        List<Map<String, Object>> data = senderPisCodes.stream().map(obj -> {
            Map<String, Object> senders = new HashMap<>();
            EmployeePojo senderDetail = userMgmtServiceData.getEmployeeDetail(obj);
            if (senderDetail == null)
                return null;
            senders.put("nameEn", senderDetail.getNameEn());
            senders.put("nameNp", senderDetail.getNameNp());
            senders.put("sectionId", senderDetail.getSectionId());
            senders.put("officeCode", senderDetail.getOffice() != null ? senderDetail.getOffice().getCode() : null);
            senders.put("pisCode", obj);
            senders.put("type", SearchRecommentionTypeEnum.WITHIN);
            return senders;
        })
                .collect(Collectors.toList());

        return data;
    }

    @Override
    public Long testInternalLetterCount() {

        // this is only for test purpose
        Long value = dispatchLetterMapper.countAllInternalLetters("212310");

        return value;
    }

    //this function check the user get the letter for approve
    private void isApprover(String pisCode, String sectionCode, Long id) {
        Set<String> listPisCodes = new HashSet<>();

        listPisCodes.add(pisCode);
        if (this.getPreviousPisCode(pisCode, sectionCode) != null)
            listPisCodes.addAll(this.getPreviousPisCode(pisCode, sectionCode));

        //List<String> pisCodes = dispatchLetterMapper.getApproverList(id, sectionCode);
        //if (!listPisCodes.stream().anyMatch(x -> pisCodes.contains(x))) {
        if (dispatchLetterMapper.checkApproverList(id, listPisCodes, sectionCode)) {
            log.info("pis code: " + pisCode + " does not get this letter : " + id + " for approve");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid User");
        }
    }

    //this function check the user is involved in any section or not
    private void checkUserSection(EmployeePojo employeePojo) {
        if (employeePojo == null)
            throw new RuntimeException("Employee detail not found with id : " + tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ");
    }

    //this function check that the user is eligible for send dispatch letter for review or not
    private void checkSendForReview(String pisCode, String sectionCode, Long id) {
        Set<String> listPisCodes = new HashSet<>();

        listPisCodes.add(pisCode);
        if (this.getPreviousPisCode(pisCode, sectionCode) != null)
            listPisCodes.addAll(this.getPreviousPisCode(pisCode, sectionCode));

        //List<String> pisCodes = dispatchLetterMapper.getReviewerList(id, sectionCode);
        // if (!listPisCodes.stream().anyMatch(x-> pisCodes.contains(x))) {
        if (dispatchLetterMapper.checkReviewerList(id, listPisCodes, sectionCode)) {
            log.info("pis code: " + pisCode + " does not create this letter or get for review: " + id + " so it cant not be sent for review");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid User");
        }
    }

    public String convertListToString(Set<String> pisCodes) {
        AtomicReference<String> pisCodeString = new AtomicReference<>("");
        pisCodeString.set(pisCodeString.get().concat("{"));
        AtomicInteger count = new AtomicInteger(1);
        pisCodes.stream().forEach(x -> {
            pisCodeString.set(pisCodeString.get().concat(x));
            if (count.get() < pisCodes.size()) {
                pisCodeString.set(pisCodeString.get().concat(","));

            }
            count.getAndIncrement();
        });
        pisCodeString.set(pisCodeString.get().concat("}"));
        return pisCodeString.get();
    }

    @Override
    public DispatchLetterDTO getDispatchLetterByIdForQr(Long id, String type, String  mobileNumber) {

        //for work on transferred employee chalani
//        Set<String> listPisCodes = new HashSet<>();
//        String tokenPisCode = tokenProcessorService.getPisCode();
//        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);
//
//        //check user is involved in any section or not
//        checkUserSection(tokenUser);
//
//        String tokenUserSection = tokenUser.getSectionId();

//        listPisCodes.add(tokenPisCode);
//        if (tokenUser.getSectionId() != null) {
//            if (this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
//                listPisCodes.addAll(this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
//        }

        DispatchLetterDTO dispatchLetterResponsePojo = dispatchLetterMapper.getDispatchLetterDetailById(id);
        dispatchLetterResponsePojo.setMobileNumber(mobileNumber);

//        if (!dispatchLetterResponsePojo.getIsDraft()) {
//            if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
//                List<String> involvedOffices = dispatchLetterMapper.getInvolvedOffices(id);
//                log.info("Involved offices in dispatch letter: " + involvedOffices);
//                if (!involvedOffices.contains(tokenProcessorService.getOfficeCode()))
//                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
//            } else {
//                String strPisCodes = this.convertListToString(listPisCodes);
//                log.info("dispatch letter id: " + id + " and section: " + tokenUser.getSectionId());
//                System.out.println("previousPisCodes: " + strPisCodes);
//                if (!dispatchLetterMapper.checkInvolvedChalani(id, tokenUser.getSectionId(), strPisCodes))
//                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
//            }
//        } else {
//            if (!draftShareRepo.checkPermissionDispatchShare(id, tokenPisCode, tokenUserSection)
//                    && !(dispatchLetterResponsePojo.getSenderPisCode().equals(tokenPisCode) && dispatchLetterResponsePojo.getSenderSectionCode().equals(tokenUserSection)))
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
//        }

//        dispatchLetterResponsePojo.setPreviousPisCodes(listPisCodes);

        //get office head pis code for delegation detail
//        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
//        String officeHeadPisCode = "";
//        if (officeHeadPojo != null && officeHeadPojo.getPisCode() != null)
//            officeHeadPisCode = officeHeadPojo.getPisCode();

        List<DispatchLetterCommentsPojo> dispatchLetterCommentsPojos = dispatchLetterMapper.getAllCommentsOfDispatchLetter(id);
        if (dispatchLetterCommentsPojos != null && !dispatchLetterCommentsPojos.isEmpty()) {
            dispatchLetterCommentsPojos.forEach(x -> {
                x.setDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getDate().toString())));
            });
        }

        dispatchLetterResponsePojo.setAllComments(dispatchLetterCommentsPojos);

        Boolean isHashed = dispatchLetterResponsePojo != null ? dispatchLetterResponsePojo.getHash_content() == null ? Boolean.FALSE : Boolean.TRUE : Boolean.FALSE;
        dispatchLetterResponsePojo.setDispatchNo(dispatchLetterResponsePojo.getDispatchNo());
        EmployeeMinimalPojo employeeMinimal = userMgmtServiceData.getEmployeeDetailMinimal(dispatchLetterResponsePojo.getSenderPisCode());
        if (employeeMinimal != null) {
            dispatchLetterResponsePojo.setSenderName(employeeMinimal.getEmployeeNameEn());
            dispatchLetterResponsePojo.setSenderNameNp(employeeMinimal.getEmployeeNameNp());
        }

        if (dispatchLetterResponsePojo.getDelegatedId() != null) {
            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(dispatchLetterResponsePojo.getDelegatedId());

//            if (dispatchLetterResponsePojo.getSenderPisCode().equals(officeHeadPisCode))
//                dispatchLetterResponsePojo.setIsDelegated(Boolean.TRUE);

            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                dispatchLetterResponsePojo.setSenderName(delegationResponsePojo.getToEmployee().getNameEn());
                dispatchLetterResponsePojo.setSenderNameNp(delegationResponsePojo.getToEmployee().getNameNp());

                //check the delegated user is reassignment and set value accordingly
                if (delegationResponsePojo.getIsReassignment() != null
                        && delegationResponsePojo.getIsReassignment()) {
                    dispatchLetterResponsePojo.setIsReassignment(Boolean.TRUE);
                    if (delegationResponsePojo.getFromSection() != null)
                        dispatchLetterResponsePojo.setReassignmentSection(delegationResponsePojo.getFromSection());
                } else {
                    dispatchLetterResponsePojo.setIsDelegated(Boolean.TRUE);
                }
                dispatchLetterResponsePojo.setSignedPis(delegationResponsePojo.getToEmployee().getCode());
            }
        }

        if (type != null && type.equals("receiver") && !dispatchLetterResponsePojo.getDocuments().isEmpty()) {
            dispatchLetterResponsePojo.setDocuments(this.getActiveDocuments(dispatchLetterResponsePojo.getDocuments()));
        }

        if (dispatchLetterResponsePojo.getRemarksPisCode() != null && !dispatchLetterResponsePojo.getRemarksPisCode().isEmpty()) {
            DelegationTableMapper delegationTableMapper = delegationTableMapperRepo.getByDispatchId(dispatchLetterResponsePojo.getDispatchId());
            dispatchLetterResponsePojo.setIsRemarksUserDelegated(false);
            if (delegationTableMapper != null
                    && delegationTableMapper.getDelegatedId() != null
                    && delegationTableMapper.getStatusTo() != null
                    && delegationTableMapper.getStatusTo().equals(Status.A)) {
                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(delegationTableMapper.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null && delegationResponsePojo.getToEmployee().getCode() != null) {

//                    if (dispatchLetterResponsePojo.getRemarksPisCode().equals(officeHeadPisCode))
//                        dispatchLetterResponsePojo.setIsRemarksUserDelegated(Boolean.TRUE);
                    //check the delegated user is reassignment and set value accordingly
                    if (delegationResponsePojo.getIsReassignment() != null
                            && delegationResponsePojo.getIsReassignment()) {
                        dispatchLetterResponsePojo.setIsRemarksUserReassignment(Boolean.TRUE);
                        if (delegationResponsePojo.getFromSection() != null)
                            dispatchLetterResponsePojo.setRemarksUserReassignmentSection(delegationResponsePojo.getFromSection());
                    } else {
                        dispatchLetterResponsePojo.setIsRemarksUserDelegated(Boolean.TRUE);
                        dispatchLetterResponsePojo.setRemarksUserDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                        dispatchLetterResponsePojo.setRemarksUserDesignationNameEn(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameEn() : "");
                    }

                    dispatchLetterResponsePojo.setRemarksUserDetails(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                }
            } else {
                EmployeeMinimalPojo remarksUserMinimal = userMgmtServiceData.getEmployeeDetailMinimal(dispatchLetterResponsePojo.getRemarksPisCode());
                dispatchLetterResponsePojo.setRemarksUserDetails(remarksUserMinimal);
                if (dispatchLetterResponsePojo.getRemarksDesignationCode() != null) {
                    DetailPojo designationPojo = userMgmtServiceData.getDesignationDetailByCode(dispatchLetterResponsePojo.getRemarksDesignationCode());
                    if (designationPojo != null) {
                        dispatchLetterResponsePojo.setRemarksUserDesignationNameNp(designationPojo.getNameNp());
                        dispatchLetterResponsePojo.setRemarksUserDesignationNameEn(designationPojo.getNameEn());
                    }
                }
            }
        }

        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getSignatureIsActive() != null && dispatchLetterResponsePojo.getSignature().getSignatureIsActive()) {
            Boolean isHashedSignatureData = dispatchLetterResponsePojo.getSignature().getHashContent() == null ? Boolean.FALSE : Boolean.TRUE;

            VerificationInformation verification = verifySignatureService.verify(dispatchLetterResponsePojo.getContent(), dispatchLetterResponsePojo.getSignature().getSignatureData(), isHashedSignatureData);
            dispatchLetterResponsePojo.setVerificationInformation(verification);

            if (verification != null) {

                Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                //check for alternate result ie. if the letter is verified or not verified record already exists or not
                Boolean result = signatureVerificationLogRepository.
                        existsDispatchLog(
                                SignatureType.DISPATCH.toString(),
                                dispatchLetterResponsePojo.getDispatchId());

                if (result == null || result != isVerified)
                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                            .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                            .signatureType(SignatureType.DISPATCH)
                            .isVerified(isVerified)
                            .signatureBy(dispatchLetterResponsePojo.getSignedPis() == null ? dispatchLetterResponsePojo.getSignature().getPisCode() : dispatchLetterResponsePojo.getSignedPis())
                            .individualStatus(dispatchLetterResponsePojo.getStatus())
                            .build());
            }
        }

        if (dispatchLetterResponsePojo.getSignature() != null &&
                dispatchLetterResponsePojo.getSignature().getDesignationCode() != null &&
                !dispatchLetterResponsePojo.getSignature().getDesignationCode().isEmpty()) {
            dispatchLetterResponsePojo.getSignature().setDesignationPojo(userMgmtServiceData.getDesignationDetail(dispatchLetterResponsePojo.getSignature().getDesignationCode()));
        }


        if (dispatchLetterResponsePojo.getRemarksSignatureIsActive() != null && dispatchLetterResponsePojo.getRemarksSignatureIsActive()) {
            String newCont = dispatchLetterResponsePojo.getContent() + " " + dispatchLetterResponsePojo.getRemarks();
            VerificationInformation verification = verifySignatureService.verify(newCont, dispatchLetterResponsePojo.getRemarksSignatureData(), isHashed);
            dispatchLetterResponsePojo.setRemarksVerificationInformation(verification);

            if (verification != null) {

                Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                //check for alternate result ie. if the letter is verified or not verified record already exists or not
                Boolean result = signatureVerificationLogRepository.
                        existsDispatchLog(
                                SignatureType.DISPATCH.toString(),
                                dispatchLetterResponsePojo.getDispatchId());

                if (result == null || result != isVerified)
                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                            .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                            .signatureType(SignatureType.DISPATCH)
                            .isVerified(isVerified)
                            .signatureBy(dispatchLetterResponsePojo.getRemarksUserDetails().getPisCode())
                            .individualStatus(dispatchLetterResponsePojo.getStatus())
                            .build());
            }

        }

        boolean involve = false;

        if (type != null && type.equals("receiver") && dispatchLetterResponsePojo.getInclude() != null && !dispatchLetterResponsePojo.getInclude())
            involve = true;


        if (!involve) {
            List<GenericReferenceDto> memoReferences = memoReferenceMapper.getChalaniMemoReference(dispatchLetterResponsePojo.getDispatchId());

            if (memoReferences != null && !memoReferences.isEmpty()) {
                List<MemoResponsePojo> memoResponsePojos = new ArrayList<>();
                for (GenericReferenceDto tippani : memoReferences) {
                    if (tippani != null && tippani.getId() != null) {

                        if (type != null && type.equals("receiver") && tippani.getInclude() != null && !tippani.getInclude())
                            continue;

                        MemoResponsePojo letter = memoMapper.getMemoById(tippani.getId());
                        letter.setReferenceId(tippani.getReferenceId());
                        letter.setReferenceIsEditable(tippani.getReferenceIsEditable());
                        letter.setReferenceEmployee(userMgmtServiceData.getEmployeeDetailMinimal(tippani.getPisCode()));
                        memoResponsePojos.add(letter);
                    }
                }
                if (!memoResponsePojos.isEmpty()) {
                    for (MemoResponsePojo data : memoResponsePojos) {
                        data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                        data.setCreatedTimeNp(dateConverter.convertBSToDevnagari(data.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
                        data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()));
                    }
                }
                dispatchLetterResponsePojo.setMemoReferences(memoResponsePojos);
            }

            List<GenericReferenceDto> dartaReference = memoReferenceMapper.getChalaniDartaReference(dispatchLetterResponsePojo.getDispatchId());

            if (dartaReference != null && !dartaReference.isEmpty()) {
                List<ReceivedLetterResponsePojo> receivedLetterResponsePojos = new ArrayList<>();
                for (GenericReferenceDto darta : dartaReference) {
                    if (darta != null && darta.getId() != null) {

                        if (type != null && type.equals("receiver") && darta.getInclude() != null && !darta.getInclude())
                            continue;

                        ReceivedLetterResponsePojo letter = receivedLetterMapper.getReceivedLetter(darta.getId());
                        letter.setReferenceId(darta.getReferenceId());
                        letter.setReferenceIsEditable(darta.getReferenceIsEditable());
                        letter.setReferenceCreator(userMgmtServiceData.getEmployeeDetailMinimal(darta.getPisCode()));
                        receivedLetterResponsePojos.add(letter);
                    }
                }
                if (!receivedLetterResponsePojos.isEmpty()) {
                    for (ReceivedLetterResponsePojo data : receivedLetterResponsePojos) {
                        data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()));
                        data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                        data.setCreatedTimeNp(dateConverter.convertBSToDevnagari(data.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
                    }
                }
                dispatchLetterResponsePojo.setDartaReferences(receivedLetterResponsePojos);
            }

            List<GenericReferenceDto> chalaniReference = memoReferenceMapper.getChalaniChalaniReference(dispatchLetterResponsePojo.getDispatchId());

            if (chalaniReference != null && !chalaniReference.isEmpty()) {
                List<DispatchLetterDTO> dispatchLetterDTOS = new ArrayList<>();
                for (GenericReferenceDto chalani : chalaniReference) {
                    if (chalani != null && chalani.getId() != null) {

                        if (type != null && type.equals("receiver") && chalani.getInclude() != null && !chalani.getInclude())
                            continue;

                        DispatchLetterDTO letter = dispatchLetterMapper.getDispatchLetterDetailById(chalani.getId());
                        letter.setReferenceId(chalani.getReferenceId());
                        letter.setReferenceIsEditable(chalani.getReferenceIsEditable());
                        letter.setReferenceCreator(userMgmtServiceData.getEmployeeDetailMinimal(chalani.getPisCode()));
                        dispatchLetterDTOS.add(letter);
                    }
                }
                if (!dispatchLetterDTOS.isEmpty()) {
                    for (DispatchLetterDTO data : dispatchLetterDTOS) {
                        EmployeeMinimalPojo employeeMinimal1 = userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode());
                        if (employeeMinimal != null) {
                            data.setSenderName(employeeMinimal1.getEmployeeNameEn());
                            data.setSenderNameNp(employeeMinimal1.getEmployeeNameNp());
                        }
                    }
                }
                dispatchLetterResponsePojo.setChalaniReference(dispatchLetterDTOS);
            }
        }

        DispatchLetterReview dispatchActive = dispatchLetterReviewRepo.getDispatchByActiveStatus(dispatchLetterResponsePojo.getDispatchId());

        if (dispatchActive != null) {
            if (dispatchActive.getReceiverPisCode().equals(tokenProcessorService.getPisCode()) && dispatchActive.getActive()) {
                dispatchLetterResponsePojo.setEditable(true);
            }
        }

        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getPisCode() != null) {
            String reviewContent;
            DispatchLetterReview dispatchLetterReview = dispatchLetterReviewRepo.findLatestByUser(dispatchLetterResponsePojo.getSignature().getPisCode(), dispatchLetterResponsePojo.getDispatchId());

//            condition when chalani creator signature is replaced by chalani approver
            if (dispatchLetterResponsePojo.getSignature() != null
                    && dispatchLetterResponsePojo.getSignature().getPisCode() != null
                    && dispatchLetterResponsePojo.getRemarksPisCode() != null
                    && dispatchLetterResponsePojo.getSignature().getPisCode().equals(dispatchLetterResponsePojo.getRemarksPisCode())
                    && dispatchLetterResponsePojo.getRemarksSignatureIsActive() != null
                    && dispatchLetterResponsePojo.getRemarksSignatureIsActive()) {
                reviewContent = dispatchLetterResponsePojo.getContent() + " " + dispatchLetterResponsePojo.getRemarks();
                VerificationInformation verification = verifySignatureService.verify(reviewContent, dispatchLetterResponsePojo.getRemarksSignatureData(), isHashed);
                dispatchLetterResponsePojo.setActiveSignatureData(verification);

                //set signature to approver log for already approved letter
                if (dispatchLetterResponsePojo.getIsAlreadyApproved() != null && dispatchLetterResponsePojo.getIsAlreadyApproved())
                    dispatchLetterResponsePojo.setRemarksVerificationInformation(verification);

                if (verification != null) {

                    Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                    //check for alternate result ie. if the letter is verified or not verified record already exists or not
                    Boolean result = signatureVerificationLogRepository.
                            existsDispatchLog(
                                    SignatureType.DISPATCH.toString(),
                                    dispatchLetterResponsePojo.getDispatchId());

                    if (result == null || result != isVerified)
                        signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                                .signatureType(SignatureType.DISPATCH)
                                .isVerified(isVerified)
                                .signatureBy(dispatchLetterResponsePojo.getRemarksUserDetails().getPisCode())
                                .individualStatus(dispatchLetterResponsePojo.getStatus())
                                .build());
                }

            }

//            condition when chalani creator signature is not changed
            if (dispatchLetterReview != null
                    && dispatchLetterResponsePojo.getSignature() != null
                    && dispatchLetterResponsePojo.getSignature().getPisCode() != null
                    && dispatchLetterReview.getSenderPisCode() != null
                    && dispatchLetterReview.getSenderPisCode().equals(dispatchLetterResponsePojo.getSignature().getPisCode())
                    && dispatchLetterReview.getRemarksSignatureIsActive() != null
                    && dispatchLetterReview.getRemarksSignatureIsActive()) {
                reviewContent = dispatchLetterResponsePojo.getContent() + " " + dispatchLetterReview.getRemarks();
                Boolean isHashedReview = dispatchLetterReview.getHashContent() == null ? Boolean.FALSE : Boolean.TRUE;

                VerificationInformation verification = verifySignatureService.verify(reviewContent, dispatchLetterReview.getRemarksSignature(), isHashedReview);
                dispatchLetterResponsePojo.setActiveSignatureData(verification);

                //set signature to approver log for already approved letter
                if (dispatchLetterResponsePojo.getIsAlreadyApproved() != null && dispatchLetterResponsePojo.getIsAlreadyApproved())
                    dispatchLetterResponsePojo.setRemarksVerificationInformation(verification);

                if (verification != null) {

                    Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                    //check for alternate result ie. if the letter is verified or not verified record already exists or not
                    Boolean result = signatureVerificationLogRepository.
                            existsDispatchLog(
                                    SignatureType.DISPATCH.toString(),
                                    dispatchLetterResponsePojo.getDispatchId());

                    if (result == null || result != isVerified)
                        signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                                .signatureType(SignatureType.DISPATCH)
                                .isVerified(isVerified)
                                .signatureBy(dispatchLetterResponsePojo.getSignedPis() == null ? dispatchLetterResponsePojo.getSignature().getPisCode() : dispatchLetterResponsePojo.getSignedPis())
                                .individualStatus(dispatchLetterResponsePojo.getStatus())
                                .build());
                }

            }
        }

        if (dispatchLetterResponsePojo.getIsAlreadyApproved())
            dispatchLetterResponsePojo.setApproval(new ArrayList<>());
        if (dispatchLetterResponsePojo.getApproval() != null && !dispatchLetterResponsePojo.getApproval().isEmpty()) {
//            String finalOfficeHeadPisCode = officeHeadPisCode;
            dispatchLetterResponsePojo.getApproval().forEach(x -> {
                x.setIsDelegated(false);
                if (x.getReceiverPisCode() != null) {
                    EmployeeMinimalPojo receiver = userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode());
                    if (receiver != null) {
                        x.setReceiverNameNp(receiver.getEmployeeNameNp());
                        x.setReceiverName(receiver.getEmployeeNameEn());
                    }

//                    if (x.getIsSeen() != null && !x.getIsSeen()) {
//                        if (tokenProcessorService.getPisCode().equals(x.getReceiverPisCode())) {
//                            notificationService.notificationProducer(
//                                    NotificationPojo.builder()
//                                            .moduleId(dispatchLetterResponsePojo.getDispatchId())
//                                            .module(MODULE_APPROVAL_KEY)
//                                            .sender(tokenProcessorService.getPisCode())
//                                            .receiver(x.getSenderPisCode())
//                                            .subject(customMessageSource.getNepali("manual.received"))
//                                            .detail(customMessageSource.getNepali("chalani.view", receiver != null ? receiver.getEmployeeNameNp() : "", dispatchLetterResponsePojo.getSubject()))
//                                            .pushNotification(true)
//                                            .received(false)
//                                            .build()
//                            );
//                            dispatchLetterReviewRepo.setSeen(dispatchLetterResponsePojo.getDispatchId(), tokenProcessorService.getPisCode());
//                        }
//                    }
                }

                EmployeeMinimalPojo sender = null;
                if (x.getSenderPisCode() != null)
                    sender = userMgmtServiceData.getEmployeeDetailMinimal(x.getSenderPisCode());

                if (x.getDelegatedId() != null) {

//                    if (x.getSenderPisCode().equals(finalOfficeHeadPisCode))
//                        x.setIsDelegated(true);

                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
//                        EmployeeMinimalPojo delegatedSender = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode());

                        //check the delegated user is reassignment and set value accordingly
                        if (delegationResponsePojo.getIsReassignment() != null
                                && delegationResponsePojo.getIsReassignment()) {
                            x.setIsReassignment(Boolean.TRUE);
                            if (delegationResponsePojo.getFromSection() != null)
                                x.setReassignmentSection(delegationResponsePojo.getFromSection());
                        } else {
                            x.setIsDelegated(true);
                        }

                        x.setSenderName(delegationResponsePojo.getToEmployee().getNameEn());
                        x.setSenderNameNp(delegationResponsePojo.getToEmployee().getNameNp());
                        if (sender != null)
                            x.setSenderDesignationNameNp(sender.getFunctionalDesignation() != null ? sender.getFunctionalDesignation().getNameN() : null);
                        x.setSignedPis(delegationResponsePojo.getToEmployee().getCode());
                    }

                } else {
                    if (sender != null) {
                        x.setSenderName(sender.getEmployeeNameEn());
                        x.setSenderNameNp(sender.getEmployeeNameNp());
                        x.setSenderDesignationNameNp(sender.getFunctionalDesignation() != null ? sender.getFunctionalDesignation().getNameN() : null);
                        x.setSignedPis(sender.getPisCode());
                    }
                }

                if (x.getSignatureIsActive() != null && x.getSignatureIsActive()) {
                    String newContent;
                    Boolean isHashedApproval = x.getHash_content() == null ? Boolean.FALSE : Boolean.TRUE;
                    if (x.getContentLogId() == null) {
                        newContent = dispatchLetterResponsePojo.getContent() + " " + x.getRemarks();

                        VerificationInformation verification = verifySignatureService.verify(newContent, x.getSignatureData(), isHashedApproval);
                        x.setSignatureVerification(verification);

                        if (verification != null) {

                            Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                            //check for alternate result ie. if the letter is verified or not verified record already exists or not
                            Boolean result = signatureVerificationLogRepository.
                                    existsDispatchReviewLog(
                                            SignatureType.DISPATCH_ACTIVITY.toString(),
                                            dispatchLetterResponsePojo.getDispatchId(),
                                            x.getApprovalId());

                            if (result == null || result != isVerified)
                                signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                        .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                                        .dispatchReviewId(x.getApprovalId())
                                        .signatureType(SignatureType.DISPATCH_ACTIVITY)
                                        .isVerified(isVerified)
                                        .signatureBy(x.getSignedPis())
                                        .individualStatus(x.getApprovalStatus().toString())
                                        .build());
                        }
                    } else {
                        Optional<DispatchLetterUpdateHistory> logContent = dispatchLetterUpdateHistoryRepo.findById(x.getContentLogId());
                        if (logContent.isPresent()) {
                            newContent = logContent.get().getContent() + " " + x.getRemarks();

                            VerificationInformation verification = verifySignatureService.verify(newContent, x.getSignatureData(), isHashedApproval);
                            x.setSignatureVerification(verification);
                            if (verification != null) {

                                Boolean isVerified = verification.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                                //check for alternate result ie. if the letter is verified or not verified record already exists or not
                                Boolean result = signatureVerificationLogRepository.
                                        existsDispatchReviewLog(
                                                SignatureType.DISPATCH_ACTIVITY.toString(),
                                                dispatchLetterResponsePojo.getDispatchId(),
                                                x.getApprovalId());

                                if (result == null || result != isVerified)
                                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                            .dispatchId(dispatchLetterResponsePojo.getDispatchId())
                                            .dispatchReviewId(x.getApprovalId())
                                            .signatureType(SignatureType.DISPATCH_ACTIVITY)
                                            .isVerified(isVerified)
                                            .signatureBy(x.getSignedPis())
                                            .individualStatus(x.getApprovalStatus().toString())
                                            .build());
                            }
                        }
                    }
                }

            });
        }

        this.getATemplateForQr(dispatchLetterResponsePojo, dispatchLetterResponsePojo.getSingular(), type);

//        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
//
//        if (dispatchLetterResponsePojo.getSenderPisCode().equals(tokenProcessorService.getPisCode())
//                && employeePojo != null
//                && dispatchLetterResponsePojo.getSenderSectionCode() != null
//                && dispatchLetterResponsePojo.getSenderSectionCode().equals(employeePojo.getSectionId()))
//            dispatchLetterResponsePojo.setReferenceIsEditable(true);
//        else
//            dispatchLetterResponsePojo.setReferenceIsEditable(false);

        return dispatchLetterResponsePojo;

    }

    private DispatchLetterDTO getATemplateForQr(DispatchLetterDTO dispatchLetterResponsePojo, boolean isSingular, String type) {

        //get office head pis code
        String officeHeadPisCode = "";

        // logic for internal letter
        if (dispatchLetterResponsePojo.getDispatchLetterInternal() != null && !dispatchLetterResponsePojo.getDispatchLetterInternal().isEmpty()) {
            dispatchLetterResponsePojo.getDispatchLetterInternal().forEach(x -> {
                Boolean isSectionName = x.getIsSectionName();
                if (x.getWithinOrganization() != null ? x.getWithinOrganization() : x.getInternalReceiverPiscode() != null ? Boolean.TRUE : Boolean.FALSE) {
                    EmployeePojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetail(x.getInternalReceiverPiscode());

                    SectionPojo userSection = null;
                    if (x.getInternalReceiverSectionId() != null) {
                        try {
                            userSection = userMgmtServiceData.getSectionDetail(Long.parseLong(x.getInternalReceiverSectionId()));
                        } catch (NumberFormatException e) {

                        }
                    }
                    if (employeeMinimalPojo != null) {
                        x.setEmployeeName(employeeMinimalPojo.getNameEn() != null ? getStringPascalCase(employeeMinimalPojo.getNameEn()) : "");
                        x.setEmployeeNameNp(employeeMinimalPojo.getNameNp() != null ? employeeMinimalPojo.getNameNp() : "");
                        x.setSectionName(userSection != null ? userSection.getNameEn() != null ? getStringPascalCase(userSection.getNameEn()) : "" : "");
                        x.setSectionNameNp(userSection != null ? userSection.getNameNp() != null ? userSection.getNameNp() : "" : "");
                        x.setDesignationName(employeeMinimalPojo.getFunctionalDesignation() != null ? employeeMinimalPojo.getFunctionalDesignation().getName() != null ? getStringPascalCase(employeeMinimalPojo.getFunctionalDesignation().getName()) : "" : "");
                        x.setDesignationNameNp(employeeMinimalPojo.getFunctionalDesignation() != null ? employeeMinimalPojo.getFunctionalDesignation().getNameN() != null ? employeeMinimalPojo.getFunctionalDesignation().getNameN() : "" : "");
                    } else {
                        x.setEmployeeName("");
                        x.setEmployeeNameNp("");
                        x.setSectionName("");
                        x.setSectionNameNp("");
                        x.setDesignationName("");
                        x.setDesignationNameNp("");
                    }
                } else if (x.getWithinOrganization() != null ? !x.getWithinOrganization() : x.getInternalReceiverPiscode() == null ? Boolean.TRUE : Boolean.FALSE) {
                    OfficePojo officePojo = userMgmtServiceData.getOfficeDetail(x.getInternalReceiverOfficeCode());

                    if (officePojo != null) {
                        x.setEmployeeName(officePojo.getNameEn() != null ? getStringPascalCase(officePojo.getNameEn()) : "");
                        x.setEmployeeNameNp(officePojo.getNameNp() != null ? officePojo.getNameNp() : "");
                        x.setDesignationName(officePojo.getAddressEn() != null ? officePojo.getAddressEn() : "");
                        x.setDesignationNameNp(officePojo.getAddressNp() != null ? officePojo.getAddressNp() : "");
                    } else {
                        x.setEmployeeName("");
                        x.setEmployeeNameNp("");
                        x.setDesignationName("");
                        x.setDesignationNameNp("");
                    }

                    if (x.getInternalReceiverSectionId() != null && !x.getInternalReceiverSectionId().isEmpty()) {
                        SectionPojo sectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(x.getInternalReceiverSectionId()));
                        if (sectionPojo != null) {
                            x.setSectionName(sectionPojo.getNameEn() != null ? getStringPascalCase(sectionPojo.getNameEn()) : "");
                            x.setSectionNameNp(sectionPojo.getNameNp() != null ? sectionPojo.getNameNp() : "");
                        } else {
                            x.setSectionName("");
                            x.setSectionNameNp("");
                        }
                    } else {
                        x.setSectionName(x.getInternalReceiverSectionName() != null ? getStringPascalCase(x.getInternalReceiverSectionName()) : "");
                        x.setSectionNameNp(x.getInternalReceiverSectionName() != null ? x.getInternalReceiverSectionName() : "");
                    }

                }
            });
        }

        // logic for external letter
        if (dispatchLetterResponsePojo.getDispatchLetterExternal() != null && !dispatchLetterResponsePojo.getDispatchLetterExternal().isEmpty()) {
            dispatchLetterResponsePojo.getDispatchLetterExternal().forEach(x -> {
                x.setEmployeeName(x.getReceiverName() != null ? x.getReceiverName() : "");
                x.setEmployeeNameNp(x.getReceiverName() != null ? x.getReceiverName() : "");
            });
        }

        // sender detail
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(dispatchLetterResponsePojo.getSenderPisCode());
        String toEmployee = null;
        EmployeePojo requester;
        DesignationPojo requesterDesignation = null;
        boolean isOfficeHead = false;
        DetailPojo requesterSection = null;

        // check if  office head signature
//        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getPisCode() != null) {
//            OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
//            if (officeHeadPojo != null && officeHeadPojo.getPisCode() != null && officeHeadPojo.getPisCode().equals(dispatchLetterResponsePojo.getSignature().getPisCode()))
//                isOfficeHead = true;
//        }

        DetailPojo reassignmentSection = null;
        Boolean isReassignment = Boolean.FALSE;
        Boolean isDelegated = Boolean.FALSE;
        DelegationResponsePojo delegationResponsePojo;
        // check if its delegated
        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getDelegatedId() != null) {
            delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(dispatchLetterResponsePojo.getSignature().getDelegatedId());
            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                toEmployee = delegationResponsePojo.getToEmployee().getCode();

//                if (delegationResponsePojo != null
//                        && delegationResponsePojo.getFromEmployee() != null
//                        && delegationResponsePojo.getFromEmployee().getCode() != null
//                        && delegationResponsePojo.getFromEmployee().getCode().equals(officeHeadPisCode))
//                    isDelegated = Boolean.TRUE;

                // here need to task
                if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment()) {
                    isReassignment = Boolean.TRUE;
                    if (delegationResponsePojo.getFromSection() != null) {
                        reassignmentSection = delegationResponsePojo.getFromSection();
                    }

                } else {
                    isDelegated = Boolean.TRUE;
                }
                if (delegationResponsePojo.getFromSection() != null) {
                    requesterSection = delegationResponsePojo.getFromSection();
                }
            }
        }

        if (toEmployee != null) {
            requester = userMgmtServiceData.getEmployeeDetail(toEmployee);

            if (isOfficeHead && requester != null) {
                requester.setNameNp(requester.getNameNp());
            } else if (requester != null && requester.getFunctionalDesignation() != null) {

                //logic for delegation info
                requesterDesignation = new DesignationPojo();
                requesterDesignation.setNameEn(
                        requester.getFunctionalDesignation().getName() != null ?
                                isDelegated ?
                                        DELEGATED_EN + ", " + requester.getFunctionalDesignation().getName() :
                                        isReassignment ?
                                                ADDITIONAL_RESPONSIBILITY_EN + ", " + reassignmentSection.getNameEn() + ", " + requester.getFunctionalDesignation().getName() :
                                                requester.getFunctionalDesignation().getName() :
                                "");
                requesterDesignation.setNameNp(requester.getFunctionalDesignation().getNameN() != null ?
                        isDelegated ?
                                DELEGATED_NEP + ", " + requester.getFunctionalDesignation().getNameN() :
                                isReassignment ?
                                        ADDITIONAL_RESPONSIBILITY_NEP + ", " + reassignmentSection.getNameNp() + ", " + requester.getFunctionalDesignation().getNameN() :
                                        requester.getFunctionalDesignation().getNameN() :
                        "");
            }
        } else {
            requester = userMgmtServiceData.getEmployeeDetail(dispatchLetterResponsePojo.getSignature() != null ? dispatchLetterResponsePojo.getSignature().getPisCode() : null);
            if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getDesignationCode() != null)
                requesterDesignation = userMgmtServiceData.getDesignationDetail(dispatchLetterResponsePojo.getSignature().getDesignationCode());
            if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getSectionCode() != null) {
                SectionPojo sectionPojoDetail = userMgmtServiceData.getSectionDetail(Long.parseLong(dispatchLetterResponsePojo.getSignature().getSectionCode()));
                if (sectionPojoDetail != null && requesterSection == null) {
                    requesterSection = new DetailPojo();
                    requesterSection.setNameEn(sectionPojoDetail.getNameEn());
                    requesterSection.setNameNp(sectionPojoDetail.getNameNp());
                }
            }
        }

        String footer = "";
//        FooterDataDto footerData = footerDataMapper.getFooterByOfficeCode(tokenProcessorService.getOfficeCode());
//        if (footerData != null)
//            footer = footerData.getFooter();

//        DesignationPojo requesterDesignation = null;
        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getDesignationCode() != null && requesterDesignation == null)
            requesterDesignation = userMgmtServiceData.getDesignationDetail(dispatchLetterResponsePojo.getSignature().getDesignationCode());

        SectionPojo sectionPojo = null;
        if (dispatchLetterResponsePojo.getSignature() != null && dispatchLetterResponsePojo.getSignature().getIncludeSectionId() != null && dispatchLetterResponsePojo.getSignature().getIncludeSectionId() && dispatchLetterResponsePojo.getSignature().getIncludedSectionId() != null)
            sectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(dispatchLetterResponsePojo.getSignature().getIncludedSectionId()));

        String img = "data:image/png;base64,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";

        OfficePojo officeDetail = userMgmtServiceData.getOfficeDetail(dispatchLetterResponsePojo.getSenderOfficeCode());
        OfficePojo parentOffice = userMgmtServiceData.getOfficeDetail(officeDetail != null ? officeDetail.getParentCode() : null);

        if (parentOffice != null && parentOffice.getCode().equals("8886"))
            parentOffice = null;

        boolean isEnglish = dispatchLetterResponsePojo.getIsEnglish() != null ? dispatchLetterResponsePojo.getIsEnglish() : false;
        boolean isAd = dispatchLetterResponsePojo.getIsAd() != null ? dispatchLetterResponsePojo.getIsAd() : false;
        boolean includeSection = dispatchLetterResponsePojo.getIncludeSection() != null ? dispatchLetterResponsePojo.getIncludeSection() : true;
        String lang = "NEP";
        String header = null;
        boolean useDynamicHeader = false;
        OfficeTemplatePojo dynamicTemplateHeader = null;

        if (isEnglish) {
            lang = "EN";
        }

        // get template as per saved if its added
        if (dispatchLetterResponsePojo.getTemplateHeaderId() != null) {
//            todo add validation
            dynamicTemplateHeader = userMgmtServiceData.getOfficeTemplateById(dispatchLetterResponsePojo.getTemplateHeaderId());
        } else {
            // get active template
            if (Boolean.TRUE.equals(dispatchLetterResponsePojo.getIsDraft())) {
                dynamicTemplateHeader = userMgmtServiceData.getOfficeTemplate(userMgmtServiceData.getEmployeeDetailMinimal(dispatchLetterResponsePojo.getSenderPisCode()).getOfficeCode(), "H");
            }
        }


        // remove this and add dynamic header as compulsory
        if (dynamicTemplateHeader != null) {
            useDynamicHeader = true;
            if (isEnglish && dynamicTemplateHeader.getTemplateEn() != null &&
                    !dynamicTemplateHeader.getTemplateEn().isEmpty())
                header = dynamicTemplateHeader.getTemplateEn();
            else if (!isEnglish && dynamicTemplateHeader.getTemplateNp() != null
                    && !dynamicTemplateHeader.getTemplateNp().isEmpty())
                header = dynamicTemplateHeader.getTemplateNp();
        }

        OfficeTemplatePojo dynamicTemplateFooter;
        if (dispatchLetterResponsePojo.getTemplateFooterId() != null) {
            dynamicTemplateFooter = userMgmtServiceData.getOfficeTemplateById(dispatchLetterResponsePojo.getTemplateFooterId());
        } else {
            dynamicTemplateFooter = userMgmtServiceData.getOfficeTemplate(userMgmtServiceData.getEmployeeDetailMinimal(dispatchLetterResponsePojo.getSenderPisCode()).getOfficeCode(), "F");
        }
        if (dynamicTemplateFooter != null) {
            if (isEnglish && dynamicTemplateFooter.getTemplateEn() != null &&
                    !dynamicTemplateFooter.getTemplateEn().isEmpty())
                footer = dynamicTemplateFooter.getTemplateEn();
            else if (!isEnglish && dynamicTemplateFooter.getTemplateNp() != null
                    && !dynamicTemplateFooter.getTemplateNp().isEmpty())
                footer = dynamicTemplateFooter.getTemplateNp();
        }

        List<Bodartha> bodartha = new ArrayList<>();
        List<Bodartha> karyartha = new ArrayList<>();
        List<Bodartha> sadarBodartha = new ArrayList<>();
        List<RequestTo> requestTos = new ArrayList<>();
        List<String> templates = new ArrayList<>();
        final RequestTo[] req = {null};
        final String shree = "श्री ";

        AtomicReference<Boolean> isCC = new AtomicReference<>();
        AtomicReference<Boolean> isExternalAndReceiver = new AtomicReference<>();
        isCC.set(Boolean.FALSE);
        isExternalAndReceiver.set(Boolean.FALSE);
        if (dispatchLetterResponsePojo.getDispatchLetterInternal() != null && !dispatchLetterResponsePojo.getDispatchLetterInternal().isEmpty()) {
            dispatchLetterResponsePojo.getDispatchLetterInternal().forEach(x -> {

                Boolean isGroupName = x.getIsGroupName() != null ? x.getIsGroupName() : Boolean.FALSE;
                String groupNameEn = "";
                String groupNameNp = "";
                if (isGroupName && x.getGroupId() != null) {
                    OfficeGroupPojo officeGroupPojo = userMgmtServiceData.getOfficeGroupById(x.getGroupId());
                    groupNameEn = officeGroupPojo.getNameEn();
                    groupNameNp = officeGroupPojo.getNameNp();
                }
                boolean isSaluted = false;
                String designationEn = "";
                String designationNp = "";

                if (x.getSalutation() != null)
                    isSaluted = true;

                Boolean isSectionName = x.getIsSectionName() != null ? x.getIsSectionName() : Boolean.FALSE;
                if (x.getInternalReceiver() != null && x.getInternalReceiver()) {

                    if (x.getDesignationName() != null)
                        designationEn = ", " + getStringPascalCase(x.getDesignationName());
                    if (x.getDesignationNameNp() != null)
                        designationNp = ", " + x.getDesignationNameNp();

//                    EmployeePojo receiverDetail = userMgmtServiceData.getEmployeeDetail(x.getInternalReceiverPiscode());

                    SectionPojo receiverSectionPojo = null;
                    if (x.getInternalReceiverSectionId() != null) {
                        receiverSectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(x.getInternalReceiverSectionId()));
                    }

                    RequestTo requestTo = new RequestTo().builder()
                            .office(isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() + designationEn : isGroupName ? groupNameNp : isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() + designationNp)
                            .sectionName(isSectionName ? receiverSectionPojo != null ? isEnglish ? receiverSectionPojo.getNameEn() : "श्री " + receiverSectionPojo.getNameNp() : null : null)
                            .isSectionName(isSectionName)
                            .isGroupName(isGroupName)
                            .groupId(x.getGroupId())
                            .order(x.getOrder()).build();


                    if (isSaluted || isGroupName || isSectionName) {
                        requestTo.setAddress("");
                    } else {
                        requestTo.setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                    }
                    requestTos.add(requestTo);

                    if (req[0] == null)
                        req[0] = requestTo;

                    if (x.getInternalReceiverPiscode() != null && x.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())) {
                        req[0] = new RequestTo().builder()
                                .office(isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() + designationEn : isGroupName ? groupNameNp : isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() + designationNp)
                                .sectionName(isSectionName ? receiverSectionPojo != null ? isEnglish ? receiverSectionPojo.getNameEn() : "श्री " + receiverSectionPojo.getNameNp() : null : null)
                                .isSectionName(isSectionName)
                                .isGroupName(isGroupName)
                                .build();

                        if (isSaluted || isGroupName) {
                            req[0].setAddress("");
                        } else {
                            req[0].setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                        }
                    }

                } else if (x.getInternalReceiverCc() != null && x.getInternalReceiverCc() && (x.getBodarthaType() == null || x.getBodarthaType().equals(BodarthaEnum.B))) {

                    Bodartha bIn = new Bodartha().builder()
                            .office(isSectionName ? isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? shree + " " + x.getSectionNameNp() : "" : isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() != null ? x.getEmployeeName() : "" : isGroupName ? groupNameNp : x.getEmployeeNameNp() != null ? isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() : "")
                            .remarks(x.getRemarks())
                            .order(x.getOrder()).build();

                    if (isSaluted || isGroupName || isSectionName) {
                        bIn.setSection("");
                        bIn.setAddress("");
                    } else {
                        bIn.setSection(isEnglish ? x.getDesignationName() != null ? x.getDesignationName() : "" : x.getDesignationNameNp() != null ? x.getDesignationNameNp() : "");
                        bIn.setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                    }
                    bodartha.add(bIn);

                } else if (x.getInternalReceiverCc() != null && x.getInternalReceiverCc() && x.getBodarthaType().equals(BodarthaEnum.K)) {

                    Bodartha kar = new Bodartha().builder()
                            .office(isSectionName ? isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? shree + " " + x.getSectionNameNp() : "" : isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() != null ? x.getEmployeeName() : "" : isGroupName ? groupNameNp : x.getEmployeeNameNp() != null ? isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() : "")
                            .remarks(x.getRemarks())
                            .order(x.getOrder()).build();

                    if (isSaluted || isGroupName || isSectionName) {
                        kar.setSection("");
                        kar.setAddress("");
                    } else {
                        kar.setSection(isEnglish ? x.getDesignationName() != null ? x.getDesignationName() : "" : x.getDesignationNameNp() != null ? x.getDesignationNameNp() : "");
                        kar.setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                    }
                    karyartha.add(kar);

                } else if (x.getInternalReceiverCc() != null && x.getInternalReceiverCc() && x.getBodarthaType().equals(BodarthaEnum.S)) {

                    Bodartha sad = new Bodartha().builder()
                            .office(isSectionName ? isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? shree + " " + x.getSectionNameNp() : "" : isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() != null ? x.getEmployeeName() : "" : isGroupName ? groupNameNp : x.getEmployeeNameNp() != null ? isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() : "")
                            .remarks(x.getRemarks())
                            .order(x.getOrder()).build();

                    if (isSaluted || isGroupName || isSectionName) {
                        sad.setSection("");
                        sad.setAddress("");
                    } else {
                        sad.setSection(isEnglish ? x.getDesignationName() != null ? x.getDesignationName() : "" : x.getDesignationNameNp() != null ? x.getDesignationNameNp() : "");
                        sad.setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                    }
                    sadarBodartha.add(sad);
                }

                if (x.getInternalReceiverCc() != null && x.getInternalReceiverCc() && x.getInternalReceiverPiscode() != null && x.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())) {
                    isCC.set(Boolean.TRUE);
                }

                if (x.getInternalReceiverPiscode() != null && x.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode()) && req[0] == null) {
                    isExternalAndReceiver.set(Boolean.TRUE);
                }
            });
        }

        if (dispatchLetterResponsePojo.getDispatchLetterExternal() != null && !dispatchLetterResponsePojo.getDispatchLetterExternal().isEmpty()) {
            dispatchLetterResponsePojo.getDispatchLetterExternal().forEach(x -> {

                String externalSection = "";
                if (x.getReceiverOfficeSectionSubSection() != null)
                    externalSection = " ," + x.getReceiverOfficeSectionSubSection();

                if (x.getIsCc() != null && !x.getIsCc()) {

                    RequestTo to = new RequestTo().builder()
                            .office(x.getReceiverName() != null ? x.getReceiverName() + externalSection : "")
                            .isSectionName(Boolean.FALSE)
                            .isGroupName(Boolean.FALSE)
                            .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                            .order(x.getOrder()).build();

                    requestTos.add(to);

                } else if (x.getIsCc() != null && x.getIsCc() && (x.getBodarthaType() == null || x.getBodarthaType().equals(BodarthaEnum.B))) {

                    Bodartha bd = new Bodartha().builder()
                            .office(x.getReceiverName() != null ? x.getReceiverName() : "")
                            .remarks(x.getRemarks())
                            .isExternal(true)
                            .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                            .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                            .order(x.getOrder()).build();

                    bodartha.add(bd);

                } else if (x.getIsCc() != null && x.getIsCc() && x.getBodarthaType().equals(BodarthaEnum.K)) {

                    Bodartha karEx = new Bodartha().builder()
                            .office(x.getReceiverName() != null ? x.getReceiverName() : "")
                            .remarks(x.getRemarks())
                            .isExternal(true)
                            .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                            .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                            .order(x.getOrder()).build();

                    karyartha.add(karEx);

                } else if (x.getIsCc() != null && x.getIsCc() && x.getBodarthaType().equals(BodarthaEnum.S)) {

                    Bodartha sadEx = new Bodartha().builder()
                            .office(x.getReceiverName() != null ? x.getReceiverName() : "")
                            .remarks(x.getRemarks())
                            .isExternal(true)
                            .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                            .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                            .order(x.getOrder()).build();

                    sadarBodartha.add(sadEx);

                }
            });
        }

        String chalaniDate = isEnglish ? isAd ? dispatchLetterResponsePojo.getDispatchDate() != null ? dispatchLetterResponsePojo.getDispatchDate().toString()
                : ""
                : dispatchLetterResponsePojo.getDispatchDateNp() != null ? dispatchLetterResponsePojo.getDispatchDateNp()
                : ""
                : isAd ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDate().toString())
                : ""
                : dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp())
                : "";

        String chalaniNo = dispatchLetterResponsePojo.getDispatchNo();

        if (isEnglish && dispatchLetterResponsePojo.getReferenceCode() != null) {
            chalaniNo = dispatchLetterResponsePojo.getDispatchNo() + "-" + dispatchLetterResponsePojo.getReferenceCode().substring(1);
        }

        requestTos.sort(Comparator.comparing(RequestTo::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        bodartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        karyartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        sadarBodartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

        Boolean includeSectionInLetter = dispatchLetterResponsePojo.getSignature() != null ?
                dispatchLetterResponsePojo.getSignature().getIncludeSectionInLetter() != null ?
                        dispatchLetterResponsePojo.getSignature().getIncludeSectionInLetter()
                        : Boolean.FALSE : Boolean.FALSE;

        VerificationInformation verificationInformation = dispatchLetterResponsePojo.getActiveSignatureData();
        if (type != null && type.equals("receiver") && req[0] != null && !isCC.get()) {
            GeneralTemplate internalTemplate = new GeneralTemplate().builder()
                    .header(useDynamicHeader ? header : null)
                    .logo_url(img)
                    .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                    .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                    .department(isEnglish ? parentOffice != null ? getStringPascalCase(parentOffice.getNameEn()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                    .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                    .letter_date(chalaniDate)
                    .section_header(includeSection ? sectionPojo != null ? isEnglish ? getStringPascalCase(sectionPojo.getNameEn()) : sectionPojo.getNameNp() : null : null)
                    .chali_no(chalaniNo)
                    .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                    .request_to_office(req[0].getOffice() != null ? req[0].getOffice() : "")
                    .request_to_office_address(req[0].getAddress() != null ? req[0].getAddress() : "")
                    .body_message(dispatchLetterResponsePojo.getContent())
                    .subject(dispatchLetterResponsePojo.getSubject())
                    .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? employeePojo.getNameEn() : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                    .requester_position(isEnglish ? requesterDesignation != null ? getStringPascalCase(requesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : requesterDesignation != null ? requesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                    .bodartha(bodartha)
                    .bodartha_karyartha(karyartha)
                    .saadar_awagataartha(sadarBodartha)
                    .resource_type("C")
                    .resource_id(dispatchLetterResponsePojo.getDispatchId())
                    .footer(footer)
                    .signatureDetail(dispatchLetterResponsePojo.getSignature())
                    .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                    .dispatchDateEn(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertAdToBs(dispatchLetterResponsePojo.getDispatchDate().toString()) : null : null)
                    .dispatchDateNp(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp()) : null : null)
                    .status(dispatchLetterResponsePojo.getStatus())
                    .verificationInformation(verificationInformation)
                    .hasSubject(dispatchLetterResponsePojo.getHasSubject())
                    .sectionName(req[0].getSectionName())
                    .isSectionName(req[0].getIsSectionName() != null ? req[0].getIsSectionName() : Boolean.FALSE)
                    .isGroupName(req[0].getIsGroupName() != null ? req[0].getIsGroupName() : Boolean.FALSE)
                    .sectionLetter(includeSectionInLetter && requesterSection != null ? isEnglish ? requesterSection.getNameEn() : requesterSection.getNameNp() : null)
                    .build();

            System.out.println("gson1: " + new Gson().toJson(internalTemplate));
            // template generate
            internalTemplate.setDispatchId(dispatchLetterResponsePojo.getDispatchId().toString());
            internalTemplate.setIsQrApp(true);
            internalTemplate.setMobileNumber(dispatchLetterResponsePojo.getMobileNumber());
            templates.add(letterTemplateProxy.getGeneralTemplateForQR(internalTemplate, lang));
        } else if (type == null || (type != null && !type.equals("receiver")) || isCC.get() || isExternalAndReceiver.get()) {
            if (!isSingular) {
                String finalChalaniNo = chalaniNo;
                OfficePojo finalParentOffice = parentOffice;
                String finalLang = lang;
                DesignationPojo finalRequesterDesignation = requesterDesignation;
                SectionPojo finalSectionPojo = sectionPojo;
                String finalFooter = footer;
                String finalHeader = header;
                boolean finalUseDynamicHeader = useDynamicHeader;
                DetailPojo finalSection = requesterSection;

                List<RequestTo> requestToListWithUniqueSectionName = removeSameSectionName(requestTos);

                requestToListWithUniqueSectionName.forEach(x -> {
                    GeneralTemplate internalTemplate = new GeneralTemplate().builder()
                            .header(finalUseDynamicHeader ? finalHeader : null)
                            .logo_url(img)
                            .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                            .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                            .department(isEnglish ? finalParentOffice != null ? getStringPascalCase(finalParentOffice.getNameEn()) : "" : finalParentOffice != null ? finalParentOffice.getNameNp() : "")
                            .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                            .letter_date(chalaniDate)
                            .section_header(includeSection ? finalSectionPojo != null ? isEnglish ? getStringPascalCase(finalSectionPojo.getNameEn()) : finalSectionPojo.getNameNp() : null : null)
                            .chali_no(finalChalaniNo)
                            .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                            .request_to_office(x.getOffice())
                            .request_to_office_address(x.getAddress())
                            .body_message(dispatchLetterResponsePojo.getContent())
                            .subject(dispatchLetterResponsePojo.getSubject())
                            .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getNameEn()) : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                            .requester_position(isEnglish ? finalRequesterDesignation != null ? getStringPascalCase(finalRequesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : finalRequesterDesignation != null ? finalRequesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                            .bodartha(bodartha)
                            .bodartha_karyartha(karyartha)
                            .saadar_awagataartha(sadarBodartha)
                            .resource_type("C")
                            .resource_id(dispatchLetterResponsePojo.getDispatchId())
                            .footer(finalFooter)
                            .signatureDetail(dispatchLetterResponsePojo.getSignature())
                            .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                            .dispatchDateEn(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertAdToBs(dispatchLetterResponsePojo.getDispatchDate().toString()) : null : null)
                            .dispatchDateNp(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp()) : null : null)
                            .status(dispatchLetterResponsePojo.getStatus())
                            .verificationInformation(verificationInformation)
                            .hasSubject(dispatchLetterResponsePojo.getHasSubject())
                            .sectionName(x.getSectionName())
                            .isSectionName(x.getIsSectionName() != null ? x.getIsSectionName() : Boolean.FALSE)
                            .isGroupName(x.getIsGroupName() != null ? x.getIsGroupName() : Boolean.FALSE)
                            .sectionLetter(includeSectionInLetter && finalSection != null ? isEnglish ? finalSection.getNameEn() : finalSection.getNameNp() : null)
                            .build();

                    System.out.println("gson2: " + new Gson().toJson(internalTemplate));
                    internalTemplate.setDispatchId(dispatchLetterResponsePojo.getDispatchId().toString());
                    internalTemplate.setIsQrApp(true);
                    internalTemplate.setMobileNumber(dispatchLetterResponsePojo.getMobileNumber());
                    templates.add(letterTemplateProxy.getGeneralTemplateForQR(internalTemplate, finalLang));
                });
            } else {
                GeneralTemplate internalTemplate = new GeneralTemplate().builder()
                        .header(useDynamicHeader ? header : null)
                        .logo_url(img)
                        .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                        .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                        .department(isEnglish ? parentOffice != null ? getStringPascalCase(parentOffice.getNameEn()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                        .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                        .letter_date(chalaniDate)
                        .section_header(includeSection ? sectionPojo != null ? isEnglish ? getStringPascalCase(sectionPojo.getNameEn()) : sectionPojo.getNameNp() : null : null)
                        .chali_no(chalaniNo)
                        .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                        .request_to_many(removeSameSectionName(requestTos))
                        .body_message(dispatchLetterResponsePojo.getContent())
                        .subject(dispatchLetterResponsePojo.getSubject())
                        .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getNameEn()) : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                        .requester_position(isEnglish ? requesterDesignation != null ? getStringPascalCase(requesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : requesterDesignation != null ? requesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                        .bodartha(bodartha)
                        .bodartha_karyartha(karyartha)
                        .saadar_awagataartha(sadarBodartha)
                        .resource_type("C")
                        .resource_id(dispatchLetterResponsePojo.getDispatchId())
                        .footer(footer)
                        .signatureDetail(dispatchLetterResponsePojo.getSignature())
                        .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                        .dispatchDateEn(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertAdToBs(dispatchLetterResponsePojo.getDispatchDate().toString()) : null : null)
                        .dispatchDateNp(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp()) : null : null)
                        .status(dispatchLetterResponsePojo.getStatus())
                        .verificationInformation(verificationInformation)
                        .hasSubject(dispatchLetterResponsePojo.getHasSubject())
                        .sectionLetter(includeSectionInLetter && requesterSection != null ? isEnglish ? requesterSection.getNameEn() : requesterSection.getNameNp() : null)
                        .build();

                System.out.println("gson3: " + new Gson().toJson(internalTemplate));
                dispatchLetterResponsePojo.setIsTableFormat(useDynamicHeader ? Boolean.TRUE : isEnglish ? Boolean.TRUE : Boolean.FALSE);
                internalTemplate.setDispatchId(dispatchLetterResponsePojo.getDispatchId().toString());
                internalTemplate.setIsQrApp(true);
                internalTemplate.setMobileNumber(dispatchLetterResponsePojo.getMobileNumber());
                //todo: new for this as well
                templates.add(letterTemplateProxy.getGeneralMultipleTemplateForQr(internalTemplate, lang));
            }
        } /*else if (type != null && type.equals("receiver") && req[0] == null) {
            GeneralTemplate internalTemplate = new GeneralTemplate().builder()
                    .header(useDynamicHeader ? header : null)
                    .logo_url(img)
                    .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                    .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                    .department(isEnglish ? parentOffice != null ? getStringPascalCase(parentOffice.getNameEn()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                    .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                    .letter_date(chalaniDate)
                    .section_header(includeSection ? sectionPojo != null ? isEnglish ? getStringPascalCase(sectionPojo.getNameEn()) : sectionPojo.getNameNp() : null : null)
                    .chali_no(chalaniNo)
                    .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                    .request_to_many(requestTos)
                    .body_message(dispatchLetterResponsePojo.getContent())
                    .subject(dispatchLetterResponsePojo.getSubject())
                    .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? employeePojo.getNameEn() : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                    .requester_position(isEnglish ? requesterDesignation != null ? getStringPascalCase(requesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : requesterDesignation != null ? requesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                    .bodartha(bodartha)
                    .bodartha_karyartha(karyartha)
                    .saadar_awagataartha(sadarBodartha)
                    .resource_type("C")
                    .resource_id(dispatchLetterResponsePojo.getDispatchId())
                    .footer(footer)
                    .signatureDetail(dispatchLetterResponsePojo.getSignature())
                    .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                    .dispatchDateEn(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDate() != null ? dateConverter.convertAdToBs(dispatchLetterResponsePojo.getDispatchDate().toString()) : null : null)
                    .dispatchDateNp(dispatchLetterResponsePojo.getStatus().equals("A") ? dispatchLetterResponsePojo.getDispatchDateNp() != null ? dateConverter.convertBSToDevnagari(dispatchLetterResponsePojo.getDispatchDateNp()) : null : null)
                    .status(dispatchLetterResponsePojo.getStatus())
                    .build();

            dispatchLetterResponsePojo.setIsTableFormat(useDynamicHeader ? Boolean.TRUE : isEnglish ? Boolean.TRUE : Boolean.FALSE);
            templates.add(letterTemplateProxy.getGeneralMultipleTemplate(internalTemplate, lang));
        }*/

        dispatchLetterResponsePojo.setTemplates(templates);

        if (dispatchLetterResponsePojo.getStatus() != null && dispatchLetterResponsePojo.getStatus().equals("A")) {
            List<Ocr> ocrList = new ArrayList<>();
            DesignationPojo designationPojo = null;
            if (dispatchLetterResponsePojo.getApproval() != null && !dispatchLetterResponsePojo.getApproval().isEmpty()) {
                List<DispatchLetterApprovalPojo> newApprovalList = new ArrayList<>(dispatchLetterResponsePojo.getApproval());
                newApprovalList.sort(Comparator.comparing(DispatchLetterApprovalPojo::getApprovalId, Comparator.nullsLast(Comparator.naturalOrder())));
                for (DispatchLetterApprovalPojo approvalPojo : newApprovalList) {
                    DetailPojo senderSection = null;
                    if (approvalPojo.getSenderPisCode() != null) {

                        if (approvalPojo.getDelegatedId() != null) {
                            DelegationResponsePojo delegationResponsePojoSignature = userMgmtServiceData.getDelegationDetailsById(dispatchLetterResponsePojo.getSignature().getDelegatedId());
                            if (delegationResponsePojoSignature != null && delegationResponsePojoSignature.getToEmployee() != null) {

//                                if (delegationResponsePojoSignature != null
//                                        && delegationResponsePojoSignature.getFromEmployee() != null
//                                        && delegationResponsePojoSignature.getFromEmployee().getCode() != null
//                                        && delegationResponsePojoSignature.getFromEmployee().getCode().equals(officeHeadPisCode))
//                                    approvalPojo.setIsDelegated(Boolean.TRUE);

                                // here need to task
                                if (delegationResponsePojoSignature.getIsReassignment() != null && delegationResponsePojoSignature.getIsReassignment()) {
                                    approvalPojo.setIsReassignment(Boolean.TRUE);
                                    if (delegationResponsePojoSignature.getFromSection() != null)
                                        approvalPojo.setReassignmentSection(delegationResponsePojoSignature.getFromSection());
                                } else {
                                    approvalPojo.setIsDelegated(Boolean.TRUE);
                                }
                                if (delegationResponsePojoSignature.getFromSection() != null)
                                    senderSection = delegationResponsePojoSignature.getFromSection();
                            }
                        } else {
                            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(approvalPojo.getSenderPisCode());
                            if (employeeMinimalPojo != null) {
                                approvalPojo.setSenderName(getStringPascalCase(employeeMinimalPojo.getEmployeeNameEn()));
                                approvalPojo.setSenderNameNp(employeeMinimalPojo.getEmployeeNameNp());
                            }

                            if (approvalPojo.getSenderSectionCode() != null) {
                                SectionPojo sectionPojoDetail = userMgmtServiceData.getSectionDetail(Long.parseLong(approvalPojo.getSenderSectionCode()));
                                if (sectionPojoDetail != null && senderSection == null) {
                                    senderSection = new DetailPojo();
                                    senderSection.setNameEn(sectionPojoDetail.getNameEn());
                                    senderSection.setNameNp(sectionPojoDetail.getNameNp());
                                }
                            }
                        }
                    }
                    if (approvalPojo.getSenderDesignationCode() != null) {
                        designationPojo = userMgmtServiceData.getDesignationDetail(approvalPojo.getSenderDesignationCode());
                    }
                    Ocr ocr = new Ocr().builder()
                            .review(approvalPojo.getRemarks())
                            .requester_name(isEnglish ? getStringPascalCase(approvalPojo.getSenderName()) : approvalPojo.getSenderNameNp())
                            .requester_position(designationPojo != null ? isEnglish ? getStringPascalCase(designationPojo.getNameEn()) : designationPojo.getNameNp() : "")
                            .verificationInformation(approvalPojo.getSignatureVerification())
                            .sectionLetter(includeSectionInLetter && senderSection != null ? isEnglish ? senderSection.getNameEn() : senderSection.getNameNp() : null)
                            .build();
                    ocrList.add(ocr);

                    if (approvalPojo.getReverted() != null && approvalPojo.getReverted())
                        ocrList.clear();
                }
            }

            if (dispatchLetterResponsePojo.getRemarks() != null && dispatchLetterResponsePojo.getRemarksPisCode() != null && dispatchLetterResponsePojo.getRemarksDesignationCode() != null) {
                //EmployeeMinimalPojo remarksUser = userMgmtServiceData.getEmployeeDetailMinimal(dispatchLetterResponsePojo.getRemarksPisCode());
                //DesignationPojo remarksDesignation = userMgmtServiceData.getDesignationDetail(dispatchLetterResponsePojo.getRemarksDesignationCode());

                Ocr remarksOcr = new Ocr();
                remarksOcr.setReview(dispatchLetterResponsePojo.getRemarks());

                if (dispatchLetterResponsePojo.getRemarksUserDetails() != null)
                    remarksOcr.setRequester_name(isEnglish ?
                            dispatchLetterResponsePojo.getIsRemarksUserDelegated() ?
                                    DELEGATED_EN + ", " + getStringPascalCase(dispatchLetterResponsePojo.getRemarksUserDetails().getEmployeeNameEn())
                                    : getStringPascalCase(dispatchLetterResponsePojo.getRemarksUserDetails().getEmployeeNameEn())
                            : dispatchLetterResponsePojo.getIsRemarksUserDelegated() ?
                            DELEGATED_NEP + ", " + dispatchLetterResponsePojo.getRemarksUserDetails().getEmployeeNameNp()
                            : dispatchLetterResponsePojo.getRemarksUserDetails().getEmployeeNameNp());

                remarksOcr.setRequester_position(isEnglish ? getStringPascalCase(dispatchLetterResponsePojo.getRemarksUserDesignationNameEn()) : dispatchLetterResponsePojo.getRemarksUserDesignationNameNp());

                remarksOcr.setVerificationInformation(dispatchLetterResponsePojo.getRemarksVerificationInformation());

                DetailPojo detailSection = null;
                if (dispatchLetterResponsePojo.getRemarksSectionCode() != null) {
                    SectionPojo sectionPojoDetail = userMgmtServiceData.getSectionDetail(Long.parseLong(dispatchLetterResponsePojo.getRemarksSectionCode()));
                    if (sectionPojoDetail != null) {
                        detailSection = new DetailPojo();
                        detailSection.setNameEn(sectionPojoDetail.getNameEn());
                        detailSection.setNameNp(sectionPojoDetail.getNameNp());
                    }
                }

                remarksOcr.setSectionLetter(includeSectionInLetter && detailSection != null ? isEnglish ? detailSection.getNameEn() : detailSection.getNameNp() : null);

                ocrList.add(remarksOcr);
            }

            GeneralTemplate ocTemplate = new GeneralTemplate().builder()
                    .header(useDynamicHeader ? header : null)
                    .logo_url(img)
                    .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                    .ministry(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getNameEn()) : "" : officeDetail != null ? officeDetail.getNameNp() : "")
                    .department(isEnglish ? parentOffice != null ? getStringPascalCase(parentOffice.getNameEn()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                    .address_top(isEnglish ? officeDetail != null ? getStringPascalCase(officeDetail.getAddressEn()) : "" : officeDetail != null ? officeDetail.getAddressNp() : "")
                    .letter_date(chalaniDate)
                    .section_header(includeSection ? sectionPojo != null ? isEnglish ? getStringPascalCase(sectionPojo.getNameEn()) : sectionPojo.getNameNp() : null : null)
                    .chali_no(chalaniNo)
                    .letter_no(!isEnglish ? dispatchLetterResponsePojo.getReferenceCode() != null ? dispatchLetterResponsePojo.getReferenceCode().substring(1) : "" : "")
                    .request_to_many(requestTos)
                    .ocr(ocrList)
                    .body_message(dispatchLetterResponsePojo.getContent())
                    .subject(dispatchLetterResponsePojo.getSubject())
                    .requester_name(isEnglish ? requester != null ? getStringPascalCase(requester.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getNameEn()) : "" : requester != null ? requester.getNameNp() : employeePojo != null ? employeePojo.getNameNp() : "")
                    .requester_position(isEnglish ? requesterDesignation != null ? getStringPascalCase(requesterDesignation.getNameEn()) : employeePojo != null ? getStringPascalCase(employeePojo.getFunctionalDesignation().getName()) : "" : requesterDesignation != null ? requesterDesignation.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation().getNameN() : "")
                    .bodartha(bodartha)
                    .bodartha_karyartha(karyartha)
                    .saadar_awagataartha(sadarBodartha)
                    .resource_type("C")
                    .resource_id(dispatchLetterResponsePojo.getDispatchId())
                    .footer(footer)
                    .senderOfficeCode(dispatchLetterResponsePojo.getSenderOfficeCode())
                    .verificationInformation(dispatchLetterResponsePojo.getActiveSignatureData())
                    .hasSubject(dispatchLetterResponsePojo.getHasSubject())
                    .sectionLetter(includeSectionInLetter ? isEnglish ? requesterSection.getNameEn() : requesterSection.getNameNp() : null)
                    .build();

            System.out.println("Oc: " + new Gson().toJson(ocTemplate));
            //todo: diff for qr
            ocTemplate.setDispatchId(dispatchLetterResponsePojo.getDispatchId().toString());
            ocTemplate.setMobileNumber(dispatchLetterResponsePojo.getMobileNumber());
            ocTemplate.setIsQrApp(true);

            dispatchLetterResponsePojo.setOcTemplate(letterTemplateProxy.getOcTemplate(ocTemplate, lang));
        }

        return dispatchLetterResponsePojo;
    }
}
