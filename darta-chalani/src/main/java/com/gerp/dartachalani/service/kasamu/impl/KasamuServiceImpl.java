package com.gerp.dartachalani.service.kasamu.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.document.DocumentMasterResponsePojo;
import com.gerp.dartachalani.dto.document.DocumentSavePojo;
import com.gerp.dartachalani.dto.kasamu.*;
import com.gerp.dartachalani.enums.KasamuSubjectType;
import com.gerp.dartachalani.mapper.KasamuMapper;
import com.gerp.dartachalani.model.kasamu.*;
import com.gerp.dartachalani.repo.kasamu.*;
import com.gerp.dartachalani.service.InitialService;
import com.gerp.dartachalani.service.kasamu.KasamuService;
import com.gerp.dartachalani.service.rabbitmq.RabbitMQService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.dartachalani.utils.DocumentUtil;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class KasamuServiceImpl implements KasamuService {

    private final KasamuRepository kasamuRepository;
    private final KasamuStateRepository kasamuStateRepository;
    private final KasamuDocumentDetailsRepository kasamuDocumentDetailsRepository;
    private final KasamuCommentRepository kasamuCommentRepository;
    private final ExternalKasamuEmployeeRepository externalKasamuEmployeeRepository;
    private final ModelMapper modelMapper;
    private final TokenProcessorService tokenProcessorService;
    private final InitialService initialService;
    private final KasamuMapper kasamuMapper;

    private final String MODULE_FORWARD_KEY = PermissionConstants.KASAMU_FORWARD;
    private final String MODULE_FINALIZED_KEY = PermissionConstants.KASAMU_FINALIZED;
    private final String MODULE_OWN_KEY = PermissionConstants.KASAMU_OWN;

    private final String CREATED = "created";
    private final String INBOX = "inbox";
    private final String FINALIZED = "finalized";

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private DocumentUtil documentUtil;

    @Autowired
    private RabbitMQService notificationService;

    @Autowired
    private CustomMessageSource customMessageSource;

    @Autowired
    private DateConverter dateConverter;


    public KasamuServiceImpl(KasamuRepository kasamuRepository,
                             KasamuStateRepository kasamuStateRepository,
                             KasamuDocumentDetailsRepository kasamuDocumentDetailsRepository,
                             KasamuCommentRepository kasamuCommentRepository,
                             ExternalKasamuEmployeeRepository externalKasamuEmployeeRepository,
                             ModelMapper modelMapper,
                             TokenProcessorService tokenProcessorService,
                             InitialService initialService,
                             KasamuMapper kasamuMapper) {
        this.kasamuRepository = kasamuRepository;
        this.kasamuStateRepository = kasamuStateRepository;
        this.kasamuDocumentDetailsRepository = kasamuDocumentDetailsRepository;
        this.kasamuCommentRepository = kasamuCommentRepository;
        this.externalKasamuEmployeeRepository = externalKasamuEmployeeRepository;
        this.modelMapper = modelMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.initialService = initialService;
        this.kasamuMapper = kasamuMapper;

    }

    /**
     * save kasamu
     **/
    @Override
    public KasamuResponsePojo save(KasamuRequestPojo kasamuRequestPojo) {

        String pisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(pisCode);
        String activeFiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
        String officeCode = tokenProcessorService.getOfficeCode();

        validateKasamu(kasamuRequestPojo, activeFiscalYearCode);

        if (employeePojo.getSection() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "प्रयोगकर्ताको शाखा फेला परेन");

        Kasamu kasamu = new Kasamu().builder()
                .employeePisCode(kasamuRequestPojo.getEmployeePisCode())
                .employeeSectionCode(kasamuRequestPojo.getEmployeeSectionCode())
                .pisCode(pisCode)
                .sectionCode(employeePojo.getSectionId())
                .officeCode(officeCode)
                .employeeOfficeCode(officeCode)
                .registrationNo(initialService.getDartaNumber(tokenProcessorService.getOfficeCode()))
                .fiscalYearCode(activeFiscalYearCode)
                .isExternalEmployee(kasamuRequestPojo.getIsExternalEmployee())
                .subject(kasamuRequestPojo.getSubject())
                .subjectType(kasamuRequestPojo.getSubjectType())
                .priority(LetterPriority.VH)
                .privacy(LetterPrivacy.HC)
                .build();


        if (kasamuRequestPojo.getDocument() != null && !kasamuRequestPojo.getDocument().isEmpty())
            processDocument(kasamuRequestPojo.getDocument(), kasamu, true, employeePojo, activeFiscalYearCode);

        if (kasamuRequestPojo.getSupporting() != null && !kasamuRequestPojo.getSupporting().isEmpty())
            this.processUpdateMultipleDocument(kasamuRequestPojo.getSupporting(), kasamu, false, employeePojo, activeFiscalYearCode);

        Kasamu savedKasamu = kasamuRepository.save(kasamu);

        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(pisCode);
        String tokenUserSection = tokenUser != null ? tokenUser.getSection() != null ? tokenUser.getSection().getId().toString() : null : null;

        List<KasamuState> kasamuStates = new ArrayList<>();
        List<NotificationPojo> notificationPojoList = new ArrayList<>();

        //send notification to employee whose kasamu is created
        if (kasamuRequestPojo.getIsExternalEmployee() != null && kasamuRequestPojo.getIsExternalEmployee()) {
            if (kasamuRequestPojo.getExternalEmployee() == null)
                throw new RuntimeException("External employee can't be null");
            // ExternalKasamuEmployee externalKasamuEmployee = modelMapper.map(kasamuRequestPojo.getExternalEmployee(), ExternalKasamuEmployee.class);
            ExternalKasamuEmployee externalKasamuEmployee = new ExternalKasamuEmployee().builder()
                    .pisCode(kasamuRequestPojo.getExternalEmployee().getPisCode())
                    .name(kasamuRequestPojo.getExternalEmployee().getName())
                    .designationCode(kasamuRequestPojo.getExternalEmployee().getDesignationCode())
                    .positionCode(kasamuRequestPojo.getExternalEmployee().getPositionCode())
                    .currentOfficeName(kasamuRequestPojo.getExternalEmployee().getCurrentOfficeName())
                    .kasamu(kasamu)
                    .serviceCode(kasamuRequestPojo.getExternalEmployee().getServiceCode())
                    .groupCode(kasamuRequestPojo.getExternalEmployee().getGroupCode())
                    .subGroupCode(kasamuRequestPojo.getExternalEmployee().getSubGroupCode())
                    .build();

            externalKasamuEmployeeRepository.save(externalKasamuEmployee);

        } else {
            notificationPojoList.add(NotificationPojo.builder()
                    .moduleId(kasamu.getId())
                    .module(MODULE_OWN_KEY)
                    .sender(pisCode)
                    .receiver(kasamuRequestPojo.getEmployeePisCode())
                    .subject(customMessageSource.getNepali("darta.kasamu"))
                    .detail(customMessageSource.getNepali("darta.kasamu.created", tokenUser != null ? tokenUser.getNameNp() : "", kasamu.getSubject()))
                    .pushNotification(true)
                    .received(true)
                    .build());
        }

        if (kasamuRequestPojo.getReceiver() != null && !kasamuRequestPojo.getReceiver().isEmpty()) {
            kasamuRequestPojo.getReceiver().forEach(x -> {
                EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());

                KasamuState kasamuState = new KasamuState().builder()
                        .kasamu(kasamu)
                        .description(kasamuRequestPojo.getDescription())
                        .receiverPisCode(x.getReceiverPisCode())
                        .receiverSectionCode(x.getReceiverSectionId())
                        .receiverOfficeCode(officeCode)
                        .senderPisCode(pisCode)
                        .senderSectionCode(tokenUserSection)
                        .senderOfficeCode(officeCode)
                        .isCc(Boolean.FALSE)
                        .isSeen(Boolean.FALSE)
                        .build();

                kasamuStates.add(kasamuState);

                // send notification to kasamu receiver
                notificationPojoList.add(NotificationPojo.builder()
                        .moduleId(kasamu.getId())
                        .module(MODULE_FORWARD_KEY)
                        .sender(pisCode)
                        .receiver(x.getReceiverPisCode())
                        .subject(customMessageSource.getNepali("darta.kasamu"))
                        .detail(customMessageSource.getNepali("darta.kasamu.forward", tokenUser != null ? tokenUser.getNameNp() : "", kasamu.getSubject()))
                        .pushNotification(true)
                        .received(true)
                        .build());
            });
        }

        if (kasamuRequestPojo.getRec() != null && !kasamuRequestPojo.getRec().isEmpty()) {
            kasamuRequestPojo.getRec().forEach(x -> {
                EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());

                KasamuState kasamuStateCc = new KasamuState().builder()
                        .kasamu(kasamu)
                        .description(kasamuRequestPojo.getDescription())
                        .receiverPisCode(x.getReceiverPisCode())
                        .receiverSectionCode(x.getReceiverSectionId())
                        .receiverOfficeCode(officeCode)
                        .senderPisCode(pisCode)
                        .senderSectionCode(tokenUserSection)
                        .senderOfficeCode(officeCode)
                        .isCc(Boolean.TRUE)
                        .isSeen(Boolean.FALSE)
                        .build();

                kasamuStates.add(kasamuStateCc);

                //send kasamu to cc receiver
                notificationPojoList.add(NotificationPojo.builder()
                        .moduleId(kasamu.getId())
                        .module(MODULE_FORWARD_KEY)
                        .sender(pisCode)
                        .receiver(x.getReceiverPisCode())
                        .subject(customMessageSource.getNepali("darta.kasamu"))
                        .detail(customMessageSource.getNepali("darta.kasamu.forward", tokenUser != null ? tokenUser.getNameNp() : "", kasamu.getSubject()))
                        .pushNotification(true)
                        .received(true)
                        .build());
            });
        }

        //save all the kasamu states
        kasamuStateRepository.saveAll(kasamuStates);

        //send notification
//        notificationPojoList.forEach(x -> {
//            notificationService.notificationProducer(x);
//        });

        return modelMapper.map(savedKasamu, KasamuResponsePojo.class);
    }

    /**
     * forward kasamu
     */
    @Override
    public KasamuResponsePojo forward(KasamuStateRequestPojo data) {

        Kasamu kasamu = kasamuRepository.findById(data.getKasamuId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kasamu not found with id: " + data.getKasamuId()));

        String pisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(pisCode);
        String tokenUserSection = tokenUser != null ? tokenUser.getSection() != null ? tokenUser.getSection().getId().toString() : null : null;
        String officeCode = tokenProcessorService.getOfficeCode();

        //get active kasamu for sender and inactive it
        KasamuState activeKasamu = kasamuStateRepository.getActiveKasamu(data.getKasamuId(), pisCode, tokenUserSection);
        if (activeKasamu != null) {
            activeKasamu.setActive(Boolean.FALSE);
            kasamuStateRepository.save(activeKasamu);
        }

        //get active cc kasamu for sender and inactive it
        KasamuState activeKasamuCc = kasamuStateRepository.getActiveKasamuCc(data.getKasamuId(), pisCode, tokenUserSection);
        if (activeKasamuCc != null) {
            activeKasamuCc.setActive(Boolean.FALSE);
            kasamuStateRepository.save(activeKasamuCc);
        }

        List<KasamuState> kasamuStates = new ArrayList<>();
        List<NotificationPojo> notificationPojoList = new ArrayList<>();
        if (data.getReceiver() != null && !data.getReceiver().isEmpty()) {
            data.getReceiver().forEach(x -> {
                EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());

                //get active kasamu for receiver
                KasamuState kasamuStateActiveInReceipient = kasamuStateRepository.getActiveKasamuAll(data.getKasamuId(), x.getReceiverPisCode(), x.getReceiverSectionId());
                if (kasamuStateActiveInReceipient != null) {
                    throw new RuntimeException(receipient != null ? receipient.getNameNp() + " संग यो का.स.मु. पहिले नै भएकोले फेरि पठाउन निषेध गरिएको छ|" : "Invalid");
                }

                KasamuState kasamuState = new KasamuState().builder()
                        .kasamu(kasamu)
                        .description(data.getDescription())
                        .receiverPisCode(x.getReceiverPisCode())
                        .receiverSectionCode(x.getReceiverSectionId())
                        .receiverOfficeCode(officeCode)
                        .senderPisCode(pisCode)
                        .senderSectionCode(tokenUserSection)
                        .senderOfficeCode(officeCode)
                        .isCc(Boolean.FALSE)
                        .isSeen(Boolean.FALSE)
                        .build();

                kasamuStates.add(kasamuState);

                notificationPojoList.add(NotificationPojo.builder()
                        .moduleId(kasamu.getId())
                        .module(MODULE_FORWARD_KEY)
                        .sender(pisCode)
                        .receiver(x.getReceiverPisCode())
                        .subject(customMessageSource.getNepali("darta.kasamu"))
                        .detail(customMessageSource.getNepali("darta.kasamu.forward", tokenUser != null ? tokenUser.getNameNp() : "", kasamu.getSubject()))
                        .pushNotification(true)
                        .received(true)
                        .build());

            });
        }

        if (data.getRec() != null && !data.getRec().isEmpty()) {
            data.getRec().forEach(x -> {
                EmployeePojo receipient = userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode());

                //get active kasamu for receiver
                KasamuState kasamuStateActiveInReceipient = kasamuStateRepository.getActiveKasamuAll(data.getKasamuId(), x.getReceiverPisCode(), x.getReceiverSectionId());
                if (kasamuStateActiveInReceipient != null) {
                    throw new RuntimeException(receipient != null ? receipient.getNameNp() + " संग यो का.स.मु. पहिले नै भएकोले फेरि पठाउन निषेध गरिएको छ|" : "Invalid");
                }

                KasamuState kasamuStateCc = new KasamuState().builder()
                        .kasamu(kasamu)
                        .description(data.getDescription())
                        .receiverPisCode(x.getReceiverPisCode())
                        .receiverSectionCode(x.getReceiverSectionId())
                        .receiverOfficeCode(officeCode)
                        .senderPisCode(pisCode)
                        .senderSectionCode(tokenUserSection)
                        .senderOfficeCode(officeCode)
                        .isCc(Boolean.TRUE)
                        .isSeen(Boolean.FALSE)
                        .build();

                kasamuStates.add(kasamuStateCc);

                notificationPojoList.add(NotificationPojo.builder()
                        .moduleId(kasamu.getId())
                        .module(MODULE_FORWARD_KEY)
                        .sender(pisCode)
                        .receiver(x.getReceiverPisCode())
                        .subject(customMessageSource.getNepali("darta.kasamu"))
                        .detail(customMessageSource.getNepali("darta.kasamu.forward", tokenUser != null ? tokenUser.getNameNp() : "", kasamu.getSubject()))
                        .pushNotification(true)
                        .received(true)
                        .build());
            });
        }

        //save all the kasamu states
        kasamuStateRepository.saveAll(kasamuStates);

        //send notification
        notificationPojoList.forEach(x -> {
            notificationService.notificationProducer(x);
        });

        return modelMapper.map(kasamu, KasamuResponsePojo.class);
    }

    @Override
    public KasamuResponsePojo update(KasamuRequestPojo kasamuRequestPojo, Long id) {
        Kasamu kasamu = kasamuRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kasamu not found with id: " + id));

        return null;
    }


    @Override
    public KasamuResponsePojo getById(Long id) {
        Kasamu kasamu = kasamuRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kasamu not found with id: " + id));
        KasamuResponsePojo responsePojo = modelMapper.map(kasamu, KasamuResponsePojo.class);

        if (kasamu.getIsExternalEmployee() != null && kasamu.getIsExternalEmployee()) {

            ExternalEmployeeResponsePojo externalEmployeeResponsePojo = modelMapper.map(kasamu.getExternalKasamuEmployee(), ExternalEmployeeResponsePojo.class);
            if (kasamu.getExternalKasamuEmployee() != null && kasamu.getExternalKasamuEmployee().getDesignationCode() != null && !kasamu.getExternalKasamuEmployee().getDesignationCode().equals("")) {
                DesignationPojo designation = userMgmtServiceData.getDesignationDetail(kasamu.getExternalKasamuEmployee().getDesignationCode());
                externalEmployeeResponsePojo.setDesignation(designation);
            }

            if (kasamu.getExternalKasamuEmployee() != null && kasamu.getExternalKasamuEmployee().getPositionCode() != null && !kasamu.getExternalKasamuEmployee().getPositionCode().equals("")) {
                PositionPojo position = userMgmtServiceData.getPositionDetail(kasamu.getExternalKasamuEmployee().getPositionCode());
                externalEmployeeResponsePojo.setPosition(position);
            }

            if (kasamu.getExternalKasamuEmployee() != null && kasamu.getExternalKasamuEmployee().getServiceCode() != null && !kasamu.getExternalKasamuEmployee().getServiceCode().equals("")) {
                ServicePojo service = userMgmtServiceData.getServiceGroupDetail(kasamu.getExternalKasamuEmployee().getServiceCode());
                externalEmployeeResponsePojo.setService(service);
            }

            if (kasamu.getExternalKasamuEmployee() != null && kasamu.getExternalKasamuEmployee().getGroupCode() != null && !kasamu.getExternalKasamuEmployee().getGroupCode().equals("")) {
                ServicePojo group = userMgmtServiceData.getServiceGroupDetail(kasamu.getExternalKasamuEmployee().getGroupCode());
                externalEmployeeResponsePojo.setGroup(group);
            }

            if (kasamu.getExternalKasamuEmployee() != null && kasamu.getExternalKasamuEmployee().getSubGroupCode() != null && !kasamu.getExternalKasamuEmployee().getSubGroupCode().equals("")) {
                ServicePojo subGroup = userMgmtServiceData.getServiceGroupDetail(kasamu.getExternalKasamuEmployee().getSubGroupCode());
                externalEmployeeResponsePojo.setSubGroup(subGroup);
            }

            responsePojo.setExternalEmployee(externalEmployeeResponsePojo);

        } else {
            EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(kasamu.getEmployeePisCode());
            responsePojo.setEmployee(employeePojo);
        }

        EmployeePojo creator = userMgmtServiceData.getEmployeeDetail(kasamu.getPisCode());
        responsePojo.setCreator(creator);

        responsePojo.setDocumentId(kasamuDocumentDetailsRepository.getByKasamuId(id));

        responsePojo.setCreatedDateBs(dateConverter.convertAdToBs(responsePojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
        responsePojo.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(responsePojo.getCreatedDate().toLocalDateTime().toLocalDate().toString())));

        List<KasamuState> kasamuStates = kasamuStateRepository.getActivityList(id);
        List<KasamuStateResponsePojo> response = kasamuStates.stream().map(x -> modelMapper.map(x, KasamuStateResponsePojo.class))
                .collect(Collectors.toList());

        response.forEach(x -> {
            x.setReceiver(userMgmtServiceData.getEmployeeDetail(x.getReceiverPisCode()));
            x.setSender(userMgmtServiceData.getEmployeeDetail(x.getSenderPisCode()));
            x.setCreatedDateBs(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            x.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString())));

            List<KasamuCommentPojo> comments = kasamuCommentRepository.getByKasamuState(x.getId()).stream().map(y -> modelMapper.map(y, KasamuCommentPojo.class)).collect(Collectors.toList());
            comments.forEach(z -> {
                z.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
            });
            x.setComments(comments);

        });

        responsePojo.setKasamuState(response);

        return responsePojo;
    }


    @Override
    public Page<KasamuResponsePojo> getCreatedKasamuList(GetRowsRequest paginateRequest) {

        Page<KasamuResponsePojo> page = new Page(paginateRequest.getPage(), paginateRequest.getLimit());

        String pisCode = tokenProcessorService.getPisCode();

        EmployeePojo employeeDetail = userMgmtServiceData.getEmployeeDetail(pisCode);

        if (employeeDetail == null)
            throw new RuntimeException("Employee not found with pis: " + pisCode);
        if (employeeDetail.getSection() == null)
            throw new RuntimeException("Employee section not found");

//        KasamuResponsePaginationPojo pagination = new KasamuResponsePaginationPojo();
//        Pageable pageable = PageRequest.of(paginateRequest.getPage(), paginateRequest.getLimit());
//        String activeFiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
//        List<Kasamu> list = kasamuRepository.getByCreator(pisCode, activeFiscalYearCode, pageable);
//        Integer total = kasamuRepository.countByCreator(pisCode, activeFiscalYearCode);
//        int page = paginateRequest.getPage();
//        int limit = paginateRequest.getLimit();
//        pagination.setCurrent(page);
//        pagination.setSize(limit);
//        pagination.setPages((int) Math.ceil(((double) total / limit)));
//        pagination.setTotal(total);
//        pagination.setRecords(list.stream().map(x -> modelMapper.map(x, KasamuResponsePojo.class)).collect(Collectors.toList()));

        page = kasamuMapper.getCreatedKasamuList(
                page,
                pisCode,
                employeeDetail.getSectionId(),
                paginateRequest.getSearchField()
        );

        page.getRecords().forEach(x -> {
            EmployeePojo creator = userMgmtServiceData.getEmployeeDetail(x.getPisCode());
            x.setCreator(creator);

            if (x.getIsExternalEmployee() != null && x.getIsExternalEmployee()) {

                ExternalEmployeeResponsePojo externalEmployeeResponsePojo = kasamuMapper.getExternalEmployeeByKasamuId(x.getId());

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getDesignationCode() != null && !externalEmployeeResponsePojo.getDesignationCode().equals("")) {
                    DesignationPojo designation = userMgmtServiceData.getDesignationDetail(externalEmployeeResponsePojo.getDesignationCode());
                    externalEmployeeResponsePojo.setDesignation(designation);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getPositionCode() != null && !externalEmployeeResponsePojo.getPositionCode().equals("")) {
                    PositionPojo position = userMgmtServiceData.getPositionDetail(externalEmployeeResponsePojo.getPositionCode());
                    externalEmployeeResponsePojo.setPosition(position);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getServiceCode() != null && !externalEmployeeResponsePojo.getServiceCode().equals("")) {
                    ServicePojo service = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getServiceCode());
                    externalEmployeeResponsePojo.setService(service);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getGroupCode() != null && !externalEmployeeResponsePojo.getGroupCode().equals("")) {
                    ServicePojo group = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getGroupCode());
                    externalEmployeeResponsePojo.setGroup(group);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getSubGroupCode() != null && !externalEmployeeResponsePojo.getSubGroupCode().equals("")) {
                    ServicePojo subGroup = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getSubGroupCode());
                    externalEmployeeResponsePojo.setSubGroup(subGroup);
                }

                x.setExternalEmployee(externalEmployeeResponsePojo);

            } else {
                EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(x.getEmployeePisCode());
                x.setEmployee(employeePojo);
            }
            x.setCreatedDateBs(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            x.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
        });
        return page;
    }


    @Override
    public Page<KasamuResponsePojo> getInboxKasamuList(GetRowsRequest paginateRequest) {
        Page<KasamuResponsePojo> page = new Page(paginateRequest.getPage(), paginateRequest.getLimit());

        String pisCode = tokenProcessorService.getPisCode();

        EmployeePojo employeeDetail = userMgmtServiceData.getEmployeeDetail(pisCode);

        if (employeeDetail == null)
            throw new RuntimeException("Employee not found with pis: " + pisCode);
        if (employeeDetail.getSection() == null)
            throw new RuntimeException("Employee section not found");
//        KasamuResponsePaginationPojo pagination = new KasamuResponsePaginationPojo();
//        Pageable pageable = PageRequest.of(paginateRequest.getPage(), paginateRequest.getLimit());
//        String activeFiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
//        List<Kasamu> list = kasamuRepository.getInbox(pisCode, activeFiscalYearCode, pageable);
//        Integer total = kasamuRepository.countInbox(pisCode, activeFiscalYearCode);
//        int page = paginateRequest.getPage();
//        int limit = paginateRequest.getLimit();
//        pagination.setCurrent(page);
//        pagination.setSize(limit);
//        pagination.setPages((int) Math.ceil(((double) total / limit)));
//        pagination.setTotal(total);
//        pagination.setRecords(list.stream().map(x -> modelMapper.map(x, KasamuResponsePojo.class)).collect(Collectors.toList()));

        page = kasamuMapper.getKasamuInboxList(
                page,
                pisCode,
                employeeDetail.getSectionId(),
                paginateRequest.getSearchField()
        );

        page.getRecords().forEach(x -> {
            EmployeePojo creator = userMgmtServiceData.getEmployeeDetail(x.getPisCode());

            x.setCreator(creator);

            if (x.getIsExternalEmployee() != null && x.getIsExternalEmployee()) {

                ExternalEmployeeResponsePojo externalEmployeeResponsePojo = kasamuMapper.getExternalEmployeeByKasamuId(x.getId());

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getDesignationCode() != null && !externalEmployeeResponsePojo.getDesignationCode().equals("")) {
                    DesignationPojo designation = userMgmtServiceData.getDesignationDetail(externalEmployeeResponsePojo.getDesignationCode());
                    externalEmployeeResponsePojo.setDesignation(designation);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getPositionCode() != null && !externalEmployeeResponsePojo.getPositionCode().equals("")) {
                    PositionPojo position = userMgmtServiceData.getPositionDetail(externalEmployeeResponsePojo.getPositionCode());
                    externalEmployeeResponsePojo.setPosition(position);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getServiceCode() != null && !externalEmployeeResponsePojo.getServiceCode().equals("")) {
                    ServicePojo service = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getServiceCode());
                    externalEmployeeResponsePojo.setService(service);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getGroupCode() != null && !externalEmployeeResponsePojo.getGroupCode().equals("")) {
                    ServicePojo group = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getGroupCode());
                    externalEmployeeResponsePojo.setGroup(group);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getSubGroupCode() != null && !externalEmployeeResponsePojo.getSubGroupCode().equals("")) {
                    ServicePojo subGroup = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getSubGroupCode());
                    externalEmployeeResponsePojo.setSubGroup(subGroup);
                }
                x.setExternalEmployee(externalEmployeeResponsePojo);

            } else {
                EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(x.getEmployeePisCode());
                x.setEmployee(employeePojo);
            }

            x.setCreatedDateBs(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            x.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
        });

        return page;
    }


    @Override
    public Page<KasamuResponsePojo> getFinalizedKasamuList(GetRowsRequest paginateRequest) {
        Page<KasamuResponsePojo> page = new Page(paginateRequest.getPage(), paginateRequest.getLimit());

        String pisCode = tokenProcessorService.getPisCode();

        EmployeePojo employeeDetail = userMgmtServiceData.getEmployeeDetail(pisCode);

        if (employeeDetail == null)
            throw new RuntimeException("Employee not found with pis: " + pisCode);
        if (employeeDetail.getSection() == null)
            throw new RuntimeException("Employee section not found");
//        KasamuResponsePaginationPojo pagination = new KasamuResponsePaginationPojo();
//        Pageable pageable = PageRequest.of(paginateRequest.getPage(), paginateRequest.getLimit());
//        String activeFiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
//        List<Kasamu> list = kasamuRepository.getFinalized(pisCode, activeFiscalYearCode, pageable);
//        Integer total = kasamuRepository.countFinalized(pisCode, activeFiscalYearCode);
//        int page = paginateRequest.getPage();
//        int limit = paginateRequest.getLimit();
//        pagination.setCurrent(page);
//        pagination.setSize(limit);
//        pagination.setPages((int) Math.ceil(((double) total / limit)));
//        pagination.setTotal(total);
//        pagination.setRecords(list.stream().map(x -> modelMapper.map(x, KasamuResponsePojo.class)).collect(Collectors.toList()));

        page = kasamuMapper.getFinalizedKasamuList(
                page,
                pisCode,
                employeeDetail.getSectionId(),
                paginateRequest.getSearchField()
        );

        page.getRecords().forEach(x -> {
            EmployeePojo creator = userMgmtServiceData.getEmployeeDetail(x.getPisCode());
            if (x.getIsExternalEmployee() != null && x.getIsExternalEmployee()) {

                ExternalEmployeeResponsePojo externalEmployeeResponsePojo = kasamuMapper.getExternalEmployeeByKasamuId(x.getId());

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getDesignationCode() != null && !externalEmployeeResponsePojo.getDesignationCode().equals("")) {
                    DesignationPojo designation = userMgmtServiceData.getDesignationDetail(externalEmployeeResponsePojo.getDesignationCode());
                    externalEmployeeResponsePojo.setDesignation(designation);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getPositionCode() != null && !externalEmployeeResponsePojo.getPositionCode().equals("")) {
                    PositionPojo position = userMgmtServiceData.getPositionDetail(externalEmployeeResponsePojo.getPositionCode());
                    externalEmployeeResponsePojo.setPosition(position);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getServiceCode() != null && !externalEmployeeResponsePojo.getServiceCode().equals("")) {
                    ServicePojo service = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getServiceCode());
                    externalEmployeeResponsePojo.setService(service);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getGroupCode() != null && !externalEmployeeResponsePojo.getGroupCode().equals("")) {
                    ServicePojo group = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getGroupCode());
                    externalEmployeeResponsePojo.setGroup(group);
                }

                if (externalEmployeeResponsePojo != null && externalEmployeeResponsePojo.getSubGroupCode() != null && !externalEmployeeResponsePojo.getSubGroupCode().equals("")) {
                    ServicePojo subGroup = userMgmtServiceData.getServiceGroupDetail(externalEmployeeResponsePojo.getSubGroupCode());
                    externalEmployeeResponsePojo.setSubGroup(subGroup);
                }
                x.setExternalEmployee(externalEmployeeResponsePojo);

            } else {
                EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(x.getEmployeePisCode());
                x.setEmployee(employeePojo);
            }
            x.setCreator(creator);
            x.setCreatedDateBs(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            x.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
        });

        return page;
    }


    @Override
    public Page<KasamuResponsePojo> getEmployeeList(GetRowsRequest paginateRequest) {
        Page<KasamuResponsePojo> page = new Page(paginateRequest.getPage(), paginateRequest.getLimit());

        String pisCode = tokenProcessorService.getPisCode();

        EmployeePojo employeeDetail = userMgmtServiceData.getEmployeeDetail(pisCode);

        if (employeeDetail == null)
            throw new RuntimeException("Employee not found with pis: " + pisCode);
        if (employeeDetail.getSection() == null)
            throw new RuntimeException("Employee section not found");
//        KasamuResponsePaginationPojo pagination = new KasamuResponsePaginationPojo();
//        Pageable pageable = PageRequest.of(paginateRequest.getPage(), paginateRequest.getLimit());
//        String activeFiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
//        List<Kasamu> list = kasamuRepository.getByEmployee(pisCode, activeFiscalYearCode, pageable);
//        Integer total = kasamuRepository.countByEmployee(pisCode, activeFiscalYearCode);
//        int page = paginateRequest.getPage();
//        int limit = paginateRequest.getLimit();
//        pagination.setCurrent(page);
//        pagination.setSize(limit);
//        pagination.setPages((int) Math.ceil(((double) total / limit)));
//        pagination.setTotal(total);
//        pagination.setRecords(list.stream().map(x -> modelMapper.map(x, KasamuResponsePojo.class)).collect(Collectors.toList()));

        page = kasamuMapper.getEmployeeKasamuList(
                page,
                pisCode,
                employeeDetail.getSectionId(),
                paginateRequest.getSearchField()
        );

        page.getRecords().forEach(x -> {
            EmployeePojo creator = userMgmtServiceData.getEmployeeDetail(x.getPisCode());
            if (x.getEmployeePisCode() != null) {
                EmployeePojo employee = userMgmtServiceData.getEmployeeDetail(x.getEmployeePisCode());
                x.setEmployee(employee);
            }
            x.setCreator(creator);
            x.setCreatedDateBs(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            x.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
        });

        return page;
    }

    @Override
    public Boolean finalizedKasamu(StatusPojo statusPojo) {
        Kasamu kasamu = kasamuRepository.findById(statusPojo.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kasamu not found with id: " + statusPojo.getId()));

        String pisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeeDetail = userMgmtServiceData.getEmployeeDetail(pisCode);
        if (employeeDetail == null)
            throw new RuntimeException("Employee not found with pis: " + pisCode);
        if (employeeDetail.getSection() == null)
            throw new RuntimeException("Employee section not found");

        //check is the kasamu is already finalized
        if (kasamu.getCompletionStatus() == Status.FN) {
            throw new RuntimeException("Kasamu is already finalized");
        }

        //check if the user is kasamu receiver or not
        if (!kasamuStateRepository.isValidateForFinalized(pisCode, statusPojo.getId())) {
            throw new RuntimeException("Does not have finalized permission");
        }

        KasamuState kasamuState = kasamuStateRepository.getKasamuState(pisCode, statusPojo.getId());
        kasamuState.setStatus(Status.FN);
        kasamuStateRepository.save(kasamuState);

        if (statusPojo.getDescription() != null) {
            KasamuComment kasamuComment = new KasamuComment().builder()
                    .comment(statusPojo.getDescription())
                    .kasamuState(kasamuState)
                    .build();

            kasamuCommentRepository.save(kasamuComment);
        }

        //check all the receiver kasamu state are finalized or not
        //note this is not necessary as of now since we are forwarding only once
        if (kasamuStateRepository.isAllStateFinalized(statusPojo.getId())) {
            kasamu.setCompletionStatus(Status.FN);
            kasamuRepository.save(kasamu);
        }


        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(pisCode);

        //send notification to kasamu creator
        notificationService.notificationProducer(NotificationPojo.builder()
                .moduleId(kasamu.getId())
                .module(MODULE_FINALIZED_KEY)
                .sender(pisCode)
                .receiver(kasamu.getPisCode())
                .subject(customMessageSource.getNepali("darta.kasamu"))
                .detail(customMessageSource.getNepali("darta.kasamu.finalized", tokenUser != null ? tokenUser.getNameNp() : "", kasamu.getSubject()))
                .pushNotification(true)
                .received(true)
                .build());

        if (kasamu.getEmployeePisCode() != null && !kasamu.getEmployeePisCode().equals("")) {
            //send notification to kasamu owner
            notificationService.notificationProducer(NotificationPojo.builder()
                    .moduleId(kasamu.getId())
                    .module(MODULE_FINALIZED_KEY)
                    .sender(pisCode)
                    .receiver(kasamu.getEmployeePisCode())
                    .subject(customMessageSource.getNepali("darta.kasamu"))
                    .detail(customMessageSource.getNepali("darta.kasamu.finalized", tokenUser != null ? tokenUser.getNameNp() : "", kasamu.getSubject()))
                    .pushNotification(true)
                    .received(true)
                    .build());
        }

        return true;
    }

    @Override
    public Map<String, Object> getSearchRecommendationCreate(GetRowsRequest getRowsRequest) {
        String pisCode = tokenProcessorService.getPisCode();

        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(pisCode);

        if (employeePojo != null && employeePojo.getSection() == null)
            throw new RuntimeException("Employee section not found");

        List<String> pisCodes;
        List<String> externalEmployees;

        Map<String, Object> searchField = getRowsRequest.getSearchField();

        if (searchField == null)
            throw new RuntimeException("Search Field required");

        Object type = searchField.get("type");
        if (type == null)
            throw new RuntimeException("Type requied in search field");
        switch (type.toString()) {
            case CREATED:
                pisCodes = kasamuMapper.getPisCodeCreatedList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
                externalEmployees = kasamuMapper.getExternalEmployeeCreatedList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
                break;
            case INBOX:
                pisCodes = kasamuMapper.getPisCodeInboxList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
                externalEmployees = kasamuMapper.getExternalEmployeeInboxList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
                break;
            case FINALIZED:
                pisCodes = kasamuMapper.getPisCodeFinalizedList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
                externalEmployees = kasamuMapper.getExternalEmployeeFinalizedList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
                break;
            default:
                throw new RuntimeException("invalid type: " + type + ", type must be created, inbox or finalized");
        }

        List<Map<String, Object>> names = pisCodes.stream().map(x -> {
            Map<String, Object> map = new HashMap<>();
            EmployeePojo receiverDetail = userMgmtServiceData.getEmployeeDetail(x);
            if (receiverDetail == null) {
                return null;
            }
            map.put("nameNp", receiverDetail.getNameNp());
            map.put("nameEn", receiverDetail.getNameEn());
            map.put("pisCode", x);
            return map;
        }).collect(Collectors.toList());

        externalEmployees.removeIf(Objects::isNull);

        List<Map<String, Object>> externalNames = externalEmployees.stream().map(x -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", x);
            return map;
        }).collect(Collectors.toList());

        names.removeIf(Objects::isNull);


        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("employeeDetails", names);
        returnMap.put("externalEmployeeDetails", externalNames);

        return returnMap;
    }

    @Override
    public Map<String, Object> getSearchRecommendationInbox(GetRowsRequest getRowsRequest) {
        String pisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(pisCode);

        if (employeePojo != null && employeePojo.getSection() == null)
            throw new RuntimeException("Employee section not found");
        List<String> pisCodes = kasamuMapper.getPisCodeInboxList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
        List<String> externalEmployees = kasamuMapper.getExternalEmployeeInboxList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
        List<Map<String, Object>> creatorNames = pisCodes.stream().map(x -> {
            Map<String, Object> map = new HashMap<>();
            EmployeePojo receiverDetail = userMgmtServiceData.getEmployeeDetail(x);
            if (receiverDetail == null) {
                return null;
            }
            map.put("nameNp", receiverDetail.getNameNp());
            map.put("nameEn", receiverDetail.getNameEn());
            map.put("pisCode", x);
            return map;
        }).collect(Collectors.toList());

        externalEmployees.removeIf(Objects::isNull);

        List<Map<String, Object>> externalNames = externalEmployees.stream().map(x -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", x);
            return map;
        }).collect(Collectors.toList());

        creatorNames.removeIf(Objects::isNull);

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("employeeDetails", creatorNames);
        returnMap.put("externalEmployeeDetails", externalNames);
        return returnMap;
    }

    @Override
    public Map<String, Object> getSearchRecommendationFinalized(GetRowsRequest getRowsRequest) {
        String pisCode = tokenProcessorService.getPisCode();
        EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(pisCode);

        if (employeePojo != null && employeePojo.getSection() == null)
            throw new RuntimeException("Employee section not found");
        List<String> pisCodes = kasamuMapper.getPisCodeFinalizedList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
        List<String> externalEmployees = kasamuMapper.getExternalEmployeeFinalizedList(pisCode, employeePojo.getSection().getId() + "", getRowsRequest.getSearchField());
        List<Map<String, Object>> creatorNames = pisCodes.stream().map(x -> {
            Map<String, Object> map = new HashMap<>();
            EmployeePojo receiverDetail = userMgmtServiceData.getEmployeeDetail(x);
            if (receiverDetail == null) {
                return null;
            }
            map.put("nameNp", receiverDetail.getNameNp());
            map.put("nameEn", receiverDetail.getNameEn());
            map.put("pisCode", x);
            return map;
        }).collect(Collectors.toList());

        externalEmployees.removeIf(Objects::isNull);

        List<Map<String, Object>> externalNames = externalEmployees.stream().map(x -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", x);
            return map;
        }).collect(Collectors.toList());

        creatorNames.removeIf(Objects::isNull);

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("employeeDetails", creatorNames);
        returnMap.put("externalEmployeeDetails", externalNames);
        return returnMap;
    }

    private void processDocument(List<MultipartFile> document, Kasamu kasamu, Boolean
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
                        .type("4")
                        .build(),
                document
        );

        if (pojo == null)
            throw new RuntimeException("कागच पत्र सेब हुन सकेन");

        if (pojo != null) {
            kasamu.setKasamuDocumentDetails(
                    pojo.getDocuments().stream().map(
                            x -> new KasamuDocumentDetails().builder()
                                    .documentId(x.getId())
                                    .documentName(x.getName())
                                    .documentSize(x.getSizeKB())
                                    .isMain(isMain)
                                    .build()
                    ).collect(Collectors.toList())
            );
        }
    }

    private void processUpdateMultipleDocument(List<MultipartFile> documents, Kasamu
            kasamu, Boolean isMain, EmployeePojo employeePojo, String fiscalYearCode) {
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
                            .type("4")
                            .build(),
                    documents
            );

            List<KasamuDocumentDetails> files = new ArrayList<>();

            if (pojo == null)
                throw new RuntimeException("कागच पत्र सेब हुन सकेन");

            if (pojo != null) {
                pojo.getDocuments().forEach(
                        x -> {
                            files.add(
                                    new KasamuDocumentDetails().builder()
                                            .documentId(x.getId())
                                            .documentName(x.getName())
                                            .documentSize(x.getSizeKB())
                                            .isMain(isMain)
                                            .build()
                            );
                        }
                );
            }
            kasamu.getKasamuDocumentDetails().addAll(files);
        }
    }

    private void validateKasamu(KasamuRequestPojo kasamuRequestPojo, String fiscalYearCode) {

        if (kasamuRequestPojo.getSubjectType() == KasamuSubjectType.ANNUAL) {
            if (kasamuRequestPojo.getEmployeePisCode() == null && kasamuRepository.validateAnnualExternal(kasamuRequestPojo.getExternalEmployee().getPisCode(), fiscalYearCode)) {
                throw new RuntimeException("Annual Kasamu Already exists");
            }
            if (kasamuRequestPojo.getEmployeePisCode() != null && kasamuRepository.validateAnnual(kasamuRequestPojo.getEmployeePisCode(), fiscalYearCode)) {
                throw new RuntimeException("Annual Kasamu Already exists");
            }
        } else if (kasamuRequestPojo.getSubjectType() == KasamuSubjectType.SEMIANNUALFIRST || kasamuRequestPojo.getSubjectType() == KasamuSubjectType.SEMIANNUALSECOND) {
            if (kasamuRequestPojo.getEmployeePisCode() == null && kasamuRepository.validateSemiAnnualExternal(kasamuRequestPojo.getExternalEmployee().getPisCode(), fiscalYearCode)) {
                throw new RuntimeException("Semi-Annual Kasamu Already Exists");
            }

            if (kasamuRequestPojo.getEmployeePisCode() != null && kasamuRepository.validateSemiAnnual(kasamuRequestPojo.getEmployeePisCode(), fiscalYearCode)) {
                throw new RuntimeException("Semi-Annual Kasamu Already Exists");
            }
        }

    }
}
