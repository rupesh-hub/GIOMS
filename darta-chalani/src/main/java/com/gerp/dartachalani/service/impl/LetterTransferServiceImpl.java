package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.dto.EmployeePojo;
import com.gerp.dartachalani.dto.LetterTransferPojo;
import com.gerp.dartachalani.dto.LetterTransferResponsePojo;
import com.gerp.dartachalani.dto.SectionEmployeePojo;
import com.gerp.dartachalani.mapper.*;
import com.gerp.dartachalani.model.LetterTransferHistory;
import com.gerp.dartachalani.model.dispatch.DispatchLetter;
import com.gerp.dartachalani.model.dispatch.DispatchLetterReceiverInternal;
import com.gerp.dartachalani.model.dispatch.DispatchLetterReview;
import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.dartachalani.model.memo.MemoApproval;
import com.gerp.dartachalani.model.memo.MemoForwardHistory;
import com.gerp.dartachalani.model.memo.MemoSuggestion;
import com.gerp.dartachalani.model.receive.ReceivedLetter;
import com.gerp.dartachalani.model.receive.ReceivedLetterForward;
import com.gerp.dartachalani.repo.*;
import com.gerp.dartachalani.service.LetterTransferService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.enums.Status;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
@Transactional
public class LetterTransferServiceImpl implements LetterTransferService {

    private final UserMapper userMapper;
    private final LetterTransferHistoryRepo letterTransferHistoryRepo;
    private final MemoForwardHistoryRepo memoForwardHistoryRepo;
    private final ReceivedLetterMapper receivedLetterMapper;
    private final DispatchLetterMapper dispatchLetterMapper;
    private final MemoMapper memoMapper;
    private final ReceivedLetterRepo receivedLetterRepo;
    private final ReceivedLetterForwardRepo receivedLetterForwardRepo;
    private final DispatchLetterReviewRepo dispatchLetterReviewRepo;
    private final DispatchLetterReceiverInternalRepo dispatchLetterReceiverInternalRepo;
    private final DispatchLetterRepo dispatchLetterRepo;
    private final MemoApprovalMapper memoApprovalMapper;
    private final MemoSuggestionMapper memoSuggestionMapper;
    private final MemoApprovalRepo memoApprovalRepo;
    private final MemoSuggestionRepo memoSuggestionRepo;
    private final MemoRepo memoRepo;
    private final DispatchLetterServiceImpl dispatchLetterServiceImpl;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private DateConverter dateConverter;

    public LetterTransferServiceImpl(UserMapper userMapper,
                                     LetterTransferHistoryRepo letterTransferHistoryRepo,
                                     MemoForwardHistoryRepo memoForwardHistoryRepo,
                                     ReceivedLetterMapper receivedLetterMapper,
                                     ReceivedLetterRepo receivedLetterRepo,
                                     ReceivedLetterForwardRepo receivedLetterForwardRepo,
                                     DispatchLetterReviewRepo dispatchLetterReviewRepo,
                                     DispatchLetterReceiverInternalRepo dispatchLetterReceiverInternalRepo,
                                     DispatchLetterRepo dispatchLetterRepo,
                                     MemoApprovalMapper memoApprovalMapper,
                                     MemoSuggestionMapper memoSuggestionMapper,
                                     MemoApprovalRepo memoApprovalRepo,
                                     MemoSuggestionRepo memoSuggestionRepo,
                                     MemoRepo memoRepo,
                                     MemoMapper memoMapper,
                                     DispatchLetterMapper dispatchLetterMapper,
                                     DispatchLetterServiceImpl dispatchLetterServiceImpl) {
        this.userMapper = userMapper;
        this.letterTransferHistoryRepo = letterTransferHistoryRepo;
        this.memoForwardHistoryRepo = memoForwardHistoryRepo;
        this.receivedLetterMapper = receivedLetterMapper;
        this.receivedLetterRepo = receivedLetterRepo;
        this.receivedLetterForwardRepo = receivedLetterForwardRepo;
        this.dispatchLetterReviewRepo = dispatchLetterReviewRepo;
        this.dispatchLetterReceiverInternalRepo = dispatchLetterReceiverInternalRepo;
        this.dispatchLetterRepo = dispatchLetterRepo;
        this.memoApprovalMapper = memoApprovalMapper;
        this.memoSuggestionMapper = memoSuggestionMapper;
        this.memoApprovalRepo = memoApprovalRepo;
        this.memoSuggestionRepo = memoSuggestionRepo;
        this.memoRepo = memoRepo;
        this.memoMapper = memoMapper;
        this.dispatchLetterMapper = dispatchLetterMapper;
        this.dispatchLetterServiceImpl = dispatchLetterServiceImpl;
    }

    @Override
    public List<EmployeeMinimalPojo> findUserBySection(Long sectionCode) {
        List<String> users = userMapper.findUserBySection(sectionCode.toString());
        List<EmployeeMinimalPojo> employeePojos = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            users.forEach(
                    pisCode -> {
                        if (pisCode != null && !pisCode.equals(""))
                            employeePojos.add(userMgmtServiceData.getEmployeeDetailMinimal(pisCode));
                    }
            );
        }
        List<SectionEmployeePojo> userList = userMgmtServiceData.getSectionEmployeeList(sectionCode);
        if (userList != null && !userList.isEmpty()) {
            SectionEmployeePojo user = userList.get(0);
            if (user.getEmployeeList() != null && !user.getEmployeeList().isEmpty()) {
                employeePojos.addAll(user.getEmployeeList());
            }
        }
        return employeePojos.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(EmployeeMinimalPojo::getPisCode))),
                        ArrayList::new));
    }

    @Override
    public Long persistLetterTransfer(LetterTransferPojo data) {
        Gson gson = new Gson();
        if (data != null &&
                data.getFromPisCode() != null &&
                data.getToPisCode() != null &&
                data.getFromSectionCode() != null &&
                data.getToSectionCode() != null &&
                data.getFromPisCode().equals(data.getToPisCode()) &&
                data.getFromSectionCode().equals(data.getToSectionCode())
        )
            throw new CustomException("Invalid: Same users");

        EmployeePojo senderDetail = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());
        EmployeePojo receiverDetail = userMgmtServiceData.getEmployeeDetail(data.getToPisCode());

        if (data != null &&
                !StringUtils.isBlank(data.getFromPisCode()) &&
                !StringUtils.isBlank(data.getFromSectionCode()) &&
                !StringUtils.isBlank(data.getToPisCode()) &&
                !StringUtils.isBlank(data.getToSectionCode())) {

            Set<String> listPisCodes = new HashSet<>();
            listPisCodes.add(data.getFromPisCode());

            if (dispatchLetterServiceImpl.getPreviousPisCode(data.getFromPisCode(), data.getFromSectionCode()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(data.getFromPisCode(), data.getFromSectionCode()));
            if (data.isTransferAll()) {

                List<Long> dartaIds = userMapper.getInvolvedReceivedLetter(listPisCodes, data.getFromSectionCode());
                data.setDartaIds(dartaIds);
                //persistDarta(data, senderDetail);

                List<Long> chalaniIds = userMapper.getInvolvedDispatchLetter(listPisCodes, data.getFromSectionCode());
                data.setChalaniIds(chalaniIds);
                //persistChalani(data, senderDetail);

                List<Long> tippaniIds = userMapper.getInvolvedMemo(listPisCodes, data.getFromSectionCode());
                data.setTippaniIds(tippaniIds);

            }

            persistDarta(data, senderDetail, receiverDetail, listPisCodes);
            persistChalani(data, senderDetail, receiverDetail, listPisCodes);
            persistTippani(data, senderDetail, receiverDetail);
            //persistInternalLetter(data, senderDetail, receiverDetail);

        } else
            throw new CustomException("Invalid");

        LetterTransferHistory letterTransferHistory = LetterTransferHistory.builder()
                .pisCode(tokenProcessorService.getPisCode())
                .officeCode(tokenProcessorService.getOfficeCode())
                .historyData(gson.toJson(data)).build();
        //letterTransferHistoryRepo.save(letterTransferHistory);

        return letterTransferHistory.getId();
    }

    @Override
    public List<LetterTransferResponsePojo> getLetterTransferHistoryByOfficeCode() {
        List<LetterTransferResponsePojo> datas = userMapper.getLetterHistoryByOfficeCode(tokenProcessorService.getOfficeCode());
        Gson gson = new Gson();
        datas.forEach(data -> {
            if (data.getPisCode() != null)
                data.setCreator(userMgmtServiceData.getEmployeeDetailMinimal(data.getPisCode()));
            data.setCreatedDateNp(data.getCreatedDate() != null ? dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(data.getCreatedDate().toString())) : null);
            data.setHistoryDataPojo(gson.fromJson(data.getHistoryData(), LetterTransferPojo.class));

            if (data.getHistoryDataPojo() != null) {

                List<String> dartaSubjects = new ArrayList<>();
                List<String> chalaniSubjects = new ArrayList<>();
                List<String> tippaniSubjects = new ArrayList<>();

                if (data.getHistoryDataPojo().getFromPisCode() != null)
                    data.getHistoryDataPojo().setFromEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getHistoryDataPojo().getFromPisCode()));
                if (data.getHistoryDataPojo().getToPisCode() != null)
                    data.getHistoryDataPojo().setToEmployee(userMgmtServiceData.getEmployeeDetailMinimal(data.getHistoryDataPojo().getToPisCode()));

                if (data.getHistoryDataPojo().getDartaIds() != null && !data.getHistoryDataPojo().getDartaIds().isEmpty()) {
                    data.getHistoryDataPojo().getDartaIds().forEach(
                            id -> dartaSubjects.add(receivedLetterMapper.getSubjectById(id))
                    );
                    data.getHistoryDataPojo().setDartaSubjects(dartaSubjects);
                }

                if (data.getHistoryDataPojo().getChalaniIds() != null && !data.getHistoryDataPojo().getChalaniIds().isEmpty()) {
                    data.getHistoryDataPojo().getChalaniIds().forEach(
                            id -> chalaniSubjects.add(dispatchLetterMapper.getSubjectById(id))
                    );
                    data.getHistoryDataPojo().setChalaniSubjects(chalaniSubjects);
                }

                if (data.getHistoryDataPojo().getTippaniIds() != null && !data.getHistoryDataPojo().getTippaniIds().isEmpty()) {
                    data.getHistoryDataPojo().getTippaniIds().forEach(
                            id -> tippaniSubjects.add(memoMapper.getSubjectById(id))
                    );
                    data.getHistoryDataPojo().setTippaniSubjects(tippaniSubjects);
                }
            }
        });
        return datas;
    }

    private void persistDarta(LetterTransferPojo data, EmployeePojo senderDetail, EmployeePojo receiverDetail, Set<String> senderPisCodes) {
        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(data.getToPisCode());

        if (dispatchLetterServiceImpl.getPreviousPisCode(data.getToPisCode(), data.getToSectionCode()) != null)
            listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(data.getToPisCode(), data.getToSectionCode()));

        if (data.getDartaIds() != null && !data.getDartaIds().isEmpty()) {
            List<ReceivedLetterForward> receivedLetterForwards = new ArrayList<>();
            data.getDartaIds().forEach(
                    id -> {
                        ReceivedLetter receivedLetter = receivedLetterRepo.findById(id).orElseThrow(() -> new RuntimeException("Letter Not Found"));
                        ReceivedLetterForward activeForward = receivedLetterMapper.findActive(id, senderPisCodes, data.getFromSectionCode());
                        ReceivedLetterForward activeForwardReceiver = receivedLetterMapper.findActive(id, listPisCodes, data.getToSectionCode());

                        if (!data.isTransferAll() && activeForwardReceiver != null) {
                            throw new RuntimeException(receiverDetail != null ? receiverDetail.getNameNp() + " संग यो पत्र पहिले नै भएकोले उक्त पत्र स्थानान्तरण गर्न निषेध गरिएको छ|" : "Invalid");
                        }

                        if (activeForward != null && activeForwardReceiver == null) {
                            activeForward.setReceivedLetter(receivedLetterRepo.findById(id).get());
                            activeForward.setActive(false);
                            receivedLetterForwardRepo.save(activeForward);

                            ReceivedLetterForward receivedLetterForward = ReceivedLetterForward.builder()
                                    .receivedLetter(receivedLetterRepo.findById(id).get())
                                    .receiverPisCode(data.getToPisCode())
                                    .receiverSectionId(data.getToSectionCode())
                                    .receiverDesignationCode(null)
                                    .senderPisCode(data.getFromPisCode())
                                    .senderSectionId(data.getFromSectionCode())
                                    .senderDesignationCode(null)
                                    .description("Transferred")
                                    .isReceived(false)
                                    .senderParentCode(activeForward.getSenderParentCode())
                                    .isCc(false)
                                    .isSeen(false)
                                    .completion_status(activeForward.getCompletion_status() == Status.FN ? activeForward.getCompletion_status() : Status.P)
                                    .isTransferred(Boolean.TRUE)
                                    .build();
                            receivedLetterForwards.add(receivedLetterForward);
                        }
                    }
            );
            if (!receivedLetterForwards.isEmpty())
                receivedLetterForwardRepo.saveAll(receivedLetterForwards);
        }
    }

    private void persistChalani(LetterTransferPojo data, EmployeePojo senderDetail, EmployeePojo receiverDetail, Set<String> senderPisCodes) {
        if (data.getChalaniIds() != null && !data.getChalaniIds().isEmpty()) {

            Set<String> listPisCodes = new HashSet<>();
            listPisCodes.add(data.getToPisCode());

            if (dispatchLetterServiceImpl.getPreviousPisCode(data.getToPisCode(), data.getToSectionCode()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(data.getToPisCode(), data.getToSectionCode()));

            List<DispatchLetterReview> dispatchLetterReviews = new ArrayList<>();
            data.getChalaniIds().forEach(
                    id -> {
                        DispatchLetterReview dispatchActive = dispatchLetterReviewRepo.getDispatchByActiveStatus(id);

                        DispatchLetter dispatchLetter = dispatchLetterRepo.findById(id).orElseThrow(() -> new RuntimeException("Letter not found"));

                        if (dispatchActive != null &&
                                dispatchActive.getReceiverPisCode() != null &&
                                dispatchActive.getReceiverPisCode().equals(data.getFromPisCode()) &&
                                dispatchActive.getReceiverSectionCode() != null &&
                                dispatchActive.getReceiverSectionCode().equals(data.getFromSectionCode())) {
                            dispatchActive.setActive(false);
                            dispatchActive.setStatus(Status.F);
                            dispatchLetterReviewRepo.saveAndFlush(dispatchActive);

                            DispatchLetterReview dispatchLetterReview = DispatchLetterReview.builder()
                                    .id(id)
                                    .receivedDate(LocalDate.now())
                                    .receivedDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()))
                                    .receiverOfficeCode(dispatchActive != null ? dispatchActive.getReceiverOfficeCode() : null)
                                    .receiverPisCode(data.getToPisCode())
                                    .receiverSectionCode(data.getToSectionCode())
                                    .receiverDesignationCode(null)
                                    .senderPisCode(data.getFromPisCode())
                                    .senderSectionCode(data.getFromSectionCode())
                                    .senderDesignationCode(null)
                                    .remarks("Transferred")
                                    .remarksSignatureIsActive(dispatchActive != null ? dispatchActive.getRemarksSignatureIsActive() : null)
                                    .remarksSignature(dispatchActive != null ? dispatchActive.getRemarksSignature() : null)
                                    .status(Status.P)
                                    .subject(dispatchActive != null ? dispatchActive.getSubject() : null)
                                    .dispatchLetter(dispatchLetter)
                                    .isSeen(false)
                                    .reverted(false)
                                    .isTransferred(Boolean.TRUE)
                                    .build();
                            dispatchLetterReviews.add(dispatchLetterReview);
                        }

                        if (dispatchLetter.getStatus() == Status.A) {

                            //get internal letter with respect to from employee pis codes
                            DispatchLetterReceiverInternal dispatchLetterReceiverInternal = dispatchLetterReceiverInternalRepo.getInternalByReceiverActiveOwn(data.getFromPisCode(), data.getFromSectionCode(), id);
                            if (dispatchLetterReceiverInternal == null) {

                                //get internal letter with respect to including previous employee pis codes
                                dispatchLetterReceiverInternal = dispatchLetterReceiverInternalRepo.getInternalByReceiverActive(senderPisCodes, data.getFromSectionCode(), id);
                            }

                            //get internal letter with respect to to employee pis codes
                            List<DispatchLetterReceiverInternal> activeLetterUser = dispatchLetterMapper.findActiveLetters(dispatchLetter.getId(), listPisCodes, data.getToSectionCode());
                            if (!data.isTransferAll() && activeLetterUser != null && !activeLetterUser.isEmpty()) {

                                //throw exception if the transfer is not all transfer and the to employee already have this letter
                                throw new RuntimeException(receiverDetail != null ? receiverDetail.getNameNp() + " संग यो पत्र पहिले नै भएकोले उक्त पत्र स्थानान्तरण गर्न निषेध गरिएको छ|" : "Invalid");
                            }
                            if (dispatchLetterReceiverInternal != null && activeLetterUser != null && activeLetterUser.isEmpty()) {

                                //update receiver internal to transfer
                                dispatchLetterReceiverInternal.setActive(false);
                                dispatchLetterReceiverInternalRepo.save(dispatchLetterReceiverInternal);

                                DispatchLetterReceiverInternal dlrInternal = new DispatchLetterReceiverInternal();

                                dlrInternal.setCompletion_status(dispatchLetterReceiverInternal.getCompletion_status());
                                dlrInternal.setDescription("Transferred");
                                dlrInternal.setDispatchLetter(dispatchLetter);
                                dlrInternal.setReceiverSectionId(data.getToSectionCode());
                                dlrInternal.setReceiverPisCode(data.getToPisCode());
                                dlrInternal.setReceiverOfficeCode(dispatchLetterReceiverInternal.getReceiverOfficeCode());
                                dlrInternal.setSenderPisCode(data.getFromPisCode());
                                dlrInternal.setSenderSectionId(data.getFromSectionCode());
                                dlrInternal.setToCC(false);
                                dlrInternal.setCompletion_status(dispatchLetterReceiverInternal.getCompletion_status() == Status.FN ? dispatchLetterReceiverInternal.getCompletion_status() : Status.P);
                                dlrInternal.setWithin_organization(true);
                                dlrInternal.setIsTransferred(Boolean.TRUE);
                                dispatchLetterReceiverInternalRepo.saveAndFlush(dlrInternal);
                            }
                        }
                    }
            );

            if (!dispatchLetterReviews.isEmpty())
                dispatchLetterReviewRepo.saveAll(dispatchLetterReviews);
        }

    }

    private void persistTippani(LetterTransferPojo data, EmployeePojo senderDetail, EmployeePojo receiverDetail) {
        if (data.getTippaniIds() != null && !data.getTippaniIds().isEmpty()) {
            List<MemoApproval> memoApprovals = new ArrayList<>();
            List<MemoSuggestion> memoSuggestions = new ArrayList<>();

            EmployeePojo approverDetail = userMgmtServiceData.getEmployeeDetail(data.getToPisCode());

            data.getTippaniIds().forEach(ids -> {
                boolean isApproval = false;
                boolean isSuggestion = false;
                Memo memo = memoRepo.findById(ids).orElseThrow(() -> new CustomException("Tippani not found"));

                MemoApproval memoApproval = memoApprovalMapper.findActive(ids);
                MemoSuggestion memoSuggestion = memoSuggestionMapper.findActive(ids);

                Boolean isInvolvedUser = memoForwardHistoryRepo.checkInvolvedUser(ids, data.getToPisCode(), data.getToSectionCode());

                if (!data.isTransferAll() && isInvolvedUser != null && isInvolvedUser) {
                    throw new RuntimeException(receiverDetail != null ? receiverDetail.getNameNp() + " संग यो टिप्पणी पहिले नै भएकोले उक्त टिप्पणी स्थानान्तरण गर्न निषेध गरिएको छ|" : "Invalid");
                }

                if (memoApproval != null
                        && memoApproval.getApproverPisCode() != null
                        && memoApproval.getApproverPisCode().equals(data.getFromPisCode())
                        && memoApproval.getApproverSectionCode() != null
                        && memoApproval.getApproverSectionCode().equals(data.getFromSectionCode())
                        && memoApproval.getIsExternal() != null
                        && !memoApproval.getIsExternal()
                )
                    isApproval = true;

                if (isApproval && isInvolvedUser != null
                        && !isInvolvedUser) {

                    MemoApproval approval = MemoApproval.builder()
                            .status(memoApproval.getStatus())
                            .approverPisCode(data.getToPisCode())
                            .approverSectionCode(data.getToSectionCode())
                            .approverDesignationCode(null)
                            .approverOfficeCode(tokenProcessorService.getOfficeCode())
                            .senderPisCode(data.getFromPisCode())
                            .senderOfficeCode(tokenProcessorService.getOfficeCode())
                            .senderSectionCode(data.getFromSectionCode())
                            .senderDesignationCode(null)
                            .remarks("Transferred")
                            .reverted(false)
                            .isExternal(false)
                            .suggestion(false)
                            .log(memoApproval != null ? memoApproval.getLog() + 1 : 1)
                            .isSeen(false)
                            .memo(memo)
                            .isTransferred(Boolean.TRUE)
                            .build();
                    memoApprovals.add(approval);

                    if (memoApproval != null) {
                        memoApproval.setStatus(Status.F);
                        memoApproval.setActive(false);
                        memoApprovalMapper.updateById(
                                memoApproval
                        );
                    }

                    //disable the user from history whose letter were transferred
                    memoForwardHistoryRepo.setIsActiveByPisCode(data.getFromPisCode(), ids, data.getFromSectionCode());

                    memoForwardHistoryRepo.save(
                            new MemoForwardHistory().builder()
                                    .memo(memo)
                                    .officeCode(tokenProcessorService.getOfficeCode())
                                    .pisCode(data.getToPisCode())
                                    .sectionCode(data.getToSectionCode())
                                    .designationCode(approverDetail != null ? approverDetail.getFunctionalDesignationCode() : null)
                                    .build()
                    );
                }

                if (memoSuggestion != null
                        && memoSuggestion.getApproverPisCode() != null
                        && memoSuggestion.getApproverPisCode().equals(data.getFromPisCode())
                        && memoSuggestion.getApproverSectionCode() != null
                        && memoSuggestion.getApproverSectionCode().equals(data.getFromSectionCode()))
                    isSuggestion = true;

                if (isSuggestion && isInvolvedUser != null
                        && !isInvolvedUser) {


                    MemoSuggestion suggestion = MemoSuggestion.builder()
                            .status(memoSuggestion.getStatus())
                            .approverPisCode(data.getToPisCode())
                            .approverOfficeCode(tokenProcessorService.getOfficeCode())
                            .approverSectionCode(data.getToSectionCode())
                            .approverDesignationCode(null)
                            .senderPisCode(data.getFromPisCode())
                            .senderOfficeCode(tokenProcessorService.getOfficeCode())
                            .senderSectionCode(data.getFromSectionCode())
                            .senderDesignationCode(null)
                            .initialSender(memoSuggestion.getInitialSender())
                            .firstSender(memoSuggestion.getFirstSender().equals(data.getFromPisCode()) ? data.getToPisCode() : memoSuggestion.getFirstSender())
                            .log(memoSuggestion.getLog() + 1)
                            .isSeen(false)
                            .memo(memo)
                            .remarks("Transferred")
                            .isTransferred(Boolean.TRUE)
                            .build();
                    memoSuggestions.add(suggestion);

                    memoSuggestion.setStatus(Status.F);
                    memoSuggestion.setActive(false);
                    memoSuggestionMapper.updateById(
                            memoSuggestion
                    );
                }
            });
            if (!memoApprovals.isEmpty())
                memoApprovalRepo.saveAll(memoApprovals);
            if (!memoSuggestions.isEmpty())
                memoSuggestionRepo.saveAll(memoSuggestions);
        }
    }

    private void persistInternalLetter(LetterTransferPojo data, EmployeePojo senderDetail, EmployeePojo receiverDetail) {

        Set<String> listPisCodes = new HashSet<>();
        listPisCodes.add(data.getFromPisCode());

        if (dispatchLetterServiceImpl.getPreviousPisCode(data.getFromPisCode(), data.getFromSectionCode()) != null)
            listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(data.getFromPisCode(), data.getFromSectionCode()));

        if (data.getInternalLetterIds() != null && !data.getInternalLetterIds().isEmpty()) {
            data.getInternalLetterIds().forEach(id -> {

                Optional<DispatchLetter> dispatchLetter = dispatchLetterRepo.findById(id);
                if (dispatchLetter.isPresent()) {
                    List<DispatchLetterReceiverInternal> activeLetters = dispatchLetterMapper.findActiveLetters(id, listPisCodes, data.getFromSectionCode());

                    if (activeLetters != null && !activeLetters.isEmpty()) {
                        activeLetters.forEach(activeLetter -> {
                            activeLetter.setDispatchLetter(dispatchLetter.get());
                            activeLetter.setActive(false);
                            activeLetter.setWithin_organization(Boolean.TRUE);
                            dispatchLetterReceiverInternalRepo.save(activeLetter);
                        });
                    }

                    //  DispatchLetterReceiverInternal activeLetterCC = dispatchLetterMapper.findActiveCC(dispatchLetter.getId(), tokenPisCode, senderSectionCode);
                    List<DispatchLetterReceiverInternal> activeLettersCC = dispatchLetterMapper.findActiveCCLetters(id, listPisCodes, data.getFromSectionCode());

                    if (activeLettersCC != null && !activeLettersCC.isEmpty()) {
                        activeLettersCC.forEach(activeLetterCC -> {
                            activeLetterCC.setDispatchLetter(dispatchLetter.get());
                            activeLetterCC.setActive(false);
                            activeLetterCC.setWithin_organization(Boolean.TRUE);
                            dispatchLetterReceiverInternalRepo.save(activeLetterCC);
                        });
                    }

                    DispatchLetterReceiverInternal dlrInternal = new DispatchLetterReceiverInternal();

                    dlrInternal.setCompletion_status(Status.P);
                    dlrInternal.setDescription("Transferred");
                    dlrInternal.setDispatchLetter(dispatchLetter.get());
                    dlrInternal.setReceiverSectionId(data.getToSectionCode());
                    dlrInternal.setReceiverPisCode(data.getToPisCode());
                    dlrInternal.setReceiverOfficeCode(tokenProcessorService.getOfficeCode());
                    dlrInternal.setSenderPisCode(data.getFromPisCode());
                    dlrInternal.setSenderSectionId(data.getFromSectionCode());
                    dlrInternal.setToCC(Boolean.FALSE);
                    dlrInternal.setWithin_organization(true);
                    dispatchLetterReceiverInternalRepo.saveAndFlush(dlrInternal);
                }
            });
        }
    }

}
