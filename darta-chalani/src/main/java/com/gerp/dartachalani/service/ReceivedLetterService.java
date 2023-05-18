package com.gerp.dartachalani.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.model.receive.ReceivedLetter;
import com.gerp.dartachalani.model.receive.ReceivedLetterForward;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface ReceivedLetterService extends GenericService<ReceivedLetter, Long>{

    ReceivedLetter saveReceivedLetter(DispatchLetterDTO receivedLetterRequestPojo);

    ReceivedLetter saveManual(ManualReceivedRequestPojo manualReceivedRequestPojo);

    void updateManuallyReceivedLetter(ManualReceivedRequestPojo manualReceivedRequestPojo);

    void updateStatus(StatusPojo statusPojo);

    void deleteReceivedLetter(Long letterId);

    Boolean forwardReceivedLetter(ReceivedLetterForwardRequestPojo receivedLetterRequestPojo, boolean isMultiple);

    ReceivedLetterResponsePojo getReceivedLetter(Long id, GetRowsRequest request);

    List<ReceivedLetterResponsePojo> getAllReceivedLetter();

    Page<ReceivedLetterResponsePojo> getAllReceivedLettersForward(GetRowsRequest paginatedRequest);

    List<ReceivedLetterResponsePojo> getAllManuallyReceivedLetter();

    List<ReceivedLetterResponsePojo> getAllManualLettersForward();

    Page<ReceivedLetterResponsePojo> getManualReceiverFinalized(GetRowsRequest paginatedRequest);

    Page<ReceivedLetterResponsePojo> getManualReceiverInProgress(GetRowsRequest paginatedRequest);

    Long saveMessage(ReceivedLetterMessageRequestPojo receivedLetterMessageRequestPojo);

    Page<ReceivedLetterResponsePojo> filterData(GetRowsRequest paginatedRequest);

    Page<ReceivedLetterResponsePojo> getArchiveDarta(GetRowsRequest paginatedRequest);

    Boolean forwardManualLetter(ReceivedLetterForwardRequestPojo receivedLetterRequestPojo, Boolean isMultiple);

    List<ReceivedLetterResponsePojo> getAllManualOfficeHead();

    List<ReceivedLetterForward> forwardReceivedLetterCc(ReceivedLetterForwardRequestPojo receivedLetterRequestPojo);

    Page<ReceivedLetterResponsePojo> pageData(GetRowsRequest paginatedRequest);

    ReceivedLetterForward revertReceivedLetter(Long receivedLetterId, String description);

    List<ForwardResponsePojo> getCurrentReceivedLetterOwners(Long receivedId);

    Long setImportantFlag(Long id, boolean value);

    Map<String,Object> getDartaSearchRecommendation();

    SectionInvolvedPojo checkSectionIsInvolved(String sectionCode);

    Page<ReceivedLetterResponsePojo> getReceivedLetterForTransfer(GetRowsRequest paginatedRequest);
}