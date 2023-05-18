package com.gerp.dartachalani.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.model.dispatch.*;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface DispatchLetterService extends GenericService<DispatchLetter, Long> {
    DispatchLetter saveLetter(DispatchLetterRequestPojo dispatchLetterRequestPojo);

    List<DispatchLetterReceiverInternal> dispatchLetterInternal(DispatchLetterRequestPojo dispatchLetterRequestPojo);

    List<DispatchLetterReceiverExternal> dispatchLetterReceiverExternal(DispatchLetterRequestPojo dispatchLetterRequestPojo);

    DispatchLetter update(DispatchLetterRequestPojo dispatchLetterRequestPojo);

    DispatchLetterDTO getDispatchLetterById(Long id, String type);


    DispatchLetterForward dispatch(DispatchForwardRequestPojo dispatchForwardRequestPojo);

//    List<DispatchedLetterReceiver> forward(DispatchReceiverRequestPojo dispatchReceiverRequestPojo);

    List<DispatchedResponsePojo> getAllDispatched();
    Page<DispatchedResponsePojo> getAllInternalLetters(GetRowsRequest request);

    void deleteDispatchedLetter(Long letterId);

    void archiveDispatchedLetter(Long letterId);
    void restoreDispatchedLetter(Long letterId);

    DispatchLetter saveDispatch(DispatchLetterRequestPojo dispatchLetterRequestPojo);

    ArrayList<DispatchLetterResponsePojo>getAllDraftDispatch();

    ArrayList<DispatchLetterResponsePojo>getAllDispatch();
    Long forwardDispatchLetterWithinOrganization(DispatchLetterReceiverInternalPojo dispatchLetterReceiverInternalPojo);
    String approveDispatchLetter(UpdateDispatchLetterPojo updateDispatchLetterPojo);
    List<DispatchedResponsePojo> getAllForwardedLetters();
    List<DispatchLetterInternalDTO> getForwardedLetterDetail(Long dispatchLetterId);
    List<DispatchedResponsePojo> getInProgressDispatchLetters();
    List<DispatchedResponsePojo> getFinalizedDispatchLetters();
    Long updateForwardedDispatchLetter(UpdateDispatchLetterPojo updateDispatchLetterPojo);
    Long sendForReview(DispatchLetterReviewPojo dispatchLetterReviewPojo);
    List<DispatchedResponsePojo> getReviewedDispatchLetters();
    Long updateForReview(UpdateDispatchLetterPojo updateDispatchLetterPojo);
    Long backForReview(UpdateDispatchLetterPojo updateDispatchLetterPojo);

    Page<DispatchLetterResponsePojo> filterData(GetRowsRequest paginatedRequest);

    Page<DispatchLetterResponsePojo> getArchiveChalani(GetRowsRequest paginatedRequest);

    List<DispatchLetterExternalDTO> getExternalUsers();

    Long savePdf(SaveDispatchPdfPojo data);

    DispatchLetter contentUpdate(DispatchLetterRequestPojo dispatchLetterRequestPojo);

    Long setImportantFlag(Long id, boolean value, String type);

    Map<String,Object> getSearchRecommendation();

    List<Map<String,Object>> getInternalLetterSender();

    Long testInternalLetterCount();

    DispatchLetterDTO getDispatchLetterByIdForQr(Long id, String type, String mobileNumber);




}
