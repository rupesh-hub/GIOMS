package com.gerp.dartachalani.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.Proxy.LetterTemplateProxy;
import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.document.DocumentMasterResponsePojo;
import com.gerp.dartachalani.dto.document.DocumentPojo;
import com.gerp.dartachalani.dto.document.DocumentSavePojo;
import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.dartachalani.dto.group.OfficeGroupPojo;
import com.gerp.dartachalani.mapper.*;
import com.gerp.dartachalani.model.DelegationTableMapper;
import com.gerp.dartachalani.model.dispatch.DispatchLetterReview;
import com.gerp.dartachalani.model.dispatch.SignatureData;
import com.gerp.dartachalani.model.external.ExternalRecords;
import com.gerp.dartachalani.model.receive.*;
import com.gerp.dartachalani.repo.*;
import com.gerp.dartachalani.service.InitialService;
import com.gerp.dartachalani.service.MemoService;
import com.gerp.dartachalani.service.ReceivedLetterService;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.dartachalani.service.digitalSignature.VerifySignatureService;
import com.gerp.dartachalani.service.rabbitmq.RabbitMQService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.dartachalani.utils.DartaChalaniConstants;
import com.gerp.dartachalani.utils.DocumentUtil;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.BodarthaEnum;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.Status;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
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
public class ReceivedLetterServiceImpl extends GenericServiceImpl<ReceivedLetter, Long> implements ReceivedLetterService {

    private final ReceivedLetterRepo receivedLetterRepo;
    private final ReceivedLetterForwardRepo receivedLetterForwardRepo;
    private final ReceivedLetterMessageRepo receivedLetterMessageRepo;
    private final ManualReceivedLetterRepo manualReceivedLetterRepo;
    private final ManuallyReceivedLetterRepo manuallyReceivedLetterRepo;
    private final ManuallyReceivedLetterRepo manualReceivedLetterDetailRepo;
    private final ReceivedLetterMapper receivedLetterMapper;
    private final TokenProcessorService tokenProcessorService;
    private final InitialService initialService;
    private final MemoReferenceRepo memoReferenceRepo;
    private final ExternalRecordsRepo externalRecordsRepo;
    private final MemoMapper memoMapper;
    private final DispatchLetterMapper dispatchLetterMapper;
    private final MemoReferenceMapper memoReferenceMapper;
    private final SignatureDataRepo signatureDataRepo;
    private final VerifySignatureService verifySignatureService;
    private final FooterDataMapper footerDataMapper;
    private final DispatchLetterReviewRepo dispatchLetterReviewRepo;
    private final DelegationTableMapperRepo delegationTableMapperRepo;
    private final String MODULE_KEY = PermissionConstants.RECEIVED_LETTER_MODULE_NAME;
    private final String MODULE_APPROVAL_KEY = PermissionConstants.RECEIVED_LETTER;
    private final String DELEGATED_NEP = DartaChalaniConstants.DELEGATED_NEP;
    private final String DELEGATED_EN = DartaChalaniConstants.DELEGATED_EN;
    private final String ADDITIONAL_RESPONSIBILITY_NEP = DartaChalaniConstants.ADDITIONAL_RESPONSIBILITY_NEP;
    private final String ADDITIONAL_RESPONSIBILITY_EN = DartaChalaniConstants.ADDITIONAL_RESPONSIBILITY_EN;
    private final String TRANSFER_FROM_PIS_CODE = DartaChalaniConstants.TRANSFER_FROM_PIS_CODE;
    private final String TRANSFER_FROM_SECTION_CODE = DartaChalaniConstants.TRANSFER_FROM_SECTION_CODE;
    @Autowired
    @Lazy
    private MemoService memoService;
    @Autowired
    private UserMgmtServiceData userMgmtServiceData;
    @Autowired
    private DocumentUtil documentUtil;
    @Autowired
    private DateConverter dateConverter;
    @Autowired
    private LetterTemplateProxy letterTemplateProxy;
    @Autowired
    private RabbitMQService notificationService;
    @Autowired
    private CustomMessageSource customMessageSource;

    public ReceivedLetterServiceImpl(ReceivedLetterRepo receivedLetterRepo,
                                     ReceivedLetterForwardRepo receivedLetterForwardRepo,
                                     ReceivedLetterMessageRepo receivedLetterMessageRepo,
                                     ManualReceivedLetterRepo manualReceivedLetterRepo,
                                     ManuallyReceivedLetterRepo manuallyReceivedLetterRepo,
                                     ReceivedLetterMapper receivedLetterMapper,
                                     ManuallyReceivedLetterRepo manualReceivedLetterDetailRepo,
                                     TokenProcessorService tokenProcessorService,
                                     InitialService initialService1,
                                     MemoReferenceRepo memoReferenceRepo,
                                     MemoMapper memoMapper,
                                     DispatchLetterMapper dispatchLetterMapper,
                                     MemoReferenceMapper memoReferenceMapper,
                                     ExternalRecordsRepo externalRecordsRepo,
                                     SignatureDataRepo signatureDataRepo,
                                     VerifySignatureService verifySignatureService,
                                     FooterDataMapper footerDataMapper,
                                     DispatchLetterReviewRepo dispatchLetterReviewRepo,
                                     DelegationTableMapperRepo delegationTableMapperRepo) {
        super(receivedLetterRepo);
        this.receivedLetterRepo = receivedLetterRepo;
        this.receivedLetterForwardRepo = receivedLetterForwardRepo;
        this.receivedLetterMessageRepo = receivedLetterMessageRepo;
        this.manualReceivedLetterRepo = manualReceivedLetterRepo;
        this.manuallyReceivedLetterRepo = manuallyReceivedLetterRepo;
        this.manualReceivedLetterDetailRepo = manualReceivedLetterDetailRepo;
        this.receivedLetterMapper = receivedLetterMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.initialService = initialService1;
        this.memoReferenceRepo = memoReferenceRepo;
        this.memoMapper = memoMapper;
        this.dispatchLetterMapper = dispatchLetterMapper;
        this.memoReferenceMapper = memoReferenceMapper;
        this.externalRecordsRepo = externalRecordsRepo;
        this.signatureDataRepo = signatureDataRepo;
        this.verifySignatureService = verifySignatureService;
        this.footerDataMapper = footerDataMapper;
        this.dispatchLetterReviewRepo = dispatchLetterReviewRepo;
        this.delegationTableMapperRepo = delegationTableMapperRepo;
    }


    @Override
    public ReceivedLetter saveReceivedLetter(DispatchLetterDTO data) {

        // add template id
        data.getDispatchLetterInternal().stream().forEach(x -> {

            String dartaNum = initialService.getDartaNumber(x.getInternalReceiverOfficeCode());
            if (dartaNum != null) {

                OfficePojo officePojo = userMgmtServiceData.getOfficeDetail(x.getInternalReceiverOfficeCode());
                if (officePojo.getIsGiomsActive() != null && officePojo.getIsGiomsActive()) {

                    ReceivedLetter receivedLetter = new ReceivedLetter().builder()
                            .letterPriority(data.getLetterPriority())
                            .letterPrivacy(data.getLetterPrivacy())
                            .pisCode(tokenProcessorService.getPisCode())
                            .officeCode(x.getInternalReceiverOfficeCode())
                            .fiscalYearCode(data.getFiscalYearCode())
                            .senderOfficeCode(data.getSenderOfficeCode())
                            .dispatchNo(data.getDispatchNo())
                            .dispatchDateEn(data.getDispatchDate())
                            .content(data.getContent())
                            .registrationNo(dartaNum)
                            .referenceNo(data.getReferenceCode())
                            .dispatchDateNp(data.getDispatchDateNp())
                            .subject(data.getSubject())
                            .entryType(false)
                            .isDraft(data.getIsDraft())
                            .dispatchId(data.getDispatchId())
                            .include(data.getInclude())
                            .isEnglish(data.getIsEnglish())
                            .signatureIsActive(data.getSignature().getSignatureIsActive())
                            .signature(data.getSignature().getSignatureData())
                            .hashContent(data.getHash_content())
                            .remarks(data.getRemarks())
                            .remarksPisCode(data.getRemarksPisCode())
                            .remarksSignatureIsActive(data.getRemarksSignatureIsActive())
                            .remarksSignature(data.getRemarksSignatureData())
                            .isSingular(data.getSingular())
                            .isReceiver(x.getInternalReceiver())
                            .salutation(x.getSalutation())
                            .templateHeaderId(data.getTemplateHeaderId())
                            .templateFooterId(data.getTemplateFooterId())
                            .receivedLetterDocumentDetails(getDoc(data))
                            .build();

                    receivedLetterRepo.save(receivedLetter);

                    OfficeHeadPojo senderOfficeHead = userMgmtServiceData.getOfficeHeadDetail(data.getSenderOfficeCode());
                    OfficeHeadPojo receiverOfficeHead = userMgmtServiceData.getOfficeHeadDetail(x.getInternalReceiverOfficeCode());

                    if (receiverOfficeHead != null) {
                        notificationService.notificationProducer(
                                NotificationPojo.builder()
                                        .moduleId(receivedLetter.getId())
                                        .module(MODULE_APPROVAL_KEY)
                                        .sender(senderOfficeHead.getPisCode())
                                        .receiver(receiverOfficeHead.getPisCode())
                                        .subject(customMessageSource.getNepali("manual.received"))
                                        .detail(customMessageSource.getNepali("darta.create", receivedLetter.getRegistrationNo(), receivedLetter.getSubject()))
                                        .pushNotification(true)
                                        .received(true)
                                        .build()
                        );
                    }

                }
            }
        });

        return null;
    }

    private List<ReceivedLetterDocumentDetails> getDoc(DispatchLetterDTO data) {
        List<ReceivedLetterDocumentDetails> receivedLetterDocumentDetails = new ArrayList<>();
        data.getDocuments().stream().forEach(x -> {
            ReceivedLetterDocumentDetails d = new ReceivedLetterDocumentDetails();
            d.setDocumentId(x.getId());
            d.setDocumentName(x.getName());
            d.setDocumentSize(x.getSizeKB());
            receivedLetterDocumentDetails.add(d);

        });

        return receivedLetterDocumentDetails;
    }

    @Override
    public Boolean forwardReceivedLetter(ReceivedLetterForwardRequestPojo data, boolean isMultiple) {
        String tokenPisCode = tokenProcessorService.getPisCode();

        AtomicReference<Boolean> isSuccess = new AtomicReference<>(true);
        ReceivedLetter receivedLetter = receivedLetterRepo.findById(data.getReceivedLetterId()).get();

        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        String tokenUserSection = tokenUser != null ? tokenUser.getSection() != null ? tokenUser.getSection().getId().toString() : null : null;

        //check user is involved in any section or not
        checkUserSection(tokenUser);

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        // check if the user is creator or office_head of office where_letter is created
        if ((!tokenProcessorService.getRoles().contains("OFFICE_HEAD") &&
                receivedLetterMapper.checkIsReceiver(data.getReceivedLetterId(), listPisCodes, tokenUser.getSectionId()))
                //!listPisCodes.stream().anyMatch(code -> receivedLetterMapper.getReceiverPisCodes(data.getReceivedLetterId(), senderDetail.getSectionId()).contains(code)))
                || !receivedLetter.getOfficeCode().equals(tokenProcessorService.getOfficeCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid User");
        }


        ReceivedLetterForward activeForward = receivedLetterMapper.findActive(data.getReceivedLetterId(), listPisCodes, tokenUserSection);

        if (activeForward != null) {
            activeForward.setReceivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get());
            activeForward.setActive(false);
            activeForward.setCompletion_status(receivedLetterForwardRepo.findById(activeForward.getId()).get().getCompletion_status());
            receivedLetterForwardRepo.save(activeForward);
        }

        ReceivedLetterForward activeForwardCc = receivedLetterMapper.findActiveCc(data.getReceivedLetterId(), tokenPisCode, tokenUserSection);
        if (activeForwardCc != null) {
            activeForwardCc.setReceivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get());
            activeForwardCc.setActive(false);
            activeForwardCc.setCompletion_status(receivedLetterForwardRepo.findById(activeForwardCc.getId()).get().getCompletion_status());
            receivedLetterForwardRepo.save(activeForwardCc);
        }

        //EmployeePojo senderDetail = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        List<ReceivedLetterForward> forwards = new ArrayList<>();

        if (data.getReceiver() != null && !data.getReceiver().isEmpty()) {
            data.getReceiver().forEach(x -> {

                EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());
                String receipientSection = receipient != null ? receipient.getSection() != null ? receipient.getSection().getId().toString() : null : null;

                //for work on transferred employee letter
                Set<String> receiverPisCodes = new HashSet<>();

                receiverPisCodes.add(x.getReceiverPisCode());
                if (receipient.getSectionId() != null) {
                    if (this.getPreviousPisCode(x.getReceiverPisCode(), receipient.getSectionId()) != null)
                        receiverPisCodes.addAll(this.getPreviousPisCode(x.getReceiverPisCode(), receipient.getSectionId()));
                }

                ReceivedLetterForward findActiveUser = receivedLetterMapper.findActive(data.getReceivedLetterId(), receiverPisCodes, x.getReceiverSectionId());

                if (findActiveUser != null) {
                    if (isMultiple) {
                        isSuccess.set(false);
                        return;
                    } else {
                        throw new RuntimeException(receipient != null ? receipient.getNameNp() + " संग यो पत्र पहिले नै भएकोले उक्त पत्र फेरि पठाउन निषेध गरिएको छ|" : "Invalid");
                    }
                }

                ReceivedLetterForward receivedLetterForward = new ReceivedLetterForward().builder()
                        .receivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get())
                        .receiverPisCode(x.getReceiverPisCode())
                        .receiverSectionId(x.getReceiverSectionId())
                        .receiverDesignationCode(x.getReceiverDesignationCode())
                        .senderPisCode(tokenProcessorService.getPisCode())
                        .senderSectionId(tokenUser.getSectionId())
                        .senderDesignationCode(tokenUser.getFunctionalDesignationCode())
                        .description(data.getDescription())
                        .isReceived(true)
                        .senderParentCode(tokenProcessorService.getPisCode())
                        .isCc(false)
                        .isSeen(false)
                        .build();

                forwards.add(receivedLetterForward);

                notificationService.notificationProducer(
                        NotificationPojo.builder()
                                .moduleId(receivedLetter.getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(x.getReceiverPisCode())
                                .subject(customMessageSource.getNepali("manual.received"))
                                .detail(customMessageSource.getNepali("darta.forward", tokenUser.getNameNp(), receivedLetter.getSubject()))
                                .pushNotification(true)
                                .received(true)
                                .build()
                );
            });
        }

        if (data.getRec() != null && !data.getRec().isEmpty()) {
            for (ReceiverPojo receiver : data.getRec()) {
                ReceivedLetterForward receivedLetterForwardCc = new ReceivedLetterForward().builder()
                        .receivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get())
                        .receiverPisCode(receiver.getReceiverPisCode())
                        .receiverSectionId(receiver.getReceiverSectionId())
                        .receiverDesignationCode(receiver.getReceiverDesignationCode())
                        .senderPisCode(tokenProcessorService.getPisCode())
                        .senderSectionId(data.getSenderSectionId())
                        .senderDesignationCode(data.getSenderDesignationCode())
                        .description(data.getDescription())
                        .isReceived(true)
                        .isCc(true)
                        .isSeen(false)
                        .build();

                forwards.add(receivedLetterForwardCc);

                notificationService.notificationProducer(
                        NotificationPojo.builder()
                                .moduleId(data.getReceivedLetterId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(receiver.getReceiverPisCode())
                                .subject(customMessageSource.getNepali("manual.received"))
                                .detail(customMessageSource.getNepali("darta.forward", tokenUser.getNameNp(), receivedLetter.getSubject()))
                                .pushNotification(true)
                                .received(true)
                                .build()
                );
            }
        }

        receivedLetterForwardRepo.saveAll(forwards);
        return isSuccess.get();
    }

    @Override
    public Boolean forwardManualLetter(ReceivedLetterForwardRequestPojo data, Boolean isMultiple) {

        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo senderDetail = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check involved in section
        checkUserSection(senderDetail);

        AtomicReference<Boolean> isSuccess = new AtomicReference<>(true);

        ReceivedLetter receivedLetter = receivedLetterRepo.findById(data.getReceivedLetterId()).get();

        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);
        String tokenUserSection = tokenUser != null ? tokenUser.getSection() != null ? tokenUser.getSection().getId().toString() : null : null;

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        ReceivedLetterForward activeForward = receivedLetterMapper.findActive(data.getReceivedLetterId(), listPisCodes, tokenUserSection);

        if (activeForward != null) {
            activeForward.setReceivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get());
            activeForward.setActive(false);
            receivedLetterForwardRepo.save(activeForward);
        }

        ReceivedLetterForward activeForwardCc = receivedLetterMapper.findActiveCc(data.getReceivedLetterId(), tokenPisCode, tokenUserSection);
        if (activeForwardCc != null) {
            activeForwardCc.setReceivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get());
            activeForwardCc.setActive(false);
            receivedLetterForwardRepo.save(activeForwardCc);
        }


        // check if the user is creator or office_head of office where_letter is created
        if ((!tokenProcessorService.getRoles().contains("OFFICE_HEAD") &&
                receivedLetterMapper.checkIsReceiver(data.getReceivedLetterId(), listPisCodes, senderDetail.getSectionId()))
                //!listPisCodes.stream().anyMatch(code -> receivedLetterMapper.getReceiverPisCodes(data.getReceivedLetterId(), senderDetail.getSectionId()).contains(code)))
                || !receivedLetter.getOfficeCode().equals(tokenProcessorService.getOfficeCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid User");
        }

        List<ReceivedLetterForward> forwards = new ArrayList<>();

        if (data.getReceiver() != null && !data.getReceiver().isEmpty()) {
            data.getReceiver().forEach(x -> {

                EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());
                String receipientSection = receipient != null ? receipient.getSection() != null ? receipient.getSection().getId().toString() : null : null;

                ReceivedLetterForward findActiveUser = receivedLetterMapper.findAllActive(data.getReceivedLetterId(), x.getReceiverPisCode(), x.getReceiverSectionId());

                if (findActiveUser != null) {
                    if (isMultiple) {
                        isSuccess.set(false);
                        return;
                    } else {
                        throw new RuntimeException(receipient != null ? receipient.getNameNp() + " संग यो पत्र पहिले नै भएकोले उक्त पत्र फेरि पठाउन निषेध गरिएको छ|" : "Invalid");
                    }
                }

                ReceivedLetterForward receivedLetterForward = new ReceivedLetterForward().builder()
                        .receivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get())
                        .receiverPisCode(x.getReceiverPisCode())
                        .receiverSectionId(x.getReceiverSectionId())
                        .receiverDesignationCode(x.getReceiverDesignationCode())
                        .senderPisCode(tokenProcessorService.getPisCode())
                        .senderSectionId(senderDetail != null ? senderDetail.getSection() != null ? senderDetail.getSection().getId().toString() : null : null)
                        .senderDesignationCode(senderDetail != null ? senderDetail.getFunctionalDesignationCode() : null)
                        .description(data.getDescription())
                        .isReceived(false)
                        .senderParentCode(tokenProcessorService.getPisCode())
                        .isCc(false)
                        .isSeen(false)
                        .build();

                forwards.add(receivedLetterForward);

                notificationService.notificationProducer(
                        NotificationPojo.builder()
                                .moduleId(receivedLetter.getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(x.getReceiverPisCode())
                                .subject(customMessageSource.getNepali("manual.received"))
                                .detail(customMessageSource.getNepali("darta.forward", senderDetail != null ? senderDetail.getNameNp() : "", receivedLetter.getSubject()))
                                .pushNotification(true)
                                .received(true)
                                .build()
                );
            });
        }

        if (data.getRec() != null && !data.getRec().isEmpty()) {
            for (ReceiverPojo receiver : data.getRec()) {

                EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(receiver.getReceiverPisCode());
                String receipientSection = receipient != null ? receipient.getSection() != null ? receipient.getSection().getId().toString() : null : null;

                ReceivedLetterForward findActiveUser = receivedLetterMapper.findAllActive(data.getReceivedLetterId(), receiver.getReceiverPisCode(), receiver.getReceiverSectionId());

                if (findActiveUser != null)
                    throw new RuntimeException(receipient != null ? receipient.getNameNp() + " संग यो पत्र पहिले नै भएकोले उक्त पत्र फेरि पठाउन निषेध गरिएको छ|" : "Invalid");

                ReceivedLetterForward receivedLetterForwardCc = new ReceivedLetterForward().builder()
                        .receivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get())
                        .receiverPisCode(receiver.getReceiverPisCode())
                        .receiverSectionId(receiver.getReceiverSectionId())
                        .receiverDesignationCode(receiver.getReceiverDesignationCode())
                        .senderPisCode(tokenProcessorService.getPisCode())
                        .senderSectionId(data.getSenderSectionId())
                        .senderDesignationCode(data.getSenderDesignationCode())
                        .description(data.getDescription())
                        .isReceived(false)
                        .isCc(true)
                        .isSeen(false)
                        .build();

                forwards.add(receivedLetterForwardCc);

                notificationService.notificationProducer(
                        NotificationPojo.builder()
                                .moduleId(data.getReceivedLetterId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(receiver.getReceiverPisCode())
                                .subject(customMessageSource.getNepali("manual.received"))
                                .detail(customMessageSource.getNepali("darta.forward", senderDetail.getNameNp(), receivedLetter.getSubject()))
                                .pushNotification(true)
                                .received(true)
                                .build()
                );
            }
        }
        receivedLetterForwardRepo.saveAll(forwards);

        return isSuccess.get();
    }

    @Override
    public List<ReceivedLetterForward> forwardReceivedLetterCc(ReceivedLetterForwardRequestPojo data) {

        List<ReceivedLetterForward> forwards = new ArrayList<>();

        ReceivedLetter receivedLetter = receivedLetterRepo.findById(data.getReceivedLetterId()).get();
        EmployeePojo senderDetail = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        for (ReceiverPojo receiver : data.getRec()) {

            EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(receiver.getReceiverPisCode());
            String receipientSection = receipient != null ? receipient.getSection() != null ? receipient.getSection().getId().toString() : null : null;

            ReceivedLetterForward findActiveUser = receivedLetterMapper.findAllActive(data.getReceivedLetterId(), receiver.getReceiverPisCode(), receiver.getReceiverSectionId());

            if (findActiveUser != null)
                throw new RuntimeException(receipient != null ? receipient.getNameNp() + " संग यो पत्र पहिले नै भएकोले उक्त पत्र फेरि पठाउन निषेध गरिएको छ|" : "Invalid");

            ReceivedLetterForward receivedLetterForward = new ReceivedLetterForward().builder()
                    .receivedLetter(receivedLetterRepo.findById(data.getReceivedLetterId()).get())
                    .receiverPisCode(receiver.getReceiverPisCode())
                    .receiverSectionId(receiver.getReceiverSectionId())
                    .receiverDesignationCode(receiver.getReceiverDesignationCode())
                    .senderPisCode(tokenProcessorService.getPisCode())
                    .senderSectionId(data.getSenderSectionId())
                    .senderDesignationCode(data.getSenderDesignationCode())
                    .description(data.getDescription())
                    .isReceived(data.getIsReceived())
                    .isCc(true)
                    .isSeen(false)
                    .build();

            forwards.add(receivedLetterForward);

            notificationService.notificationProducer(
                    NotificationPojo.builder()
                            .moduleId(data.getReceivedLetterId())
                            .module(MODULE_APPROVAL_KEY)
                            .sender(tokenProcessorService.getPisCode())
                            .receiver(receiver.getReceiverPisCode())
                            .subject(customMessageSource.getNepali("manual.received"))
                            .detail(customMessageSource.getNepali("darta.forward", senderDetail.getNameNp(), receivedLetterForward.getReceivedLetter().getSubject()))
                            .pushNotification(true)
                            .received(true)
                            .build()
            );
        }
        return receivedLetterForwardRepo.saveAll(forwards);
    }

    @Override
    public void updateStatus(StatusPojo statusPojo) {

        String tokenPisCode = tokenProcessorService.getPisCode();

        if (statusPojo.getStatus().equals(Status.IP) || statusPojo.getStatus().equals(Status.FN)) {
            ReceivedLetterForward data = receivedLetterForwardRepo.findById(statusPojo.getId()).get();

            EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

            //check user is involved in any section or not
            checkUserSection(employeePojo);

            //check user is receiver or not
            checkIsReceiver(tokenPisCode, employeePojo.getSectionId(), data.getReceivedLetter().getId());

            OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());

            DelegationTableMapper delegationTableMapper = DelegationTableMapper.builder()
                    .tableName(DcTablesEnum.RECEIVED_FORWARD)
                    .receivedLetterForward(data)
                    .statusFrom(data.getCompletion_status())
                    .statusTo(statusPojo.getStatus())
                    .build();

            delegationTableMapperRepo.save(delegationTableMapper);

            data.setCompletion_status(statusPojo.getStatus());
            receivedLetterForwardRepo.save(data);

            List<ReceivedLetterForward> dataTwo = receivedLetterForwardRepo.findReceivedLetterByPisAndSection(data.getReceivedLetter().getId(), data.getReceiverPisCode(), data.getReceiverSectionId());
            List<DelegationTableMapper> delegationTableMappers = new ArrayList<>();
            if (dataTwo != null) {
                dataTwo.forEach(x -> {
                            DelegationTableMapper delegationTableMapper2 = DelegationTableMapper.builder()
                                    .tableName(DcTablesEnum.RECEIVED_FORWARD)
                                    .receivedLetterForward(x)
                                    .statusFrom(x.getCompletion_status())
                                    .statusTo(statusPojo.getStatus())
                                    .build();
                            delegationTableMappers.add(delegationTableMapper2);
                            x.setCompletion_status(statusPojo.getStatus());
                        }
                );
                receivedLetterForwardRepo.saveAll(dataTwo);
                delegationTableMapperRepo.saveAll(delegationTableMappers);
            }

            if (statusPojo.getDescription() != null && !statusPojo.getDescription().equals("")) {
                ReceivedLetterMessage receivedLetterMessage = new ReceivedLetterMessage().builder()
                        .receivedLetterForward(data)
                        .comment(statusPojo.getDescription())
                        .pisCode(tokenProcessorService.getPisCode())
                        .build();
                receivedLetterMessageRepo.save(receivedLetterMessage);
            }

            if (officeHeadPojo != null) {
                notificationService.notificationProducer(
                        NotificationPojo.builder()
                                .moduleId(data.getReceivedLetter().getId())
                                .module(MODULE_APPROVAL_KEY)
                                .sender(tokenProcessorService.getPisCode())
                                .receiver(officeHeadPojo.getPisCode())
                                .subject(customMessageSource.getNepali("manual.received"))
                                .detail(customMessageSource.getNepali("darta.status", employeePojo.getNameNp(), data.getReceivedLetter().getSubject()))
                                .pushNotification(true)
                                .received(false)
                                .build()
                );
            }

            Boolean isAllLetterFinalized = receivedLetterForwardRepo.isAllLetterFinalized(data.getReceivedLetter().getId());
            if (isAllLetterFinalized) {
                receivedLetterRepo.updateStatus(data.getReceivedLetter().getId(), "FN");
            }
        } else
            throw new RuntimeException("Invalid");
    }

    @Override
    public ReceivedLetter saveManual(ManualReceivedRequestPojo data) {

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        String activeFiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
        OfficeHeadPojo officeHead = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());


        if (employeePojo != null && employeePojo.getSection() == null)
            throw new RuntimeException("प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ");

        ReceivedLetter receivedLetter = new ReceivedLetter().builder()
                .letterPriority(data.getLetterPriority())
                .letterPrivacy(data.getLetterPrivacy())
                .pisCode(tokenProcessorService.getPisCode())
                .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                .officeCode(tokenProcessorService.getOfficeCode())
                .fiscalYearCode(data.getFiscalYearCode())
                .senderOfficeCode(data.getSenderOfficeCode() != null ? data.getSenderOfficeCode() : tokenProcessorService.getOfficeCode())
                .registrationNo(initialService.getDartaNumber(tokenProcessorService.getOfficeCode()))
                .referenceNo(data.getReferenceNo())
                .dispatchNo(data.getDispatchNo())
                .dispatchDateEn(data.getDispatchDateEn())
                .dispatchDateNp(data.getDispatchDateNp())
                .subject(data.getSubject())
                .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() != null ? employeePojo.getFunctionalDesignationCode() : null : null)
                .entryType(true)
                .manualIsCc(data.getManualIsCc() != null && data.getManualIsCc())
                .isDraft(false).build();

        if (data.getLetterPrivacy().equals(LetterPrivacy.NM)) {
            if (data.getDocument() != null && !data.getDocument().isEmpty())
                this.processDocument(data.getDocument(), receivedLetter, true, employeePojo, activeFiscalYearCode);
            if (data.getSupporting() != null && !data.getSupporting().isEmpty())
                this.processUpdateMultipleDocument(data.getSupporting(), receivedLetter, false, employeePojo, activeFiscalYearCode);
        }

        receivedLetterRepo.save(receivedLetter);

        if (data.getTaskId() != null || data.getProjectId() != null) {
            ExternalRecords records = new ExternalRecords().builder()
                    .receivedLetter(receivedLetter)
                    .projectId(data.getProjectId())
                    .taskId(data.getTaskId())
                    .build();
            externalRecordsRepo.save(records);
        }

        ManualReceivedLetterDetail manualReceivedLetterDetail = new ManualReceivedLetterDetail().builder()
                .senderName(data.getSenderDetail().getSenderName())
                .sectionOfficeSectionSubsection(data.getSenderDetail().getSenderOfficeSectionSubsection())
                .senderAddress(data.getSenderDetail().getSenderAddress())
                .senderEmail(data.getSenderDetail().getSenderEmail())
                .senderPhoneNo(data.getSenderDetail().getSenderPhoneNo())
                .manualReceivedLetterType(data.getSenderDetail().getManualReceivedLetterType())
                .sectionCode(data.getSenderDetail().getSectionCode())
                .receivedLetter(receivedLetter)
                .build();
        manualReceivedLetterDetailRepo.save(manualReceivedLetterDetail);

        notificationService.notificationProducer(
                NotificationPojo.builder()
                        .moduleId(receivedLetter.getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(tokenProcessorService.getPisCode())
                        .receiver(officeHead.getPisCode())
                        .subject(customMessageSource.getNepali("manual.received"))
                        .detail(customMessageSource.getNepali("darta.create", receivedLetter.getRegistrationNo(), receivedLetter.getSubject()))
                        .pushNotification(true)
                        .received(true)
                        .build()
        );

        return receivedLetter;
    }

    @Override
    public void updateManuallyReceivedLetter(ManualReceivedRequestPojo manualReceivedRequestPojo) {

        String tokenPisCode = tokenProcessorService.getPisCode();
        ReceivedLetterResponsePojo manualLetter = receivedLetterMapper.getReceivedLetter(manualReceivedRequestPojo.getId());

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenPisCode);
        String activeFiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();

        if (manualLetter.getForwards() != null && !manualLetter.getForwards().isEmpty() && !isReverted(manualLetter.getForwards(), manualLetter)) {
            throw new RuntimeException("Already Forwarded, Cannot Update");
        }

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        // check if the user is creator or office_head of office where_letter is created
        if ((!tokenProcessorService.getRoles().contains("OFFICE_HEAD") &&
                !listPisCodes.contains(manualLetter.getPisCode()))
                || !manualLetter.getOfficeCode().equals(tokenProcessorService.getOfficeCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid User");
        }

        ReceivedLetter update = receivedLetterRepo.findById(manualReceivedRequestPojo.getId()).get();
//        ReceivedLetterResponsePojo parentChild = getReceivedLetter(manualReceivedRequestPojo.getId());
        ManualReceivedLetterDetail childUpdate = manualReceivedLetterDetailRepo.findById(update.getManualReceivedLetterDetail().getId()).get();

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();

        ManualReceivedLetterDetail manualReceivedLetterDetail = new ManualReceivedLetterDetail().builder()
                .senderPhoneNo(manualReceivedRequestPojo.getSenderDetail().getSenderPhoneNo())
                .senderEmail(manualReceivedRequestPojo.getSenderDetail().getSenderEmail())
                .senderAddress(manualReceivedRequestPojo.getSenderDetail().getSenderAddress())
                .sectionOfficeSectionSubsection(manualReceivedRequestPojo.getSenderDetail().getSenderOfficeSectionSubsection())
                .senderName(manualReceivedRequestPojo.getSenderDetail().getSenderName())
                .manualReceivedLetterType(manualReceivedRequestPojo.getSenderDetail().getManualReceivedLetterType())
                .sectionCode(manualReceivedRequestPojo.getSenderDetail().getSectionCode())
                .build();
        try {
            beanUtilsBean.copyProperties(childUpdate, manualReceivedLetterDetail);
        } catch (Exception e) {
            throw new RuntimeException("Child does not exist");
        }

        ReceivedLetter receivedLetter = new ReceivedLetter().builder()
                .subject(manualReceivedRequestPojo.getSubject())
                .dispatchDateNp(manualReceivedRequestPojo.getDispatchDateNp())
                .dispatchDateEn(manualReceivedRequestPojo.getDispatchDateEn())
                .letterPriority(manualReceivedRequestPojo.getLetterPriority())
                .letterPrivacy(manualReceivedRequestPojo.getLetterPrivacy())
                .manualIsCc(manualReceivedRequestPojo.getManualIsCc() != null &&
                        manualReceivedRequestPojo.getManualIsCc())
                .dispatchNo(manualReceivedRequestPojo.getDispatchNo())
                .manualReceivedLetterDetail(childUpdate)
                .senderOfficeCode(manualReceivedRequestPojo.getSenderOfficeCode())
                .build();

        try {
            beanUtilsBean.copyProperties(update, receivedLetter);
        } catch (Exception e) {
            throw new RuntimeException("It does not exist");
        }

        if (update.getDocumentMasterId() != null) {
            if (manualReceivedRequestPojo.getDocument() != null && !manualReceivedRequestPojo.getDocument().isEmpty())
                this.processUpdateMultipleDocument(manualReceivedRequestPojo.getDocument(), update, true, employeePojo, activeFiscalYearCode);
        } else {
            if (manualReceivedRequestPojo.getDocument() != null && !manualReceivedRequestPojo.getDocument().isEmpty())
                this.processDocument(manualReceivedRequestPojo.getDocument(), update, true, employeePojo, activeFiscalYearCode);
        }
        if (manualReceivedRequestPojo.getSupporting() != null && !manualReceivedRequestPojo.getSupporting().isEmpty())
            this.processUpdateMultipleDocument(manualReceivedRequestPojo.getSupporting(), update, false, employeePojo, activeFiscalYearCode);

        if (manualReceivedRequestPojo.getDocumentsToRemove() != null && !manualReceivedRequestPojo.getDocumentsToRemove().isEmpty())
            this.deleteDocuments(manualReceivedRequestPojo.getDocumentsToRemove());

        receivedLetterRepo.save(update);
    }

    public boolean isReverted(List<ForwardResponsePojo> forwards, ReceivedLetterResponsePojo receivedLetter) {
        String tokenPisCode = tokenProcessorService.getPisCode();
        return forwards.stream().anyMatch(s -> s.getIsActive() && s.getIsReverted() &&
                s.getReceiverPisCode().equals(tokenPisCode) && receivedLetter.getPisCode().equals(tokenPisCode));
    }

    @Override
    public void deleteReceivedLetter(Long letterId) {

        String token = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(token);
        checkUserSection(employeePojo);

        Set<String> listPisCodes = new HashSet<>();

        listPisCodes.add(token);
        if (employeePojo.getSectionId() != null) {
            if (getPreviousPisCode(token, employeePojo.getSectionId()) != null)
                listPisCodes.addAll(getPreviousPisCode(token, employeePojo.getSectionId()));
        }

        ReceivedLetter receivedLetter = receivedLetterRepo.findById(letterId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Letter not found"));
        ReceivedLetterForward receivedLetterForward = receivedLetterForwardRepo.getLatestForward(letterId);
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());

        if (listPisCodes.contains(receivedLetter.getPisCode())
                && officeHeadPojo != null
                && officeHeadPojo.getPisCode() != null
                && receivedLetterForward != null
                && receivedLetterForward.getReceiverPisCode() != null
                && officeHeadPojo.getPisCode().equals(receivedLetterForward.getReceiverPisCode())
                && receivedLetterForward.getIsReverted()) {
            receivedLetterRepo.softDelete(letterId);
        } else {
            throw new RuntimeException("Can't archive");
        }
    }

    @Override
    public Long saveMessage(ReceivedLetterMessageRequestPojo receivedLetterMessageRequestPojo) {

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        ReceivedLetterForward data = receivedLetterForwardRepo.findById(receivedLetterMessageRequestPojo.getForwardId()).get();

        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());


        if (data.getReceivedLetter().getStatus().equals(Status.FN)) {
            throw new RuntimeException("Letter Already Finalized");
        }

        if (listPisCodes.contains(data.getReceiverPisCode())) {
            ReceivedLetterMessage receivedLetterMessage = new ReceivedLetterMessage().builder()
                    .receivedLetterForward(data)
                    .comment(receivedLetterMessageRequestPojo.getComment())
                    .pisCode(tokenProcessorService.getPisCode())
                    .build();
            receivedLetterMessageRepo.save(receivedLetterMessage);
        } else
            throw new RuntimeException("Invalid User");

        notificationService.notificationProducer(
                NotificationPojo.builder()
                        .moduleId(data.getReceivedLetter().getId())
                        .module(MODULE_APPROVAL_KEY)
                        .sender(tokenProcessorService.getPisCode())
                        .receiver(officeHeadPojo.getPisCode())
                        .subject(customMessageSource.getNepali("manual.received"))
                        .detail(customMessageSource.getNepali("darta.comment", employeePojo.getNameNp(), data.getReceivedLetter().getSubject()))
                        .pushNotification(true)
                        .received(false)
                        .build()
        );

        return data.getId();
    }

    @Override
    public ReceivedLetterResponsePojo getReceivedLetter(Long id, GetRowsRequest request) {

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user is involved in any section or not
        checkUserSection(tokenUser);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        System.out.println("Received Letter id: " + id + " and section: " + tokenUser.getSectionId());
        String roles = tokenProcessorService.getRoles();
        if (roles.contains("OFFICE_HEAD") || roles.contains("DARTACREATOR") || roles.contains("DARTAVIEWER")) {
            List<String> involvedOffices = receivedLetterMapper.getInvolvedOffices(id);

            System.out.println("Involved offices in received letter: " + involvedOffices);
            if (!involvedOffices.contains(tokenProcessorService.getOfficeCode()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
        } else {
            //List<String> involvedUsers = receivedLetterMapper.getAllInvolvedUsers(id, tokenUser.getSectionId());
            String strPisCodes = this.convertListToString(listPisCodes);
            System.out.println("previousPisCodes: " + strPisCodes);
            //if (!listPisCodes.stream().anyMatch(pis -> involvedUsers.contains(pis)))
            if (!receivedLetterMapper.checkInvolvedDarta(id, tokenUser.getSectionId(), strPisCodes))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");
        }

        // IsInvolvedDto result = receivedLetterMapper.is_involved_user_received_letter(id, tokenProcessorService.getPisCode(), isHead, tokenProcessorService.getOfficeCode());
        // if (result != null && !result.isInvolved())
        //   throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Does not have permission to view letter");

        ReceivedLetterResponsePojo manualLetter = receivedLetterMapper.getReceivedLetter(id);
        manualLetter.setPreviousPisCodes(listPisCodes);

        Boolean isHashed = manualLetter != null ? manualLetter.getHash_content() == null ? Boolean.FALSE : Boolean.TRUE : Boolean.FALSE;

        if (manualLetter.getDelegatedId() != null) {
            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(manualLetter.getDelegatedId());
            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                manualLetter.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));

                // here need to task
                if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment() && delegationResponsePojo.getFromSection() != null) {
                    manualLetter.setIsReassignment(delegationResponsePojo.getIsReassignment());
                    manualLetter.setReassignmentSection(delegationResponsePojo.getFromSection());
                }
            }
        } else
            manualLetter.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(manualLetter.getPisCode()));

        manualLetter.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
        manualLetter.setCreatedTimeNp(dateConverter.convertBSToDevnagari(manualLetter.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
        OfficePojo officePojo = userMgmtServiceData.getOfficeDetail(manualLetter.getSenderOfficeCode());
        manualLetter.setOfficeDetails(userMgmtServiceData.getOfficeDetail(manualLetter.getSenderOfficeCode()));

        OfficePojo officePojoTo = userMgmtServiceData.getOfficeDetail(tokenProcessorService.getOfficeCode());

        if (officePojo != null) {
            manualLetter.setSenderOfficeName(officePojo.getNameEn());
            manualLetter.setSenderOfficeNameNp(officePojo.getNameNp());
        }

        String officeHeadPisCode = null;
        OfficeHeadPojo officeHeadPojo = userMgmtServiceData.getOfficeHeadDetail(manualLetter.getSenderOfficeCode());
        if (officeHeadPojo != null && officeHeadPojo.getPisCode() != null)
            officeHeadPisCode = officeHeadPojo.getPisCode();

        for (ForwardResponsePojo data : manualLetter.getForwards()) {
            data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
            data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            data.setCreatedTimeNp(dateConverter.convertBSToDevnagari(data.getCreatedDate().toLocalDateTime().toLocalTime().toString()));

            data.setIsDelegated(false);
            EmployeeMinimalPojo senderMinimal = null;
            if (data.getSenderPisCode() != null)
                senderMinimal = userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode());


            if (data.getDelegatedId() != null) {

                if (data.getSenderPisCode().equals(officeHeadPisCode))
                    data.setIsDelegated(true);

                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(data.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {

                    data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                    data.setSenderDesignationNameNp(senderMinimal != null ? senderMinimal.getFunctionalDesignation() != null ? senderMinimal.getFunctionalDesignation().getNameN() : null : null);

                    // here need to task
                    if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment() && delegationResponsePojo.getFromSection() != null) {

                        data.setIsReassignment(delegationResponsePojo.getIsReassignment());
                        data.setReassignmentSection(delegationResponsePojo.getFromSection());
                    }
                }
            } else
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));

            for (ReceivedLetterMessageRequestPojo pojo : data.getComments()) {
                pojo.setCreatedDateNp(dateConverter.convertAdToBs(pojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                pojo.setCreatedTimeNp(dateConverter.convertBSToDevnagari(pojo.getCreatedDate().toLocalDateTime().toLocalTime().toString()));
                String toEmployee = null;
                if (pojo.getDelegatedId() != null) {
                    DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(pojo.getDelegatedId());
                    toEmployee = delegationResponsePojo.getToEmployee().getCode();
                    pojo.setIsDelegated(Boolean.TRUE);
                    if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment() && delegationResponsePojo.getFromSection() != null) {
                        pojo.setIsDelegated(Boolean.FALSE);
                        pojo.setIsReassignment(Boolean.TRUE);
                        pojo.setReassignmentSection(delegationResponsePojo.getFromSection());
                    }

                }
                pojo.setCommentCreator(userMgmtServiceData.getEmployeeDetail(pojo.getPisCode() != null ? pojo.getPisCode() :
                        data.getReceiverPisCode()));
            }

            if (data.getIsActive() != null && data.getIsActive()) {
                if (data.getIsSeen() != null && !data.getIsSeen()) {
                    if (tokenProcessorService.getPisCode().equals(data.getReceiverPisCode())) {
                        notificationService.notificationProducer(
                                NotificationPojo.builder()
                                        .moduleId(manualLetter.getId())
                                        .module(MODULE_APPROVAL_KEY)
                                        .sender(tokenProcessorService.getPisCode())
                                        .receiver(data.getSenderPisCode())
                                        .subject(customMessageSource.getNepali("manual.received"))
                                        .detail(customMessageSource.getNepali("darta.view", data.getReceiver().getEmployeeNameNp(), manualLetter.getSubject()))
                                        .pushNotification(true)
                                        .received(false)
                                        .build()
                        );
                        receivedLetterForwardRepo.setSeen(manualLetter.getId(), tokenProcessorService.getPisCode());
                    }
                }
            }
        }

//        String officeHeadPisCode = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode()).getPisCode();

        if ((manualLetter.getLetterPrivacy().equals(LetterPrivacy.HC) || manualLetter.getLetterPrivacy().equals(LetterPrivacy.CN)) && !roles.contains("OFFICE_HEAD"))
            manualLetter.setDocument(null);
        else if (manualLetter.getDocument() != null && !manualLetter.getDocument().isEmpty()) {
            manualLetter.setDocument(this.getActiveDocuments(manualLetter.getDocument()));
        }

        if (!manualLetter.getEntryType()) {

            EmployeePojo signatureUser = null;
            DesignationPojo signatureDesignation = null;
            SectionPojo sectionPojo = null;

            //variable for delegated info
            DetailPojo reassignmentSection = null;
            Boolean isReassignment = Boolean.FALSE;
            Boolean isDelegated = Boolean.FALSE;
            DetailPojo requesterSection = null;
            Boolean includeSectionInLetter = Boolean.FALSE;
            if (manualLetter.getDispatchId() != null) {
                SignatureData signatureData = signatureDataRepo.getByDispatchId(manualLetter.getDispatchId());

                includeSectionInLetter = signatureData != null ?
                        signatureData.getIncludeSectionInLetter() != null ?
                                signatureData.getIncludeSectionInLetter()
                                : Boolean.FALSE : Boolean.FALSE;

                if (signatureData != null) {

                    if (signatureData.getDelegatedId() != null) {
                        DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(signatureData.getDelegatedId());
                        if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null) {
                            signatureUser = userMgmtServiceData.getEmployeeDetail(delegationResponsePojo.getToEmployee().getCode());

//                            if (delegationResponsePojo != null
//                                    && delegationResponsePojo.getFromEmployee() != null
//                                    && delegationResponsePojo.getFromEmployee().getCode() != null
//                                    && delegationResponsePojo.getFromEmployee().getCode().equals(officeHeadPisCode))
//                                isDelegated = Boolean.TRUE;

                            // here need to task
                            if (delegationResponsePojo.getIsReassignment() != null && delegationResponsePojo.getIsReassignment()) {
                                isReassignment = Boolean.TRUE;
                                if (delegationResponsePojo.getFromSection() != null) {
                                    reassignmentSection = delegationResponsePojo.getFromSection();
                                    requesterSection = delegationResponsePojo.getFromSection();
                                }
                            } else {
                                isDelegated = Boolean.TRUE;
                            }
                            if (delegationResponsePojo.getFromSection() != null) {
                                requesterSection = delegationResponsePojo.getFromSection();
                            }
                            if (signatureUser.getFunctionalDesignation() != null) {
                                signatureDesignation = new DesignationPojo();
                                signatureDesignation.setNameEn(signatureUser.getFunctionalDesignation().getName());
                                signatureDesignation.setNameNp(signatureUser.getFunctionalDesignation().getNameN());

                                signatureDesignation.setNameEn(signatureUser.getFunctionalDesignation().getName() != null ?
                                        isDelegated ? DELEGATED_EN + ", " + signatureUser.getFunctionalDesignation().getName() :
                                                isReassignment ? ADDITIONAL_RESPONSIBILITY_EN + ", " + reassignmentSection.getNameEn() + ", " + signatureUser.getFunctionalDesignation().getName() :
                                                        signatureUser.getFunctionalDesignation().getName() : "");
                                signatureDesignation.setNameNp(signatureUser.getFunctionalDesignation().getNameN() != null ?
                                        isDelegated ? DELEGATED_NEP + ", " + signatureUser.getFunctionalDesignation().getNameN() :
                                                isReassignment ? ADDITIONAL_RESPONSIBILITY_NEP + ", " + reassignmentSection.getNameNp() + ", " + signatureUser.getFunctionalDesignation().getNameN() :
                                                        signatureUser.getFunctionalDesignation().getNameN() : "");

                            }
                        }
                    } else {
                        signatureUser = userMgmtServiceData.getEmployeeDetail(signatureData.getPisCode());

                        if (signatureData.getDesignationCode() != null) {
                            signatureDesignation = userMgmtServiceData.getDesignationDetail(signatureData.getDesignationCode());
                            manualLetter.setSignatureDesignationData(signatureDesignation);
                        }
                        if (signatureData.getSectionCode() != null) {
                            SectionPojo sectionPojoDetail = userMgmtServiceData.getSectionDetail(Long.parseLong(signatureData.getSectionCode()));
                            if (sectionPojoDetail != null && requesterSection == null) {
                                requesterSection = new DetailPojo();
                                requesterSection.setNameEn(sectionPojoDetail.getNameEn());
                                requesterSection.setNameNp(sectionPojoDetail.getNameNp());
                            }
                        }
                    }

                    if (signatureData.getIncludeSectionId() != null && signatureData.getIncludeSectionId() && signatureData.getIncludedSectionId() != null)
                        sectionPojo = userMgmtServiceData.getSectionDetail(Long.parseLong(signatureData.getIncludedSectionId()));

                    String reviewContent;
                    DispatchLetterReview dispatchLetterReview = dispatchLetterReviewRepo.findLatestByUser(signatureData.getPisCode(), manualLetter.getDispatchId());
                    if (signatureData.getPisCode() != null
                            && manualLetter.getRemarksPisCode() != null
                            && signatureData.getPisCode().equals(manualLetter.getRemarksPisCode())
                            && manualLetter.getRemarksSignatureIsActive() != null
                            && manualLetter.getRemarksSignatureIsActive()) {
                        reviewContent = manualLetter.getContent() + " " + manualLetter.getRemarks();
                        manualLetter.setActiveSignatureData(verifySignatureService.verify(reviewContent, manualLetter.getRemarksSignature(), isHashed));
                    }
                    if (dispatchLetterReview != null
                            && signatureData.getPisCode() != null
                            && dispatchLetterReview.getSenderPisCode() != null
                            && dispatchLetterReview.getSenderPisCode().equals(signatureData.getPisCode())
                            && dispatchLetterReview.getRemarksSignatureIsActive() != null
                            && dispatchLetterReview.getRemarksSignatureIsActive()) {
                        reviewContent = manualLetter.getContent() + " " + dispatchLetterReview.getRemarks();
                        Boolean isHashedReview = dispatchLetterReview.getHashContent() == null ? Boolean.FALSE : Boolean.TRUE;
                        manualLetter.setActiveSignatureData(verifySignatureService.verify(reviewContent, dispatchLetterReview.getRemarksSignature(), isHashedReview));
                    }

                }
            }

            if (manualLetter.getSignatureIsActive() != null && manualLetter.getSignatureIsActive()) {
                VerificationInformation verificationInformation = verifySignatureService.verify(manualLetter.getContent(), manualLetter.getSignature(), isHashed);
                manualLetter.setVerificationInformation(verificationInformation);
            }

            boolean isEnglish = manualLetter.getIsEnglish() != null ? manualLetter.getIsEnglish() : false;
            String lang = "NEP";

            if (isEnglish) {
                lang = "EN";
            }

            String img = "data:image/png;base64,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";
            EmployeePojo officeHeadDetail = userMgmtServiceData.getEmployeeDetail(officeHeadPojo != null ? officeHeadPojo.getPisCode() : null);
//            OfficePojo officeDetail = userMgmtServiceData.getOfficeDetail(manualLetter.getOfficeCode());
            OfficePojo parentOffice = userMgmtServiceData.getOfficeDetail(officePojo.getParentCode());
            if (parentOffice != null && parentOffice.getCode().equals("8886"))
                parentOffice = null;

            List<DispatchLetterInternalDTO> internalDTOS = dispatchLetterMapper.getInternalsByDispatchId(manualLetter.getDispatchId());
            List<DispatchLetterExternalDTO> externalDTOS = dispatchLetterMapper.getExternalsByDispatchId(manualLetter.getDispatchId());
            Boolean hasSubject = dispatchLetterMapper.checkHasSubjectByDispatchId(manualLetter.getDispatchId());
            List<Bodartha> bodartha = new ArrayList<>();
            List<Bodartha> karyartha = new ArrayList<>();
            List<Bodartha> sadarBodartha = new ArrayList<>();
            List<RequestTo> requestTos = new ArrayList<>();
            final RequestTo[] req = {null};
            final String shree = "श्री ";

            if (internalDTOS != null && !internalDTOS.isEmpty()) {
                internalDTOS.forEach(x -> {

                    Boolean isGroupName = x.getIsGroupName() != null ? x.getIsGroupName() : Boolean.FALSE;
                    String groupNameEn = "";
                    String groupNameNp = "";
                    if (isGroupName && x.getGroupId() != null) {
                        OfficeGroupPojo officeGroupPojo = userMgmtServiceData.getOfficeGroupById(x.getGroupId());
                        groupNameEn = officeGroupPojo.getNameEn();
                        groupNameNp = officeGroupPojo.getNameNp();
                    }

                    Boolean isSectionName = x.getIsSectionName() != null ? x.getIsSectionName() : Boolean.FALSE;

                    boolean isSaluted = false;
                    if (x.getSalutation() != null)
                        isSaluted = true;

                    if (x.getWithinOrganization() != null ? x.getWithinOrganization() : x.getInternalReceiverPiscode() != null ? Boolean.TRUE : Boolean.FALSE) {
                        EmployeePojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetail(x.getInternalReceiverPiscode());
                        if (employeeMinimalPojo != null) {
                            x.setEmployeeName(employeeMinimalPojo.getNameEn() != null ? StringUtils.capitalize(employeeMinimalPojo.getNameEn().toLowerCase()) : "");
                            x.setEmployeeNameNp(employeeMinimalPojo.getNameNp() != null ? employeeMinimalPojo.getNameNp() : "");
                            x.setSectionName(employeeMinimalPojo.getSection() != null ? employeeMinimalPojo.getSection().getName() != null ? StringUtils.capitalize(employeeMinimalPojo.getSection().getName().toLowerCase()) : "" : "");
                            x.setSectionNameNp(employeeMinimalPojo.getSection() != null ? employeeMinimalPojo.getSection().getNameN() != null ? employeeMinimalPojo.getSection().getNameN() : "" : "");
                            x.setDesignationName(employeeMinimalPojo.getFunctionalDesignation() != null ? employeeMinimalPojo.getFunctionalDesignation().getName() != null ? StringUtils.capitalize(employeeMinimalPojo.getFunctionalDesignation().getName().toLowerCase()) : "" : "");
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

                        OfficePojo officePojo1 = userMgmtServiceData.getOfficeDetail(x.getInternalReceiverOfficeCode());
                        if (officePojo1 != null) {
                            x.setEmployeeName(officePojo1.getNameEn() != null ? StringUtils.capitalize(officePojo1.getNameEn().toLowerCase()) : "");
                            x.setEmployeeNameNp(officePojo1.getNameNp() != null ? officePojo1.getNameNp() : "");
                            x.setDesignationName(officePojo1.getAddressEn() != null ? officePojo1.getAddressEn() : "");
                            x.setDesignationNameNp(officePojo1.getAddressNp() != null ? officePojo1.getAddressNp() : "");
                        } else {
                            x.setEmployeeName("");
                            x.setEmployeeNameNp("");
                            x.setDesignationName("");
                            x.setDesignationNameNp("");
                        }

                        if (x.getInternalReceiverSectionId() != null) {
                            SectionPojo sectionPojo1 = userMgmtServiceData.getSectionDetail(Long.parseLong(x.getInternalReceiverSectionId()));
                            if (sectionPojo1 != null) {
                                x.setSectionName(sectionPojo1.getNameEn() != null ? StringUtils.capitalize(sectionPojo1.getNameEn().toLowerCase()) : "");
                                x.setSectionNameNp(sectionPojo1.getNameNp() != null ? sectionPojo1.getNameNp() : "");
                            } else {
                                x.setSectionName("");
                                x.setSectionNameNp("");
                            }
                        } else {
                            x.setSectionName(x.getInternalReceiverSectionName() != null ? StringUtils.capitalize(x.getInternalReceiverSectionName().toLowerCase()) : "");
                            x.setSectionNameNp(x.getInternalReceiverSectionName() != null ? x.getInternalReceiverSectionName() : "");
                        }

                    }

                    String designationEn = "";
                    String designationNp = "";

                    if (x.getDesignationName() != null)
                        designationEn = " ," + StringUtils.capitalize(x.getDesignationName().toLowerCase());
                    if (x.getDesignationNameNp() != null)
                        designationNp = " ," + x.getDesignationNameNp();

                    if (x.getInternalReceiver() != null && x.getInternalReceiver()) {


                        EmployeePojo receiverDetail = userMgmtServiceData.getEmployeeDetail(x.getInternalReceiverPiscode());

                        RequestTo requestTo = new RequestTo().builder()
                                .office(isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() + designationEn : isGroupName ? groupNameNp : isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() + designationNp)
                                .sectionName(isSectionName ? receiverDetail.getSection() != null ? isEnglish ? receiverDetail.getSection().getName() : "श्री " + receiverDetail.getSection().getNameN() : null : null)
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

                        if ((x.getWithinOrganization() != null ? !x.getWithinOrganization() : x.getInternalReceiverPiscode() == null ? Boolean.TRUE : Boolean.FALSE)
                                && manualLetter.getIsReceiver() != null
                                && manualLetter.getIsReceiver()
                                && manualLetter.getSenderOfficeCode() != null
                                && manualLetter.getSenderOfficeCode().equals(tokenProcessorService.getOfficeCode())) {

                            req[0] = new RequestTo().builder()
                                    .office(isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() + designationEn : isGroupName ? groupNameNp : isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() + designationNp)
                                    .sectionName(isSectionName ? receiverDetail.getSection() != null ? isEnglish ? receiverDetail.getSection().getName() : "श्री " + receiverDetail.getSection().getNameN() : null : null)
                                    .isSectionName(isSectionName)
                                    .isGroupName(isGroupName)
                                    .build();

                            if (isSaluted || isGroupName || isSectionName) {
                                req[0].setAddress("");
                            } else {
                                req[0].setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                            }
                        }

                        if ((x.getWithinOrganization() != null ? !x.getWithinOrganization() : x.getInternalReceiverPiscode() == null ? Boolean.TRUE : Boolean.FALSE)
                                && manualLetter.getIsReceiver() != null
                                && manualLetter.getIsReceiver()
                                && manualLetter.getOfficeCode() != null
                                && manualLetter.getOfficeCode().equals(tokenProcessorService.getOfficeCode())
                                && x.getInternalReceiverOfficeCode() != null
                                && manualLetter.getOfficeCode().equals(x.getInternalReceiverOfficeCode())) {

                            req[0] = new RequestTo().builder()
                                    .office(isEnglish ? isGroupName ? groupNameEn : x.getEmployeeName() + designationEn : isGroupName ? groupNameNp : isSaluted ? x.getSalutation() : shree + x.getEmployeeNameNp() + designationNp)
                                    .sectionName(isSectionName ? receiverDetail.getSection() != null ? isEnglish ? receiverDetail.getSection().getName() : "श्री " + receiverDetail.getSection().getNameN() : null : null)
                                    .isSectionName(isSectionName)
                                    .isGroupName(isGroupName)
                                    .build();

                            if (!isSaluted) {
                                req[0].setAddress(isEnglish ? x.getSectionName() != null ? x.getSectionName() : "" : x.getSectionNameNp() != null ? x.getSectionNameNp() : "");
                            } else {
                                req[0].setAddress("");
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
                });
            }

            if (externalDTOS != null && !externalDTOS.isEmpty()) {
                externalDTOS.forEach(x -> {

                    if (x.getIsCc() != null) {
                        String externalSection = "";
                        if (x.getReceiverOfficeSectionSubSection() != null)
                            externalSection = " ," + x.getReceiverOfficeSectionSubSection();

                        if (!x.getIsCc()) {

                            RequestTo to = new RequestTo().builder()
                                    .office(x.getReceiverName() != null ? shree + x.getReceiverName() + externalSection : "")
//                                    .office(x.getReceiverName() != null ? isSaluted ? x.getSalutation() : shree + x.getReceiverName() + externalSection : "")
                                    .isGroupName(Boolean.FALSE)
                                    .isSectionName(Boolean.FALSE)
                                    .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                                    .order(x.getOrder()).build();
                            requestTos.add(to);

                        } else if (x.getIsCc() && (x.getBodarthaType() == null || x.getBodarthaType().equals(BodarthaEnum.B))) {

                            Bodartha bd = new Bodartha().builder()
                                    .office(x.getReceiverName() != null ? shree + x.getReceiverName() : "")
                                    .remarks(x.getRemarks())
                                    .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                                    .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                                    .order(x.getOrder()).build();

                            bodartha.add(bd);
                        } else if (x.getIsCc() != null && x.getIsCc() && x.getBodarthaType().equals(BodarthaEnum.K)) {

                            Bodartha karEx = new Bodartha().builder()
                                    .office(x.getReceiverName() != null ? shree + x.getReceiverName() : "")
                                    .remarks(x.getRemarks())
                                    .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                                    .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                                    .order(x.getOrder()).build();

                            karyartha.add(karEx);
                        } else if (x.getIsCc() != null && x.getIsCc() && x.getBodarthaType().equals(BodarthaEnum.S)) {

                            Bodartha sadEx = new Bodartha().builder()
                                    .office(x.getReceiverName() != null ? shree + x.getReceiverName() : "")
                                    .remarks(x.getRemarks())
                                    .section(x.getReceiverOfficeSectionSubSection() != null ? x.getReceiverOfficeSectionSubSection() : "")
                                    .address(x.getExtenalReceiverAddress() != null ? x.getExtenalReceiverAddress() : "")
                                    .order(x.getOrder()).build();

                            sadarBodartha.add(sadEx);
                        }
                    }
                });
            }

            requestTos.sort(Comparator.comparing(RequestTo::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

            bodartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

            karyartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

            sadarBodartha.sort(Comparator.comparing(Bodartha::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));

            String footer = "";
           /* FooterDataDto footerData = footerDataMapper.getFooterByOfficeCode(manualLetter.getSenderOfficeCode());
            if (footerData != null)
                footer = footerData.getFooter();*/

            String template;

            String header = null;
            boolean useDynamicHeader = false;

            OfficeTemplatePojo dynamicTemplateHeader = userMgmtServiceData.getOfficeTemplate(manualLetter.getSenderOfficeCode(), "H");

            if (dynamicTemplateHeader != null && Boolean.TRUE.equals(dynamicTemplateHeader.getIsActive())) {
                useDynamicHeader = true;
                if (isEnglish && dynamicTemplateHeader.getTemplateEn() != null &&
                        !dynamicTemplateHeader.getTemplateEn().isEmpty())
                    header = dynamicTemplateHeader.getTemplateEn();
                else if (!isEnglish && dynamicTemplateHeader.getTemplateNp() != null
                        && !dynamicTemplateHeader.getTemplateNp().isEmpty())
                    header = dynamicTemplateHeader.getTemplateNp();
            }

            OfficeTemplatePojo dynamicTemplateFooter = userMgmtServiceData.getOfficeTemplate(manualLetter.getSenderOfficeCode(), "F");

            if (dynamicTemplateFooter != null && Boolean.TRUE.equals(dynamicTemplateFooter.getIsActive())) {
                if (isEnglish && dynamicTemplateFooter.getTemplateEn() != null &&
                        !dynamicTemplateFooter.getTemplateEn().isEmpty())
                    footer = dynamicTemplateFooter.getTemplateEn();
                else if (!isEnglish && dynamicTemplateFooter.getTemplateNp() != null
                        && !dynamicTemplateFooter.getTemplateNp().isEmpty())
                    footer = dynamicTemplateFooter.getTemplateNp();
            }


            VerificationInformation verificationInformation = manualLetter.getActiveSignatureData();
            if (manualLetter.getIsReceiver() != null && manualLetter.getIsReceiver() && req[0] != null) {
                GeneralTemplate generalTemplate = new GeneralTemplate().builder()
                        .header(useDynamicHeader ? header : null)
                        .logo_url(img)
                        .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                        .department(isEnglish ? parentOffice != null ? StringUtils.capitalize(parentOffice.getNameEn().toLowerCase()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                        .ministry(isEnglish ? StringUtils.capitalize(officePojo.getNameEn().toLowerCase()) : officePojo.getNameNp())
                        .address_top(isEnglish ? StringUtils.capitalize(officePojo.getAddressEn().toLowerCase()) : officePojo.getAddressNp())
                        .subject(manualLetter.getSubject())
                        .body_message(manualLetter.getContent())
                        .letter_date(isEnglish ? manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString() : dateConverter.convertBSToDevnagari(manualLetter.getCreatedDateNp()))
                        .chali_no(manualLetter.getDispatchNo())
                        .section_header(sectionPojo != null ? isEnglish ? StringUtils.capitalize(sectionPojo.getNameEn().toLowerCase()) : sectionPojo.getNameNp() : null)
                        .letter_no(manualLetter.getReferenceNo())
                        .request_to_office(req[0].getOffice() != null ? req[0].getOffice() : "")
                        .request_to_office_address(req[0].getAddress() != null ? req[0].getAddress() : "")
                        .requester_name(isEnglish ? signatureUser != null ? StringUtils.capitalize(signatureUser.getNameEn().toLowerCase()) : officeHeadPojo != null ? StringUtils.capitalize(officeHeadPojo.getEmployeeNameEn().toLowerCase()) : "" : signatureUser != null ? signatureUser.getNameNp() : officeHeadPojo != null ? officeHeadPojo.getEmployeeNameNp() : "")
                        .requester_position(isEnglish ? signatureDesignation != null ? StringUtils.capitalize(signatureDesignation.getNameEn().toLowerCase()) : officeHeadDetail != null ? StringUtils.capitalize(officeHeadDetail.getFunctionalDesignation().getName().toLowerCase()) : "" : signatureDesignation != null ? signatureDesignation.getNameNp() : officeHeadDetail != null ? officeHeadDetail.getFunctionalDesignation().getNameN() : "")
                        .resource_type("D")
                        .resource_id(manualLetter.getId())
                        .bodartha(bodartha)
                        .bodartha_karyartha(karyartha)
                        .saadar_awagataartha(sadarBodartha)
                        .footer(footer)
                        .verificationInformation(verificationInformation)
                        .isGroupName(req[0].getIsGroupName())
                        .hasSubject(hasSubject)
                        .isSectionName(req[0].getIsSectionName())
                        .sectionLetter(includeSectionInLetter && requesterSection != null ? isEnglish ? requesterSection.getNameEn() : requesterSection.getNameNp() : null)
                        .build();

                System.out.println("has subject in recv: " + hasSubject);
                System.out.println("gson1: " + new Gson().toJson(generalTemplate));

                template = letterTemplateProxy.getGeneralTemplate(generalTemplate, lang);

            } else {
                GeneralTemplate generalTemplate = new GeneralTemplate().builder()
                        .header(useDynamicHeader ? header : null)
                        .logo_url(img)
                        .organization(isEnglish ? "Government Of Nepal" : "नेपाल सरकार")
                        .department(isEnglish ? parentOffice != null ? StringUtils.capitalize(parentOffice.getNameEn().toLowerCase()) : "" : parentOffice != null ? parentOffice.getNameNp() : "")
                        .ministry(isEnglish ? StringUtils.capitalize(officePojo.getNameEn().toLowerCase()) : officePojo.getNameNp())
                        .address_top(isEnglish ? StringUtils.capitalize(officePojo.getAddressEn().toLowerCase()) : officePojo.getAddressNp())
                        .request_to_many(removeSameSectionName(requestTos))
                        .subject(manualLetter.getSubject())
                        .body_message(manualLetter.getContent())
                        .letter_date(isEnglish ? manualLetter.getCreatedDate().toString() : dateConverter.convertBSToDevnagari(manualLetter.getCreatedDateNp()))
                        .chali_no(manualLetter.getDispatchNo())
                        .section_header(sectionPojo != null ? isEnglish ? sectionPojo.getNameEn() : sectionPojo.getNameNp() : null)
                        .letter_no(manualLetter.getReferenceNo())
                        .requester_name(isEnglish ? signatureUser != null ? StringUtils.capitalize(signatureUser.getNameEn().toLowerCase()) : officeHeadPojo != null ? StringUtils.capitalize(officeHeadPojo.getEmployeeNameEn().toLowerCase()) : "" : signatureUser != null ? signatureUser.getNameNp() : officeHeadPojo != null ? officeHeadPojo.getEmployeeNameNp() : "")
                        .requester_position(isEnglish ? signatureDesignation != null ? StringUtils.capitalize(signatureDesignation.getNameEn().toLowerCase()) : officeHeadDetail != null ? StringUtils.capitalize(officeHeadDetail.getFunctionalDesignation().getName().toLowerCase()) : "" : signatureDesignation != null ? signatureDesignation.getNameNp() : officeHeadDetail != null ? officeHeadDetail.getFunctionalDesignation().getNameN() : "")
                        .resource_type("D")
                        .resource_id(manualLetter.getId())
                        .bodartha(bodartha)
                        .bodartha_karyartha(karyartha)
                        .saadar_awagataartha(sadarBodartha)
                        .footer(footer)
                        .verificationInformation(verificationInformation)
                        .hasSubject(hasSubject)
                        .sectionLetter(includeSectionInLetter && requesterSection != null ? isEnglish ? requesterSection.getNameEn() : requesterSection.getNameNp() : null)
                        .build();

                System.out.println("gson2: " + new Gson().toJson(generalTemplate));

                manualLetter.setIsTableFormat(useDynamicHeader ? Boolean.TRUE : isEnglish ? Boolean.TRUE : Boolean.FALSE);

                template = letterTemplateProxy.getGeneralMultipleTemplate(generalTemplate, lang);
            }

            manualLetter.setTemplate(template);
        }

        if (manualLetter.getDispatchId() != null) {
            List<String> pdf = receivedLetterMapper.getDispatchPdf(manualLetter.getDispatchId());
            manualLetter.setPdfData(pdf);
        }

        if (manualLetter.getDispatchId() != null && manualLetter.getInclude() != null && manualLetter.getInclude()) {
            List<GenericReferenceDto> memoReferences = memoReferenceMapper.getChalaniMemoReference(manualLetter.getDispatchId());

            if (memoReferences != null && !memoReferences.isEmpty()) {
                List<MemoResponsePojo> memoResponsePojos = new ArrayList<>();
                for (GenericReferenceDto tippani : memoReferences) {
                    if (tippani != null && tippani.getId() != null && tippani.getInclude() != null && tippani.getInclude()) {
                        MemoResponsePojo letter = memoMapper.getMemoById(tippani.getId());
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
                manualLetter.setMemoReferences(memoResponsePojos);
            }

            List<GenericReferenceDto> dartaReference = memoReferenceMapper.getChalaniDartaReference(manualLetter.getDispatchId());

            if (dartaReference != null && !dartaReference.isEmpty()) {
                List<ReceivedLetterResponsePojo> receivedLetterResponsePojos = new ArrayList<>();
                for (GenericReferenceDto darta : dartaReference) {
                    if (darta != null && darta.getId() != null && darta.getInclude() != null && darta.getInclude()) {
                        ReceivedLetterResponsePojo letter = receivedLetterMapper.getReceivedLetter(darta.getId());
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
                manualLetter.setDartaReferences(receivedLetterResponsePojos);
            }

            List<GenericReferenceDto> chalaniReference = memoReferenceMapper.getChalaniChalaniReference(manualLetter.getDispatchId());

            if (chalaniReference != null && !chalaniReference.isEmpty()) {
                List<DispatchLetterDTO> dispatchLetterDTOS = new ArrayList<>();
                for (GenericReferenceDto chalani : chalaniReference) {
                    if (chalani != null && chalani.getId() != null && chalani.getInclude() != null && chalani.getInclude()) {
                        DispatchLetterDTO letter = dispatchLetterMapper.getDispatchLetterDetailById(chalani.getId());
                        letter.setReferenceCreator(userMgmtServiceData.getEmployeeDetailMinimal(chalani.getPisCode()));
                        dispatchLetterDTOS.add(letter);
                    }
                }
                if (!dispatchLetterDTOS.isEmpty()) {
                    for (DispatchLetterDTO data : dispatchLetterDTOS) {
                        EmployeeMinimalPojo employeeMinimal1 = userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode());
                        if (employeeMinimal1 != null) {
                            data.setSenderName(employeeMinimal1.getEmployeeNameEn());
                            data.setSenderNameNp(employeeMinimal1.getEmployeeNameNp());
                        }
                    }
                }
                manualLetter.setChalaniReferences(dispatchLetterDTOS);
            }

        }

        if (request != null && request.getSearchField() != null) {
            List<Long> ids = null;

            if (request.getSearchField().get("type") != null && request.getSearchField().get("type").equals("page"))
                ids = receivedLetterMapper.getNextPrevValues(request.getPisCode(), tokenProcessorService.getOfficeCode(), request.getSearchField());
            else if (request.getSearchField().get("type") != null && request.getSearchField().get("type").equals("pagination"))
                ids = receivedLetterMapper.getNextPrevValuesUser(tokenProcessorService.getPisCode(), tokenProcessorService.getOfficeCode(), request.getSearchField());

            if (ids != null && !ids.isEmpty() && ids.size() > 1) {
                int index = ids.indexOf(request.getId());
                if (index > 0)
                    manualLetter.setPrevId(ids.get(index - 1));
                if (index < ids.size() - 1)
                    manualLetter.setNextId(ids.get(index + 1));
            }
        }

        return manualLetter;
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

    private List<DocumentPojo> getActiveDocuments(List<DocumentPojo> document) {
        List<DocumentPojo> newPojo = new ArrayList<>();
        for (DocumentPojo data : document) {
            if (data.getIsActive() != null && data.getIsActive())
                newPojo.add(data);
        }
        return newPojo;
    }

    @Override
    public List<ReceivedLetterResponsePojo> getAllManuallyReceivedLetter() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        List<ReceivedLetterResponsePojo> manualLetters = receivedLetterMapper.getAllManuallyReceivedLetters(tokenProcessorService.getOfficeCode(), tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());
        return getReceivedLetterResponsePojos(manualLetters);
    }

    @Override
    public List<ReceivedLetterResponsePojo> getAllManualOfficeHead() {
        List<ReceivedLetterResponsePojo> manualLetters = receivedLetterMapper.getAllManuallyReceivedLettersByOfficeCode(tokenProcessorService.getOfficeCode());
        return getReceivedLetterResponsePojos(manualLetters);
    }

    @Override
    public List<ReceivedLetterResponsePojo> getAllManualLettersForward() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        List<ReceivedLetterResponsePojo> manualLetters = receivedLetterMapper.getAllManualLettersForward(tokenProcessorService.getOfficeCode(), tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());
        return getReceivedLetterResponsePojos(manualLetters);
    }

    @Override
    public List<ReceivedLetterResponsePojo> getAllReceivedLetter() {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        List<ReceivedLetterResponsePojo> manualLetters = receivedLetterMapper.getAllByReceiverPisCode(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString());
        return getReceivedLetterResponsePojos(manualLetters);
    }

    @Override
    public Page<ReceivedLetterResponsePojo> getAllReceivedLettersForward(GetRowsRequest paginatedRequest) {
        String psCd = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        Set<String> listPisCodes = new HashSet<>();

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        OfficeHeadPojo officeHead = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
        boolean isReceived = true;
        if (officeHead != null && officeHead.getPisCode().equals(tokenProcessorService.getPisCode()))
            isReceived = false;

        boolean isHead = false;
        String r = tokenProcessorService.getRoles();
        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            isHead = true;
        }

        Page<ReceivedLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        if (this.getPreviousPisCode(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString()) != null)
            listPisCodes.addAll(this.getPreviousPisCode(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString()));

        if (paginatedRequest.getSearchField() != null ? paginatedRequest.getSearchField().get("manualIsCc") != null ? paginatedRequest.getSearchField().get("manualIsCc").equals(Boolean.TRUE) : false : false) {
            page = receivedLetterMapper.getAllReceivedLettersForwardFilterByCC(page, tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString(), listPisCodes, isReceived, paginatedRequest.getSearchField(), tokenProcessorService.getOfficeCode());
        } else {
            page = receivedLetterMapper.getAllReceivedLettersForward(page, tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString(), listPisCodes, isReceived, paginatedRequest.getSearchField(), tokenProcessorService.getOfficeCode());
        }
        for (ReceivedLetterResponsePojo manualLetter : page.getRecords()) {

            if (isHead) {
                if (manualLetter.getIsImportantHead() == null || (manualLetter.getIsImportantHead() != null && !manualLetter.getIsImportantHead()))
                    manualLetter.setIsImportant(Boolean.FALSE);
                if (manualLetter.getIsImportantHead() != null && manualLetter.getIsImportantHead())
                    manualLetter.setIsImportant(Boolean.TRUE);
            } else {
                if (manualLetter.getIsImportant() != null && manualLetter.getIsImportant() && !listPisCodes.contains(manualLetter.getPisCode()))
                    manualLetter.setIsImportant(Boolean.FALSE);
            }

            manualLetter.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(manualLetter.getSenderOfficeCode());
            manualLetter.setOfficeDetails(userMgmtServiceData.getOfficeDetail(manualLetter.getSenderOfficeCode()));
            manualLetter.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            manualLetter.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);

            if (manualLetter.getLetterType() != null && manualLetter.getLetterType().equals("Receive")) {
                List<ForwardResponsePojo> forwards = receivedLetterMapper.getForwards(manualLetter.getId());
                if (forwards != null) {
                    for (ForwardResponsePojo forwardResponsePojo : forwards) {
                        if (forwardResponsePojo.getReceiverPisCode() != null && !forwardResponsePojo.getReceiverPisCode().equals("")) {
                            forwardResponsePojo.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getReceiverPisCode()));
                        }
                        forwardResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));

                        if (forwardResponsePojo.getDelegatedId() != null) {
                            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(forwardResponsePojo.getDelegatedId());
                            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                        } else if (forwardResponsePojo.getSenderPisCode() != null)
                            forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getSenderPisCode()));

                        if (forwardResponsePojo.getReceiverPisCode().equals(tokenProcessorService.getPisCode()) && forwardResponsePojo.getIsActive() != null && forwardResponsePojo.getIsActive())
                            manualLetter.setStatus(forwardResponsePojo.getStatus());

                        EmployeePojo employeePojo1 = userMgmtServiceData.getEmployeeDetail(forwardResponsePojo.getReceiverPisCode());

                        if (forwardResponsePojo.getReceiverPisCode() != null
                                && forwardResponsePojo.getReceiverPisCode().equals(tokenProcessorService.getPisCode())
                                && employeePojo1 != null && employeePojo1.getSectionId() != null && forwardResponsePojo.getReceiverSectionId() != null
                                && forwardResponsePojo.getReceiverSectionId().equals(employeePojo1.getSectionId())) {
                            if (forwardResponsePojo.getIsCc() != null && forwardResponsePojo.getIsCc())
                                manualLetter.setIsCC(forwardResponsePojo.getIsCc());
                            if (Boolean.TRUE.equals(forwardResponsePojo.getIsImportant()))
                                manualLetter.setIsImportant(true);
                        }
                    }
                    manualLetter.setForwards(forwards);
                }
                ReceivedLetterDetailResponsePojo details = receivedLetterMapper.getDetails(manualLetter.getId());
                manualLetter.setDetails(details);
            }

            if (manualLetter.getLetterType() != null && manualLetter.getLetterType().equals("Dispatch")) {
                List<DispatchLetterInternalDTO> dispatchLetterInternalDTOS = dispatchLetterMapper.getInternalsByDispatchId(manualLetter.getId());
                if (dispatchLetterInternalDTOS != null) {
                    for (DispatchLetterInternalDTO data : dispatchLetterInternalDTOS) {
                        if (data.getInternalReceiverPiscode() != null && data.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())
                                && employeePojo.getSectionId() != null && data.getInternalReceiverSectionId() != null
                                && employeePojo.getSectionId().equals(data.getInternalReceiverSectionId())) {
                            manualLetter.setStatus(Status.valueOf(data.getCompletionStatus()));
                            if (data.getInternalReceiverCc() != null && data.getInternalReceiverCc())
                                manualLetter.setIsCC(data.getInternalReceiverCc());
                            if (data.getInternalReceiver() != null && data.getInternalReceiver())
                                manualLetter.setIsCC(!data.getInternalReceiver());
                            if (Boolean.TRUE.equals(data.getIsImportant()))
                                manualLetter.setIsImportant(true);
                        }
                    }
                }
            }
//            ForwardResponsePojo activeDarta = receivedLetterMapper.findActiveDarta(manualLetter.getId());
//            if (activeDarta != null && activeDarta.getReceiverPisCode() != null) {
//                activeDarta.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(activeDarta.getReceiverPisCode()));
//                manualLetter.setActiveDarta(activeDarta);
//            }
        }
        return page;
    }

    @Override
    public Page<ReceivedLetterResponsePojo> getManualReceiverInProgress(GetRowsRequest paginatedRequest) {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        Set<String> listPisCodes = new HashSet<>();

        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        boolean isHead = false;

        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            isHead = true;
        }

        String officeCode = employeePojo != null ? employeePojo.getOffice() != null ? employeePojo.getOffice().getCode() : "" : "";

//        OfficeHeadPojo officeHead = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
//        boolean isReceived = true;
//        if (officeHead != null && officeHead.getPisCode().equals(tokenProcessorService.getPisCode()))
//            isReceived = false;
        String status = "IP";
        Boolean isImp = false;
        if (paginatedRequest.getSearchField() != null
                && paginatedRequest.getSearchField().get("favourite") != null
                && paginatedRequest.getSearchField().get("favourite").equals(true)) {
            status = null;
            isImp = true;
        }

        Page<ReceivedLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        if (this.getPreviousPisCode(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString()) != null)
            listPisCodes.addAll(this.getPreviousPisCode(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString()));
        page = receivedLetterMapper.getReceiverInProgress(page, tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString(), listPisCodes, paginatedRequest.getSearchField(), status, isHead, officeCode, isImp);
        for (ReceivedLetterResponsePojo manualLetter : page.getRecords()) {

            if (isHead) {
                if (manualLetter.getIsImportantHead() == null || (manualLetter.getIsImportantHead() != null && !manualLetter.getIsImportantHead()))
                    manualLetter.setIsImportant(Boolean.FALSE);
                if (manualLetter.getIsImportantHead() != null && manualLetter.getIsImportantHead())
                    manualLetter.setIsImportant(Boolean.TRUE);
            } else {
                if (manualLetter.getIsImportant() != null && manualLetter.getIsImportant() && !listPisCodes.contains(manualLetter.getPisCode()))
                    manualLetter.setIsImportant(Boolean.FALSE);
            }

            manualLetter.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(manualLetter.getSenderOfficeCode());
            manualLetter.setOfficeDetails(userMgmtServiceData.getOfficeDetail(manualLetter.getSenderOfficeCode()));
            manualLetter.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            manualLetter.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);

            if (manualLetter.getLetterType() != null && manualLetter.getLetterType().equals("Receive")) {
                List<ForwardResponsePojo> forwards = receivedLetterMapper.getForwards(manualLetter.getId());
                if (forwards != null) {
                    for (ForwardResponsePojo forwardResponsePojo : forwards) {
                        forwardResponsePojo.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getReceiverPisCode()));
                        forwardResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));

                        if (forwardResponsePojo.getDelegatedId() != null) {
                            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(forwardResponsePojo.getDelegatedId());
                            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                        } else
                            forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getSenderPisCode()));

                        if (forwardResponsePojo.getReceiverPisCode().equals(tokenProcessorService.getPisCode())) {
                            manualLetter.setStatus(forwardResponsePojo.getStatus());
                            if (Boolean.TRUE.equals(forwardResponsePojo.getIsActive()))
                                manualLetter.setCurrentIsActive(true);
                        }

                        EmployeePojo employeePojo1 = userMgmtServiceData.getEmployeeDetail(forwardResponsePojo.getReceiverPisCode());

                        if (forwardResponsePojo.getReceiverPisCode() != null
                                && forwardResponsePojo.getReceiverPisCode().equals(tokenProcessorService.getPisCode())
                                && employeePojo1 != null && employeePojo1.getSectionId() != null && forwardResponsePojo.getReceiverSectionId() != null
                                && forwardResponsePojo.getReceiverSectionId().equals(employeePojo1.getSectionId())) {
                            if (forwardResponsePojo.getIsCc() != null && forwardResponsePojo.getIsCc())
                                manualLetter.setIsCC(forwardResponsePojo.getIsCc());
                            if (Boolean.TRUE.equals(forwardResponsePojo.getIsImportant()))
                                manualLetter.setIsImportant(true);
                        }
                    }
                    manualLetter.setForwards(forwards);
                }
                ReceivedLetterDetailResponsePojo details = receivedLetterMapper.getDetails(manualLetter.getId());
                manualLetter.setDetails(details);
            }
            if (manualLetter.getLetterType() != null && manualLetter.getLetterType().equals("Dispatch")) {
                List<DispatchLetterInternalDTO> dispatchLetterInternalDTOS = dispatchLetterMapper.getInternalsByDispatchId(manualLetter.getId());
                if (dispatchLetterInternalDTOS != null) {
                    for (DispatchLetterInternalDTO data : dispatchLetterInternalDTOS) {

                        if (data.getInternalReceiverPiscode() != null
                                && data.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())
                                && employeePojo.getSectionId() != null && data.getInternalReceiverSectionId() != null
                                && employeePojo.getSectionId().equals(data.getInternalReceiverSectionId())) {
                            manualLetter.setStatus(Status.valueOf(data.getCompletionStatus()));
                            manualLetter.setCurrentIsActive(data.getIsActive());
                            if (data.getInternalReceiverCc() != null && data.getInternalReceiverCc())
                                manualLetter.setIsCC(data.getInternalReceiverCc());
                            if (data.getInternalReceiver() != null && data.getInternalReceiver())
                                manualLetter.setIsCC(!data.getInternalReceiver());
                            if (Boolean.TRUE.equals(data.getIsImportant()))
                                manualLetter.setIsImportant(true);
                        }
                    }
                }
            }
//            ForwardResponsePojo activeDarta = receivedLetterMapper.findActiveDarta(manualLetter.getId(), );
//            if (activeDarta != null && activeDarta.getReceiverPisCode() != null) {
//                activeDarta.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(activeDarta.getReceiverPisCode()));
//                manualLetter.setActiveDarta(activeDarta);
//            }
        }
        return page;
    }

    @Override
    public Page<ReceivedLetterResponsePojo> getManualReceiverFinalized(GetRowsRequest paginatedRequest) {
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        Set<String> listPisCodes = new HashSet<>();
        if (employeePojo.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

//        OfficeHeadPojo officeHead = userMgmtServiceData.getOfficeHeadDetail(tokenProcessorService.getOfficeCode());
//        boolean isReceived = true;
//        if (officeHead != null && officeHead.getPisCode().equals(tokenProcessorService.getPisCode()))
//            isReceived = false;

        boolean isHead = false;

        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            isHead = true;
        }

        if (this.getPreviousPisCode(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString()) != null)
            listPisCodes.addAll(this.getPreviousPisCode(tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString()));
        Page<ReceivedLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        page = receivedLetterMapper.getReceiverFinalized(page, tokenProcessorService.getPisCode(), employeePojo.getSection().getId().toString(), listPisCodes, paginatedRequest.getSearchField(), tokenProcessorService.getOfficeCode());
        for (ReceivedLetterResponsePojo manualLetter : page.getRecords()) {

            if (isHead) {
                if (manualLetter.getIsImportantHead() == null || (manualLetter.getIsImportantHead() != null && !manualLetter.getIsImportantHead()))
                    manualLetter.setIsImportant(Boolean.FALSE);
                if (manualLetter.getIsImportantHead() != null && manualLetter.getIsImportantHead())
                    manualLetter.setIsImportant(Boolean.TRUE);
            } else {
                if (manualLetter.getIsImportant() != null && manualLetter.getIsImportant() && !listPisCodes.contains(manualLetter.getPisCode()))
                    manualLetter.setIsImportant(Boolean.FALSE);
            }

            manualLetter.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(manualLetter.getSenderOfficeCode());
            manualLetter.setOfficeDetails(userMgmtServiceData.getOfficeDetail(manualLetter.getSenderOfficeCode()));
            manualLetter.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            manualLetter.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);

            if (manualLetter.getLetterType() != null && manualLetter.getLetterType().equals("Receive")) {
                List<ForwardResponsePojo> forwards = receivedLetterMapper.getForwards(manualLetter.getId());
                if (forwards != null) {
                    for (ForwardResponsePojo forwardResponsePojo : forwards) {
                        if (forwardResponsePojo.getReceiverPisCode() != null && !forwardResponsePojo.getReceiverPisCode().equals(""))
                            forwardResponsePojo.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getReceiverPisCode()));
                        forwardResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));

                        if (forwardResponsePojo.getDelegatedId() != null) {
                            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(forwardResponsePojo.getDelegatedId());
                            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                        } else
                            forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getSenderPisCode()));

                        EmployeePojo employeePojo1 = userMgmtServiceData.getEmployeeDetail(forwardResponsePojo.getReceiverPisCode());

                        if (forwardResponsePojo.getReceiverPisCode() != null
                                && forwardResponsePojo.getReceiverPisCode().equals(tokenProcessorService.getPisCode())
                                && employeePojo1 != null && employeePojo1.getSectionId() != null && forwardResponsePojo.getReceiverSectionId() != null
                                && forwardResponsePojo.getReceiverSectionId().equals(employeePojo1.getSectionId())) {

                            if (forwardResponsePojo.getIsCc() != null && forwardResponsePojo.getIsCc())
                                manualLetter.setIsCC(forwardResponsePojo.getIsCc());

                            if (Boolean.TRUE.equals(forwardResponsePojo.getIsImportant()))
                                manualLetter.setIsImportant(true);
                        }
                    }
                    manualLetter.setForwards(forwards);
                }
                ReceivedLetterDetailResponsePojo details = receivedLetterMapper.getDetails(manualLetter.getId());
                manualLetter.setDetails(details);
            }
            if (manualLetter.getLetterType() != null && manualLetter.getLetterType().equals("Dispatch")) {
                List<DispatchLetterInternalDTO> dispatchLetterInternalDTOS = dispatchLetterMapper.getInternalsByDispatchId(manualLetter.getId());
                if (dispatchLetterInternalDTOS != null) {
                    for (DispatchLetterInternalDTO data : dispatchLetterInternalDTOS) {
                        if (data.getInternalReceiverPiscode() != null
                                && data.getInternalReceiverPiscode().equals(tokenProcessorService.getPisCode())
                                && employeePojo.getSectionId() != null && data.getInternalReceiverSectionId() != null
                                && employeePojo.getSectionId().equals(data.getInternalReceiverSectionId())) {

                            if (data.getInternalReceiverCc() != null && data.getInternalReceiverCc())
                                manualLetter.setIsCC(data.getInternalReceiverCc());

                            if (data.getInternalReceiver() != null && data.getInternalReceiver())
                                manualLetter.setIsCC(!data.getInternalReceiver());

                            if (Boolean.TRUE.equals(data.getIsImportant()))
                                manualLetter.setIsImportant(true);
                        }
                    }
                }
            }
//            ForwardResponsePojo activeDarta = receivedLetterMapper.findActiveDarta(manualLetter.getId());
//            if (activeDarta != null && activeDarta.getReceiverPisCode() != null) {
//                activeDarta.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(activeDarta.getReceiverPisCode()));
//                manualLetter.setActiveDarta(activeDarta);
//            }
        }
        return page;
    }

    @Override
    public Page<ReceivedLetterResponsePojo> filterData(GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        Set<String> listPisCodes = new HashSet<>();


        String transferredFromPisCode = paginatedRequest.getSearchField().get("transferFromPisCode") != null ?
                paginatedRequest.getSearchField().get("transferFromPisCode").toString() : null;

        String transferFromSectionCode = paginatedRequest.getSearchField().get("transferFromSectionCode") != null ?
                paginatedRequest.getSearchField().get("transferFromSectionCode").toString() : null;

        EmployeePojo employeePojos = userMgmtServiceData.getEmployeeDetail(transferredFromPisCode != null ? transferredFromPisCode : tokenProcessorService.getPisCode());

        if (employeePojos.getSection() == null)
            throw new RuntimeException("Not Involved In any Section");

        if (paginatedRequest.getPisCode() != null)
            paginatedRequest.setPisCode(paginatedRequest.getPisCode().equals(tokenProcessorService.getPisCode()) ? paginatedRequest.getPisCode() : "null");

        String receiverPisCode = "receiverPisCode";
        if (paginatedRequest.getSearchField() != null && paginatedRequest.getSearchField().get(receiverPisCode) != null && paginatedRequest.getSearchField().get(receiverPisCode).equals(tokenProcessorService.getPisCode()))
            paginatedRequest.getSearchField().put(receiverPisCode, paginatedRequest.getSearchField().get(receiverPisCode));
        else if (paginatedRequest.getSearchField() != null) {
            paginatedRequest.getSearchField().put(receiverPisCode, null);
        }

        if (employeePojos.getSection() != null) {
            if (this.getPreviousPisCode(transferredFromPisCode != null ? transferredFromPisCode : tokenProcessorService.getPisCode(), employeePojos.getSection().getId().toString()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(transferredFromPisCode != null ? transferredFromPisCode : tokenProcessorService.getPisCode(), employeePojos.getSection().getId().toString()));
        } else if (paginatedRequest.getSearchField().get("receiverPisCode") != null) {
            EmployeePojo employeePisCode = userMgmtServiceData.getEmployeeDetail(paginatedRequest.getSearchField().get(receiverPisCode).toString());
            if (employeePisCode.getSection() != null) {
                if (this.getPreviousPisCode(paginatedRequest.getSearchField().get(receiverPisCode).toString(), employeePisCode.getSection().getId().toString()) != null)
                    listPisCodes.addAll(this.getPreviousPisCode(paginatedRequest.getSearchField().get(receiverPisCode).toString(), employeePisCode.getSection().getId().toString()));
            }
        }

        //when get list for letter transfer add transferFromPisCode to receiverPisCode
        if (transferredFromPisCode != null) {
            paginatedRequest.getSearchField().put(receiverPisCode, transferredFromPisCode);
            paginatedRequest.getSearchField().put("receiverSectionCode", employeePojos.getSectionId());
            paginatedRequest.getSearchField().put("isActive", true);
        }

        page = receivedLetterMapper.filterData(
                page,
                paginatedRequest.getPisCode(),
                listPisCodes,
                tokenProcessorService.getOfficeCode(),
                paginatedRequest.getSearchField()
        );

        for (ReceivedLetterResponsePojo data : page.getRecords()) {

            if (data.getIsImportant() != null && data.getIsImportant() && !listPisCodes.contains(data.getPisCode()))
                data.setIsImportant(Boolean.FALSE);

            data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(data.getSenderOfficeCode());
            data.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            data.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);
            data.setOfficeDetails(userMgmtServiceData.getOfficeDetail(data.getSenderOfficeCode()));

            if (data.getDelegatedId() != null) {
                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(data.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                    data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
            } else
                data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()));

            List<ForwardResponsePojo> forwards = receivedLetterMapper.getForwards(data.getId());
            if (forwards != null) {
                for (ForwardResponsePojo forwardResponsePojo : forwards) {
                    if (forwardResponsePojo.getReceiverPisCode() != null && !forwardResponsePojo.getReceiverPisCode().equals("")) {
                        forwardResponsePojo.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getReceiverPisCode()));
                    }
                    forwardResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));

                    if (forwardResponsePojo.getDelegatedId() != null) {
                        DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(forwardResponsePojo.getDelegatedId());
                        if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                            forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                    } else
                        forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getSenderPisCode()));

                    EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(forwardResponsePojo.getReceiverPisCode());

                    if (forwardResponsePojo.getReceiverPisCode() != null
                            && forwardResponsePojo.getReceiverPisCode().equals(tokenProcessorService.getPisCode())
                            && employeePojo != null && employeePojo.getSectionId() != null && forwardResponsePojo.getReceiverSectionId() != null
                            && forwardResponsePojo.getReceiverSectionId().equals(employeePojo.getSectionId())) {

                        if (forwardResponsePojo.getIsCc() != null && forwardResponsePojo.getIsCc())
                            data.setIsCC(forwardResponsePojo.getIsCc());

                        if (Boolean.TRUE.equals(forwardResponsePojo.getIsImportant()))
                            data.setIsImportant(true);
                    }
                }
                data.setForwards(forwards);
            }
        }

        return page;
    }

    @Override
    public Page<ReceivedLetterResponsePojo> getArchiveDarta(GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        Set<String> listPisCodes = new HashSet<>();

        String pisCode = tokenProcessorService.getPisCode();

        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(pisCode);

        checkUserSection(tokenUser);

        String sectionCode = tokenUser.getSectionId();

        listPisCodes.add(pisCode);
        if (this.getPreviousPisCode(pisCode, sectionCode) != null)
            listPisCodes.addAll(this.getPreviousPisCode(pisCode, sectionCode));

        page = receivedLetterMapper.getArchiveDarta(
                page,
                listPisCodes,
                tokenProcessorService.getOfficeCode(),
                sectionCode,
                paginatedRequest.getSearchField()
        );

        for (ReceivedLetterResponsePojo data : page.getRecords()) {

            if (data.getIsImportant() != null && data.getIsImportant() && !listPisCodes.contains(data.getPisCode()))
                data.setIsImportant(Boolean.FALSE);

            data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(data.getSenderOfficeCode());
            data.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            data.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);
            data.setOfficeDetails(userMgmtServiceData.getOfficeDetail(data.getSenderOfficeCode()));

            if (data.getDelegatedId() != null) {
                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(data.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                    data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
            } else
                data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()));

            List<ForwardResponsePojo> forwards = receivedLetterMapper.getForwards(data.getId());
            if (forwards != null) {
                for (ForwardResponsePojo forwardResponsePojo : forwards) {
                    if (forwardResponsePojo.getReceiverPisCode() != null && !forwardResponsePojo.getReceiverPisCode().equals("")) {
                        forwardResponsePojo.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getReceiverPisCode()));
                    }
                    forwardResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));

                    if (forwardResponsePojo.getDelegatedId() != null) {
                        DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(forwardResponsePojo.getDelegatedId());
                        if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                            forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                    } else
                        forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getSenderPisCode()));

                    EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(forwardResponsePojo.getReceiverPisCode());

                    if (forwardResponsePojo.getReceiverPisCode() != null
                            && forwardResponsePojo.getReceiverPisCode().equals(tokenProcessorService.getPisCode())
                            && employeePojo != null && employeePojo.getSectionId() != null && forwardResponsePojo.getReceiverSectionId() != null
                            && forwardResponsePojo.getReceiverSectionId().equals(employeePojo.getSectionId())) {

                        if (forwardResponsePojo.getIsCc() != null && forwardResponsePojo.getIsCc())
                            data.setIsCC(forwardResponsePojo.getIsCc());

                        if (Boolean.TRUE.equals(forwardResponsePojo.getIsImportant()))
                            data.setIsImportant(true);
                    }
                }
                data.setForwards(forwards);
            }
        }

        return page;
    }

    @Override
    public Page<ReceivedLetterResponsePojo> pageData(GetRowsRequest paginatedRequest) {
        Page<ReceivedLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        Set<String> listPisCodes = new HashSet<>();

        if (paginatedRequest.getPisCode() != null) {
            paginatedRequest.setPisCode(paginatedRequest.getPisCode().equals(tokenProcessorService.getPisCode()) ? paginatedRequest.getPisCode() : "null");

            if (paginatedRequest.getPisCode().equals(tokenProcessorService.getPisCode())) {
                if (paginatedRequest.getSectionId() != null) {
                    if (this.getPreviousPisCode(tokenProcessorService.getPisCode(), paginatedRequest.getSectionId().toString()) != null)
                        listPisCodes.addAll(this.getPreviousPisCode(tokenProcessorService.getPisCode(), paginatedRequest.getSectionId().toString()));
                }
            }
        }

//        String receiverPisCode = "receiverPisCode";
//        if (paginatedRequest.getSearchField() != null && paginatedRequest.getSearchField().get(receiverPisCode) != null && paginatedRequest.getSearchField().get(receiverPisCode).equals(tokenProcessorService.getPisCode()))
//            paginatedRequest.getSearchField().put(receiverPisCode, paginatedRequest.getSearchField().get(receiverPisCode));
//        else if (paginatedRequest.getSearchField() != null) {
//            paginatedRequest.getSearchField().put(receiverPisCode, null);
//        }
//        Map<String, Object> newMap = new HashMap<>();
//        newMap.put(receiverPisCode, tokenProcessorService.getPisCode());
//
//        if (paginatedRequest.getSearchField() != null)
//            paginatedRequest.getSearchField().putIfAbsent(receiverPisCode, tokenProcessorService.getPisCode());
//        else
//            paginatedRequest.setSearchField(newMap);
//
//        page = receivedLetterMapper.pageData(
//                page,
//                paginatedRequest.getPisCode(),
//                tokenProcessorService.getOfficeCode(),
//                paginatedRequest.getSearchField(),
//                paginatedRequest.getSearchField().get(receiverPisCode).toString()
//        );

        String receiverPisCode = "receiverPisCode";
        if (paginatedRequest.getSearchField() != null && paginatedRequest.getSearchField().get(receiverPisCode) != null && paginatedRequest.getSearchField().get(receiverPisCode).equals(tokenProcessorService.getPisCode()))
            paginatedRequest.getSearchField().put(receiverPisCode, paginatedRequest.getSearchField().get(receiverPisCode));
        else if (paginatedRequest.getSearchField() != null) {
            paginatedRequest.getSearchField().put(receiverPisCode, null);
        }

        page = receivedLetterMapper.pageData(
                page,
                paginatedRequest.getPisCode(),
                tokenProcessorService.getOfficeCode(),
                listPisCodes,
                paginatedRequest.getSearchField(),
                tokenProcessorService.getPisCode()
        );

        for (ReceivedLetterResponsePojo data : page.getRecords()) {

            if (data.getIsImportantHead() == null || (data.getIsImportantHead() != null && !data.getIsImportantHead()))
                data.setIsImportant(Boolean.FALSE);
            if (data.getIsImportantHead() != null && data.getIsImportantHead())
                data.setIsImportant(Boolean.TRUE);

            data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(data.getSenderOfficeCode());
            data.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            data.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);
            data.setOfficeDetails(userMgmtServiceData.getOfficeDetail(data.getSenderOfficeCode()));

            if (data.getDelegatedId() != null) {
                DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(data.getDelegatedId());
                if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                    data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
            } else
                data.setEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()));
            data.setIsCC(data.getManualIsCc());
        }

        return page;
    }

    @Override
    @SneakyThrows
    public ReceivedLetterForward revertReceivedLetter(Long receivedLetterId, String description) {
//        List<String> pisCodeList = receivedLetterForwardRepo.getPisCodeList(receivedLetterId);
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        //check user is involved in any section or not
        checkUserSection(tokenUser);

        //check user is receiver or not
        checkIsReceiver(tokenPisCode, tokenUser.getSectionId(), receivedLetterId);

        String tokenUserSection = tokenUser != null ? tokenUser.getSection() != null ? tokenUser.getSection().getId().toString() : null : null;

        ReceivedLetterForward activeUserLetter = receivedLetterForwardRepo.findByReceivedLetterIdAndReceiverPisCode(receivedLetterId, tokenProcessorService.getPisCode(), tokenUserSection);
//        int i = pisCodeList.lastIndexOf(tokenProcessorService.getPisCode());
        if (activeUserLetter == null) {
            throw new CustomException(customMessageSource.get("no.letter.found"));
        }
        if (activeUserLetter.getSenderParentCode() == null) {
            throw new CustomException(customMessageSource.get("no.user.revert"));
        }
//        if (i == -1 || pisCodeList.size() ==1){
//            throw new CustomException(customMessageSource.get("no.user.revert"));
//        }
        ReceivedLetterForward receivedLetterOld;
        ReceivedLetterForward receivedLetterForward = receivedLetterForwardRepo
                .findBySenderParentCode(activeUserLetter.getSenderParentCode(), receivedLetterId);
        if (receivedLetterForward == null) {
            ReceivedLetter receivedLetter = receivedLetterRepo.findById(receivedLetterId).get();
            receivedLetterOld = new ReceivedLetterForward();
//        receivedLetterOld.setReceiverPisCode(pisCodeList.get(i-1));
            receivedLetterOld.setSenderDesignationCode(activeUserLetter.getReceiverDesignationCode());
//            receivedLetterOld.setSenderSectionId(activeUserLetter.getSenderSectionId());
            receivedLetterOld.setSenderSectionId(activeUserLetter.getReceiverSectionId());
//            receivedLetterOld.setReceiverDesignationCode(activeUserLetter.getReceiverDesignationCode());
            receivedLetterOld.setReceiverDesignationCode(activeUserLetter.getSenderDesignationCode());
//            receivedLetterOld.setReceiverSectionId(activeUserLetter.getReceiverSectionId());
            receivedLetterOld.setReceiverSectionId(activeUserLetter.getSenderSectionId());
            receivedLetterOld.setDescription(description);
            //-- receivedLetterOld.setSenderSectionId(activeUserLetter.getSenderSectionId());
            receivedLetterOld.setCompletion_status(activeUserLetter.getCompletion_status());
            receivedLetterOld.setToCc(activeUserLetter.getToCc());
            receivedLetterOld.setIsReceived(activeUserLetter.getIsReceived());
            //-- receivedLetterOld.setIsCc(activeUserLetter.getIsCc());
            receivedLetterOld.setReceivedLetter(receivedLetter);

        } else {
            receivedLetterOld = new ReceivedLetterForward();
            BeanUtils.copyProperties(receivedLetterOld, receivedLetterForward);
            receivedLetterOld.setId(null);
            receivedLetterOld.setCreatedBy(null);
            receivedLetterOld.setDescription(description);
            receivedLetterOld.setCreatedDate(null);
            receivedLetterOld.setActive(true);
            receivedLetterOld.setLastModifiedBy(null);
            receivedLetterOld.setLastModifiedBy(null);
        }
        receivedLetterOld.setSenderPisCode(tokenProcessorService.getPisCode());
        receivedLetterOld.setReceiverPisCode(activeUserLetter.getSenderParentCode());
//        receivedLetterOld.setIsReverted(true);

        activeUserLetter.setActive(false);
        receivedLetterOld.setIsReverted(true);
        receivedLetterForwardRepo.save(activeUserLetter);
        receivedLetterForwardRepo.save(receivedLetterOld);
        return receivedLetterOld;
    }

    @Override
    public List<ForwardResponsePojo> getCurrentReceivedLetterOwners(Long receivedId) {
        List<ForwardResponsePojo> responsePojos = receivedLetterMapper.findActiveDarta(receivedId);
        if (responsePojos != null && !responsePojos.isEmpty()) {
            responsePojos.forEach(x -> {
                if (x.getReceiverPisCode() != null)
                    x.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode()));
                if (x.getSenderPisCode() != null)
                    x.setSender(userMgmtServiceData.getEmployeeDetailMinimal(x.getSenderPisCode()));
            });
        }
        return responsePojos;
    }

    @Override
    public Long setImportantFlag(Long id, boolean value) {
        ReceivedLetter receivedLetter = receivedLetterRepo.findById(id).orElseThrow(() -> new CustomException("Letter Not Found"));
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        List<ReceivedLetterForward> letterForwards = new ArrayList<>();

        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            receivedLetter.setIsImportantHead(receivedLetter.getIsImportantHead() != null ? !receivedLetter.getIsImportantHead() : Boolean.TRUE);
            receivedLetter.setLastModifiedDateImpHead(new Timestamp(new Date().getTime()));
            receivedLetterRepo.save(receivedLetter);
        } else if (employeePojo != null && employeePojo.getSectionId() != null) {
            if (receivedLetter.getPisCode() != null &&
                    receivedLetter.getPisCode().equals(tokenProcessorService.getPisCode()) &&
                    receivedLetter.getSectionCode().equals(employeePojo.getSectionId())) {
                receivedLetter.setIsImportant(receivedLetter.getIsImportant() != null ? !receivedLetter.getIsImportant() : Boolean.TRUE);
                receivedLetter.setLastModifiedDateImp(new Timestamp(new Date().getTime()));
                receivedLetterRepo.save(receivedLetter);
            }
            letterForwards = receivedLetterForwardRepo.findReceivedLetterByPisAndSection(receivedLetter.getId(), tokenProcessorService.getPisCode(), employeePojo.getSectionId());
        }
        if (letterForwards != null && !letterForwards.isEmpty()) {
            letterForwards.forEach(x -> {
                        x.setIsImportant(value);
                        x.setLastModifiedDate(x.getLastModifiedDate());
                    }
            );

            receivedLetterForwardRepo.saveAll(letterForwards);
        }


        return receivedLetter.getId();
    }


    private List<ReceivedLetterResponsePojo> getReceivedLetterResponsePojos
            (List<ReceivedLetterResponsePojo> manualLetters) {
        for (ReceivedLetterResponsePojo manualLetter : manualLetters) {
            manualLetter.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(manualLetter.getSenderOfficeCode());
            manualLetter.setOfficeDetails(userMgmtServiceData.getOfficeDetail(manualLetter.getSenderOfficeCode()));
            manualLetter.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            manualLetter.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);
            for (ForwardResponsePojo data : manualLetter.getForwards()) {
                data.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(data.getReceiverPisCode()));
                data.setSender(userMgmtServiceData.getEmployeeDetailMinimal(data.getSenderPisCode()));
                data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            }
        }
        return manualLetters;
    }

    private void processDocument(List<MultipartFile> document, ReceivedLetter receivedLetter, Boolean
            isMain, EmployeePojo employeePojo, String activeFiscalYear) {

        for (MultipartFile file : document) {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!ext.equalsIgnoreCase("pdf")) {
                throw new RuntimeException("फाइल PDF प्रकारको हुनुपर्छ");
            }
        }

        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .tag("darta_chalani")
                        .subModuleName("darta")
                        .fiscalYearCode(activeFiscalYear)
                        .designationCode(employeePojo != null ? employeePojo.getFunctionalDesignationCode() : null)
                        .sectionCode(employeePojo != null ? employeePojo.getSection() != null ? employeePojo.getSection().getId().toString() : null : null)
                        .type("1")
                        .build(),
                document
        );

        if (pojo == null)
            throw new RuntimeException("कागच पत्र सेब हुन सकेन");

        if (pojo != null) {
            receivedLetter.setDocumentMasterId(pojo.getDocumentMasterId());
            receivedLetter.setReceivedLetterDocumentDetails(
                    pojo.getDocuments().stream().map(
                            x -> new ReceivedLetterDocumentDetails().builder()
                                    .documentId(x.getId())
                                    .documentName(x.getName())
                                    .documentSize(x.getSizeKB())
                                    .isMain(isMain)
                                    .build()
                    ).collect(Collectors.toList())
            );
        }
    }

    private void processUpdateMultipleDocument(List<MultipartFile> documents, ReceivedLetter
            receivedLetter, Boolean isMain, EmployeePojo employeePojo, String fiscalYearCode) {
        if (documentUtil.checkEmpty(documents)) {

            for (MultipartFile file : documents) {
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                assert ext != null;
                if (!ext.equalsIgnoreCase("pdf")) {
                    throw new RuntimeException("File should be of type PDF");
                }
            }

            DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                    new DocumentSavePojo().builder()
                            .pisCode(tokenProcessorService.getPisCode())
                            .officeCode(tokenProcessorService.getOfficeCode())
                            .tag("darta_chalani")
                            .subModuleName("darta")
                            .type("1")
                            .build(),
                    documents
            );

            List<ReceivedLetterDocumentDetails> files = new ArrayList<>();

            if (pojo == null)
                throw new RuntimeException("कागच पत्र सेब हुन सकेन");

            if (pojo != null) {
                pojo.getDocuments().forEach(
                        x -> {
                            files.add(
                                    new ReceivedLetterDocumentDetails().builder()
                                            .documentId(x.getId())
                                            .documentName(x.getName())
                                            .documentSize(x.getSizeKB())
                                            .isMain(isMain)
                                            .build()
                            );
                        }
                );
            }
            receivedLetter.getReceivedLetterDocumentDetails().addAll(files);
        }

    }

    private void deleteDocuments(List<Long> documentsToRemove) {
        if (documentsToRemove != null && !documentsToRemove.isEmpty()) {
            for (Long id : documentsToRemove) {
                receivedLetterRepo.softDeleteDoc(id);
            }
//            documentUtil.deleteDocuments(documentsToRemove);
        }
    }

    @Override
    public Map<String, Object> getDartaSearchRecommendation() {
        String officeCode = tokenProcessorService.getOfficeCode();
        List<DartaSearchRecommendationDto> dartaSearchRecommendationData = receivedLetterMapper.getDartaSearchRecommendationData(officeCode);
        Map<String, Object> response = new HashMap<>();
        // extracting unique data for every column returned from the above query
        List<Map<String, Object>> uniqueManualSenderName = dartaSearchRecommendationData.stream()
                .filter(obj -> obj.getManualSenderName() != null && !obj.getManualSenderName().isEmpty() && !obj.getManualSenderName().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> {
                            Map<String, Object> tempMap = new HashMap<>();
                            tempMap.put("name", obj.getManualSenderName());
                            tempMap.put("type", obj.getType());
                            return tempMap;
                        }
                )
                .distinct()
                .collect(Collectors.toList());

        List<Map<String, Object>> uniqueSenderOfficeCode = dartaSearchRecommendationData.stream()
                .filter(obj -> obj.getSenderOfficeCode() != null && !obj.getSenderOfficeCode().isEmpty() && !obj.getSenderOfficeCode().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> {
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put("senderOfficeCode", obj.getSenderOfficeCode());
                    tempMap.put("type", obj.getType());
                    return tempMap;
                })
                .distinct()
                .collect(Collectors.toList());

        // building response body
        List<Map<String, Object>> manualSenderDetails = uniqueManualSenderName.stream().map(obj -> {
            Map<String, Object> tempSenderDetail = new HashMap<>();
            tempSenderDetail.put("name", obj.get("name"));
            tempSenderDetail.put("type", obj.get("type"));
            return tempSenderDetail;
        })
                .collect(Collectors.toList());

        List<Map<String, Object>> senderDetails = uniqueSenderOfficeCode.stream().map(obj -> {

            Map<String, Object> tempOfficeDetail = new HashMap<>();
            OfficeMinimalPojo senderOfficeCode = userMgmtServiceData.getOfficeDetailMinimal((String) obj.get("senderOfficeCode"));
            if (senderOfficeCode == null) {
                return null;
            }
            tempOfficeDetail.put("officeNameEn", senderOfficeCode.getNameEn());
            tempOfficeDetail.put("officeNameNp", senderOfficeCode.getNameNp());
            tempOfficeDetail.put("officeCode", senderOfficeCode.getCode());
            tempOfficeDetail.put("type", obj.get("type"));
            return tempOfficeDetail;
        })
                .collect(Collectors.toList());

        response.put("manualSenderDetails", manualSenderDetails);
        response.put("senderDetails", senderDetails);
        return response;
    }

    @Override
    public SectionInvolvedPojo checkSectionIsInvolved(String sectionCode) {

        SectionInvolvedPojo data = receivedLetterMapper.checkSectionIsInvolved(sectionCode);

        return data;
    }

    @Override
    public Page<ReceivedLetterResponsePojo> getReceivedLetterForTransfer(GetRowsRequest paginatedRequest) {
        EmployeePojo loginUser = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        Set<String> listPisCodes = new HashSet<>();

        checkUserSection(loginUser);

        String transferFromPisCode = paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE) != null ? paginatedRequest.getSearchField().get(TRANSFER_FROM_PIS_CODE).toString() : null;


        if (transferFromPisCode == null)
            throw new RuntimeException("Required field " + TRANSFER_FROM_PIS_CODE);

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(transferFromPisCode);

        String officeCode = employeePojo.getOffice() != null ? employeePojo.getOffice().getCode() : "";

        Page<ReceivedLetterResponsePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        if (employeePojo != null && employeePojo.getSection() != null)
            if (this.getPreviousPisCode(transferFromPisCode, employeePojo.getSection().getId().toString()) != null)
                listPisCodes.addAll(this.getPreviousPisCode(transferFromPisCode, employeePojo.getSection().getId().toString()));

        page = receivedLetterMapper.getReceivedLetterForTransfer(page, transferFromPisCode, employeePojo.getSection().getId().toString(), listPisCodes, paginatedRequest.getSearchField(), officeCode);

        for (ReceivedLetterResponsePojo manualLetter : page.getRecords()) {
            manualLetter.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(manualLetter.getSenderOfficeCode());
            manualLetter.setOfficeDetails(userMgmtServiceData.getOfficeDetail(manualLetter.getSenderOfficeCode()));
            manualLetter.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            manualLetter.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);

            if (manualLetter.getLetterType() != null && manualLetter.getLetterType().equals("Receive")) {
                List<ForwardResponsePojo> forwards = receivedLetterMapper.getForwards(manualLetter.getId());
                if (forwards != null) {
                    for (ForwardResponsePojo forwardResponsePojo : forwards) {
                        forwardResponsePojo.setReceiver(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getReceiverPisCode()));
                        forwardResponsePojo.setCreatedDateNp(dateConverter.convertAdToBs(manualLetter.getCreatedDate().toLocalDateTime().toLocalDate().toString()));

                        if (forwardResponsePojo.getDelegatedId() != null) {
                            DelegationResponsePojo delegationResponsePojo = userMgmtServiceData.getDelegationDetailsById(forwardResponsePojo.getDelegatedId());
                            if (delegationResponsePojo != null && delegationResponsePojo.getToEmployee() != null)
                                forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(delegationResponsePojo.getToEmployee().getCode()));
                        } else
                            forwardResponsePojo.setSender(userMgmtServiceData.getEmployeeDetailMinimal(forwardResponsePojo.getSenderPisCode()));

                        if (forwardResponsePojo.getReceiverPisCode().equals(transferFromPisCode)) {
                            manualLetter.setStatus(forwardResponsePojo.getStatus());
                            if (Boolean.TRUE.equals(forwardResponsePojo.getIsActive()))
                                manualLetter.setCurrentIsActive(true);
                        }
                    }
                    manualLetter.setForwards(forwards);
                }
                ReceivedLetterDetailResponsePojo details = receivedLetterMapper.getDetails(manualLetter.getId());
                manualLetter.setDetails(details);
            }
            if (manualLetter.getLetterType() != null && manualLetter.getLetterType().equals("Dispatch")) {
                List<DispatchLetterInternalDTO> dispatchLetterInternalDTOS = dispatchLetterMapper.getInternalsByDispatchId(manualLetter.getId());
                if (dispatchLetterInternalDTOS != null) {
                    for (DispatchLetterInternalDTO data : dispatchLetterInternalDTOS) {

                        if (data.getInternalReceiverPiscode() != null
                                && data.getInternalReceiverPiscode().equals(transferFromPisCode)
                                && employeePojo.getSectionId() != null && data.getInternalReceiverSectionId() != null
                                && employeePojo.getSectionId().equals(data.getInternalReceiverSectionId())) {
                            manualLetter.setStatus(Status.valueOf(data.getCompletionStatus()));
                            manualLetter.setCurrentIsActive(data.getIsActive());
                            if (data.getInternalReceiverCc() != null && data.getInternalReceiverCc())
                                manualLetter.setIsCC(data.getInternalReceiverCc());
                            if (data.getInternalReceiver() != null && data.getInternalReceiver())
                                manualLetter.setIsCC(!data.getInternalReceiver());
                            if (Boolean.TRUE.equals(data.getIsImportant()))
                                manualLetter.setIsImportant(true);
                        }
                    }
                }
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

    //this function check the user is involved in any section or not
    private void checkUserSection(EmployeePojo employeePojo) {
        if (employeePojo == null)
            throw new RuntimeException("Employee detail not found with id : " + tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ");
    }

    private void checkIsReceiver(String pisCode, String sectionCode, Long letterId) {

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();

        listPisCodes.add(pisCode);
        if (sectionCode != null) {
            if (this.getPreviousPisCode(pisCode, sectionCode) != null)
                listPisCodes.addAll(this.getPreviousPisCode(pisCode, sectionCode));
        }

        if (receivedLetterMapper.checkIsReceiver(letterId, listPisCodes, sectionCode)) {
            log.info("pis code: " + pisCode + " does not get this letter : " + letterId + " as receiver so that it can not be processed");
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
}