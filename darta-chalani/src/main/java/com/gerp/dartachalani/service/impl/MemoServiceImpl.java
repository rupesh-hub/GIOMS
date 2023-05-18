package com.gerp.dartachalani.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.Proxy.ConvertHtlToFileProxy;
import com.gerp.dartachalani.Proxy.DocumentServiceData;
import com.gerp.dartachalani.Proxy.LetterTemplateProxy;
import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.converter.DtoConverter;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.document.*;
import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.dartachalani.dto.systemFiles.SystemFilesDto;
import com.gerp.dartachalani.dto.template.SenderEmployeeDetail;
import com.gerp.dartachalani.dto.template.TippaniContent;
import com.gerp.dartachalani.dto.template.TippaniDetail;
import com.gerp.dartachalani.mapper.*;
import com.gerp.dartachalani.model.draft.share.DraftShare;
import com.gerp.dartachalani.model.draft.share.DraftShareLog;
import com.gerp.dartachalani.model.external.ExternalRecords;
import com.gerp.dartachalani.model.memo.*;
import com.gerp.dartachalani.model.signature.SignatureType;
import com.gerp.dartachalani.model.signature.SignatureVerificationLog;
import com.gerp.dartachalani.repo.*;
import com.gerp.dartachalani.repo.signature.SignatureVerificationLogRepository;
import com.gerp.dartachalani.service.DispatchLetterService;
import com.gerp.dartachalani.service.InitialService;
import com.gerp.dartachalani.service.MemoService;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.dartachalani.service.digitalSignature.VerifySignatureService;
import com.gerp.dartachalani.service.rabbitmq.RabbitMQService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.dartachalani.utils.BASE64DecodedMultipartFile;
import com.gerp.dartachalani.utils.DartaChalaniConstants;
import com.gerp.dartachalani.utils.DocumentUtil;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Status;
import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.ApprovalPojo;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Service
@Transactional
public class MemoServiceImpl extends GenericServiceImpl<Memo, Long> implements MemoService {

    private final MemoRepo memoRepo;
    private final DispatchLetterRepo dispatchLetterRepo;
    private final ReceivedLetterRepo receivedLetterRepo;
    private final MemoForwardRepo memoForwardRepo;
    private final MemoApprovalRepo memoApprovalRepo;
    private final MemoCommentRepo memoCommentRepo;
    private final MemoContentRepo memoContentRepo;
    private final MemoReferenceRepo memoReferenceRepo;
    private final MemoDocumentDetailsRepo memoDocumentDetailsRepo;
    private final ExternalRecordsRepo externalRecordsRepo;
    private final SignatureVerificationLogRepository signatureVerificationLogRepository;
    private final MemoMapper memoMapper;
    private final MemoApprovalMapper memoApprovalMapper;
    private final DispatchLetterMapper dispatchLetterMapper;
    private final ReceivedLetterMapper receivedLetterMapper;
    private final CustomMessageSource customMessageSource;
    private final MemoSuggestionRepo memoSuggestionRepo;
    private final DraftShareRepo draftShareRepo;
    private final DraftShareLogRepo draftShareLogRepo;
    private final MemoSuggestionMapper memoSuggestionMapper;
    private final MemoReferenceMapper memoReferenceMapper;
    private final ConvertHtlToFileProxy convertHtlToFileProxy;
    private final InitialService initialService;
    private final MemoForwardHistoryRepo memoForwardHistoryRepo;
    private final DispatchLetterServiceImpl dispatchLetterServiceImpl;
    private final String MODULE_KEY = PermissionConstants.MEMO_MODULE_NAME;
    private final String MODULE_APPROVAL_KEY = PermissionConstants.MEMO;

    private final String TRANSFER_FROM_PIS_CODE = DartaChalaniConstants.TRANSFER_FROM_PIS_CODE;
    private final String TRANSFER_FROM_SECTION_CODE = DartaChalaniConstants.TRANSFER_FROM_SECTION_CODE;
    private final String DELEGATED_NEP = DartaChalaniConstants.DELEGATED_NEP;
    private final String DELEGATED_EN = DartaChalaniConstants.DELEGATED_EN;
    private final String ADDITIONAL_RESPONSIBILITY_NEP = DartaChalaniConstants.ADDITIONAL_RESPONSIBILITY_NEP;
    private final String ADDITIONAL_RESPONSIBILITY_EN = DartaChalaniConstants.ADDITIONAL_RESPONSIBILITY_EN;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;
    @Autowired
    private DocumentUtil documentUtil;
    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private DateConverter dateConverter;
    @Autowired
    private DocumentServiceData documentServiceData;
    @Autowired
    private LetterTemplateProxy letterTemplateProxy;
    @Autowired
    @Lazy
    private DispatchLetterService dispatchLetterService;
    @Autowired
    @Lazy
    private ReceivedLetterServiceImpl receivedLetterService;
    @Autowired
    private RabbitMQService notificationService;
    @Autowired
    private VerifySignatureService verifySignatureService;

    public MemoServiceImpl(MemoRepo memoRepo,
                           DispatchLetterRepo dispatchLetterRepo,
                           ReceivedLetterRepo receivedLetterRepo,
                           MemoForwardRepo memoForwardRepo,
                           MemoApprovalRepo memoApprovalRepo,
                           MemoCommentRepo memoCommentRepo,
                           MemoContentRepo memoContentRepo,
                           MemoReferenceRepo memoReferenceRepo,
                           MemoDocumentDetailsRepo memoDocumentDetailsRepo,
                           MemoMapper memoMapper,
                           MemoApprovalMapper memoApprovalMapper,
                           CustomMessageSource customMessageSource,
                           DispatchLetterMapper dispatchLetterMapper,
                           ReceivedLetterMapper receivedLetterMapper,
                           MemoSuggestionRepo memoSuggestionRepo,
                           MemoSuggestionMapper memoSuggestionMapper,
                           MemoReferenceMapper memoReferenceMapper,
                           ExternalRecordsRepo externalRecordsRepo,
                           ConvertHtlToFileProxy convertHtlToFileProxy,
                           InitialService initialService,
                           DispatchLetterServiceImpl dispatchLetterServiceImpl,
                           MemoForwardHistoryRepo memoForwardHistoryRepo,
                           SignatureVerificationLogRepository signatureVerificationLogRepository,
                           DraftShareRepo draftShareRepo,
                           DraftShareLogRepo draftShareLogRepo) {
        super(memoRepo);
        this.memoRepo = memoRepo;
        this.dispatchLetterRepo = dispatchLetterRepo;
        this.receivedLetterRepo = receivedLetterRepo;
        this.memoForwardRepo = memoForwardRepo;
        this.memoApprovalRepo = memoApprovalRepo;
        this.memoCommentRepo = memoCommentRepo;
        this.memoContentRepo = memoContentRepo;
        this.memoReferenceRepo = memoReferenceRepo;
        this.memoDocumentDetailsRepo = memoDocumentDetailsRepo;
        this.memoMapper = memoMapper;
        this.memoApprovalMapper = memoApprovalMapper;
        this.customMessageSource = customMessageSource;
        this.dispatchLetterMapper = dispatchLetterMapper;
        this.receivedLetterMapper = receivedLetterMapper;
        this.memoSuggestionRepo = memoSuggestionRepo;
        this.memoSuggestionMapper = memoSuggestionMapper;
        this.memoReferenceMapper = memoReferenceMapper;
        this.externalRecordsRepo = externalRecordsRepo;
        this.convertHtlToFileProxy = convertHtlToFileProxy;
        this.dispatchLetterServiceImpl = dispatchLetterServiceImpl;
        this.initialService = initialService;
        this.memoForwardHistoryRepo = memoForwardHistoryRepo;
        this.signatureVerificationLogRepository = signatureVerificationLogRepository;
        this.draftShareRepo = draftShareRepo;
        this.draftShareLogRepo = draftShareLogRepo;
    }

    @Override
    public Memo saveMemo(MemoRequestPojo data) {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        String activeFiscalYear = userMgmtServiceData.findActiveFiscalYear().getCode();

        if (employeePojo != null && employeePojo.getSection() == null)
            throw new RuntimeException("प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ");

        Memo memo = new Memo().builder()
                .subject(data.getSubject())
                .content(data.getContent())
                .isDraft(data.getIsDraft())
                .signature(data.getSignature())
                .signatureIsActive(data.getSignatureIsActive())
                .hashContent(data.getHashContent())
                .pisCode(tokenProcessorService.getPisCode())
                .officeCode(tokenProcessorService.getOfficeCode())
                .fiscalYearCode(userMgmtServiceData.findActiveFiscalYear().getCode())
                .referenceNo(userMgmtServiceData.findActiveFiscalYear().getNameN())
                .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() != null ? employeePojo.getFunctionalDesignationCode() : null : null)
                .tippaniNo(initialService.getMemoNumber(tokenProcessorService.getOfficeCode()))
                .memo(data.getMemoId() == null ? null : memoRepo.findById(data.getMemoId()).get())
                .isArchive(Boolean.FALSE)
                .build();

        if (data.getDocument() != null && !data.getDocument().isEmpty()) {
            this.processDocument(data.getDocument(), memo, employeePojo, activeFiscalYear);
        }

        // setting template header
        if (dispatchLetterServiceImpl.getActiveTemplateHeaderFooter(TemplateType.H) == null)
            throw new RuntimeException("Template can not be null");
        memo.setTemplateHeaderId(dispatchLetterServiceImpl.getActiveTemplateHeaderFooter(TemplateType.H));

        // setting template footer
        memo.setTemplateFooterId(dispatchLetterServiceImpl.getActiveTemplateHeaderFooter(TemplateType.F));

        Memo saved = memoRepo.save(memo);

        MemoForwardHistory memoForwardHistory = new MemoForwardHistory().builder()
                .memo(saved)
                .officeCode(tokenProcessorService.getOfficeCode())
                .pisCode(tokenProcessorService.getPisCode())
                .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                .build();
        memoForwardHistoryRepo.save(memoForwardHistory);

        if (data.getTaskId() != null || data.getProjectId() != null) {
            ExternalRecords records = new ExternalRecords().builder()
                    .memo(saved)
                    .projectId(data.getProjectId())
                    .taskId(data.getTaskId())
                    .build();
            externalRecordsRepo.save(records);
        }

        if (!data.getIsDraft()) {

            EmployeePojo employeePojo1 = userMgmtServiceData.getEmployeeDetail(data.getApproval().getApproverPisCode());
            MemoApproval memoApproval = new MemoApproval().builder()
                    .memo(saved)
                    .approverPisCode(data.getApproval().getApproverPisCode())
                    .approverOfficeCode(tokenProcessorService.getOfficeCode())
                    .approverSectionCode(data.getApproval().getSectionCode())
                    .approverDesignationCode(employeePojo1 != null ? employeePojo1.getFunctionalDesignationCode() : null)
                    .senderPisCode(tokenProcessorService.getPisCode())
                    .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                    .senderOfficeCode(employeePojo != null ? employeePojo.getOffice().getCode() : null)
                    .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                    .status(Status.P)
                    .suggestion(false)
                    .reverted(false)
                    .isExternal(false)
                    .log(1L)
                    .isSeen(false)
                    .build();
            memoApprovalRepo.save(memoApproval);

            memoForwardHistoryRepo.save(
                    new MemoForwardHistory().builder()
                            .memo(memo)
                            .officeCode(employeePojo1 != null ? employeePojo1.getOffice().getCode() : null)
                            .pisCode(data.getApproval().getApproverPisCode())
                            .sectionCode(data.getApproval().getSectionCode())
                            .designationCode(employeePojo1 != null ? employeePojo1.getFunctionalDesignationCode() : null)
                            .build()
            );

            notificationService.notificationProducer(
                    NotificationPojo.builder()
                            .moduleId(memo.getId())
                            .module(MODULE_APPROVAL_KEY)
                            .sender(tokenProcessorService.getPisCode())
                            .receiver(data.getApproval().getApproverPisCode())
                            .subject(customMessageSource.getNepali("memo"))
                            .detail(customMessageSource.getNepali("memo.forward", employeePojo.getNameNp(), memo.getSubject()))
                            .pushNotification(true)
                            .received(true)
                            .build()
            );
        }

        //attach memo
        if (data.getAttachedMemoId() != null && !data.getAttachedMemoId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedMemoId()) {
                memoRepo.findById(id).orElseThrow(() -> new RuntimeException("No Memo Found"));

                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .referencedMemoId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        //attach dispatch letter
        if (data.getAttachedDispatchId() != null && !data.getAttachedDispatchId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedDispatchId()) {
                dispatchLetterRepo.findById(id).orElseThrow(() -> new RuntimeException("No Dispatch Found"));

                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .chalaniReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }


        //attach received letter (darta)
        if (data.getAttachedDartaId() != null && !data.getAttachedDartaId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedDartaId()) {
                receivedLetterRepo.findById(id).orElseThrow(() -> new RuntimeException("No Dispatch Found"));
                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .dartaReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getReferenceMemoId() != null && !data.getReferenceMemoId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getReferenceMemoId()) {
//                Memo memo1 = memoRepo.findById(id).orElseThrow(() -> new RuntimeException("No Memo Found"));

                //get latest memo from reference loop
                Long latestMemo = memoMapper.getLatestMemoInReferenceLoop(id);
                log.info("latestMemo: " + latestMemo);
                latestMemo = latestMemo != null ? latestMemo : id;
                Memo latest = memoRepo.findById(latestMemo).orElseThrow(() -> new RuntimeException("No Memo Found"));
                if (latest.getStatus() != Status.A)
                    throw new RuntimeException("Latest tippani " + latest.getSubject() + " not approved yet");

                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .referencedMemoId(latestMemo)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.FALSE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        //currently only memoReference is reference rest all are attach
        if (data.getChalaniReferenceId() != null && !data.getChalaniReferenceId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getChalaniReferenceId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .chalaniReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getReceivedReferenceId() != null && !data.getReceivedReferenceId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getReceivedReferenceId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .dartaReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getSystemDocuments() != null && !data.getSystemDocuments().isEmpty()) {
            List<MemoDocumentDetails> sysDocs = new ArrayList<>();
            for (SysDocumentsPojo documentsPojo : data.getSystemDocuments()) {
                MemoDocumentDetails systemDoc = new MemoDocumentDetails().builder()
                        .documentId(documentsPojo.getDocumentId())
                        .documentName(documentsPojo.getDocumentName())
                        .pisCode(tokenProcessorService.getPisCode())
                        .editable(true)
                        .memo(saved)
                        .build();
                sysDocs.add(systemDoc);
            }
            memoDocumentDetailsRepo.saveAll(sysDocs);
        }

        memoContentRepo.updateEditableByMemoId(saved.getId(), tokenProcessorService.getPisCode());
        memoDocumentDetailsRepo.updateEditableByMemoId(saved.getId(), tokenProcessorService.getPisCode());
        memoReferenceRepo.updateEditableByMemoId(saved.getId(), tokenProcessorService.getPisCode());

        return saved;
    }

    @Override
    public void deleteMemo(Long memoId) {
        String token = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(token);
        checkUserSection(employeePojo);

        String tokenUserSection = employeePojo.getSectionId();

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(token);
        if (employeePojo.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(token, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(token, employeePojo.getSectionId()));
        }

        Memo memo = memoRepo.findById(memoId).get();


        // soft delete memo reference with this memo
        memoReferenceRepo.deleteMemoReference(memoId);

        if (memo.getIsDraft()
                && ((listPisCodes.contains(memo.getPisCode())
                && employeePojo.getSectionId().equals(memo.getSectionCode())) || draftShareRepo.checkPermissionMemoDraft(memoId, token, tokenUserSection)))
            memoRepo.softDelete(memoId);
        else
            throw new RuntimeException("Can't Delete");

    }

    @Override
    public void archiveMemo(Long memoId) {
        String token = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(token);
        checkUserSection(employeePojo);

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(token);
        if (employeePojo.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(token, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(token, employeePojo.getSectionId()));
        }

        // soft delete memo reference with this memo
        memoReferenceRepo.deleteMemoReference(memoId);

        Memo memo = memoRepo.findById(memoId).get();
        if (memoMapper.getMemoActive(memoId, listPisCodes, employeePojo.getSectionId())
                && listPisCodes.contains(memo.getPisCode())
                && employeePojo.getSectionId().equals(memo.getSectionCode()))
            memoRepo.archive(memoId);
        else
            throw new RuntimeException("Can't Archive");
    }

    @Override
    public void restoreMemo(Long memoId) {
        String token = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(token);
        checkUserSection(employeePojo);

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(token);
        if (employeePojo.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(token, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(token, employeePojo.getSectionId()));
        }

        Memo memo = memoRepo.findById(memoId).get();

        if (listPisCodes.contains(memo.getPisCode())
                && employeePojo.getSectionId().equals(memo.getSectionCode()))
            memoRepo.restore(memoId);
        else
            throw new RuntimeException("Can't Restore");
    }

    @Override
    public Long saveContent(MemoContentPojo data) {
        Memo memo = memoRepo.findById(data.getId()).orElseThrow(() -> new RuntimeException("No Memo Found"));

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();

        listPisCodes.add(tokenPisCode);
        if (employeePojo.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()));
        }

        if (memo.getStatus() != null && memo.getStatus().equals(Status.A)) {
            throw new RuntimeException("Memo Already Approved");
        }

        MemoResponsePojo memoResponsePojo = memoMapper.getMemoById(data.getId());

        MemoApproval memoApproval = memoApprovalMapper.findActive(memoResponsePojo.getId());
        MemoSuggestion memoSuggestion = memoSuggestionMapper.findActive(memoResponsePojo.getId());

        if (memoApproval != null) {
            if (!memoApproval.isActive() || !listPisCodes.contains(memoApproval.getApproverPisCode()) && !memoApproval.getSuggestion())
                throw new RuntimeException("Invalid");
        }

//        if (memoSuggestion != null) {
//            if (!memoSuggestion.isActive() || !memoSuggestion.getApproverPisCode().equals(tokenProcessorService.getPisCode()))
//        }
        memoContentRepo.updateExternalEditableByMemoId(memo.getId());

        if (data.getContent() != null && !data.getContent().equals("")) {
            MemoContent memoContent = new MemoContent().builder()
                    .content(data.getContent())
                    .pisCode(tokenProcessorService.getPisCode())
                    .officeCode(tokenProcessorService.getOfficeCode())
                    .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                    .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                    .editable(true)
                    .isExternal(memoApproval != null && (memoApproval.getIsExternal()))
                    .signature(data.getSignature())
                    .signatureIsActive(data.getSignatureIsActive())
                    .hashContent(data.getHashContent())
                    .isExternalEditable(true)
                    .memo(memo)
                    .build();

            memoContentRepo.save(memoContent);
        }

        notificationService.notificationProducer(
                NotificationPojo.builder()
                        .moduleId(memo.getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(tokenProcessorService.getPisCode())
                        .receiver(memo.getPisCode())
                        .subject(customMessageSource.getNepali("memo"))
                        .detail(customMessageSource.getNepali("memo.comment", employeePojo.getNameNp(), memo.getSubject()))
                        .pushNotification(true)
                        .received(false)
                        .build()
        );

//        if (data.getDocuments() != null && !data.getDocuments().isEmpty()) {
//            List<Long> documentsToDelete = new ArrayList<>();
//            memoDocumentDetailsRepo.saveAll(this.addContentDocuments(data.getDocuments(), documentsToDelete, memo));
//        }

        return memo.getId();
    }

    @Override
    public Long editContent(MemoContentPojo message) {
        MemoContent update = memoContentRepo.findById(message.getId()).get();

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        MemoContent memoContent = MemoContent.builder()
                .content(message.getContent())
                .signatureIsActive(message.getSignatureIsActive())
                .signature(message.getSignature())
                .hashContent(message.getHashContent())
                .build();

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, memoContent);
        } catch (Exception e) {
            throw new RuntimeException("It does not exist");
        }

        boolean isActive = false;

        if (listPisCodes.contains(update.getPisCode()) && update.getEditable() != null && update.getEditable())
            isActive = true;

        if (update.getIsExternalEditable() == null || (update.getIsExternalEditable() != null && Boolean.TRUE.equals(update.getIsExternalEditable())))
            isActive = true;

        if (!isActive)
            throw new RuntimeException("Invalid");

        memoContentRepo.save(update);

        return update.getId();
    }

    @Override
    public Long editContentDoc(MemoContentPojo data) {
        Memo memo = memoRepo.findById(data.getMemoId()).get();

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();

        listPisCodes.add(tokenPisCode);
        if (employeePojo.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()));
        }

        MemoApproval memoApproval = memoApprovalMapper.findActive(memo.getId());
        if (memoApproval != null) {
            if (!memoApproval.isActive() || !listPisCodes.contains(memoApproval.getApproverPisCode()) && !memoApproval.getSuggestion())
                throw new RuntimeException("Invalid User");
        }

        List<Long> newDocumentsToRemove = new ArrayList<>();
        if (data.getDocumentsToRemove() != null && !data.getDocumentsToRemove().isEmpty()) {
            for (Long id : data.getDocumentsToRemove()) {
                MemoDocumentDetails memoDocumentDetails = memoDocumentDetailsRepo.getDetail(id, memo.getId());
                if (memoDocumentDetails != null) {
                    if (memoDocumentDetails.getPisCode().equals(tokenProcessorService.getPisCode()))
                        newDocumentsToRemove.add(memoDocumentDetails.getDocumentId());
                }
            }
            this.deleteDocuments(newDocumentsToRemove);
        }

        if (data.getDocuments() != null && !data.getDocuments().isEmpty()) {
            List<MemoDocumentDetails> memoDocumentDetails = this.updateContentDocuments(data.getDocuments(), memo, employeePojo, userMgmtServiceData.findActiveFiscalYear().getCode());

            if (!memoDocumentDetails.isEmpty()) {
                memoDocumentDetailsRepo.saveAll(memoDocumentDetails);
            }
        }

        if (data.getSystemDocuments() != null && !data.getSystemDocuments().isEmpty()) {
            List<MemoDocumentDetails> sysDocs = new ArrayList<>();
            for (SysDocumentsPojo documentsPojo : data.getSystemDocuments()) {
                MemoDocumentDetails systemDoc = new MemoDocumentDetails().builder()
                        .documentId(documentsPojo.getDocumentId())
                        .documentName(documentsPojo.getDocumentName())
                        .pisCode(tokenProcessorService.getPisCode())
                        .editable(true)
                        .memo(memo)
                        .build();
                sysDocs.add(systemDoc);
            }
            memoDocumentDetailsRepo.saveAll(sysDocs);
        }

        return memo.getId();
    }


    @Override
    public Long saveComment(MemoContentPojo memoComment) {
        MemoForward memoForward = memoForwardRepo.findById(memoComment.getId()).orElseThrow(() -> new RuntimeException("No Memo Found"));

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        if (memoForward.getCompletion_status().equals(Status.FN)) {
            throw new RuntimeException("Already Finalized");
        }

        if (listPisCodes.contains(memoForward.getReceiverPisCode())) {

            MemoResponsePojo memoResponsePojo = memoMapper.getMemoById(memoForward.getMemo().getId());

            for (ForwardResponsePojo forwardResponsePojo : memoResponsePojo.getForwards()) {
                if (forwardResponsePojo.getIsSuggestion() && memoResponsePojo.getStatus().equals(Status.A) && forwardResponsePojo.getReceiverPisCode().equals(tokenProcessorService.getPisCode())) {
                    throw new RuntimeException("Memo Already Approved");
                }
            }

            MemoComment comment = new MemoComment().builder()
                    .memoForward(memoForward)
                    .comment(memoComment.getContent())
                    .build();
            memoCommentRepo.save(comment);
        } else
            throw new RuntimeException("Invalid User");

        return memoForward.getId();
    }

    @Override
    public Long editComment(MemoContentPojo comment) {
        MemoComment update = memoCommentRepo.findById(comment.getId()).get();

        MemoComment memoComment = new MemoComment().builder()
                .comment(comment.getContent())
                .build();

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, memoComment);
        } catch (Exception e) {
            throw new RuntimeException("It does not exist");
        }

        memoCommentRepo.save(update);
        return update.getId();
    }

    @Override
    public Page<MemoResponsePojo> filterData(GetRowsRequest paginatedRequest) {
        Page<MemoResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        Set<String> listPisCodes = new HashSet<>();

        Boolean isActive = true;

        if (paginatedRequest.getSearchField().get("isApprover") != null) {
            if (paginatedRequest.getSearchField().get("isApprover").toString().equalsIgnoreCase("true")) {
                if (paginatedRequest.getSearchField().get("pisCode") != null && paginatedRequest.getSearchField().get("appSectionCode") != null) {
                    if (dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get("pisCode").toString(), paginatedRequest.getSearchField().get("appSectionCode").toString()) != null)
                        listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get("pisCode").toString(), paginatedRequest.getSearchField().get("appSectionCode").toString()));
                }
            }

        }

        if (paginatedRequest.getSearchField().get("senderPisCode") != null) {
            if (paginatedRequest.getSearchField().get("senderPisCode") != null) {
                if (dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get("senderPisCode").toString(), paginatedRequest.getSearchField().get("senderSectionCode").toString()) != null)
                    listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get("senderPisCode").toString(), paginatedRequest.getSearchField().get("senderSectionCode").toString()));
            }
        }

        if (paginatedRequest.getSearchField() != null
                && paginatedRequest.getSearchField().get("favourite") != null
                && paginatedRequest.getSearchField().get("favourite").equals(true))
            isActive = null;

        String suggestionPisCode = "suggestionPisCode";
        if (paginatedRequest.getSearchField() != null && paginatedRequest.getSearchField().get(suggestionPisCode) != null && paginatedRequest.getSearchField().get(suggestionPisCode).equals(tokenProcessorService.getPisCode())) {
            paginatedRequest.getSearchField().put(suggestionPisCode, paginatedRequest.getSearchField().get(suggestionPisCode));
            paginatedRequest.getSearchField().putIfAbsent("suggestionSectionCode", "null");
//            String data = dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get("suggestionPisCode").toString(), paginatedRequest.getSearchField().get("suggestionSectionCode").toString());

            if (dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get("suggestionPisCode").toString(), paginatedRequest.getSearchField().get("suggestionSectionCode").toString()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get("suggestionPisCode").toString(), paginatedRequest.getSearchField().get("suggestionSectionCode").toString()));

        } else if (paginatedRequest.getSearchField() != null) {
            paginatedRequest.getSearchField().put(suggestionPisCode, "null");
        }

        if (paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE) != null) {
            if (paginatedRequest.getSearchField().get(TRANSFER_FROM_SECTION_CODE) != null) {
                paginatedRequest.getSearchField().put(suggestionPisCode, paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE).toString());
                paginatedRequest.getSearchField().put("suggestionSectionCode", paginatedRequest.getSearchField().get(TRANSFER_FROM_SECTION_CODE).toString());
                if (dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE).toString(),
                        paginatedRequest.getSearchField().get(TRANSFER_FROM_SECTION_CODE).toString()) != null) {
                    listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE).toString(),
                            paginatedRequest.getSearchField().get(TRANSFER_FROM_SECTION_CODE).toString()));
                }
            }
        }


        listPisCodes.add(tokenProcessorService.getPisCode());
        page = memoMapper.filterData(
                page,
                paginatedRequest.getPisCode(),
                tokenProcessorService.getOfficeCode(),
                listPisCodes,
                paginatedRequest.getSearchField(),
                isActive,
                tokenProcessorService.getPisCode()
        );

        for (MemoResponsePojo data : page.getRecords()) {

            // this flag is only for tippani creator
            if (data.getIsImportant() != null && data.getIsImportant() && !listPisCodes.contains(data.getPisCode()))
                data.setIsImportant(Boolean.FALSE);

            if(data.getIsDraft())
                data.setDraftShareCount(draftShareRepo.memoDraftShareCount(data.getId()));

            data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(data.getOfficeCode());

            if (officePojo != null) {
                data.setOfficeNameNp(officePojo.getNameNp());
                data.setOfficeName(officePojo.getNameEn());
            }

            if (data.getDelegatedId() != null) {
                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(data.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                    data.setCreator(userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode()));
                    data.setCreatorSection(delegationResponsePojo.getToSection());
                }
            } else {
                data.setCreator(userMgmtServiceData.getEmployeeDetail(data.getPisCode()));
                if (data.getSectionCode() != null) {
                    SectionPojo sectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(data.getSectionCode()));
                    data.setCreatorSection(DtoConverter.convert(sectionPojo));
                }
            }

            List<MemoApprovalPojo> approvals = memoApprovalMapper.getApprovalByMemoId(data.getId());
            if (approvals != null && !approvals.isEmpty()) {
                data.setApproval(approvals);
                data.getApproval().forEach(
                        x -> {
                            EmployeePojo approvalUser = null;
                            if (x.getApproverPisCode() != null) {
                                if (x.getDelegatedId() != null) {
                                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                        approvalUser = userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode());
                                } else
                                    approvalUser = userMgmtServiceData.getEmployeeDetail(x.getApproverPisCode());
                                if (approvalUser != null && approvalUser.getSectionId() != null
                                        && x.getApproverPisCode().equals(tokenProcessorService.getPisCode())
                                        && x.getSectionCode() != null
                                        && x.getSectionCode().equals(approvalUser.getSectionId())) {
                                    data.setApprovalIsActive(x.getIsActive());
                                    data.setApprovalStatus(x.getStatus());
                                    if (Boolean.TRUE.equals(x.getIsImportant()))
                                        data.setIsImportant(true);
                                }
                            }
                        }
                );
                MemoApproval memoApproval = memoApprovalMapper.findActive(data.getId());
                if (memoApproval != null && memoApproval.isActive() && memoApproval.getApproverPisCode() != null && memoApproval.getApproverPisCode().equals(tokenProcessorService.getPisCode()))
                    data.setForwarded(false);
            } else
                data.setForwarded(false);

            List<MemoApprovalPojo> suggestions = memoApprovalMapper.getSuggestionByMemoId(data.getId());
            if (suggestions != null && approvals != null && !approvals.isEmpty()) {
                data.setSuggestion(suggestions);
                data.getSuggestion().forEach(
                        x -> {
                            EmployeePojo approvalUser = null;
                            if (x.getApproverPisCode() != null) {
                                if (x.getDelegatedId() != null) {
                                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                        approvalUser = userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode());
                                } else
                                    approvalUser = userMgmtServiceData.getEmployeeDetail(x.getApproverPisCode());
                                if (approvalUser != null && approvalUser.getSectionId() != null
                                        && x.getApproverPisCode().equals(tokenProcessorService.getPisCode())
                                        && x.getSectionCode() != null
                                        && x.getSectionCode().equals(approvalUser.getSectionId())) {
                                    data.setSuggestionIsActive(x.getIsActive());
                                    data.setSuggestionStatus(x.getStatus());
                                    if (Boolean.TRUE.equals(x.getIsImportant()))
                                        data.setIsImportant(true);
                                }
                            }
                        }
                );
            }

            List<MemoContentPojo> contents = memoApprovalMapper.getContentsByMemoId(data.getId());
            if (contents == null || contents.isEmpty())
                data.setContentIsPresent(false);

            //check is memo goes for suggestion or not
            if (memoRepo.getMemoStatus(data.getId()) != null && memoRepo.getMemoStatus(data.getId()))
                data.setStatus(Status.SG);
        }

        return page;
    }

    @Override
    public Page<MemoResponsePojo> getArchiveTippani(GetRowsRequest paginatedRequest) {
        Page<MemoResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        Set<String> listPisCodes = new HashSet<>();

        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        checkUserSection(employeePojo);

        listPisCodes.add(tokenPisCode);
        if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()) != null)
            listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()));

        page = memoMapper.getArchiveTippani(
                page,
                tokenProcessorService.getOfficeCode(),
                listPisCodes,
                employeePojo.getSectionId(),
                paginatedRequest.getSearchField()
        );

        for (MemoResponsePojo data : page.getRecords()) {

            // this flag is only for tippani creator
            if (data.getIsImportant() != null && data.getIsImportant() && !listPisCodes.contains(data.getPisCode()))
                data.setIsImportant(Boolean.FALSE);

            data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(data.getOfficeCode());

            if (officePojo != null) {
                data.setOfficeNameNp(officePojo.getNameNp());
                data.setOfficeName(officePojo.getNameEn());
            }

            if (data.getDelegatedId() != null) {
                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(data.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                    data.setCreator(userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode()));
            } else
                data.setCreator(userMgmtServiceData.getEmployeeDetail(data.getPisCode()));

            List<MemoApprovalPojo> approvals = memoApprovalMapper.getApprovalByMemoId(data.getId());
            if (approvals != null && !approvals.isEmpty()) {
                data.setApproval(approvals);
                data.getApproval().forEach(
                        x -> {
                            EmployeePojo approvalUser = null;
                            if (x.getApproverPisCode() != null) {
                                if (x.getDelegatedId() != null) {
                                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                        approvalUser = userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode());
                                } else
                                    approvalUser = userMgmtServiceData.getEmployeeDetail(x.getApproverPisCode());
                                if (approvalUser != null && approvalUser.getSectionId() != null
                                        && x.getApproverPisCode().equals(tokenProcessorService.getPisCode())
                                        && x.getSectionCode() != null
                                        && x.getSectionCode().equals(approvalUser.getSectionId())) {
                                    data.setApprovalIsActive(x.getIsActive());
                                    data.setApprovalStatus(x.getStatus());
                                    if (Boolean.TRUE.equals(x.getIsImportant()))
                                        data.setIsImportant(true);
                                }
                            }
                        }
                );
                MemoApproval memoApproval = memoApprovalMapper.findActive(data.getId());
                if (memoApproval != null && memoApproval.isActive() && memoApproval.getApproverPisCode() != null && memoApproval.getApproverPisCode().equals(tokenProcessorService.getPisCode()))
                    data.setForwarded(false);
            } else
                data.setForwarded(false);

            List<MemoApprovalPojo> suggestions = memoApprovalMapper.getSuggestionByMemoId(data.getId());
            if (suggestions != null && approvals != null && !approvals.isEmpty()) {
                data.setSuggestion(suggestions);
                data.getSuggestion().forEach(
                        x -> {
                            EmployeePojo approvalUser = null;
                            if (x.getApproverPisCode() != null) {
                                if (x.getDelegatedId() != null) {
                                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                        approvalUser = userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode());
                                } else
                                    approvalUser = userMgmtServiceData.getEmployeeDetail(x.getApproverPisCode());
                                if (approvalUser != null && approvalUser.getSectionId() != null
                                        && x.getApproverPisCode().equals(tokenProcessorService.getPisCode())
                                        && x.getSectionCode() != null
                                        && x.getSectionCode().equals(approvalUser.getSectionId())) {
                                    data.setSuggestionIsActive(x.getIsActive());
                                    data.setSuggestionStatus(x.getStatus());
                                    if (Boolean.TRUE.equals(x.getIsImportant()))
                                        data.setIsImportant(true);
                                }
                            }
                        }
                );
            }

            List<MemoContentPojo> contents = memoApprovalMapper.getContentsByMemoId(data.getId());
            if (contents == null || contents.isEmpty())
                data.setContentIsPresent(false);

        }

        return page;
    }

    @Override
    public void updateMemo(MemoRequestPojo data) {
        Memo update = memoRepo.findById(data.getId()).get();

        if (update.getStatus() == Status.A)
            throw new RuntimeException("Cannot update already approved");

        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user is involved in any section or not
        checkUserSection(employeePojo);

        //check user is memo creator or not
        checkIsCreator(data.getId(), tokenPisCode, employeePojo.getSectionId(), tokenProcessorService.getOfficeCode());

        String tokenUserSectionCode = employeePojo.getSectionId();

        Memo memo = new Memo().builder()
                .isDraft(data.getIsDraft())
                .subject(data.getSubject())
                .content(data.getContent())
                .signature(data.getSignature())
                .signatureIsActive(data.getSignatureIsActive())
                .hashContent(data.getHashContent())
                .build();

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(update, memo);
        } catch (Exception e) {
            throw new RuntimeException("It does not exist");
        }

        if (update.getDocumentMasterId() != null) {
            List<Long> newDocumentsToRemove = new ArrayList<>();
            if (data.getDocumentsToRemove() != null && !data.getDocumentsToRemove().isEmpty()) {
                for (Long id : data.getDocumentsToRemove()) {
                    MemoDocumentDetails memoDocumentDetails = memoDocumentDetailsRepo.getDetail(id, update.getId());
                    if (memoDocumentDetails != null) {
                        if (memoDocumentDetails.getPisCode().equals(tokenProcessorService.getPisCode()))
                            newDocumentsToRemove.add(memoDocumentDetails.getDocumentId());
                    }
                }
                this.deleteDocuments(newDocumentsToRemove);
            }
            if (data.getDocument() != null && !data.getDocument().isEmpty()) {
                List<MemoDocumentDetails> memoDocumentDetails = this.updateContentDocuments(data.getDocument(), update, employeePojo, userMgmtServiceData.findActiveFiscalYear().getCode());
                if (!memoDocumentDetails.isEmpty()) {
                    memoDocumentDetailsRepo.saveAll(memoDocumentDetails);
                }
            }
        } else {
            if (data.getDocument() != null && !data.getDocument().isEmpty())
                this.processDocument(data.getDocument(), update, employeePojo, userMgmtServiceData.findActiveFiscalYear().getCode());
        }

        if (data.getIsDraft()) {

            //share draft
            if (!update.getPisCode().equals(tokenPisCode)) {
                Optional<DraftShare> memoDraftShareOp =
                        draftShareRepo.getByReceiverPisCodeAndReceiverSectionCodeAndMemoIdAndIsActiveAndLetterType(tokenPisCode, tokenUserSectionCode, data.getId(), Boolean.TRUE, DcTablesEnum.MEMO);
                if (memoDraftShareOp.isPresent()) {
                    DraftShare draftShare = memoDraftShareOp.get();
                    draftShare.setStatus(data.getIsReadyToSubmit() ? Status.FN : Status.IP);
                    draftShareRepo.save(draftShare);

                    DraftShareLog draftShareLog = DraftShareLog.builder()
                            .fromStatus(draftShare.getStatus())
                            .toStatus(data.getIsReadyToSubmit() ? Status.FN : Status.IP)
                            .pisCode(tokenPisCode)
                            .sectionCode(tokenUserSectionCode)
                            .draftShareId(draftShare.getId())
                            .build();

                    draftShareLogRepo.save(draftShareLog);
                }
            } else {
                DraftShare senderDraftShare =
                        draftShareRepo.getSenderMemoDraftShare(tokenPisCode, tokenUserSectionCode, data.getId());

                if (senderDraftShare != null) {

                    DraftShareLog draftShareLog = DraftShareLog.builder()
                            .fromStatus(Status.P)
                            .toStatus(data.getIsReadyToSubmit() ? Status.FN : Status.IP)
                            .pisCode(tokenPisCode)
                            .sectionCode(tokenUserSectionCode)
                            .draftShareId(senderDraftShare.getId())
                            .build();

                    draftShareLogRepo.save(draftShareLog);
                }
            }
        }

        MemoApproval memoApproval = memoApprovalMapper.findActive(update.getId());
        if (memoApproval == null && data.getApproval() != null) {
            if (data.getApproval().getApproverPisCode() != null && !data.getApproval().getApproverPisCode().isEmpty()) {
                EmployeePojo employeePojo1 = userMgmtServiceData.getEmployeeDetail(data.getApproval().getApproverPisCode());
                MemoApproval saveApproval = new MemoApproval().builder()
                        .memo(update)
                        .approverPisCode(data.getApproval().getApproverPisCode())
                        .approverOfficeCode(employeePojo1 != null ? employeePojo1.getOffice().getCode() : null)
                        .approverSectionCode(data.getApproval().getSectionCode())
                        .approverDesignationCode(employeePojo1 != null ? employeePojo1.getFunctionalDesignationCode() : null)
                        .senderPisCode(tokenProcessorService.getPisCode())
                        .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                        .senderOfficeCode(employeePojo != null ? employeePojo.getOffice().getCode() : null)
                        .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                        .status(Status.P)
                        .suggestion(false)
                        .reverted(false)
                        .isExternal(false)
                        .log(1L)
                        .isSeen(false)
                        .build();
                memoApprovalRepo.save(saveApproval);

                notificationService.notificationProducer(
                        NotificationPojo.builder()
                                .moduleId(memo.getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(data.getApproval().getApproverPisCode())
                                .subject(customMessageSource.getNepali("memo"))
                                .detail(customMessageSource.getNepali("memo.forward", employeePojo1.getNameNp(), memo.getSubject()))
                                .pushNotification(true)
                                .received(true)
                                .build()
                );

                memoForwardHistoryRepo.save(
                        new MemoForwardHistory().builder()
                                .memo(update)
                                .officeCode(tokenProcessorService.getOfficeCode())
                                .pisCode(data.getApproval().getApproverPisCode())
                                .sectionCode(data.getApproval().getSectionCode())
                                .designationCode(employeePojo1.getFunctionalDesignationCode())
                                .build()
                );

                memoContentRepo.updateEditableByMemoId(update.getId(), tokenProcessorService.getPisCode());
                memoDocumentDetailsRepo.updateEditableByMemoId(update.getId(), tokenProcessorService.getPisCode());

                if (!update.getPisCode().equals(tokenPisCode)) {

                    memoForwardHistoryRepo.updateHistory(data.getId(), update.getPisCode(), update.getSectionCode(), tokenPisCode, tokenUserSectionCode, employeePojo.getFunctionalDesignationCode());
                    update.setPisCode(tokenPisCode);
                    update.setSectionCode(tokenUserSectionCode);
                    update.setOfficeCode(tokenProcessorService.getOfficeCode());
                }
            }
        }

        update = memoRepo.save(update);

        if (data.getAttachedMemoId() != null && !data.getAttachedMemoId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedMemoId()) {
                memoRepo.findById(id).orElseThrow(() -> new RuntimeException("No Memo Found"));

                MemoReference memoReference = new MemoReference().builder()
                        .memo(update)
                        .referencedMemoId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        //attach dispatch letter
        if (data.getAttachedDispatchId() != null && !data.getAttachedDispatchId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedDispatchId()) {
                dispatchLetterRepo.findById(id).orElseThrow(() -> new RuntimeException("No Dispatch Found"));

                MemoReference memoReference = new MemoReference().builder()
                        .memo(update)
                        .chalaniReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }


        //attach received letter (darta)
        if (data.getAttachedDartaId() != null && !data.getAttachedDartaId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedDartaId()) {
                receivedLetterRepo.findById(id).orElseThrow(() -> new RuntimeException("No Dispatch Found"));
                MemoReference memoReference = new MemoReference().builder()
                        .memo(update)
                        .dartaReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getReferenceMemoId() != null && !data.getReferenceMemoId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getReferenceMemoId()) {

                //get latest memo from reference loop
                Long latestMemo = memoMapper.getLatestMemoInReferenceLoop(id);
                log.info("latestMemo: " + latestMemo);
                latestMemo = latestMemo != null ? latestMemo : id;
                Memo latest = memoRepo.findById(latestMemo).orElseThrow(() -> new RuntimeException("No Memo Found"));
                if (latest.getStatus() != Status.A)
                    throw new RuntimeException("Latest tippani " + latest.getSubject() + " not approved yet");

                MemoReference memoReference = new MemoReference().builder()
                        .memo(update)
                        .referencedMemoId(latestMemo)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.FALSE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getChalaniReferenceId() != null && !data.getChalaniReferenceId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getChalaniReferenceId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .memo(update)
                        .chalaniReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getReceivedReferenceId() != null && !data.getReceivedReferenceId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getReceivedReferenceId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .memo(update)
                        .dartaReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .pisCode(tokenProcessorService.getPisCode())
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getReferencesToRemove() != null && !data.getReferencesToRemove().isEmpty()) {
            data.getReferencesToRemove().forEach(id -> {
                memoReferenceRepo.softDeleteReference(id);
            });
        }

        if (data.getAttachedMemoToRemove() != null && !data.getAttachedMemoToRemove().isEmpty()) {
            data.getAttachedMemoToRemove().forEach(id -> {
                memoReferenceRepo.softDeleteReferenceMemo(id, data.getId());
            });
        }

        if (data.getAttachedDispatchToRemove() != null && !data.getAttachedDispatchToRemove().isEmpty()) {
            data.getAttachedDispatchToRemove().forEach(id -> {
                memoReferenceRepo.softDeleteReferenceDispatch(id, data.getId());
            });
        }

        if (data.getAttachedDartaToRemove() != null && !data.getAttachedDartaToRemove().isEmpty()) {
            data.getAttachedDartaToRemove().forEach(id -> {
                memoReferenceRepo.softDeleteReferenceDarta(id, data.getId());
            });
        }

        memoContentRepo.updateEditableByMemoId(update.getId(), tokenProcessorService.getPisCode());
        memoDocumentDetailsRepo.updateEditableByMemoId(update.getId(), tokenProcessorService.getPisCode());
        memoReferenceRepo.updateEditableByMemoId(update.getId(), tokenProcessorService.getPisCode());
    }

    @Override
    public List<MemoResponsePojo> getMemoByReceiverPisCode(String receiverPisCode) {
        List<MemoResponsePojo> memos = memoMapper.getMemoByReceiverPisCode(receiverPisCode);

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }

        return memos;
    }

    @Override
    public ArrayList<MemoResponsePojo> getSaved() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        ArrayList<MemoResponsePojo> memos = memoMapper.getSaved(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }
        return memos;
    }

    @Override
    public void updateStatus(StatusPojo statusPojo) {
        if (statusPojo.getStatus().equals(Status.IP) || statusPojo.getStatus().equals(Status.FN)) {
            MemoForward data = memoForwardRepo.findById(statusPojo.getId()).get();
            data.setCompletion_status(statusPojo.getStatus());
            memoForwardRepo.save(data);

            MemoComment memoComment = new MemoComment().builder()
                    .comment(statusPojo.getDescription())
                    .memoForward(data)
                    .build();
            memoCommentRepo.save(memoComment);
        } else
            throw new RuntimeException("Invalid");
    }

    @Override
    public void updateApproval(ApprovalPojo data) {

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        String nextApproverPisCode = null;
        String nextApproverSectionCode = null;
        String nextApproverDesignationCode = null;
        String nextApproverOfficeCode = null;
        this.setUserId(data);
        Memo memo = this.findById(data.getId());
        MemoResponsePojo memoResponsePojo = memoMapper.getMemoById(data.getId());
        MemoApproval memoApproval = memoApprovalMapper.findActive(memo.getId());
        String officeCode = data.getOfficeCode() != null ? data.getOfficeCode() : tokenProcessorService.getOfficeCode();
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(officeCode);
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);
        String tokenUserSection = employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null;

        // Get the list of involved users
        ArrayList<MemoForwardHistory> approvers = memoForwardHistoryRepo.getAllInvolvedUsers(memoResponsePojo.getId());
        int index = -1;
        MemoForwardHistory nextUser;

        if (!approvers.isEmpty()) {
            for (MemoForwardHistory approver : approvers) {

                if (approver.getPisCode() != null && listPisCodes.contains(approver.getPisCode())
                        && approver.getSectionCode() != null && approver.getSectionCode().equals(tokenUserSection)) {
                    index = approvers.indexOf(approver);
                }
            }

            if (index > 0) {
                nextUser = approvers.get(index - 1);
                if (nextUser != null) {
                    nextApproverPisCode = nextUser.getPisCode();
                    nextApproverOfficeCode = nextUser.getOfficeCode();
                    nextApproverSectionCode = nextUser.getSectionCode();
                    nextApproverDesignationCode = nextUser.getDesignationCode();
                }
            }
        }

        if (memo.getStatus().equals(Status.P)) {

            switch (memoApproval.getStatus()) {
                case P:
                    break;
                default:
                    throw new RuntimeException("Can't Process");
            }

            switch (data.getStatus()) {

                case A:
                    this.validateApproval(memoApproval.getApproverPisCode());
                    memoApproval.setStatus(Status.F);
                    memoApproval.setActive(false);
                    memoApprovalMapper.updateById(
                            memoApproval
                    );
                    memoRepo.updateStatus(data.getStatus().toString(), memo.getId(), new Timestamp(new Date().getTime()));
                    memoApprovalRepo.save(
                            new MemoApproval().builder()
                                    .status(Status.A)
                                    .approverPisCode(tokenPisCode)
                                    .approverSectionCode(employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null)
                                    .approverDesignationCode(employeePojo.getFunctionalDesignationCode())
                                    .approverOfficeCode(tokenProcessorService.getOfficeCode())
                                    .senderPisCode(tokenPisCode)
                                    .senderOfficeCode(tokenProcessorService.getOfficeCode())
                                    .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                                    .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                                    .remarks(data.getRejectRemarks())
                                    .reverted(false)
                                    .isExternal(false)
                                    .suggestion(false)
                                    .log(memoApproval.getLog() + 1)
                                    .isSeen(false)
                                    .memo(memo)
                                    .build()
                    );
//                    if (data.getPdf() != null && !data.getPdf().isEmpty()) {
//                        this.savePdf(data);
//                    }

                    notificationService.notificationProducer(
                            NotificationPojo.builder()
                                    .moduleId(memo.getId())
                                    .module(MODULE_APPROVAL_KEY)
                                    .sender(memoApproval.getApproverPisCode())
                                    .receiver(memo.getPisCode())
                                    .subject(customMessageSource.getNepali("memo"))
                                    .detail(customMessageSource.getNepali("memo.approve", employeePojo.getNameNp(), memo.getSubject()))
                                    .pushNotification(true)
                                    .received(false)
                                    .build()
                    );

                    List<String> middleApproverPisCodes = memoApprovalRepo.getMiddleApproverPisCode(memo.getId(), tokenPisCode);
                    if (!middleApproverPisCodes.isEmpty()) {
                        middleApproverPisCodes.stream().forEach(
                                notificationReceiver -> {
                                    notificationService.notificationProducer(
                                            NotificationPojo.builder()
                                                    .moduleId(memo.getId())
                                                    .module(MODULE_APPROVAL_KEY)
                                                    .sender(memoApproval.getApproverPisCode())
                                                    .receiver(notificationReceiver)
                                                    .subject(customMessageSource.getNepali("memo"))
                                                    .detail(customMessageSource.getNepali("memo.approve", employeePojo.getNameNp(), memo.getSubject()))
                                                    .pushNotification(true)
                                                    .received(false)
                                                    .build()
                                    );
                                }
                        );
                    }

                    memoContentRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    memoDocumentDetailsRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    memoReferenceRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    break;
                case R:
                    this.validateApproval(memoApproval.getApproverPisCode());
                    memoApproval.setStatus(Status.F);
                    memoApproval.setActive(false);
                    memoApprovalMapper.updateById(
                            memoApproval
                    );
                    memoRepo.updateStatus(data.getStatus().toString(), memo.getId(), new Timestamp(new Date().getTime()));
                    memoApprovalRepo.save(
                            new MemoApproval().builder()
                                    .status(Status.R)
                                    .approverPisCode(tokenPisCode)
                                    .approverOfficeCode(tokenProcessorService.getOfficeCode())
                                    .approverSectionCode(employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null)
                                    .approverDesignationCode(employeePojo.getFunctionalDesignationCode())
                                    .senderPisCode(tokenPisCode)
                                    .senderOfficeCode(tokenProcessorService.getOfficeCode())
                                    .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                                    .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                                    .remarks(data.getRejectRemarks())
                                    .reverted(false)
                                    .isExternal(false)
                                    .suggestion(false)
                                    .log(memoApproval.getLog() + 1)
                                    .isSeen(false)
                                    .memo(memo)
                                    .build()
                    );

                    notificationService.notificationProducer(
                            NotificationPojo.builder()
                                    .moduleId(memo.getId())
                                    .module(MODULE_APPROVAL_KEY)
                                    .sender(memoApproval.getApproverPisCode())
                                    .receiver(memo.getPisCode())
                                    .subject(customMessageSource.getNepali("memo"))
                                    .detail(customMessageSource.getNepali("memo.reject", employeePojo.getNameNp(), memo.getSubject()))
                                    .pushNotification(true)
                                    .received(false)
                                    .build()
                    );

                    List<String> middleApproverPisCode = memoApprovalRepo.getMiddleApproverPisCode(memo.getId(), tokenPisCode);
                    if (!middleApproverPisCode.isEmpty()) {
                        middleApproverPisCode.stream().forEach(
                                notificationReceiver -> {
                                    notificationService.notificationProducer(
                                            NotificationPojo.builder()
                                                    .moduleId(memo.getId())
                                                    .module(MODULE_APPROVAL_KEY)
                                                    .sender(memoApproval.getApproverPisCode())
                                                    .receiver(notificationReceiver)
                                                    .subject(customMessageSource.getNepali("memo"))
                                                    .detail(customMessageSource.getNepali("memo.reject", employeePojo.getNameNp(), memo.getSubject()))
                                                    .pushNotification(true)
                                                    .received(false)
                                                    .build()
                                    );
                                }
                        );
                    }

                    memoContentRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    memoDocumentDetailsRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    memoReferenceRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    break;
                case F:
                    this.validateApproval(memoApproval.getApproverPisCode());
                    EmployeePojo userPojo = userMgmtServiceData.getEmployeeDetail(data.getForwardApproverPisCode() == null ? officeHeadPojo.getPisCode() : data.getForwardApproverPisCode());
                    String userSection = userPojo != null && userPojo.getSection() != null ? userPojo.getSection().getId().toString() : null;
                    if (!approvers.isEmpty()) {
                        for (MemoForwardHistory memoForwardHistory : approvers) {
                            if (memoForwardHistory.getPisCode().equals(data.getForwardApproverPisCode())
                                    && memoForwardHistory.getSectionCode().equals(userSection)
                                    && !memo.getPisCode().equals(data.getForwardApproverPisCode()))
                                throw new RuntimeException("User Already involved");
                        }
                    }

                    if (memoApproval.getSuggestion())
                        throw new RuntimeException("Tippani sent for suggestion");

                    if (data.getIsExternal()) {
                        memoApproval.setStatus(Status.P);
                        memoApproval.setActive(false);
                        memoApproval.setIsExternal(true);
                        memoApproval.setSuggestion(true);
                        memoApproval.setLastModifiedDate(new Timestamp(new Date().getTime()));
                        memoApprovalMapper.updateById(
                                memoApproval
                        );
                        EmployeePojo approverDetail = userMgmtServiceData.getEmployeeDetail(data.getForwardApproverPisCode() == null ? officeHeadPojo.getPisCode() : data.getForwardApproverPisCode());
                        if (data.getForwardApproverPisCode() != null && (data.getSectionCode() == null || data.getSectionCode().equals("")))
                            throw new RuntimeException("Approver section code can not be null or empty");
                        memoSuggestionRepo.save(
                                new MemoSuggestion().builder()
                                        .memo(memo)
                                        .remarks(data.getRejectRemarks())
                                        .senderPisCode(tokenPisCode)
                                        .senderOfficeCode(tokenProcessorService.getOfficeCode())
                                        .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                                        .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                                        .approverPisCode(data.getForwardApproverPisCode() == null ? officeHeadPojo.getPisCode() : data.getForwardApproverPisCode())
                                        .approverOfficeCode(approverDetail != null ? approverDetail.getOffice().getCode() : null)
                                        .approverSectionCode(data.getSectionCode() != null ? data.getSectionCode() : approverDetail.getSectionId())
                                        .approverDesignationCode(approverDetail != null ? approverDetail.getFunctionalDesignationCode() != null ? approverDetail.getFunctionalDesignationCode() : null : null)
                                        .initialSender(tokenPisCode)
                                        .firstSender(data.getForwardApproverPisCode() == null ? officeHeadPojo.getPisCode() : data.getForwardApproverPisCode())
                                        .log(memoApproval.getLog() + 1)
                                        .isSeen(false)
                                        .build()
                        );
                        notificationService.notificationProducer(
                                NotificationPojo.builder()
                                        .moduleId(memo.getId())
                                        .module(MODULE_APPROVAL_KEY)
                                        .sender(tokenProcessorService.getPisCode())
                                        .receiver(data.getForwardApproverPisCode() == null ? officeHeadPojo.getPisCode() : data.getForwardApproverPisCode())
                                        .subject(customMessageSource.getNepali("memo"))
                                        .detail(customMessageSource.getNepali("memo.suggestion", memo.getSubject()))
                                        .pushNotification(true)
                                        .received(true)
                                        .build()
                        );
                    } else {
                        memoApproval.setStatus(data.getStatus());
                        memoApproval.setActive(false);
                        memoApproval.setIsExternal(false);
                        memoApproval.setSuggestion(false);
                        memoApprovalMapper.updateById(
                                memoApproval
                        );
                        EmployeePojo approverDetail = userMgmtServiceData.getEmployeeDetail(data.getForwardApproverPisCode());
                        memoApprovalRepo.save(
                                new MemoApproval().builder()
                                        .status(Status.P)
                                        .approverPisCode(data.getForwardApproverPisCode())
                                        .approverSectionCode(data.getSectionCode())
                                        .approverDesignationCode(approverDetail != null ? approverDetail.getFunctionalDesignationCode() != null ? approverDetail.getFunctionalDesignationCode() : null : null)
                                        .approverOfficeCode(approverDetail != null ? approverDetail.getOffice().getCode() : null)
                                        .senderPisCode(tokenPisCode)
                                        .senderOfficeCode(tokenProcessorService.getOfficeCode())
                                        .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                                        .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                                        .remarks(data.getRejectRemarks())
                                        .reverted(false)
                                        .isExternal(false)
                                        .suggestion(false)
                                        .log(memoApproval.getLog() + 1)
                                        .isSeen(false)
                                        .memo(memo)
                                        .build()
                        );

                        memoForwardHistoryRepo.save(
                                new MemoForwardHistory().builder()
                                        .memo(memo)
                                        .officeCode(approverDetail != null ? approverDetail.getOffice().getCode() : null)
                                        .pisCode(data.getForwardApproverPisCode())
                                        .sectionCode(data.getSectionCode())
                                        .designationCode(approverDetail != null ? approverDetail.getFunctionalDesignationCode() : null)
                                        .build()
                        );

                        notificationService.notificationProducer(
                                NotificationPojo.builder()
                                        .moduleId(memo.getId())
                                        .module(MODULE_APPROVAL_KEY)
                                        .sender(tokenProcessorService.getPisCode())
                                        .receiver(data.getForwardApproverPisCode())
                                        .subject(customMessageSource.getNepali("memo"))
                                        .detail(customMessageSource.getNepali("memo.forward", employeePojo.getNameNp(), memo.getSubject()))
                                        .pushNotification(true)
                                        .received(true)
                                        .build()
                        );
                    }
                    memoContentRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    memoDocumentDetailsRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    memoReferenceRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    break;
                case RV:
                    this.validateApproval(memoApproval.getApproverPisCode());
                    if (memoApproval.getApproverPisCode().equals(memoResponsePojo.getPisCode()))
                        throw new RuntimeException("Already at the initial user, cannot revert");

                    memoApproval.setStatus(data.getStatus());
                    memoApproval.setActive(false);
                    memoApprovalMapper.updateById(
                            memoApproval
                    );

                    if (nextApproverPisCode == null)
                        throw new RuntimeException("Not found");

                    memoApprovalRepo.save(
                            new MemoApproval().builder()
                                    .status(Status.P)
                                    .approverPisCode(nextApproverPisCode)
                                    .approverOfficeCode(nextApproverOfficeCode)
                                    .approverSectionCode(nextApproverSectionCode)
                                    .approverDesignationCode(nextApproverDesignationCode)
                                    .senderOfficeCode(tokenProcessorService.getOfficeCode())
                                    .senderPisCode(tokenPisCode)
                                    .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                                    .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                                    .remarks(data.getRejectRemarks())
                                    .isExternal(false)
                                    .reverted(true)
                                    .suggestion(false)
                                    .log(memoApproval.getLog() + 1)
                                    .isSeen(false)
                                    .memo(memo)
                                    .build()
                    );
                    memoContentRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    memoDocumentDetailsRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);
                    memoReferenceRepo.updateEditableByMemoId(memo.getId(), tokenPisCode);

                    memoForwardHistoryRepo.setIsActiveByPisCode(tokenPisCode, memo.getId(), tokenUserSection);

                    notificationService.notificationProducer(
                            NotificationPojo.builder()
                                    .moduleId(memo.getId())
                                    .module(MODULE_APPROVAL_KEY)
                                    .sender(tokenPisCode)
                                    .receiver(nextApproverPisCode)
                                    .subject(customMessageSource.getNepali("memo"))
                                    .detail(customMessageSource.getNepali("memo.revert", employeePojo.getNameNp(), memo.getSubject()))
                                    .pushNotification(true)
                                    .received(true)
                                    .build()
                    );
                    break;
                default:
                    break;
            }
        } else
            throw new RuntimeException("Can't Process Request");
    }

    private void savePdf(ApprovalPojo data) {
        Optional<Memo> memo = memoRepo.findById(data.getId());

        if (!memo.isPresent())
            throw new RuntimeException("Tippani does not exist");

        Memo memoData = memo.get();

        memoData.setPdf(data.getPdf());
        memoRepo.save(memoData);
    }

    @Override
    @SneakyThrows
    public void updateSuggestion(ApprovalPojo data) {
        this.setUserId(data);
        Memo memo = this.findById(data.getId());
//        MemoResponsePojo memoResponsePojo = memoMapper.getMemoById(data.getId());
        MemoSuggestion memoSuggestion = memoSuggestionMapper.findActive(memo.getId());
//        String initial = memoSuggestionMapper.getInitialUser(memo.getId());
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (memo.getStatus().equals(Status.P)) {

            switch (memoSuggestion.getStatus()) {
                case P:
                    break;
                default:
                    throw new RuntimeException("Can't Process");
            }

            switch (data.getStatus()) {
                case F:
                    this.validateApproval(memoSuggestion.getApproverPisCode());
                    memoSuggestion.setStatus(data.getStatus());
                    memoSuggestion.setActive(false);
                    memoSuggestionMapper.updateById(
                            memoSuggestion
                    );
                    EmployeePojo approverDetails = userMgmtServiceData.getEmployeeDetail(data.getForwardApproverPisCode());
                    memoSuggestionRepo.save(
                            new MemoSuggestion().builder()
                                    .status(Status.P)
                                    .approverPisCode(data.getForwardApproverPisCode())
                                    .approverOfficeCode(approverDetails != null ? approverDetails.getOffice().getCode() : null)
                                    .approverSectionCode(data.getSectionCode())
                                    .approverDesignationCode(approverDetails != null ? approverDetails.getFunctionalDesignationCode() : null)
                                    .senderPisCode(tokenProcessorService.getPisCode())
                                    .senderOfficeCode(tokenProcessorService.getOfficeCode())
                                    .senderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                                    .senderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                                    .remarks(data.getRejectRemarks())
                                    .initialSender(memoSuggestion.getInitialSender())
                                    .firstSender(tokenProcessorService.getPisCode())
                                    .log(memoSuggestion.getLog() + 1)
                                    .isSeen(false)
                                    .memo(memo)
                                    .build()
                    );

                    memoContentRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());
                    memoDocumentDetailsRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());
                    memoReferenceRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());

                    notificationService.notificationProducer(
                            NotificationPojo.builder()
                                    .moduleId(memo.getId())
                                    .module(MODULE_APPROVAL_KEY)
                                    .sender(tokenProcessorService.getPisCode())
                                    .receiver(data.getForwardApproverPisCode())
                                    .subject(customMessageSource.getNepali("memo"))
                                    .detail(customMessageSource.getNepali("memo.suggestion", memo.getSubject()))
                                    .pushNotification(true)
                                    .received(true)
                                    .build()
                    );
                    break;
                case A:
                    this.validateApproval(memoSuggestion.getApproverPisCode());

                    EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(memoSuggestion.getFirstSender());

                    if (employeeMinimalPojo.getSection() == null)
                        throw new RuntimeException("section not assigned for employee: " + memoSuggestion.getFirstSender());

                    Set<String> firstSenderHistory = new HashSet<>();
                    List<String> previousList = dispatchLetterServiceImpl.getPreviousPisCode(memoSuggestion.getFirstSender(), employeeMinimalPojo.getSectionId().toString());
                    if (previousList != null)
                        firstSenderHistory.addAll(previousList);
                    firstSenderHistory.add(memoSuggestion.getFirstSender());
                    MemoSuggestion senderByMemo = memoSuggestionRepo.getByFirstSender(firstSenderHistory, data.getId());
                    Map<String, Object> firstSenderAndInitialSender = memoSuggestionRepo.getFirstSenderAndInitialSender(firstSenderHistory, data.getId());

                    if (senderByMemo != null) {
                        Optional<MemoContent> memoContent = memoContentRepo.findFirstByMemoAndPisCodeOrderByIdDesc(memo, tokenProcessorService.getPisCode());
                        if (memoContent.isPresent()) {
                            Optional<MemoContent> memoContentOptional = memoContentRepo.findFirstByMemoAndPisCodeOrderByIdDesc(memo, senderByMemo.getFirstSender());
                            if (memoContentOptional.isPresent()) {
                                MemoContent content = memoContentOptional.orElse(new MemoContent());
                                content.setIsExternalEditable(false);
                                memoContentRepo.save(content);
                            }
                        }

                        MemoSuggestion newMemoSuggestion = new MemoSuggestion();
//                        BeanUtils.copyProperties(newMemoSuggestion, senderByMemo);
                        newMemoSuggestion.setId(null);
                        newMemoSuggestion.setCreatedBy(null);
                        newMemoSuggestion.setCreatedDate(null);
                        newMemoSuggestion.setLastModifiedBy(null);
                        newMemoSuggestion.setLastModifiedDate(null);
                        newMemoSuggestion.setStatus(Status.P);
                        newMemoSuggestion.setReverted(true);
                        newMemoSuggestion.setIsRevert(true);
                        newMemoSuggestion.setLog(memoSuggestion.getLog() + 1);
                        newMemoSuggestion.setRemarks(data.getRejectRemarks());
                        newMemoSuggestion.setMemo(memo);
                        newMemoSuggestion.setSenderPisCode(tokenProcessorService.getPisCode());
                        newMemoSuggestion.setSenderOfficeCode(tokenProcessorService.getOfficeCode());
                        newMemoSuggestion.setSenderSectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null);
                        newMemoSuggestion.setSenderDesignationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null);
                        newMemoSuggestion.setActive(true);
                        newMemoSuggestion.setApproverPisCode(senderByMemo.getSenderPisCode());
                        newMemoSuggestion.setApproverSectionCode(senderByMemo.getSenderSectionCode());
                        newMemoSuggestion.setApproverOfficeCode(senderByMemo.getSenderOfficeCode());
                        newMemoSuggestion.setApproverDesignationCode(senderByMemo.getSenderDesignationCode());
                        newMemoSuggestion.setFirstSender(firstSenderAndInitialSender.get("first_sender") != null ? firstSenderAndInitialSender.get("first_sender").toString() : null);
                        newMemoSuggestion.setInitialSender(firstSenderAndInitialSender.get("initial_sender") != null ? firstSenderAndInitialSender.get("initial_sender").toString() : null);
                        memoSuggestionRepo.save(newMemoSuggestion);

                    }

                    memoSuggestion.setStatus(Status.F);
                    memoSuggestion.setActive(false);
                    memoSuggestion.setIsRevert(true);
                    memoSuggestionMapper.updateById(
                            memoSuggestion
                    );

                    memoContentRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());
                    memoDocumentDetailsRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());
                    memoReferenceRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());

                    notificationService.notificationProducer(
                            NotificationPojo.builder()
                                    .moduleId(memo.getId())
                                    .module(MODULE_APPROVAL_KEY)
                                    .sender(tokenProcessorService.getPisCode())
                                    .receiver(memoSuggestion.getFirstSender())
                                    .subject(customMessageSource.getNepali("memo"))
                                    .detail(customMessageSource.getNepali("memo.suggestion", memo.getSubject()))
                                    .pushNotification(true)
                                    .received(true)
                                    .build()
                    );
                    break;
                default:
                    break;
            }
        } else
            throw new RuntimeException("Can't Process Request");

    }

    private void validateApproval(String approverPisCode) {

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        if (!listPisCodes.contains(approverPisCode))
            throw new RuntimeException("Can't Process");
    }

    @Override
    public List<MemoResponsePojo> getSuggestionsForward() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        List<MemoResponsePojo> memoResponsePojos = memoMapper.getAllMemoForwardSuggestion(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());

        for (MemoResponsePojo memoResponsePojo : memoResponsePojos) {
            memoResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(memoResponsePojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (MemoApprovalPojo memoApprovalPojo : memoResponsePojo.getSuggestion()) {
                memoApprovalPojo.setCreatedDateNp(dateConverter.convertAdToBs(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                memoApprovalPojo.setApprover(userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getApproverPisCode()));
                memoApprovalPojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getSenderPisCode()));
            }
        }

        return memoResponsePojos;
    }

    @Override
    public List<MemoResponsePojo> getSuggestions() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        List<MemoResponsePojo> memoResponsePojos = memoMapper.getAllMemoSuggestion(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());

        for (MemoResponsePojo memoResponsePojo : memoResponsePojos) {
            memoResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(memoResponsePojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (MemoApprovalPojo memoApprovalPojo : memoResponsePojo.getSuggestion()) {
                memoApprovalPojo.setCreatedDateNp(dateConverter.convertAdToBs(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                memoApprovalPojo.setApprover(userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getApproverPisCode()));
                memoApprovalPojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getSenderPisCode()));
            }
        }

        return memoResponsePojos;
    }

    @Override
    public String getVerificationLink(Long id) {
        String link = null;
        MemoResponsePojo memoResponsePojo = getMemoById(id);

        if (memoResponsePojo != null && memoResponsePojo.getTemplate() != null) {
            byte[] fileConverter = convertHtlToFileProxy.getFileConverter(new FileConverterPojo(memoResponsePojo.getTemplate()));
            MultipartFile multipartFile = new BASE64DecodedMultipartFile(fileConverter, "tempName" + memoResponsePojo.getId() + ".pdf");
            link = documentServiceData.temporaryDocument(convertToMultiValueMap(multipartFile, "tempName" + memoResponsePojo.getId()));
        }

        return link;
    }

    @Override
    public void deleteContent(Long contentId) {
        Optional<MemoContent> content = memoContentRepo.findById(contentId);

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        if (!content.isPresent())
            throw new RuntimeException("Content does not exist");

        MemoContent getContent = content.get();

        if (getContent.getPisCode() != null && !listPisCodes.contains(getContent.getPisCode()))
            throw new RuntimeException("Invalid User");

        memoContentRepo.softDelete(contentId);
    }

    @Override
    public Long setImportantFlag(Long id, boolean value) {
        Memo memo = memoRepo.findById(id).orElseThrow(() -> new CustomException("Letter Not Found"));
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        List<MemoApproval> approvals = new ArrayList<>();
        List<MemoSuggestion> suggestions = new ArrayList<>();

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();

        listPisCodes.add(tokenPisCode);
        if (employeePojo.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()));
        }

        if (employeePojo != null && employeePojo.getSectionId() != null) {

//            if (memo.getIsImportant() != null && memo.getIsImportant() && !value) {
//                memo.setIsImportant(false);
//                memoRepo.save(memo);
//            } else

            if (listPisCodes.contains(memo.getPisCode())
                    && memo.getSectionCode().equals(employeePojo.getSectionId())) {
                memo.setLastModifiedDateImp(new Timestamp(new Date().getTime()));
                memo.setIsImportant(memo.getIsImportant() != null ? !memo.getIsImportant() : Boolean.TRUE);
                memoRepo.save(memo);
            }

            approvals = memoApprovalRepo.getAllMemoApprovalsByPisAndSectionCode(memo.getId(), tokenProcessorService.getPisCode(), employeePojo.getSectionId());
            suggestions = memoSuggestionRepo.getAllMemoSuggestionsByPisAndSectionCode(memo.getId(), tokenProcessorService.getPisCode(), employeePojo.getSectionId());
        }

        if (approvals != null && !approvals.isEmpty()) {
            approvals.forEach(
                    x -> x.setIsImportant(value)
            );
            memoApprovalRepo.saveAll(approvals);
        }

        if (suggestions != null && !suggestions.isEmpty()) {
            suggestions.forEach(
                    x -> x.setIsImportant(value)
            );
            memoSuggestionRepo.saveAll(suggestions);
        }

        return memo.getId();
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

    @Override
    public void externalApproval(ApprovalPojo data) {
        Memo memo = this.findById(data.getId());
        MemoApproval memoApproval = memoApprovalMapper.findActiveSuggestionApproval(memo.getId());
        MemoSuggestion memoSuggestion = memoSuggestionMapper.findActive(memo.getId());
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        EmployeePojo senderDetail = userMgmtServiceData.getEmployeeDetail(memoSuggestion.getInitialSender());

        memoApproval.setStatus(Status.F);
        memoApproval.setActive(false);
        memoApproval.setSuggestion(false);
        memoApprovalMapper.updateById(
                memoApproval
        );

        memoSuggestion.setStatus(Status.F);
        memoSuggestion.setActive(false);
        memoSuggestionMapper.updateById(
                memoSuggestion
        );

        memoApprovalRepo.save(
                new MemoApproval().builder()
                        .status(Status.P)
                        .approverPisCode(memoSuggestion.getInitialSender())
                        .approverOfficeCode(senderDetail != null ? senderDetail.getOffice() != null ? senderDetail.getOffice().getCode() : null : null)
                        .approverDesignationCode(senderDetail != null ? senderDetail.getFunctionalDesignationCode() : null)
                        .approverSectionCode(senderDetail != null ? senderDetail.getSection() != null ? senderDetail.getSection().getId().toString() : null : null)
                        .senderPisCode(tokenProcessorService.getPisCode())
                        .senderOfficeCode(tokenProcessorService.getOfficeCode())
                        .senderSectionCode(memoSuggestion.getApproverSectionCode())
                        .senderDesignationCode(memoSuggestion.getApproverDesignationCode())
                        .reverted(false)
                        .isExternal(false)
                        .suggestion(false)
                        .isBack(true)
                        .remarks(data.getRejectRemarks())
                        .log(memoSuggestion.getLog() + 1)
                        .isSeen(false)
                        .memo(memo)
                        .build()
        );

        memoContentRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());
        memoDocumentDetailsRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());
        memoReferenceRepo.updateEditableByMemoId(memo.getId(), tokenProcessorService.getPisCode());

        notificationService.notificationProducer(
                NotificationPojo.builder()
                        .moduleId(memo.getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(tokenProcessorService.getPisCode())
                        .receiver(memoSuggestion.getInitialSender())
                        .subject(customMessageSource.getNepali("memo"))
                        .detail(customMessageSource.getNepali("memo.comment", employeePojo.getNameNp(), memo.getSubject()))
                        .pushNotification(true)
                        .received(true)
                        .build()
        );
    }

    @Override
    public Memo findById(Long aLong) {
        Memo memo = super.findById(aLong);
        if (memo == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("memo")));
        return memo;
    }

    private void setUserId(ApprovalPojo data) {
        Long uuid = tokenProcessorService.getUserId();
        data.setUserId(uuid);
    }

    @Override
    public List<MemoResponsePojo> getMemoReceiverInProgress(String receiverPisCode) {
        List<MemoResponsePojo> memos = memoMapper.getMemoReceiverInProgress(receiverPisCode);

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(memo.getOfficeCode());
            memo.setOfficeName(officePojo.getNameEn());
            memo.setOfficeNameNp(officePojo.getNameNp());
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }

        return memos;
    }

    @Override
    public List<MemoResponsePojo> getMemoReceiverFinalized(String receiverPisCode) {
        List<MemoResponsePojo> memos = memoMapper.getMemoReceiverFinalized(receiverPisCode);

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(memo.getOfficeCode());
            memo.setOfficeName(officePojo.getNameEn());
            memo.setOfficeNameNp(officePojo.getNameNp());
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }

        return memos;
    }

    @Override
    public List<MemoResponsePojo> getAllMemoInProgress() {
        List<MemoResponsePojo> memos = memoMapper.getAllMemoInProgress(tokenProcessorService.getPisCode());

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }
        return memos;
    }

    @Override
    public List<MemoResponsePojo> getAllMemoFinalized() {
        List<MemoResponsePojo> memos = memoMapper.getAllMemoFinalized(tokenProcessorService.getPisCode());

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }
        return memos;
    }

    @Override
    public ArrayList<MemoResponsePojo> getDrafts() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        ArrayList<MemoResponsePojo> memos = memoMapper.getDrafts(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }
        return memos;
    }

    @Override
    public List<MemoResponsePojo> getAllMemo() {
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
        List<MemoResponsePojo> memos;

        if (officeHeadPojo.getPisCode().equals(tokenProcessorService.getPisCode()))
            memos = memoMapper.getAllMemoOffice(tokenProcessorService.getOfficeCode());
        else {
            EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

            if (employeePojo.getSection() == null)
                throw new RuntimeException("Not Involved In any Section");
            memos = memoMapper.getAllMemo(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());
        }

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }
        return memos;
    }

    @Override
    public List<MemoResponsePojo> getAllMemoForwarded() {
        List<MemoResponsePojo> memos = memoMapper.getAllMemoForwarded(tokenProcessorService.getPisCode());

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }
        return memos;
    }

    @Override
    public List<MemoResponsePojo> getAllMemoForApproval() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();

        listPisCodes.add(tokenPisCode);
        if (employeePojo.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, employeePojo.getSectionId()));
        }

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        List<MemoResponsePojo> memoResponsePojos = memoMapper.getAllMemoForApproval(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());

        for (MemoResponsePojo memoResponsePojo : memoResponsePojos) {
            memoResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(memoResponsePojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(memoResponsePojo.getOfficeCode());
            memoResponsePojo.setOfficeName(officePojo.getNameEn());
            memoResponsePojo.setOfficeNameNp(officePojo.getNameNp());

            if (memoResponsePojo.getApproval() != null && !memoResponsePojo.getApproval().isEmpty()) {
                MemoApproval memoApproval = memoApprovalMapper.findActive(memoResponsePojo.getId());
                if (memoApproval.isActive() && listPisCodes.contains(memoApproval.getApproverPisCode()))
                    memoResponsePojo.setForwarded(false);
            } else
                memoResponsePojo.setForwarded(false);

            for (MemoApprovalPojo memoApprovalPojo : memoResponsePojo.getApproval()) {
                memoApprovalPojo.setCreatedDateNp(dateConverter.convertAdToBs(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                memoApprovalPojo.setApprover(userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getApproverPisCode()));
                memoApprovalPojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getSenderPisCode()));
            }
        }

        return memoResponsePojos;
    }

    @Override
    public List<MemoResponsePojo> getAllMemoForApprovalForwarded() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        List<MemoResponsePojo> memoResponsePojos = memoMapper.getAllMemoForApprovalForwarded(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());

        for (MemoResponsePojo memoResponsePojo : memoResponsePojos) {
            memoResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(memoResponsePojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (MemoApprovalPojo memoApprovalPojo : memoResponsePojo.getApproval()) {
                memoApprovalPojo.setCreatedDateNp(dateConverter.convertAdToBs(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                memoApprovalPojo.setApprover(userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getApproverPisCode()));
                memoApprovalPojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getSenderPisCode()));
            }
        }

        return memoResponsePojos;
    }

    @Override
    public MemoResponsePojo getMemoById(Long id) {

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user in involved in any section or not
        checkUserSection(tokenUser);

        String tokenUserSection = tokenUser.getSectionId();

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        MemoResponsePojo memo = memoMapper.getMemoById(id);

        if (!memo.getIsDraft()) {
            log.info("memo id: " + id + " and section: " + tokenUser.getSectionId());
            if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
                List<String> involvedOffices = memoMapper.getInvolvedOffices(id);
                log.info("Involved offices in memo: " + involvedOffices);
                if (!involvedOffices.contains(tokenProcessorService.getOfficeCode()))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
            } else {
                String strPisCodes = dispatchLetterServiceImpl.convertListToString(listPisCodes);
                log.info("previousPisCodes: " + strPisCodes);
                if (!memoMapper.checkInvolvedTippani(id, tokenUser.getSectionId(), strPisCodes))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
            }
        } else {
            if (!draftShareRepo.checkPermissionMemoDraft(id, tokenPisCode, tokenUserSection)
                    && !(memo.getPisCode().equals(tokenPisCode) && memo.getSectionCode().equals(tokenUserSection)))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
        }

        memo.setPreviousPisCodes(listPisCodes);

        //get if it is referenced from memo or not
        memo.setMemoReferencedFrom(memoMapper.getMemoReferencedFrom(id));

        memo.setSuggestion(memo.getSuggestion().stream().sorted(Comparator.comparingLong(MemoApprovalPojo::getId).reversed()).collect(Collectors.toList()));
        memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
        memo.setCreatedTimeNp(dateConverter.convertBSToDevnagari(memo.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
        OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(memo.getOfficeCode());

        String officeHeadPisCode = "";
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
        if (officeHeadPojo != null && officeHeadPojo.getPisCode() != null)
            officeHeadPisCode = officeHeadPojo.getPisCode();

        if (officePojo != null) {
            memo.setOfficeName(officePojo.getNameEn());
            memo.setOfficeNameNp(officePojo.getNameNp());
        }

        EmployeeMinimalPojo creatorDetails = userMgmtServiceData.getEmployeeDetailMinimal(memo.getPisCode());

        if (memo.getDelegatedId() != null) {
            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(memo.getDelegatedId());

            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                EmployeeMinimalPojo delegatedUser = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode());
                memo.setEmployee(delegatedUser);

                if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment()) {
                    memo.setIsReassignment(Boolean.TRUE);
                    if (delegationResponsePojo.getFromSection() != null)
                        memo.setReassignmentSection(delegationResponsePojo.getFromSection());
                    memo.setEmployeeDesignationNameNp(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameNp() : "");
                    memo.setEmployeeDesignationNameEn(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameEn() : "");

                } else {
                    memo.setIsDelegated(Boolean.TRUE);
                    // EmployeeMinimalPojo delegatedUserFrom = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getFromEmployee().getCode());
                    memo.setEmployeeDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                    memo.setEmployeeDesignationNameEn(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameEn() : "");
                }
                if (delegationResponsePojo.getFromSection() != null) {
                    SectionPojo delegatedUserSection = new SectionPojo();
                    delegatedUserSection.setCode(delegationResponsePojo.getFromSection().getCode());
                    delegatedUserSection.setNameNp(delegationResponsePojo.getFromSection().getNameNp());
                    delegatedUserSection.setNameEn(delegationResponsePojo.getFromSection().getNameEn());
                    memo.setEmployeeSection(delegatedUserSection);
                }
            }
        } else {
            memo.setEmployee(creatorDetails);
            if (memo.getSectionCode() != null) {
                try {
                    memo.setEmployeeSection(userMgmtServiceData.getSectionDetail(Long.parseLong(memo.getSectionCode())));
                } catch (Exception e) {
                   throw new RuntimeException("can not parse section: "+memo.getSectionCode());
                }

                DetailPojo designationPojo = userMgmtServiceData.getDesignationDetailByCode(memo.getDesignationCode());
                memo.setEmployeeDesignationNameNp(designationPojo != null ? designationPojo.getNameNp() : "");
                memo.setEmployeeDesignationNameEn(designationPojo != null ? designationPojo.getNameEn() : "");
            }
        }

        if (memo.getSignatureIsActive() != null && memo.getSignatureIsActive()) {
            Boolean isHashed = memo.getHash_content() == null ? Boolean.FALSE : Boolean.TRUE;
            VerificationInformation verificationInformation = verifySignatureService.verify(memo.getContent(), memo.getSignature(), isHashed);
            memo.setVerificationInformation(verificationInformation);

            if (verificationInformation != null) {

                Boolean isVerified = verificationInformation.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                //check for alternate result ie. if the letter is verified or not verified record already exists or not

                Boolean result = signatureVerificationLogRepository.
                        existsMemoLog(
                                SignatureType.MEMO.toString(),
                                memo.getId());

                if (result == null || result != isVerified)
                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                            .memoId(memo.getId())
                            .signatureType(SignatureType.MEMO)
                            .isVerified(isVerified)
                            .signatureBy(memo.getEmployee() != null ? memo.getEmployee().getPisCode() : null)
                            .individualStatus(memo.getStatus().toString())
                            .build());
            }
        }

        if (memo.getForwards() != null && !memo.getForwards().isEmpty()) {
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(data.getReceiverPisCode() != null ? userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()) : null);
                data.setSender(data.getSenderPisCode() != null ? userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()) : null);
                data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                data.setCreatedTimeNp(dateConverter.convertBSToDevnagari(data.getCreatedDate().toLocalDateTime().toLocalTime().toString()));

                for (ReceivedLetterMessageRequestPojo comment : data.getComments()) {
                    comment.setCreatedDateNp(dateConverter.convertAdToBs(comment.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                }
            }
        }

        boolean isSuggestionUser = false;
        List<String> suggestionUsers = null;
        if (memoSuggestionMapper.isBack(memo.getId())) {
            suggestionUsers = memoSuggestionMapper.findSuggestionUsersWithBack(memo.getId());
        } else {
            suggestionUsers = memoSuggestionMapper.findSuggestionUsers(memo.getId());
        }
        if (suggestionUsers != null && !suggestionUsers.isEmpty() && suggestionUsers.contains(tokenProcessorService.getPisCode()))
            isSuggestionUser = true;

        if (isSuggestionUser)
            if (memo.getStatus() != Status.A)
                memo.setContents(memoSuggestionMapper.getAllContentTillDate(memo.getId(), tokenProcessorService.getPisCode()));


        MemoApproval memoApp = memoApprovalMapper.findActiveSuggestionApproval(memo.getId());
        List<String> involvedUsers = memoApprovalMapper.getInvolvedUsers(memo.getId());

        if (memoApp != null
                && memoApp.getSuggestion() != null
                && Boolean.TRUE.equals(memoApp.getSuggestion())
                && memoApp.getLastModifiedDate() != null
                && involvedUsers != null
                && involvedUsers.contains(tokenProcessorService.getPisCode())
                && !suggestionUsers.contains(tokenProcessorService.getPisCode())
        )
            memo.setContents(memoApprovalMapper.getAllContentTillDate(memo.getId(), tokenProcessorService.getPisCode(), memoApp.getLastModifiedDate()));

        if (memo.getContents() != null && !memo.getContents().isEmpty()) {
            memo.setContents(this.getActiveContents(memo.getContents(), memo.getId()));
            String finalOfficeHeadPisCode = officeHeadPisCode;

            memo.getContents().forEach(pojo -> {
                        pojo.setCreatedDateNp(dateConverter.convertAdToBs(pojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));

                        EmployeeMinimalPojo contentCreator = null;
                        if (pojo.getPisCode() != null)
                            contentCreator = userMgmtServiceData.getEmployeeDetailMinimal(pojo.getPisCode());

                        pojo.setIsDelegated(false);
                        if (pojo.getDelegatedId() != null) {

                            if (pojo.getPisCode().equals(finalOfficeHeadPisCode))
                                pojo.setIsDelegated(true);

                            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(pojo.getDelegatedId());
                            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                                EmployeeMinimalPojo delegatedContentUser = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode());
                                pojo.setEmployee(delegatedContentUser);
                                //check the delegated user is reassignment and set value accordingly

                                if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment()) {
                                    pojo.setIsReassignment(Boolean.TRUE);
                                    if (delegationResponsePojo.getFromSection() != null)
                                        pojo.setReassignmentSection(delegationResponsePojo.getFromSection());
                                    pojo.setEmployeeDesignationNameNp(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameNp() : "");
                                    pojo.setEmployeeDesignationNameEn(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameEn() : "");

                                } else {
                                    //EmployeeMinimalPojo delegatedContentUserFrom = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getFromEmployee().getCode());

                                    pojo.setIsDelegated(Boolean.TRUE);
                                    pojo.setEmployeeDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                                    pojo.setEmployeeDesignationNameEn(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameEn() : "");

                                }
                                if (delegationResponsePojo.getFromSection() != null) {
                                    SectionPojo delegatedUserSection = new SectionPojo();
                                    delegatedUserSection.setCode(delegationResponsePojo.getFromSection().getCode());
                                    delegatedUserSection.setNameNp(delegationResponsePojo.getFromSection().getNameNp());
                                    delegatedUserSection.setNameEn(delegationResponsePojo.getFromSection().getNameEn());
                                    pojo.setEmployeeSection(delegatedUserSection);
                                }
                            }
                        } else {
                            pojo.setEmployee(contentCreator);
                            if (pojo.getSectionCode() != null) {
                                try {
                                    pojo.setEmployeeSection(userMgmtServiceData.getSectionDetail(Long.parseLong(pojo.getSectionCode())));
                                }
                                catch(Exception e){
                                    throw new RuntimeException("can not parse section: "+pojo.getSectionCode());
                                }

                                DetailPojo designationPojo = userMgmtServiceData.getDesignationDetailByCode(pojo.getDesignationCode());

                                pojo.setEmployeeDesignationNameNp(designationPojo != null ? designationPojo.getNameNp() : "");
                                pojo.setEmployeeDesignationNameEn(designationPojo != null ? designationPojo.getNameEn() : "");

                            }
                        }


                        OfficeMinimalPojo officePojo1 = userMgmtServiceData.getOfficeDetailMinimal(pojo.getOfficeCode());
                        pojo.setOfficeName(officePojo1 != null ? officePojo1.getNameEn() : null);
                        pojo.setOfficeNameNp(officePojo1 != null ? officePojo1.getNameNp() : null);

                        if (pojo.getSignatureIsActive() != null && pojo.getSignatureIsActive()) {

                            Boolean isHashedContent = pojo.getHashContent() == null ? Boolean.FALSE : Boolean.TRUE;
                            VerificationInformation verificationInformation = verifySignatureService.verify(pojo.getContent(), pojo.getSignature(), isHashedContent);
                            pojo.setVerificationInformation(verificationInformation);

                            if (verificationInformation != null) {

                                Boolean isVerified = verificationInformation.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                                //check for alternate result ie. if the memo content is verified or not verified record already exists or not
                                Boolean result = signatureVerificationLogRepository.
                                        existsMemoContentLog(
                                                SignatureType.MEMO_CONTENT.toString(),
                                                memo.getId(),
                                                pojo.getId());

                                if (result == null || result != isVerified)
                                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                            .memoId(memo.getId())
                                            .memoContentId(pojo.getId())
                                            .signatureType(SignatureType.MEMO_CONTENT)
                                            .isVerified(isVerified)
                                            .signatureBy(pojo.getEmployee() != null ? pojo.getEmployee().getPisCode() : null)
                                            .individualStatus(memo.getStatus().toString())
                                            .build());
                            }

                        }
                    }
            );
        }

        if (memo.getApproval() != null && !memo.getApproval().isEmpty()) {
            for (MemoApprovalPojo memoApprovalPojo : memo.getApproval()) {
                memoApprovalPojo.setCreatedDateNp(dateConverter.convertAdToBs(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                memoApprovalPojo.setCreatedTimeNp(dateConverter.convertBSToDevnagari(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
                memoApprovalPojo.setApprover(memoApprovalPojo.getApproverPisCode() != null ? userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getApproverPisCode()) : null);

                EmployeeMinimalPojo senderMinimalDetails = null;

                if (memoApprovalPojo.getSenderPisCode() != null)
                    senderMinimalDetails = userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getSenderPisCode());

                memoApprovalPojo.setIsDelegated(false);
                if (memoApprovalPojo.getDelegatedId() != null) {

//                    if (memoApprovalPojo.getSenderPisCode() != null && memoApprovalPojo.getSenderPisCode().equals(officeHeadPisCode))
//                        memoApprovalPojo.setIsDelegated(true);

                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(memoApprovalPojo.getDelegatedId());
                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {

                        //check the delegated user is reassignment and set value accordingly
                        if (delegationResponsePojo.getIsReassignment() != null
                                && delegationResponsePojo.getIsReassignment()) {
                            memoApprovalPojo.setIsReassignment(Boolean.TRUE);
                            if (delegationResponsePojo.getFromSection() != null)
                                memoApprovalPojo.setReassignmentSection(delegationResponsePojo.getFromSection());

                            memoApprovalPojo.setSenderDesignationNameNp(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameNp() : "");
                        } else {
                            memoApprovalPojo.setIsDelegated(true);
                            memoApprovalPojo.setSenderDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                        }
                        memoApprovalPojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                    }
                } else
                    memoApprovalPojo.setSender(senderMinimalDetails);

                OfficeMinimalPojo approvalOffice = userMgmtServiceData.getOfficeDetailMinimal(memoApprovalPojo.getOfficeCode());
                if (approvalOffice != null)
                    memoApprovalPojo.setOfficeNameNp(approvalOffice.getNameNp());

                if (memoApprovalPojo.getOfficeCode() == null || memoApprovalPojo.getOfficeCode().equals(memo.getOfficeCode())) {
                    memoApprovalPojo.setIsExternal(false);
                }

                if (memoApprovalPojo.getIsSeen() != null && !memoApprovalPojo.getIsSeen() && memoApprovalPojo.getApproverPisCode() != null && memoApprovalPojo.getApproverPisCode().equals(tokenProcessorService.getPisCode())) {
                    notificationService.notificationProducer(
                            NotificationPojo.builder()
                                    .moduleId(memo.getId())
                                    .module(MODULE_APPROVAL_KEY)
                                    .sender(tokenProcessorService.getPisCode())
                                    .receiver(memoApprovalPojo.getSenderPisCode())
                                    .subject(customMessageSource.getNepali("memo"))
                                    .detail(customMessageSource.getNepali("memo.view", memoApprovalPojo.getApprover().getEmployeeNameNp(), memo.getSubject()))
                                    .pushNotification(true)
                                    .received(false)
                                    .build()
                    );
                    memoApprovalRepo.setSeen(memo.getId(), tokenProcessorService.getPisCode());
                }
            }
        }

        if (memo.getSuggestion() != null && !memo.getSuggestion().isEmpty()) {
            for (MemoApprovalPojo memoApprovalPojo : memo.getSuggestion()) {
                memoApprovalPojo.setCreatedDateNp(dateConverter.convertAdToBs(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                memoApprovalPojo.setCreatedTimeNp(dateConverter.convertBSToDevnagari(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
                memoApprovalPojo.setApprover(memoApprovalPojo.getApproverPisCode() != null ? userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getApproverPisCode()) : null);

                EmployeeMinimalPojo senderMinimalDetails = null;
                if (memoApprovalPojo.getSenderPisCode() != null)
                    senderMinimalDetails = userMgmtServiceData.getEmployeeDetailMinimal(memoApprovalPojo.getSenderPisCode());

                memoApprovalPojo.setIsDelegated(false);
                if (memoApprovalPojo.getDelegatedId() != null) {

//                    if (memoApprovalPojo.getSenderPisCode().equals(officeHeadPisCode))
//                        memoApprovalPojo.setIsDelegated(true);

                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(memoApprovalPojo.getDelegatedId());
                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {

                        //check the delegated user is reassignment and set value accordingly
                        if (delegationResponsePojo.getIsReassignment() != null
                                && delegationResponsePojo.getIsReassignment()) {
                            memoApprovalPojo.setIsReassignment(Boolean.TRUE);
                            if (delegationResponsePojo.getFromSection() != null)
                                memoApprovalPojo.setReassignmentSection(delegationResponsePojo.getFromSection());
                            memoApprovalPojo.setSenderDesignationNameNp(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameNp() : "");
                        } else {
                            memoApprovalPojo.setIsDelegated(true);
                            memoApprovalPojo.setSenderDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                        }
                        memoApprovalPojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                    }
                } else
                    memoApprovalPojo.setSender(senderMinimalDetails);

                OfficeMinimalPojo suggestionOffice = userMgmtServiceData.getOfficeDetailMinimal(memoApprovalPojo.getOfficeCode());
                if (suggestionOffice != null)
                    memoApprovalPojo.setOfficeNameNp(suggestionOffice.getNameNp());

                if (memoApprovalPojo.getStatus().equals(Status.P)) {
                    memoApprovalPojo.setStatus(Status.SG);
                }

                if (memoApprovalPojo.getIsSeen() != null && !memoApprovalPojo.getIsSeen() && memoApprovalPojo.getApproverPisCode() != null && memoApprovalPojo.getApproverPisCode().equals(tokenProcessorService.getPisCode())) {
                    notificationService.notificationProducer(
                            NotificationPojo.builder()
                                    .moduleId(memo.getId())
                                    .module(MODULE_APPROVAL_KEY)
                                    .sender(tokenProcessorService.getPisCode())
                                    .receiver(memoApprovalPojo.getSenderPisCode())
                                    .subject(customMessageSource.getNepali("memo"))
                                    .detail(customMessageSource.getNepali("memo.view", memoApprovalPojo.getApprover().getEmployeeNameNp(), memo.getSubject()))
                                    .pushNotification(true)
                                    .received(false)
                                    .build()
                    );
                    memoSuggestionRepo.setSeen(memo.getId(), tokenProcessorService.getPisCode());
                }
            }
        }

        if (memo.getDocument() != null && !memo.getDocument().isEmpty()) {
            memo.setDocument(this.getActiveDocuments(memo.getDocument()));
            if (memo.getDocument() != null && !memo.getDocument().isEmpty()) {
                for (DocumentPojo documentPojo : memo.getDocument()) {

                    EmployeeMinimalPojo creatorDetailsMinimal = userMgmtServiceData.getEmployeeDetailMinimal(documentPojo.getPisCode());

                    documentPojo.setIsDelegated(false);
                    if (documentPojo.getDelegatedId() != null) {

                        if (documentPojo.getPisCode().equals(officeHeadPisCode))
                            documentPojo.setIsDelegated(true);

                        DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(documentPojo.getDelegatedId());
                        if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {

                            //check the delegated user is reassignment and set value accordingly
                            if (delegationResponsePojo.getIsReassignment() != null
                                    && delegationResponsePojo.getIsReassignment()
                                    && delegationResponsePojo.getFromSection() != null) {
                                documentPojo.setIsReassignment(Boolean.TRUE);
                                documentPojo.setReassignmentSection(delegationResponsePojo.getFromSection());
                            }
                            documentPojo.setCreator(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                            documentPojo.setCreatorDesignationNameNp(creatorDetailsMinimal != null ? creatorDetailsMinimal.getFunctionalDesignation() != null ? creatorDetailsMinimal.getFunctionalDesignation().getNameN() : null : null);
                        }
                    } else
                        documentPojo.setCreator(creatorDetailsMinimal);
                }
            }
        }

        if (memo.getApproval() != null && !memo.getApproval().isEmpty()) {
            MemoApproval memoApproval = memoApprovalMapper.findActive(memo.getId());
            if (memoApproval != null && memoApproval.isActive() && memoApproval.getApproverPisCode() != null && listPisCodes.contains(memoApproval.getApproverPisCode()) && !memoApproval.getSuggestion())
                memo.setForwarded(false);
        } else
            memo.setForwarded(false);

        if (memo.getSuggestion() != null && !memo.getSuggestion().isEmpty()) {
            MemoSuggestion memoSuggestion = memoSuggestionMapper.findActive(memo.getId());
            if (memoSuggestion != null && memoSuggestion.isActive() && memoSuggestion.getApproverPisCode() != null && listPisCodes.contains(memoSuggestion.getApproverPisCode()) && memoSuggestion.getStatus().equals(Status.P))
                memo.setIsSuggestion(false);
        }

        Map<String, String> map = this.getTemplate(memo);

        memo.setTemplate(map.get("template"));
        memo.setTippaniHeader(map.get("header"));

        OfficePojo detail = userMgmtServiceData.getOfficeDetail(memo.getOfficeCode());
        OfficePojo parentOffice = userMgmtServiceData.getOfficeDetail(detail != null ? detail.getParentCode() : null);

        memo.setTemplateContent(new TippaniDetail().builder()
                .department(memo.getOfficeNameNp())
                .organization("नेपाल सरकार")
                .ministry(detail != null ? detail.getNameNp() : "")
                .department(parentOffice != null ? parentOffice.getNameNp() : "")
                .address_top(detail != null ? detail.getAddressNp() : "")
                .build()
        );

//        List<GenericReferenceDto> referenceDispatch = memoReferenceMapper.getDispatchReferences(memo.getId());
//        if (referenceDispatch != null && !referenceDispatch.isEmpty()) {
//            List<DispatchLetterDTO> dispatchLetterDTOS = new ArrayList<>();
//            for (GenericReferenceDto dispatch : referenceDispatch) {
//                if (dispatch != null && dispatch.getId() != null) {
//                    DispatchLetterDTO letter = dispatchLetterMapper.getDispatchLetterDetailById(dispatch.getId());
//                    letter.setReferenceId(dispatch.getReferenceId());
//                    letter.setReferenceIsEditable(dispatch.getReferenceIsEditable());
//                    letter.setReferenceCreator(dispatch.getPisCode() != null ? userMgmtServiceData.getEmployeeDetailMinimal(dispatch.getPisCode()) : null);
//                    dispatchLetterDTOS.add(letter);
//                }
//            }
//            if (!dispatchLetterDTOS.isEmpty()) {
//                for (DispatchLetterDTO data : dispatchLetterDTOS) {
//                    EmployeeMinimalPojo employeeMinimal = userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode());
//                    if (employeeMinimal != null) {
//                        data.setSenderName(employeeMinimal.getEmployeeNameEn());
//                        data.setSenderNameNp(employeeMinimal.getEmployeeNameNp());
//                    }
//                }
//            }
//            memo.setReferenceDispatch(dispatchLetterDTOS);
//        }
//
//        List<GenericReferenceDto> referencedDarta = memoReferenceMapper.getMemoDartaReference(memo.getId());
//        if (referencedDarta != null && !referencedDarta.isEmpty()) {
//            List<ReceivedLetterResponsePojo> receivedLetterResponsePojos = new ArrayList<>();
//            for (GenericReferenceDto received : referencedDarta) {
//                if (received != null && received.getId() != null) {
//                    ReceivedLetterResponsePojo letter = receivedLetterMapper.getReceivedLetter(received.getId());
//                    letter.setReferenceId(received.getReferenceId());
//                    letter.setReferenceIsEditable(received.getReferenceIsEditable());
//                    letter.setReferenceCreator(received.getPisCode() != null ? userMgmtServiceData.getEmployeeDetailMinimal(received.getPisCode()) : null);
//                    receivedLetterResponsePojos.add(letter);
//                }
//            }
//            if (!receivedLetterResponsePojos.isEmpty()) {
//                for (ReceivedLetterResponsePojo data : receivedLetterResponsePojos) {
//                    data.setEmployee(data.getPisCode() != null ? userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()) : null);
//                    data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
//                    data.setCreatedTimeNp(dateConverter.convertBSToDevnagari(data.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
//                }
//            }
//            memo.setReferencedReceived(receivedLetterResponsePojos);
//        }

        return memo;
    }

    @Override
    public MemoReferenceResponsePojo getMemoByIdForReference(Long memoId, Long referenceMemoId, int type) {

        if (type == 1) {
//            if (memoMapper.checkForValidMemoReference(memoId, referenceMemoId) && memoId != referenceMemoId)
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Memo " + memoId + " not referenced from " + referenceMemoId);

            //for work on transferred employee letter
            Set<String> listPisCodes = new HashSet<>();
            String tokenPisCode = tokenProcessorService.getPisCode();
            EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

            //check user in involved in any section or not
            checkUserSection(tokenUser);

            listPisCodes.add(tokenPisCode);
            if (tokenUser.getSectionId() != null) {
                if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                    listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
            }

            log.info("memo id: " + memoId + " and section: " + tokenUser.getSectionId());
            if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
                List<String> involvedOffices = memoMapper.getInvolvedOffices(memoId);
                log.info("Involved offices in memo: " + involvedOffices);
                if (!involvedOffices.contains(tokenProcessorService.getOfficeCode()))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
            } else {
                //List<String> involvedUsers = memoMapper.getInvolvedUsersTippani(id, tokenUser.getSectionId());
                String strPisCodes = dispatchLetterServiceImpl.convertListToString(listPisCodes);
                log.info("previousPisCodes: " + strPisCodes);
                //if (!listPisCodes.stream().anyMatch(pisCode -> involvedUsers.contains(pisCode)))
                if (!memoMapper.checkInvolvedTippani(memoId, tokenUser.getSectionId(), strPisCodes))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
            }
        }

        MemoResponsePojo memo = memoMapper.getMemoById(memoId);

        if (memo.getStatus() != Status.A)
            return null;

        memo.setSuggestion(memo.getSuggestion().stream().sorted(Comparator.comparingLong(MemoApprovalPojo::getId).reversed()).collect(Collectors.toList()));
        memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
        memo.setCreatedTimeNp(dateConverter.convertBSToDevnagari(memo.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
        OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(memo.getOfficeCode());

        String officeHeadPisCode = "";
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
        if (officeHeadPojo != null && officeHeadPojo.getPisCode() != null)
            officeHeadPisCode = officeHeadPojo.getPisCode();

        if (officePojo != null) {
            memo.setOfficeName(officePojo.getNameEn());
            memo.setOfficeNameNp(officePojo.getNameNp());
        }

        EmployeeMinimalPojo creatorDetails = userMgmtServiceData.getEmployeeDetailMinimal(memo.getPisCode());

        if (memo.getDelegatedId() != null) {
            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(memo.getDelegatedId());

            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                EmployeeMinimalPojo delegatedUser = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode());
                memo.setEmployee(delegatedUser);

                if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment()) {
                    memo.setIsReassignment(Boolean.TRUE);
                    if (delegationResponsePojo.getFromSection() != null)
                        memo.setReassignmentSection(delegationResponsePojo.getFromSection());
                    memo.setEmployeeDesignationNameNp(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameNp() : "");
                    memo.setEmployeeDesignationNameEn(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameEn() : "");

                } else {
                    memo.setIsDelegated(Boolean.TRUE);
                    // EmployeeMinimalPojo delegatedUserFrom = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getFromEmployee().getCode());
                    memo.setEmployeeDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                    memo.setEmployeeDesignationNameEn(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameEn() : "");
                }
                if (delegationResponsePojo.getFromSection() != null) {
                    SectionPojo delegatedUserSection = new SectionPojo();
                    delegatedUserSection.setCode(delegationResponsePojo.getFromSection().getCode());
                    delegatedUserSection.setNameNp(delegationResponsePojo.getFromSection().getNameNp());
                    delegatedUserSection.setNameEn(delegationResponsePojo.getFromSection().getNameEn());
                    memo.setEmployeeSection(delegatedUserSection);
                }
            }
        } else {
            memo.setEmployee(creatorDetails);
            if (memo.getSectionCode() != null) {
                try {
                    memo.setEmployeeSection(userMgmtServiceData.getSectionDetail(Long.parseLong(memo.getSectionCode())));
                } catch (Exception e) {
                    throw new RuntimeException("can not parse section: "+memo.getSectionCode());
                }

                DetailPojo designationPojo = userMgmtServiceData.getDesignationDetailByCode(memo.getDesignationCode());
                memo.setEmployeeDesignationNameNp(designationPojo != null ? designationPojo.getNameNp() : "");
                memo.setEmployeeDesignationNameEn(designationPojo != null ? designationPojo.getNameEn() : "");
            }

        }

        if (memo.getSignatureIsActive() != null && memo.getSignatureIsActive()) {
            Boolean isHashed = memo.getHash_content() == null ? Boolean.FALSE : Boolean.TRUE;
            VerificationInformation verificationInformation = verifySignatureService.verify(memo.getContent(), memo.getSignature(), isHashed);
            memo.setVerificationInformation(verificationInformation);

            if (verificationInformation != null) {

                Boolean isVerified = verificationInformation.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                //check for alternate result ie. if the letter is verified or not verified record already exists or not
                Boolean result = signatureVerificationLogRepository.
                        existsMemoLog(
                                SignatureType.MEMO.toString(),
                                memo.getId());

                if (result == null || result != isVerified)
                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                            .memoId(memo.getId())
                            .signatureType(SignatureType.MEMO)
                            .isVerified(isVerified)
                            .signatureBy(memo.getEmployee() != null ? memo.getEmployee().getPisCode() : null)
                            .individualStatus(memo.getStatus().toString())
                            .build());
            }
        }

        if (memo.getContents() != null && !memo.getContents().isEmpty()) {
            memo.setContents(this.getActiveContents(memo.getContents(), memo.getId()));
            String finalOfficeHeadPisCode = officeHeadPisCode;

            memo.getContents().forEach(pojo -> {
                        pojo.setCreatedDateNp(dateConverter.convertAdToBs(pojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));

                        EmployeeMinimalPojo contentCreator = null;
                        if (pojo.getPisCode() != null)
                            contentCreator = userMgmtServiceData.getEmployeeDetailMinimal(pojo.getPisCode());

                        pojo.setIsDelegated(false);
                        if (pojo.getDelegatedId() != null) {

                            if (pojo.getPisCode().equals(finalOfficeHeadPisCode))
                                pojo.setIsDelegated(true);

                            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(pojo.getDelegatedId());
                            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                                EmployeeMinimalPojo delegatedContentUser = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode());
                                pojo.setEmployee(delegatedContentUser);
                                //check the delegated user is reassignment and set value accordingly

                                if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment()) {
                                    pojo.setIsReassignment(Boolean.TRUE);
                                    if (delegationResponsePojo.getFromSection() != null)
                                        pojo.setReassignmentSection(delegationResponsePojo.getFromSection());
                                    pojo.setEmployeeDesignationNameNp(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameNp() : "");
                                    pojo.setEmployeeDesignationNameEn(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameEn() : "");

                                } else {
                                    //EmployeeMinimalPojo delegatedContentUserFrom = userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getFromEmployee().getCode());

                                    pojo.setIsDelegated(Boolean.TRUE);
                                    pojo.setEmployeeDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                                    pojo.setEmployeeDesignationNameEn(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameEn() : "");

                                }
                                if (delegationResponsePojo.getFromSection() != null) {
                                    SectionPojo delegatedUserSection = new SectionPojo();
                                    delegatedUserSection.setCode(delegationResponsePojo.getFromSection().getCode());
                                    delegatedUserSection.setNameNp(delegationResponsePojo.getFromSection().getNameNp());
                                    delegatedUserSection.setNameEn(delegationResponsePojo.getFromSection().getNameEn());
                                    pojo.setEmployeeSection(delegatedUserSection);
                                }
                            }
                        } else {
                            pojo.setEmployee(contentCreator);
                            if (contentCreator != null && contentCreator.getSectionId() != null) {
                                pojo.setEmployeeSection(userMgmtServiceData.getSectionDetail(contentCreator.getSectionId()));

                                DetailPojo designationPojo = userMgmtServiceData.getDesignationDetailByCode(pojo.getDesignationCode());

                                pojo.setEmployeeDesignationNameNp(designationPojo != null ? designationPojo.getNameNp() : "");
                                pojo.setEmployeeDesignationNameEn(designationPojo != null ? designationPojo.getNameEn() : "");

                            }if (pojo.getSectionCode() != null) {
                                try {
                                    pojo.setEmployeeSection(userMgmtServiceData.getSectionDetail(Long.parseLong(pojo.getSectionCode())));
                                }
                                catch(Exception e){
                                    throw new RuntimeException("can not parse section: "+pojo.getSectionCode());
                                }

                                DetailPojo designationPojo = userMgmtServiceData.getDesignationDetailByCode(pojo.getDesignationCode());

                                pojo.setEmployeeDesignationNameNp(designationPojo != null ? designationPojo.getNameNp() : "");
                                pojo.setEmployeeDesignationNameEn(designationPojo != null ? designationPojo.getNameEn() : "");

                            }

                        }


                        OfficeMinimalPojo officePojo1 = userMgmtServiceData.getOfficeDetailMinimal(pojo.getOfficeCode());
                        pojo.setOfficeName(officePojo1 != null ? officePojo1.getNameEn() : null);
                        pojo.setOfficeNameNp(officePojo1 != null ? officePojo1.getNameNp() : null);

                        if (pojo.getSignatureIsActive() != null && pojo.getSignatureIsActive()) {

                            Boolean isHashedContent = pojo.getHashContent() == null ? Boolean.FALSE : Boolean.TRUE;
                            VerificationInformation verificationInformation = verifySignatureService.verify(pojo.getContent(), pojo.getSignature(), isHashedContent);
                            pojo.setVerificationInformation(verificationInformation);

                            if (verificationInformation != null) {

                                Boolean isVerified = verificationInformation.getStatus() == HttpStatus.OK ? Boolean.TRUE : Boolean.FALSE;

                                //check for alternate result ie. if the letter is verified or not verified record already exists or not
                                Boolean result = signatureVerificationLogRepository.
                                        existsMemoContentLog(
                                                SignatureType.MEMO_CONTENT.toString(),
                                                memo.getId(),
                                                pojo.getId());

                                if (result == null || result != isVerified)
                                    signatureVerificationLogRepository.save(SignatureVerificationLog.builder()
                                            .memoId(memo.getId())
                                            .memoContentId(pojo.getId())
                                            .signatureType(SignatureType.MEMO_CONTENT)
                                            .isVerified(isVerified)
                                            .signatureBy(pojo.getEmployee() != null ? pojo.getEmployee().getPisCode() : null)
                                            .individualStatus(memo.getStatus().toString())
                                            .build());
                            }
                        }
                    }
            );
        }

        Map<String, String> map = this.getTemplate(memo);

        memo.setTemplate(map.get("template"));
        memo.setTippaniHeader(map.get("header"));

        OfficePojo detail = userMgmtServiceData.getOfficeDetail(memo.getOfficeCode());
        OfficePojo parentOffice = userMgmtServiceData.getOfficeDetail(detail != null ? detail.getParentCode() : null);

        memo.setTemplateContent(new TippaniDetail().builder()
                .department(memo.getOfficeNameNp())
                .organization("नेपाल सरकार")
                .ministry(detail != null ? detail.getNameNp() : "")
                .department(parentOffice != null ? parentOffice.getNameNp() : "")
                .address_top(detail != null ? detail.getAddressNp() : "")
                .build()
        );

        MemoReferenceResponsePojo memoReferenceResponsePojo = MemoReferenceResponsePojo
                .builder()
                .id(memo.getId())
                .subject(memo.getSubject())
                .status(memo.getStatus())
                .createdDateNp(memo.getCreatedDateNp())
                .employee(memo.getEmployee())
                .template(memo.getTemplate())
                .build();

        return memoReferenceResponsePojo;
    }

    private List<DocumentPojo> getActiveDocuments(List<DocumentPojo> document) {
        List<DocumentPojo> newPojo = new ArrayList<>();
        for (DocumentPojo data : document) {
            if (data.getIsActive() != null && data.getIsActive()
                    && ((data.getEditable() != null && !data.getEditable()) || (data.getPisCode() != null && data.getPisCode().equals(tokenProcessorService.getPisCode()))))
                newPojo.add(data);
        }
        return newPojo;
    }

    private List<MemoContentPojo> getActiveContents(List<MemoContentPojo> content, Long memoId) {
        List<MemoContentPojo> newPojo = new ArrayList<>();
        MemoApproval memoApproval = memoApprovalMapper.findActive(memoId);
        for (MemoContentPojo data : content) {
//            if (memoApproval != null
//                    && memoApproval.getSuggestion() != null
//                    && memoApproval.getSuggestion()
//                    && data.getPisCode() != null
//                    && !data.getPisCode().equals(tokenProcessorService.getPisCode())
//                    && data.getIsExternal() != null && data.getIsExternal())
//                continue;

            if (data.getIsActive() != null
                    && data.getIsActive()
                    && ((data.getEditable() != null
                    && !data.getEditable()) || (data.getPisCode() != null
                    && data.getPisCode().equals(tokenProcessorService.getPisCode()))))
                newPojo.add(data);
        }
        return newPojo;
    }

    @Override
    public List<MemoResponsePojo> getMemoByParentId(Long parentId) {
        List<MemoResponsePojo> memos = memoMapper.getMemoByParentId(parentId);

        for (MemoResponsePojo memo : memos) {
            memo.setCreatedDateNp(dateConverter.convertAdToBs(memo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            for (ForwardResponsePojo data : memo.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
            }
        }
        return memos;
    }

    @Override
    public List<MemoForward> forwardMemo(MemoForwardRequestPojo data) {
        ArrayList<MemoForward> memoForwards = new ArrayList<>();

        for (ReceiverPojo receiver : data.getReceiver()) {
            EmployeePojo receiverDetails = userMgmtServiceData.getEmployeeDetail(receiver.getReceiverPisCode());

            MemoForward memoForward = new MemoForward().builder()
                    .memo(memoRepo.findById(data.getMemoId()).get())
                    .receiverPisCode(receiver.getReceiverPisCode())
                    .receiverOfficeCode(tokenProcessorService.getOfficeCode())
                    .receiverSectionId(receiverDetails != null ? receiverDetails.getFunctionalDesignationCode() : null)
                    .receiverDesignationCode(receiverDetails != null ? receiverDetails.getSection() != null ? receiverDetails.getSection().getId().toString() : null : null)
                    .senderPisCode(tokenProcessorService.getPisCode())
                    .senderOfficeCode(tokenProcessorService.getOfficeCode())
                    .description(data.getDescription())
                    .build();
            memoForwards.add(memoForward);
        }
        return memoForwardRepo.saveAll(memoForwards);
    }


    private void processDocument(List<MultipartFile> document, Memo memo, EmployeePojo employeePojo, String
            activeFiscalYear) {

        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .tag("darta_chalani")
                        .subModuleName("tippani")
                        .fiscalYearCode(activeFiscalYear)
                        .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                        .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                        .type("1")
                        .build(),
                document
        );

        if (pojo == null)
            throw new RuntimeException("कागच पत्र सेब हुन सकेन");

        if (pojo != null) {
            memo.setDocumentMasterId(pojo.getDocumentMasterId());
            memo.setMemoDocumentDetails(
                    pojo.getDocuments().stream().map(
                            x -> new MemoDocumentDetails().builder()
                                    .documentId(x.getId())
                                    .documentName(x.getName())
                                    .documentSize(x.getSizeKB())
                                    .pisCode(tokenProcessorService.getPisCode())
                                    .editable(true)
                                    .build()
                    ).collect(Collectors.toList())
            );
        }
    }

    private List<MemoDocumentDetails> updateContentDocuments(List<MultipartFile> documents, Memo
            memo, EmployeePojo employeePojo, String activeFiscalYear) {
        List<MemoDocumentDetails> files = new ArrayList<>();
        if (documentUtil.checkEmpty(documents)) {
            DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                    new DocumentSavePojo().builder()
                            .pisCode(tokenProcessorService.getPisCode())
                            .officeCode(tokenProcessorService.getOfficeCode())
                            .tag("darta_chalani")
                            .subModuleName("tippani")
                            .fiscalYearCode(activeFiscalYear)
                            .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                            .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                            .type("1")
                            .build(),
                    documents
            );

            if (pojo == null)
                throw new RuntimeException("कागच पत्र सेब हुन सकेन");

            if (pojo != null) {
                pojo.getDocuments().forEach(
                        x -> {
                            files.add(new MemoDocumentDetails().builder()
                                    .documentId(x.getId())
                                    .documentName(x.getName())
                                    .documentSize(x.getSizeKB())
                                    .pisCode(tokenProcessorService.getPisCode())
                                    .editable(true)
                                    .memo(memo)
                                    .build()
                            );
                        }
                );
            }
        }

        return files;
    }

    private void deleteDocuments(List<Long> documentsToRemove) {
        if (documentsToRemove != null && !documentsToRemove.isEmpty()) {
            for (Long id : documentsToRemove) {
                memoDocumentDetailsRepo.softDelete(id);
            }
//            documentUtil.deleteDocuments(documentsToRemove);
        }
    }

    @Override
    public List<SysDocumentsPojo> getSystemDocuments() {
        DocumentSystemPojo documentSystemPojo = documentServiceData.getDocuments(tokenProcessorService.getPisCode());
        List<SysDocumentsPojo> documentsPojos = new ArrayList<>();

        if (documentSystemPojo != null) {
            if (documentSystemPojo.getData() != null && !documentSystemPojo.getData().isEmpty()) {
                for (SystemDocumentPojo data : documentSystemPojo.getData()) {
                    if (data.getDocuments() != null && !data.getDocuments().isEmpty()) {
                        documentsPojos.addAll(data.getDocuments());
                    }
                }
            }
        }
        return documentsPojos;
    }

    @Override
    public Long saveReference(ReferencePojo data) {
        Memo saved = memoRepo.findById(data.getId()).get();

        String tokenPisCode = tokenProcessorService.getPisCode();
        String tokenOfficeCode = tokenProcessorService.getOfficeCode();

        if (data.getAttachedMemoId() != null && !data.getAttachedMemoId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedMemoId()) {
                Memo memo1 = memoRepo.findById(id).orElseThrow(() -> new RuntimeException("No Memo Found"));
                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .referencedMemoId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenOfficeCode)
                        .pisCode(tokenPisCode)
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getAttachedDispatchId() != null && !data.getAttachedDispatchId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedDispatchId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .chalaniReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenOfficeCode)
                        .pisCode(tokenPisCode)
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getAttachedDartaId() != null && !data.getAttachedDartaId().isEmpty()) {
            List<MemoReference> memoReferences = new ArrayList<>();

            for (Long id : data.getAttachedDartaId()) {
                MemoReference memoReference = new MemoReference().builder()
                        .memo(saved)
                        .dartaReferenceId(id)
                        .isEditable(true)
                        .include(true)
                        .officeCode(tokenOfficeCode)
                        .pisCode(tokenPisCode)
                        .isAttach(Boolean.TRUE)
                        .build();
                memoReferences.add(memoReference);
            }
            memoReferenceRepo.saveAll(memoReferences);
        }

        if (data.getLetterType() != null && data.getLetterType().equals("tippani") && data.getReferencesToRemove() != null && !data.getReferencesToRemove().isEmpty()) {
            for (Long id : data.getReferencesToRemove()) {
                memoReferenceRepo.deleteReference(saved.getId(), id, tokenProcessorService.getPisCode());
            }
        }

        if (data.getMemoToRemove() != null) {
            memoReferenceRepo.deleteMemoAttach(saved.getId(), data.getMemoToRemove(), tokenPisCode);
        }

        if (data.getDispatchToRemove() != null) {
            memoReferenceRepo.deleteDispatchAttach(saved.getId(), data.getDispatchToRemove(), tokenPisCode);
        }

        if (data.getDartaToRemove() != null) {
            memoReferenceRepo.deleteDartaAttach(saved.getId(), data.getDartaToRemove(), tokenPisCode);
        }

        return data.getId();
    }

    private void editableStatus(Long id, String pisCode) {
        List<Long> contentId = memoContentRepo.getEditable(id, pisCode);
        List<Long> docId = memoDocumentDetailsRepo.getEditable(id, pisCode);

        if (contentId != null && !contentId.isEmpty()) {
            for (Long cId : contentId)
                memoContentRepo.setEditable(cId);
        }

        if (docId != null && !docId.isEmpty()) {
            for (Long dId : docId)
                memoDocumentDetailsRepo.setEditable(dId);
        }
    }

    private Map<String, String> getTemplate(MemoResponsePojo memo) {

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(memo.getPisCode());
        SectionPojo employeeSectionPojo = null;
        DesignationPojo employeeDesignationPojo = null;
//        OfficePojo employeeOfficePojo = userMgmtServiceData.getOfficeDetail(memo.getOfficeCode());
        if (memo.getSectionCode() != null)
            employeeSectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(memo.getSectionCode()));

        if (memo.getDesignationCode() != null)
            employeeDesignationPojo = userMgmtServiceData.getDesignationDetail(memo.getDesignationCode());
//        String officeNameNp = employeeOfficePojo != null ? employeeOfficePojo.getNameNp() : employeePojo != null ? employeePojo.getOffice() != null ? employeePojo.getOffice().getNameN() : "": "";
        String sectionName = employeeSectionPojo != null ? employeeSectionPojo.getNameNp() : employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getNameN() : "" : "";
        String designationName = employeeDesignationPojo != null ? employeeDesignationPojo.getNameNp() : employeePojo != null ? employeePojo.getFunctionalDesignation() != null ? employeePojo.getFunctionalDesignation().getNameN() : "" : "";

//        String section = employeePojo.getSection() != null ? employeePojo.getSection().getNameN() : null;

        String img = "data:image/png;base64,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";

        List<TippaniContent> tippaniContentList = new ArrayList<>();

        TippaniContent tippaniContent = new TippaniContent().builder()
                .content(memo.getContent())
                .senderEmployeeDetail(
                        new SenderEmployeeDetail().builder()
                                .date(dateConverter.convertBSToDevnagari(memo.getCreatedDateNp()))
                                .employeeName(memo.getEmployee().getEmployeeNameNp())
                                .employeeDesignation(memo.getIsDelegated() ?
                                        DELEGATED_NEP + ", " + memo.getEmployeeDesignationNameNp()
                                        : memo.getEmployeeDesignationNameNp()
                                )
                                .employeeSection(memo.getEmployeeSection() != null ? memo.getIsReassignment() ? ADDITIONAL_RESPONSIBILITY_NEP + ", " + memo.getEmployeeSection().getNameNp() : memo.getEmployeeSection().getNameNp() : "")
                                .build()
                )
                .verificationInformation(memo.getVerificationInformation())
                .build();

        tippaniContentList.add(tippaniContent);

        if (memo.getContents() != null && !memo.getContents().isEmpty()) {
            for (MemoContentPojo memoContent : memo.getContents()) {
//                EmployeePojo employee = userMgmtServiceData.getEmployeeDetail(memoContent.getEmployee() != null ? memoContent.getEmployee().getPisCode() : null);
//                OfficeMinimalPojo employeeOffice = userMgmtServiceData.getOfficeDetailMinimal(memoContent.getOfficeCode());
//
//                SectionPojo employeeSection = null;
//                DesignationPojo employeeDesignation = null;
//
//                if (memoContent.getSectionCode() != null)
//                    employeeSection = userMgmtServiceData.getSectionDetail(Long.parseLong(memoContent.getSectionCode()));

//                if (memoContent.getDesignationCode() != null)
//                    employeeDesignation = userMgmtServiceData.getDesignationDetail(memoContent.getDesignationCode());

//                String officeNameNp = employeeOffice != null ? employeeOffice.getNameNp() : employee != null ? employee.getOffice() != null ? employee.getOffice().getNameN() : "" : "";
//                String sectionNameNp = employeeSection != null ? employeeSection.getNameNp() : employee != null ? employee.getSection() != null ? employee.getSection().getNameN() : "" : "";
//                String designationNameNp = employeeDesignation != null ? employeeDesignation.getNameNp() : employee != null ? employee.getFunctionalDesignation() != null ? employee.getFunctionalDesignation().getNameN() : "" : "";
//
//                String officeHeadPisCode = "";
//                OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
//                if (officeHeadPojo != null && officeHeadPojo.getPisCode() != null)
//                    officeHeadPisCode = officeHeadPojo.getPisCode();

                TippaniContent content = new TippaniContent().builder()
                        .content(memoContent.getContent())
                        .senderEmployeeDetail(
                                new SenderEmployeeDetail().builder()
                                        .date(dateConverter.convertBSToDevnagari(memoContent.getCreatedDateNp()))
                                        .employeeName(memoContent.getEmployee() != null ? memoContent.getEmployee().getEmployeeNameNp() : "")
//                                        .employeeDesignation((memoContent.getDelegatedId() != null ? memoContent.getPisCode().equals(officeHeadPisCode) ? "निमित्त, " : "" : "") + designationNameNp + ", " + (!memoContent.getIsExternal() ? sectionNameNp : officeNameNp))
                                        .employeeDesignation(
                                                memoContent.getIsDelegated() ?
                                                        DELEGATED_NEP + ", " + memoContent.getEmployeeDesignationNameNp() :
                                                        memoContent.getEmployeeDesignationNameNp()
                                        )
                                        .employeeSection(memoContent.getEmployeeSection() != null ?
                                                memoContent.getIsReassignment() ?
                                                        ADDITIONAL_RESPONSIBILITY_NEP + ", " + memoContent.getEmployeeSection().getNameNp() :
                                                        memoContent.getEmployeeSection().getNameNp() :
                                                ""
                                        )
                                        .build()
                        )
                        .verificationInformation(memoContent.getVerificationInformation())
                        .build();
                tippaniContentList.add(content);
            }
        }

        OfficePojo detail = userMgmtServiceData.getOfficeDetail(memo.getOfficeCode());
        OfficePojo parentOffice = userMgmtServiceData.getOfficeDetail(detail != null ? detail.getParentCode() : null);

        if (parentOffice != null && parentOffice.getCode().equals("8886"))
            parentOffice = null;

        AtomicReference<String> approverPisCode = new AtomicReference<>();
        AtomicReference<Timestamp> approvedDate = new AtomicReference<>();
        memo.getApproval().forEach(data -> {
            if (data.getStatus() == Status.A) {
                approverPisCode.set(data.getApproverPisCode());
                approvedDate.set(data.getCreatedDate());

            }
        });

        //dynamic header part
        OfficeTemplatePojo dynamicTemplateHeader = null;
        String header = null;
        if (memo.getTemplateHeaderId() != null) {
            dynamicTemplateHeader = userMgmtServiceData.getOfficeTemplateById(memo.getTemplateHeaderId());
        } else {
            // get active template
            if (Boolean.TRUE.equals(memo.getIsDraft())) {
                dynamicTemplateHeader = userMgmtServiceData.getOfficeTemplate(tokenProcessorService.getOfficeCode(), "H");
            }
        }

        if (dynamicTemplateHeader != null) {
            if (dynamicTemplateHeader.getTemplateNp() != null &&
                    !dynamicTemplateHeader.getTemplateNp().isEmpty())
                header = dynamicTemplateHeader.getTemplateNp();
        }

        OfficeTemplatePojo dynamicTemplateFooter;
        String footer = null;
        if (memo.getTemplateFooterId() != null) {
            dynamicTemplateFooter = userMgmtServiceData.getOfficeTemplateById(memo.getTemplateFooterId());
        } else {
            dynamicTemplateFooter = userMgmtServiceData.getOfficeTemplate(tokenProcessorService.getOfficeCode(), "F");
        }

        if (dynamicTemplateFooter != null) {
            if (dynamicTemplateFooter.getTemplateNp() != null &&
                    !dynamicTemplateFooter.getTemplateNp().isEmpty())
                footer = dynamicTemplateFooter.getTemplateNp();
        }

        TippaniDetail tippaniDetail = new TippaniDetail().builder()
                .logoUrl(img)
                .organization("नेपाल सरकार")
                .ministry(detail != null ? detail.getNameNp() : "")
                .department(parentOffice != null ? parentOffice.getNameNp() : "")
                .address_top(detail != null ? detail.getAddressNp() : "")
                .subject(memo.getSubject())
                .resource_type("T")
                .resource_id(memo.getId())
                .tippaniContentList(tippaniContentList)
                .tippaniNo(memo.getTippaniNo() != null ? String.valueOf(memo.getTippaniNo()) : null)
                .approverPisCode(approverPisCode.get())
                .status(memo.getStatus())
                .senderOfficeCode(memo.getOfficeCode())
                .approvedDate(approvedDate.get() != null ? dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(approvedDate.get().toLocalDateTime().toLocalDate().toString())) : null)
                .dynamicHeader(header)
                .dynamicFooter(footer)
                .build();

        System.out.println("tippani: " + new Gson().toJson(tippaniDetail));

        Map<String, String> map = new HashMap<>();
        map.put("template", letterTemplateProxy.getTippaniTemplate(tippaniDetail));
        map.put("header", letterTemplateProxy.getTippaniHeader(tippaniDetail));

        return map;
    }

    @Override
    public Map<String, Object> getTippaniSearchRecommendation() {
        Map<String, Object> response = new HashMap<>();

        String suggestionPisCode = tokenProcessorService.getPisCode();

        String suggestionSectionId = userMgmtServiceData.getEmployeeDetail(suggestionPisCode).getSectionId();

        List<TippaniSearchRecommendationDto> searchRecommendation = memoMapper.getTippaniSearchFields(suggestionPisCode, suggestionSectionId);

        // extracting unique data for every column returned from the above query
        List<String> uniqueCreatorPisCode = searchRecommendation.stream()
                .filter(obj -> obj.getPisCode() != null && !obj.getPisCode().isEmpty() && !obj.getPisCode().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> obj.getPisCode())
                .distinct()
                .collect(Collectors.toList());

        List<String> uniqueCreatorOfficeCode = searchRecommendation.stream()
                .filter(obj -> obj.getOfficeCode() != null && !obj.getOfficeCode().isEmpty() && !obj.getOfficeCode().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> obj.getOfficeCode())
                .distinct()
                .collect(Collectors.toList());

        List<String> uniqueCreatorCreatorSectionCode = searchRecommendation.stream()
                .filter(obj -> obj.getSectionCode() != null && !obj.getSectionCode().isEmpty() && !obj.getSectionCode().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> obj.getSectionCode())
                .distinct()
                .collect(Collectors.toList());

        // building response body
        List<Map<String, Object>> creatorDetails = uniqueCreatorPisCode.stream().map(obj -> {
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
            return tempSenderDetail;
        })
                .collect(Collectors.toList());

        List<Map<String, Object>> creatorOfficeDetails = uniqueCreatorOfficeCode.stream().map(obj -> {

            Map<String, Object> tempEmployeeDetail = new HashMap<>();
            OfficeMinimalPojo officeDetailMinimal = userMgmtServiceData.getOfficeDetailMinimal(obj);
            if (officeDetailMinimal == null) {
                return null;
            }
            tempEmployeeDetail.put("nameEn", officeDetailMinimal.getNameEn());
            tempEmployeeDetail.put("nameNp", officeDetailMinimal.getNameNp());
            tempEmployeeDetail.put("officeCode", obj);
            return tempEmployeeDetail;
        })
                .collect(Collectors.toList());

        List<Map<String, Object>> creatorSectionDetails = uniqueCreatorCreatorSectionCode.stream().map(obj -> {
            Map<String, Object> tempReceiverOfficeDetails = new HashMap<>();
            SectionPojo sectionDetail = userMgmtServiceData.getSectionDetail(Long.parseLong(obj));
            if (sectionDetail == null) {
                return null;
            }
            tempReceiverOfficeDetails.put("nameEn", sectionDetail.getNameEn());
            tempReceiverOfficeDetails.put("nameNp", sectionDetail.getNameNp());
            tempReceiverOfficeDetails.put("sectionCode", obj);
            tempReceiverOfficeDetails.put("officeCode", sectionDetail.getOfficeCode());
            return tempReceiverOfficeDetails;
        })
                .collect(Collectors.toList());

        response.put("creatorDetails", creatorDetails);
        response.put("creatorOfficeDetails", creatorOfficeDetails);
        response.put("creatorSectionDetails", creatorSectionDetails);
        return response;
    }

    @Override
    public List<ReferenceMemoResponse> getListOfReferencedMemos(Long memoId) {

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user in involved in any section or not
        checkUserSection(tokenUser);

        String tokenUserSection = tokenUser.getSectionId();

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));

        }

        log.info("memo id: " + memoId + " and section: " + tokenUser.getSectionId());
        Memo memo = memoRepo.findById(memoId).orElseThrow(() -> new RuntimeException("memo not found with id: " + memoId));
        if (!memo.getIsDraft()) {
            if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
                List<String> involvedOffices = memoMapper.getInvolvedOffices(memoId);
                log.info("Involved offices in memo: " + involvedOffices);
                if (!involvedOffices.contains(tokenProcessorService.getOfficeCode()))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission");
            } else {
                String strPisCodes = dispatchLetterServiceImpl.convertListToString(listPisCodes);
                log.info("previousPisCodes: " + strPisCodes);

                if (!memoMapper.checkInvolvedTippani(memoId, tokenUser.getSectionId(), strPisCodes))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission");
            }
        } else {
            if (!draftShareRepo.checkPermissionMemoDraft(memoId, tokenPisCode, tokenUserSection)
                    && !(memo.getPisCode().equals(tokenPisCode) && memo.getSectionCode().equals(tokenUserSection)))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
        }

        List<ReferenceMemoResponse> referenceMemo = memoMapper.getReferenceMemosAll(memoId);

        if (referenceMemo != null && !referenceMemo.isEmpty()) {
            int count = referenceMemo.size();
            for (ReferenceMemoResponse data : referenceMemo) {
                if (data.getApprovedDate() != null) {
                    data.setApprovedDateBs(dateConverter.convertAdToBs(data.getApprovedDate().toLocalDateTime().toLocalDate().toString()));
                    data.setApprovedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(data.getApprovedDate().toLocalDateTime().toLocalDate().toString())));
                } else {
                    data.setApprovedDateBs(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                    data.setApprovedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
                }
                data.setReferenceEmployee(data.getAPisCode() != null ? userMgmtServiceData.getEmployeeDetailMinimal(data.getAPisCode()) : null);
                data.setSn("1." + count);
                count--;
            }
        }

        return referenceMemo;
    }

    @Override
    public List<MemoApprovalPojo> getActivityLog(Long memoId, Long referencedMemoId, String fiscalYearCode) {

        List<MemoApprovalPojo> approvalPojos = memoMapper.getActivityLogWithReferenceTippaniLogAll(memoId, referencedMemoId, fiscalYearCode);

        Map<String, EmployeeMinimalPojo> currentUser = new HashMap<>();

        Map<Long, SenderApproverDto> firstSuggestion = new HashMap<>();

        for (MemoApprovalPojo memoApprovalPojo : approvalPojos) {
            memoApprovalPojo.setCreatedDateNp(dateConverter.convertAdToBs(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            memoApprovalPojo.setCreatedTimeNp(dateConverter.convertBSToDevnagari(memoApprovalPojo.getCreatedDate().toLocalDateTime().toLocalTime().toString()));

            EmployeeMinimalPojo approverMinimalDetails = null;
            if (memoApprovalPojo.getApproverPisCode() != null) {
                approverMinimalDetails = currentUser.get(memoApprovalPojo.getApproverPisCode()) != null
                        ? currentUser.get(memoApprovalPojo.getApproverPisCode())
                        : addEmployee(currentUser, memoApprovalPojo.getApproverPisCode(), 1);
            }
            memoApprovalPojo.setApprover(approverMinimalDetails);

            EmployeeMinimalPojo senderMinimalDetails = null;

            if (memoApprovalPojo.getSenderPisCode() != null) {
                senderMinimalDetails = currentUser.get(memoApprovalPojo.getSenderPisCode()) != null
                        ? currentUser.get(memoApprovalPojo.getSenderPisCode())
                        : addEmployee(currentUser, memoApprovalPojo.getSenderPisCode(), 1);
            }

            memoApprovalPojo.setIsDelegated(false);
            if (memoApprovalPojo.getDelegatedId() != null) {

                System.out.println("delegated memo: " + memoApprovalPojo.getId() + " delegated id: " + memoApprovalPojo.getDelegatedId());

                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(memoApprovalPojo.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {

                    //check the delegated user is reassignment and set value accordingly
                    if (delegationResponsePojo.getIsReassignment() != null
                            && delegationResponsePojo.getIsReassignment()) {
                        memoApprovalPojo.setIsReassignment(Boolean.TRUE);
                        if (delegationResponsePojo.getFromSection() != null)
                            memoApprovalPojo.setReassignmentSection(delegationResponsePojo.getFromSection());

                        memoApprovalPojo.setSenderDesignationNameNp(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameNp() : "");
                    } else {
                        memoApprovalPojo.setIsDelegated(true);
                        memoApprovalPojo.setSenderDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                    }
                    memoApprovalPojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                }
            } else
                memoApprovalPojo.setSender(senderMinimalDetails);

            if (memoApprovalPojo.getOfficeCode() != null) {
                OfficeMinimalPojo approvalOffice = userMgmtServiceData.getOfficeDetailMinimal(memoApprovalPojo.getOfficeCode());
                if (approvalOffice != null)
                    memoApprovalPojo.setOfficeNameNp(approvalOffice.getNameNp());
            }

            SenderApproverDto senderApproverDto = firstSuggestion.get(memoApprovalPojo.getMemoId()) != null ?
                    firstSuggestion.get(memoApprovalPojo.getMemoId()) : addSenderApprover(firstSuggestion, memoApprovalPojo.getMemoId());

            if (memoApprovalPojo.getType().equals("suggestion") && memoApprovalPojo.getStatus() == Status.P)
                memoApprovalPojo.setStatus(Status.SG);
        }

        return approvalPojos;
    }

    private EmployeeMinimalPojo addEmployee(Map<String, EmployeeMinimalPojo> map, String pisCode, int count) {
        EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(pisCode);
        System.out.println("add employee with count: " + count + " is: " + employeeMinimalPojo);
        map.put(pisCode, employeeMinimalPojo);

        if (employeeMinimalPojo != null || count > 30)
            return employeeMinimalPojo;
        else
            return addEmployee(map, pisCode, count + 1);
    }

    private SenderApproverDto addSenderApprover(Map<Long, SenderApproverDto> map, Long memoId) {
        SenderApproverDto senderApproverDto = memoMapper.getFirstSuggestionDetail(memoId);
        map.put(memoId, senderApproverDto);

        return senderApproverDto;
    }

    @Override
    public List<IdSubjectDto> getMemoSubjectSearchList(Long memoId) {
        return memoMapper.getMemoSubjectSearchListAll(memoId);
    }

    @Override
    public List<MemoReferenceResponsePojo> getTemplatesOfAllLinkedReferenceMemos(Long memoId) {

        List<Long> memoIds = memoMapper.getTippaniReferenceList(memoId);


        List<MemoReferenceResponsePojo> responsePojos = new ArrayList<>();
        memoIds.stream().forEach(x -> {
            MemoReferenceResponsePojo memoReferenceResponsePojo = getMemoByIdForReference(x, memoId, 2);
            if (memoReferenceResponsePojo != null)
                responsePojos.add(memoReferenceResponsePojo);
        });

        return responsePojos;
    }

    @Override
    public List<MemoReferenceDoc> getDocumentsOfAllLinkedReferenceMemos(Long memoId, Long referenceMemoId) {

        List<MemoReferenceDoc> list = memoMapper.getMemoReferenceDocuments(memoId, referenceMemoId);

        if (list != null && !list.isEmpty()) {
            list.forEach(x -> {
                if (x.getDelegatedId() != null) {
                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                        //check the delegated user is reassignment and set value accordingly
                        if (delegationResponsePojo.getIsReassignment() != null
                                && delegationResponsePojo.getIsReassignment()) {
                            x.setIsReassignment(Boolean.TRUE);
                            if (delegationResponsePojo.getFromSection() != null)
                                x.setReassignmentSection(delegationResponsePojo.getFromSection());
                            x.setCreatorDesignationNameNp(delegationResponsePojo.getToDesignation() != null ? delegationResponsePojo.getToDesignation().getNameNp() : "");
                        } else {
                            x.setIsDelegated(true);
                            x.setCreatorDesignationNameNp(delegationResponsePojo.getFromDesignation() != null ? delegationResponsePojo.getFromDesignation().getNameNp() : "");
                        }
                        x.setCreator(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                    }
                } else if (x.getPisCode() != null)
                    x.setCreator(userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode()));
            });
        }
        return list;
    }

    @Override
    public List<SystemFilesDto> getSystemDocumentsOfAllLinkedReferenceMemos(Long memoId, Long referenceMemoId) {
        List<SystemFilesDto> list = memoMapper.getSystemFiles(memoId, referenceMemoId);

        if (list != null && !list.isEmpty()) {
            list.forEach(x -> {
                if (x.getApprovedDate() != null) {
                    x.setApprovedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getApprovedDate().toLocalDateTime().toLocalDate().toString())));
                    x.setApprovedDateEn(dateConverter.convertAdToBs(x.getApprovedDate().toLocalDateTime().toLocalDate().toString()));
                }

                x.setIsDelegated(false);
                if (x.getDelegatedId() != null) {
                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(x.getDelegatedId());
                    if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                        //check the delegated user is reassignment and set value accordingly
                        if (delegationResponsePojo.getIsReassignment() != null
                                && delegationResponsePojo.getIsReassignment()) {
                            x.setIsReassignment(Boolean.TRUE);
                        } else {
                            x.setIsDelegated(true);
                        }
                        x.setCreator(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                    }
                } else if (x.getPisCode() != null)
                    x.setCreator(userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode()));
            });
        }
        return list;
    }

    //this function check the user is involved in any section or not
    private void checkUserSection(EmployeePojo employeePojo) {
        if (employeePojo == null)
            throw new RuntimeException("Employee detail not found with id : " + tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ");
    }

    private void checkIsCreator(Long id, String pisCode, String sectionCode, String officeCode) {
        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();

        listPisCodes.add(tokenPisCode);
        if (sectionCode != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, sectionCode) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, sectionCode));
        }

        if (memoMapper.checkIsCreator(id, listPisCodes, sectionCode, officeCode)) ;
    }

}


// प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ।