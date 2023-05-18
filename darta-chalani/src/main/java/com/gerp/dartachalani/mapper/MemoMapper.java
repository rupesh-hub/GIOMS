package com.gerp.dartachalani.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.document.SystemDocumentPojo;
import com.gerp.dartachalani.dto.systemFiles.SystemFilesDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
@Component
public interface MemoMapper {

    List<MemoResponsePojo> getAllMemo(@Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode);

    List<MemoResponsePojo> getAllMemoOffice(@Param("officeCode") String officeCode);

    ArrayList<MemoResponsePojo> getDrafts(@Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode);

    ArrayList<MemoResponsePojo> getSaved(@Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode);

    MemoResponsePojo getMemoById(@Param("id") Long id);

    List<MemoResponsePojo> getMemoByParentId(@Param("parentId") Long parentId);

    List<MemoResponsePojo> getMemoByReceiverPisCode(String receiverPisCode);

    List<MemoResponsePojo> getMemoReceiverInProgress(String receiverPisCode);

    List<MemoResponsePojo> getMemoReceiverFinalized(String receiverPisCode);

    List<MemoResponsePojo> getAllMemoForwarded(@Param("pisCode") String pisCode);

    List<MemoResponsePojo> getAllMemoInProgress(String senderPisCode);

    List<MemoResponsePojo> getAllMemoFinalized(String senderPisCode);

    List<MemoResponsePojo> getAllMemoForApproval(@Param("approverPisCode") String approverPisCode, @Param("approverSectionCode") String approverSectionCode);

    List<MemoResponsePojo> getAllMemoForApprovalForwarded(@Param("senderPisCode") String senderPisCode, @Param("senderSectionCode") String senderSectionCode);

    List<MemoResponsePojo> getAllMemoSuggestion(@Param("approverPisCode") String approverPisCode, @Param("approverSectionCode") String approverSectionCode);

    List<MemoResponsePojo> getAllMemoForwardSuggestion(@Param("senderPisCode") String senderPisCode, @Param("senderSectionCode") String senderSectionCode);

    Page<MemoResponsePojo> filterData(Page<MemoResponsePojo> page, @Param("pisCode") String pisCode, @Param("officeCode") String officeCode,
                                      @Param("previousPisCode") Set<String> previousPisCode,
                                      @Param("searchField") Map<String, Object> searchField, @Param("isActive") Boolean isActive, @Param("creatorPisCode") String creatorPisCode);

    Page<MemoResponsePojo> getArchiveTippani(Page<MemoResponsePojo> page,
                                             @Param("officeCode") String officeCode,
                                             @Param("previousPisCode") Set<String> previousPisCode,
                                             @Param("sectionCode") String sectionCode,
                                             @Param("searchField") Map<String, Object> searchField);


    List<ReferenceMemoResponse> getReferenceMemos(@Param("memoId") Long memoId);

    List<ReferenceMemoResponse> getReferenceMemosAll(@Param("memoId") Long memoId);

    @Select("select memo_id from memo_document_details where document_id = #{id} and is_active = true")
    List<Long> getDocumentMemo(@Param("id") Long id);

    @Select("select id, created_date as createdDate, subject from memo where id = #{memoId}")
    DocumentDataPojo getMinimalMemo(@Param("memoId") Long memoId);

    Page<DartaChalaniGenericPojo> getDataForReference(Page<DartaChalaniGenericPojo> page, @Param("pisCode") String pisCode, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    @Select("select approver_pis_code from memo_approval where memo_id = #{memoId}")
    List<String> getInvolvedUsers(@Param("memoId") Long memoId);

    @Select("SELECT DISTINCT\n" +
            "                case when mm.pis_code is null then 'na' else mm.pis_code end,\n" +
            "                case when mm.office_code is null then 'na' else mm.office_code end,\n" +
            "                case when mm.section_code is null then 'na' else mm.section_code end\n" +
            "FROM memo mm\n" +
            "         LEFT JOIN memo_approval ma ON mm.id = ma.memo_id\n" +
            "         LEFT JOIN memo_suggestion ms on mm.id = ms.memo_id\n" +
            "WHERE mm.is_active = true\n" +
            "  and ((ms.approver_pis_code = #{suggestionPisCode} and ms.approver_section_code = #{suggestionSectionCode})\n" +
            "    or (ma.approver_pis_code = #{suggestionPisCode} and ma.approver_section_code = #{suggestionSectionCode}))")
    List<TippaniSearchRecommendationDto> getTippaniSearchFields(@Param("suggestionPisCode") String suggestionPisCode
            , @Param("suggestionSectionCode") String suggestionSectionCode);

    @Select("select subject from memo where id = #{id}")
    String getSubjectById(@Param("id") Long id);

    Page<MemoResponsePojo> getTippaniReportData(Page<MemoResponsePojo> page, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    @Select("SELECT DISTINCT mm.office_code as senderOfficeCode from memo mm " +
            "left join memo_approval ma on mm.id = ma.memo_id " +
            "left join memo_suggestion ms on mm.id = ms.memo_id " +
            "where mm.is_active = true and (ms.approver_office_code  = #{officeCode} or ma.approver_office_code = #{officeCode}) ")
    List<SearchRecommendationDto> getTippaniReportSearchRecommendation(@Param("officeCode") String officeCode);

    @Select("select * from is_involved_user_memo(#{memo_id}, #{pis_code}, #{isOfficeHead}, #{office_code})")
    IsInvolvedDto is_involved_user_memo(@Param("memo_id") Long memo_id,
                                        @Param("pis_code") String pis_code,
                                        @Param("isOfficeHead") Boolean isOfficeHead,
                                        @Param("office_code") String office_code);


    boolean checkIsCreator(@Param("id") Long id, @Param("previousPisCode") Set<String> previousPisCode, @Param("sectionCode") String sectionCode,
                           @Param("officeCode") String officeCode);

    @Select("select pis_code from memo m where m.id = #{id} and m.section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct approver_pis_code from memo_approval ma where ma.memo_id = #{id} and ma.approver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct approver_pis_code from memo_suggestion ms where ms.memo_id = #{id} and ms.approver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct sender_pis_code  from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where referenced_memo_id  = #{id}) and dl.sender_section_code = #{sectionCode}\n" +
            "union\n" +
            "select dl.remarks_pis_code from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where referenced_memo_id  = #{id}) and remarks_pis_code is not null and dl.remarks_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from dispatch_letter_review dlr where dlr.dispatch_id in (select dispatch_id  from memo_reference mr where referenced_memo_id  = #{id}) and dlr.receiver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from dispatch_letter_receiver_internal dlri where dlri.dispatch_letter_id in (select dispatch_id  from memo_reference mr where referenced_memo_id  = #{id}) and dlri.receiver_pis_code is not null and dlri.receiver_section_id = #{sectionCode}\n" +
            "union\n" +
            "select distinct pis_code from memo m where m.id in (select mr.memo_id  from memo_reference mr where referenced_memo_id  = #{id}) and m.section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct approver_pis_code  from memo_approval ma where ma.memo_id in (select mr.memo_id  from memo_reference mr where referenced_memo_id  = #{id} ) and ma.approver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct approver_pis_code  from memo_suggestion ms where ms.memo_id in (select mr.memo_id  from memo_reference mr where referenced_memo_id  = #{id}) and ms.approver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from received_letter_forward rlf2 where rlf2.received_letter_id in (select id  from received_letter rl where dispatch_id in (select dispatch_id  from memo_reference mr where referenced_memo_id  = #{id})) and rlf2.receiver_section_id = #{sectionCode}\n")
    List<String> getInvolvedUsersTippani(@Param("id") Long id, @Param("sectionCode") String sectionCode);

    @Select("select * from check_involved_tippani(#{id}, #{sectionCode}, #{previousPisCode})")
    boolean checkInvolvedTippani(@Param("id") Long id, @Param("sectionCode") String sectionCode, @Param("previousPisCode") String previousPisCode);

    @Select("select office_code from memo m where m.id = #{id}\n" +
            "union\n" +
            "select office_code  from received_letter rl where dispatch_id in (select dispatch_id  from memo_reference mr where referenced_memo_id = #{id})\n" +
            "union\n" +
            "select distinct sender_office_code  from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where referenced_memo_id = #{id})\n" +
            "union\n" +
            "select distinct office_code  from memo m where m.id in (select memo_id from memo_reference mr where referenced_memo_id = #{id}) " +
            "union \n" +
            "select ma.approver_office_code from memo_approval ma where ma.memo_id = #{id}\n" +
            "union \n" +
            "select ms.approver_office_code  from memo_suggestion ms where ms.memo_id = #{id}")
    List<String> getInvolvedOffices(@Param("id") Long id);

    boolean getMemoActive(@Param("id") Long id,
                          @Param("previousPisCode") Set<String> previousPisCode,
                          @Param("sectionCode") String sectionCode);

    Long getLatestMemoInReferenceLoop(@Param("memoId") Long memoId);

    @Select("select case when count(*)>0 then false else true end from (\n" +
            "                        WITH recursive q AS (select mr.referenced_memo_id, mr.created_date \n" +
            "                                                     from memo_reference mr\n" +
            "                                                     where mr.memo_id = #{memoId}\n" +
            "                                                    UNION ALL\n" +
            "                                                    select mr2.referenced_memo_id, mr2.created_date  \n" +
            "                                                     from memo_reference mr2\n" +
            "                                                              join q on mr2.memo_id = q.referenced_memo_id\n" +
            "                                )\n" +
            "                                SELECT q.referenced_memo_id as memoId from q\n" +
            "                               where q.referenced_memo_id  != #{memoId} and q.referenced_memo_id = #{referenceMemoId}  order by q.created_date desc) t;")
    boolean checkForValidMemoReference(@Param("memoId") Long memoId, @Param("referenceMemoId") Long referenceMemoId);

    List<MemoApprovalPojo> getActivityLogWithReferenceTippaniLog(@Param("memoId") Long memoId,
                                                                 @Param("referencedMemoId") Long id,
                                                                 @Param("fiscalYearCode") String fiscalYearCode);

    List<MemoApprovalPojo> getActivityLogWithReferenceTippaniLogAll(@Param("memoId") Long memoId,
                                                                 @Param("referencedMemoId") Long id,
                                                                 @Param("fiscalYearCode") String fiscalYearCode);

    List<MemoApprovalPojo> getActivityLog(@Param("memoId") Long memoId);

    List<IdSubjectDto> getMemoSubjectSearchList(@Param("memoId") Long memoId);

    List<IdSubjectDto> getMemoSubjectSearchListAll(@Param("memoId") Long memoId);

    List<MemoReferenceDoc> getMemoReferenceDocuments(@Param("memoId") Long memoId, @Param("referenceMemoId") Long referenceMemoId);

    //gives list of all memo ids linked by reference
    @Select("select * from tippani_reference_list(#{memoId})")
    List<Long> getTippaniReferenceList(@Param("memoId") Long memoId);

    List<SystemFilesDto> getSystemFiles(@Param("memoId") Long memoId, @Param("referenceMemoId") Long referenceMemoId);

    Long getMemoReferencedFrom(@Param("memoId") Long memoId);

    SenderApproverDto getFirstSuggestionDetail(@Param("memoId") Long memoId);
}
