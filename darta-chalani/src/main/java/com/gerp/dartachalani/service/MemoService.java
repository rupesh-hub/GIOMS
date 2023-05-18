package com.gerp.dartachalani.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.document.DocumentMasterResponsePojo;
import com.gerp.dartachalani.dto.document.DocumentResponsePojo;
import com.gerp.dartachalani.dto.document.SysDocumentsPojo;
import com.gerp.dartachalani.dto.systemFiles.SystemFilesDto;
import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.dartachalani.model.memo.MemoForward;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.ApprovalPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface MemoService extends GenericService<Memo, Long> {

    ArrayList<MemoResponsePojo> getDrafts();

    List<MemoResponsePojo> getAllMemo();

    List<MemoResponsePojo> getAllMemoForwarded();

    List<MemoResponsePojo> getAllMemoForApproval();

    List<MemoResponsePojo> getAllMemoForApprovalForwarded();

    MemoResponsePojo getMemoById(Long id);

    MemoReferenceResponsePojo getMemoByIdForReference(Long memoId, Long referencedMemoId, int type);

    List<MemoResponsePojo> getMemoByParentId(Long parentId);

    List<MemoForward> forwardMemo(MemoForwardRequestPojo memoForwardRequestPojo);

    Memo saveMemo(MemoRequestPojo memoRequestPojo);

    void deleteMemo(Long memoId);
    void archiveMemo(Long memoId);
    void restoreMemo(Long memoId);

    void updateMemo(MemoRequestPojo memoRequestPojo);

    List<MemoResponsePojo> getMemoByReceiverPisCode(String receiverPisCode);

    ArrayList<MemoResponsePojo> getSaved();

    void updateStatus(StatusPojo statusPojo);

    void updateApproval(ApprovalPojo data);

    void externalApproval(ApprovalPojo data);

    List<MemoResponsePojo> getMemoReceiverInProgress(String receiverPisCode);

    List<MemoResponsePojo> getMemoReceiverFinalized(String receiverPisCode);

    List<MemoResponsePojo> getAllMemoInProgress();

    List<MemoResponsePojo> getAllMemoFinalized();

    Long saveContent(MemoContentPojo memoContentPojo);

    Long editContent(MemoContentPojo message);

    Long editContentDoc(MemoContentPojo data);

    Long saveComment(MemoContentPojo memoComment);

    Long editComment(MemoContentPojo comment);

    Page<MemoResponsePojo> filterData(GetRowsRequest paginatedRequest);

    Page<MemoResponsePojo> getArchiveTippani(GetRowsRequest paginatedRequest);

    List<SysDocumentsPojo> getSystemDocuments();

    Long saveReference(ReferencePojo referencePojo);

    void updateSuggestion(ApprovalPojo approvalPojo);

    List<MemoResponsePojo> getSuggestionsForward();

    List<MemoResponsePojo> getSuggestions();

    String getVerificationLink(Long id);

    void deleteContent(Long contentId);

    Long setImportantFlag(Long id, boolean value);

    Map<String,Object> getTippaniSearchRecommendation();

    List<ReferenceMemoResponse> getListOfReferencedMemos(Long memoId);

    List<MemoApprovalPojo> getActivityLog(Long memoId, Long referencedMemoId, String fiscalYearCode);

    List<IdSubjectDto> getMemoSubjectSearchList(Long memoId);

    List<MemoReferenceResponsePojo> getTemplatesOfAllLinkedReferenceMemos(Long memoId);

    List<MemoReferenceDoc> getDocumentsOfAllLinkedReferenceMemos(Long memoId, Long referenceMemoId);

    List<SystemFilesDto> getSystemDocumentsOfAllLinkedReferenceMemos(Long memoId, Long referenceMemoId);
}
