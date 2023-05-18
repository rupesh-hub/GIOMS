package com.gerp.dartachalani.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.model.dispatch.DispatchLetter;
import com.gerp.dartachalani.model.dispatch.DispatchLetterReceiverInternal;
import com.gerp.dartachalani.model.dispatch.DispatchLetterReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Mapper
@Component
public interface DispatchLetterMapper {

    @Select("select dl.sender_pis_code as senderPisCode, dl.dispatch_no as dispatchNo, dl.dispatch_date_en as dispatchDateEn, dl.dispatch_date_np as dispatchDateNp, dl.subject as subject, dl.is_draft as isDraft, dl.signee as signee, dl.content as content, dlf.description as forwardDescription, dlf.description as receiverDescription, dlf.receiver_office_code as receiverOfficeCode,\n" +
            "lp.name_np as priorityNameNp, lp.name_en as priorityNameEn, lp2.name_np as privacyNameNp, lp2.name_en as privacyNameEn\n" +
            "from dispatch_letter dl\n" +
            "inner join dispatch_letter_forward dlf on dl.id= dlf.dispatch_letter_id\n" +
            "inner join dispatched_letter_receiver dlr on dlf.id=dlr.dispatch_letter_forward_id\n" +
            "inner join letter_priority lp on dl.letter_priority_id = lp.id\n" +
            "inner join letter_privacy lp2 on dl.letter_privacy_id = lp2.id;")
    List<DispatchedResponsePojo> getDispatchedLetter();

    @Select("select dl.id,dl.dispatch_no,dl.sender_pis_code,dl.signee,dl.letter_privacy,dl.content,dl.letter_priority,dl.receiver_type,dl.subject,\n" +
            "       dl.dispatch_date_en,dl.is_draft,dl.dispatch_date_np\n" +
            "    from\n" +
            "    dispatch_letter dl  where is_draft=true and dl.is_active=true and dl.sender_pis_code=#{pisCode} and dl.sender_section_code=#{senderSectionCode}")
    ArrayList<DispatchLetterResponsePojo> getAllDraft(String pisCode, @Param("senderSectionCode") String senderSectionCode);

    ArrayList<DispatchLetterResponsePojo> getAllDispatch(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode, @Param("officeHeadPis") String officeHeadPis);


    @Select("select dl.id,dl.dispatch_no,dl.sender_pis_code,dl.signee,dl.letter_privacy as letterPrivacy,dl.content,dl.letter_priority as letterPriority,dl.receiver_type,dl.subject,\n" +
            "       dl.dispatch_date_en,dl.is_draft,dl.dispatch_date_np\n" +
            "       from dispatch_letter dl\n" +
            "           left join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id \n" +
            "     where dl.is_draft=false  and dl.is_active=true and dl.status='A' and dlri.receiver_pis_code= #{pisCode} and dlri.completion_status = 'P' " +
            "order by dl.id desc")
    Page<DispatchedResponsePojo> getAllInternalLetters(Page<DispatchedResponsePojo> page, @Param("pisCode") String pisCode);

    Page<DispatchedResponsePojo> getInternalLetters(Page<DispatchedResponsePojo> page, @Param("pisCode") String pisCode, @Param("searchField") Map<String, Object> searchField, @Param("sectionId") String sectionId, @Param("previousPisCodes") Set<String> previousPisCodes);

    @Select("select distinct pis_code  from signature_data sd where sd.dispatch_letter_id in\n" +
            "(select  dl.id\n" +
            "            from dispatch_letter dl\n" +
            "            left join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id\n" +
            "            where dl.is_draft = false and dl.is_active=true and dl.status='A' and dlri.receiver_pis_code= #{pisCode} and dlri.completion_status = 'P'\n" +
            "            and dlri.is_active = true)")
    List<String> getInterLetterSenderList(@Param("pisCode") String pisCode);

    @Select("select * from dispatch_letter_receiver_internal dlri where dlri.dispatch_letter_id = #{dispatchLetterId} and dlri.receiver_pis_code= #{pisCode} and dlri.receiver_section_id = #{sectionId} and dlri.is_active = true and (dlri.tocc is null or dlri.tocc = false) limit 1")
    DispatchLetterReceiverInternal findActive(@Param("dispatchLetterId") Long dispatchLetterId, @Param("pisCode") String pisCode, @Param("sectionId") String sectionId);

    List<DispatchLetterReceiverInternal> findActiveLetters(@Param("dispatchLetterId") Long dispatchLetterId, @Param("previousPisCode") Set<String> previousPisCode, @Param("sectionId") String sectionId);

    boolean checkIsReceiver(@Param("dispatchLetterId") Long dispatchLetterId, @Param("sectionId") String sectionId, @Param("previousPisCode") Set<String> previousPisCode);

    @Select("select * from dispatch_letter_receiver_internal dlri where dlri.dispatch_letter_id = #{dispatchLetterId} and dlri.receiver_pis_code= #{pisCode} and dlri.receiver_section_id = #{sectionId} and dlri.is_active = true and (dlri.tocc is not null or dlri.tocc = true) limit 1")
    DispatchLetterReceiverInternal findActiveCC(@Param("dispatchLetterId") Long dispatchLetterId, @Param("pisCode") String pisCode, @Param("sectionId") String sectionId);

    List<DispatchLetterReceiverInternal> findActiveCCLetters(@Param("dispatchLetterId") Long dispatchLetterId, @Param("previousPisCode") Set<String> previousPisCode, @Param("sectionId") String sectionId);

    @Select("select count(*) as count" +
            "       from dispatch_letter dl\n" +
            "           left join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id \n" +
            "     where dl.is_draft=false  and dl.is_active=true and dl.status='A' and dlri.receiver_pis_code= #{pisCode} and dlri.completion_status = 'P' ")
    Long countAllInternalLetters(@Param("pisCode") String pisCode);

    @Select("select dl.id,dl.dispatch_no,dl.sender_pis_code,dl.signee,dl.letter_privacy as letterPrivacy,dl.content,dl.letter_priority as letterPriority,dl.receiver_type,\n" +
            "       dl.subject, dl.dispatch_date_en,dl.is_draft,dl.dispatch_date_np,dlri.within_organization,dlri.completion_status as status,\n" +
            "       dlri.receiver_pis_code as receiverPisCode, dlri.receiver_section_id as receiverSectionId\n" +
            "       from dispatch_letter dl\n" +
            "           inner join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id and dlri.sender_pis_code=#{pisCode}\n" +
            "     where dl.is_draft=false and dl.is_active=true")
    List<DispatchedResponsePojo> getAllForwardedLetters(String pisCode);

    @Select("      select id,dispatchNo,dispatchDateEn,subject,receiverType,string_agg(externalReceiver,',') externalReceiverName,isActive,isDraft,content,dispatchDateNp,documentName,documentId,letterPriority,senderPisCode,signee,letterPrivacy,string_agg(toreceiverName,',') toReceiverName,string_agg(ccreceiverName,',') ccReceiverName\n" +
            "      from (\n" +
            "      select dl.id as id,dlre.receiver_name as externalReceiver,dl.receiver_type as receiverType,dl.dispatch_no as dispatchNo,dl.dispatch_date_en as dispatchDateEn, dl.subject as subject,dl.dispatch_date_np as dispatchDateNp,\n" +
            "             dl.is_active as isActive,dl.content as content,dl.is_draft as isDraft,dl.letter_priority as letterPriority,dl.letter_privacy as letterPrivacy,dl.sender_pis_code as senderPisCode,dl.signee as signee,\n" +
            "             case\n" +
            "\n" +
            "                 when (dlri.within_organization = true and dlri.to_receiver=true) then dlri.receiver_pis_code\n" +
            "                 when (dlri.within_organization = false and dlri.to_receiver=true) then dlri.receiver_office_code\n" +
            "                end as toreceiverName,\n" +
            "\n" +
            "             case\n" +
            "\n" +
            "                 when (dlri.within_organization = true and dlri.tocc=true) then dlri.receiver_pis_code\n" +
            "                 when (dlri.within_organization = false and dlri.tocc=true) then dlri.receiver_office_code\n" +
            "                end as ccreceiverName\n" +
            "\n" +
            "      from dispatch_letter dl\n" +
            "               left join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id\n" +
            "               left join dispatch_letter_receiver_external dlre on dl.id = dlre.dispatch_letter_id where dl.dispatch_no= #{dispatchNo})\n" +
            "               AS b group by id,dispatchNo,dispatchDateEn,receiverType,subject,isActive,b.isDraft,content,dispatchDateNp,documentName,documentId,letterPriority,senderPisCode,signee,letterPrivacy;")
    DispatchLetterResponsePojo getAllDispatchById(String dispatchNo);

    @Select("select count(*) \n" +
            "      from dispatch_letter_receiver_internal di where di.dispatch_letter_id= #{dispatchId}")
    Integer getInternalByDispatchId(Long dispatchId);

    @Select("select di.is_active as isActive, di.receiver_pis_code as internalReceiverPiscode, di.receiver_office_code as internalReceiverOfficeCode, di.within_organization as withinOrganization, di.tocc as internalReceiverCc, di.to_receiver as internalReceiver, di.order_number as order, di.bodartha_type as bodarthaType, di.receiver_section_id as internalReceiverSectionId, di.receiver_section_name as internalReceiverSectionName, di.completion_status as completionStatus, di.is_important as isImportant, di.remarks, " +
            "di.is_section_name as isSectionName, di.is_group_name as isGroupName,di.group_id as groupId, di.salutation, di.is_transferred as isTransferred \n" +
            "      from dispatch_letter_receiver_internal di where di.dispatch_letter_id= #{dispatchId} order by di.id desc")
    List<DispatchLetterInternalDTO> getInternalsByDispatchId(@Param("dispatchId") Long dispatchId);

    @Select("select case when has_subject is null then true else has_subject end from dispatch_letter where id = #{dispatchId}")
    Boolean checkHasSubjectByDispatchId(@Param("dispatchId") Long dispatchId);

    @Select("select count(*) \n" +
            "      from dispatch_letter_receiver_external de where de.dispatch_letter_id= #{dispatchId}")
    Integer getExternalByDispatchId(Long dispatchId);

    @Select("select distinct de.receiver_name as receiverName, de.receiver_email as externalReceiverEmail, de.receiver_address as extenalReceiverAddress, de.receiver_phone_no as externalReceiverPhoneNo, de.receiver_office_section_subsection as receiverOfficeSectionSubSection\n" +
            "      from dispatch_letter_receiver_external de where de.sender_office_code= #{officeCode}")
    List<DispatchLetterExternalDTO> getExternalsByOfficeCode(@Param("officeCode") String officeCode);

    @Select("select de.receiver_name as receiverName, de.to_cc as isCc, de.order_number as order, de.bodartha_type as bodarthaType, de.remarks, " +
            "de.is_group_name as isGroupName \n" +
            "      from dispatch_letter_receiver_external de where de.dispatch_letter_id = #{dispatchId} order by de.id desc")
    List<DispatchLetterExternalDTO> getExternalsByDispatchId(@Param("dispatchId") Long dispatchId);

    DispatchLetterDTO getDispatchLetterDetailById(@Param("dispatchId") Long dispatchId);

    List<DispatchLetterCommentsPojo> getAllCommentsOfDispatchLetter(@Param("dispatchLetterId") Long dispatchLetterId);

    @Select("select di.id as internalReceiverId, di.receiver_section_id as internalReceiverSectionId, di.receiver_pis_code as internalReceiverPiscode,\n" +
            "      di.dispatch_letter_id as dispatchLetterId, di.created_date as createdDate, di.last_modified_date as modifiedDate,\n" +
            "      di.sender_pis_code as senderPisCode, di.sender_section_id as senderSectionId, di.tocc as internalReceiverCc, di.to_receiver as internalReceiver, \n" +
            "      di.description as description, di.completion_status as completionStatus, di.delegated_id as delegatedId\n" +
            "      from dispatch_letter_receiver_internal di where di.dispatch_letter_id= #{dispatchLetterId} and di.within_organization = true order by di.id desc")
    List<DispatchLetterInternalDTO> getForwardedLetterDetail(@Param("pisCode") String pisCode, @Param("dispatchLetterId") Long dispatchLetterId);

    @Select("select dl.id,dl.dispatch_no,dl.sender_pis_code,dl.signee,dl.letter_privacy as letterPrivacy,dl.content,dl.letter_priority as letterPriority,dl.receiver_type,dl.subject,\n" +
            "       dl.dispatch_date_en,dl.is_draft,dl.dispatch_date_np,dlri.within_organization,dlri.completion_status as status, dlri.receiver_pis_code as receiverPisCode\n" +
            "       from dispatch_letter dl\n" +
            "           inner join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id and dlri.receiver_pis_code=#{pidCode}  and dlri.completion_status='IP'\n" +
            "     where dl.is_draft=false and dl.is_active=true and dl.status='A' order by dl.id desc ")
    List<DispatchedResponsePojo> getInprogressDispatchLetters(String pisCode);

    @Select("select dl.id,dl.dispatch_no,dl.sender_pis_code,dl.signee,dl.letter_privacy as letterPrivacy,dl.content,dl.letter_priority as letterPriority,dl.receiver_type,dl.subject,\n" +
            "       dl.dispatch_date_en,dl.is_draft,dl.dispatch_date_np,dlri.within_organization,dlri.completion_status as status, dlri.receiver_pis_code as receiverPisCode\n" +
            "       from dispatch_letter dl\n" +
            "           inner join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id and dlri.receiver_pis_code=#{pidCode}  and dlri.completion_status='FN'\n" +
            "     where dl.is_draft=false and dl.is_active=true and dl.status='A' order by dl.id desc ")
    List<DispatchedResponsePojo> getFinalizedDispatchLetter(String pisCode);

    @Select("select dl.id,dl.dispatch_no,dl.sender_pis_code,dl.signee,dl.letter_privacy as letterPrivacy,dl.content,dl.letter_priority as letterPriority,dl.receiver_type,\n" +
            "       dl.subject, dl.dispatch_date_en,dl.is_draft,dl.dispatch_date_np,\n" +
            "       dr.status as status, dr.remarks as remarks, dr.receiver_pis_code as receiverPisCode, dr.receiver_office_name\n" +
            "       from dispatch_letter dl\n" +
            "           inner join dispatch_letter_review dr on dl.id= dr.dispatch_id and dr.receiver_pis_code=#{pisCode} and dl.is_active='true' order by dl.id desc ")
    List<DispatchedResponsePojo> getReviewedDispatchLetters(String pidCode);

    boolean getActive(@Param("id") Long id,
                      @Param("previousPisCode") Set<String> previousPisCode,
                      @Param("sectionCode") String sectionCode);

    Page<DispatchLetterResponsePojo> filterData(Page<DispatchLetterResponsePojo> page, @Param("pisCode") String pisCode, @Param("officeCode") String officeCode, @Param("previousPisCode") Set<String> previousPisCode, @Param("searchField") Map<String, Object> searchField);

    Page<DispatchLetterResponsePojo> getArchiveChalani(Page<DispatchLetterResponsePojo> page,
                                                       @Param("officeCode") String officeCode,
                                                       @Param("previousPisCode") Set<String> previousPisCode,
                                                       @Param("sectionCode") String sectionCode,
                                                       @Param("searchField") Map<String, Object> searchField);


    @Select("select dispatch_letter_id from dispatch_letter_document_details where document_id = #{id}")
    List<Long> getDocumentReceived(@Param("id") Long id);

    @Select("select id, created_date as createdDate, subject from dispatch_letter where id = #{dispatchId}")
    DocumentDataPojo getMinimalDispatch(@Param("dispatchId") Long dispatchId);

    Page<DartaChalaniGenericPojo> getDataForReference(Page<DartaChalaniGenericPojo> page, @Param("pisCode") String pisCode, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    @Select("select receiver_pis_code from dispatch_letter_review where dispatch_id = #{id}")
    List<String> getInvolvedUsers(@Param("id") Long id);

    @Select("select id as approvalId, is_active as isActive, created_date as createdDate, receiver_pis_code as receiverPisCode, receiver_section_code as receiverSectionCode, status as approvalStatus, reverted, is_important as isImportant, delegated_id as delegatedId, is_transferred as isTransferred from dispatch_letter_review where dispatch_id = #{dispatchId} order by id desc")
    List<DispatchLetterApprovalPojo> getApprovalList(@Param("dispatchId") Long dispatchId);

    @Select("select DISTINCT\n" +
            "case when dlri.receiver_pis_code is null then 'na' else dlri.receiver_pis_code end\n" +
            ",case when dl.sender_pis_code is null then 'na' else dl.sender_pis_code end\n" +
            ",case when dlre.receiver_name is null then 'na' else dlre.receiver_name end \n" +
            ",case when dlri.receiver_office_code is null then 'na' else dlri.receiver_office_code end\n" +
            "from dispatch_letter dl\n" +
            "         left join dispatch_letter_review dlr on dlr.dispatch_id = dl.id\n" +
            "         left join dispatch_letter_receiver_internal dlri on dlri.dispatch_letter_id = dl.id\n" +
            "         left join dispatch_letter_receiver_external dlre on dlre.dispatch_letter_id = dl.id\n" +
            "where dl.is_active = true and dlr.receiver_pis_code=#{pisCode} and dlr.receiver_section_code=#{sectionId}")
    List<SearchRecommendationDto> getSearchRecommendation(@Param("pisCode") String pisCode, @Param("sectionId") String sectionId);


    @Select("select subject from dispatch_letter where id = #{id}")
    String getSubjectById(@Param("id") Long id);

    Page<DispatchLetterResponsePojo> getChalaniReportData(Page<DispatchLetterResponsePojo> page, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    @Select("select distinct dlri.receiver_office_code as receiverOfficeCode from dispatch_letter dl \n" +
            "left join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id \n" +
            "where dlri.sender_office_code = #{officeCode} and dl.is_active = true")
    List<SearchRecommendationDto> getChalaniReportSearchRecommendation(@Param("officeCode") String officeCode);

    @Select("select * from is_involved_user_dispatch_letter(#{id}, #{pis_code}, #{isOfficeHead}, #{office_code})")
    IsInvolvedDto is_involved_user_dispatch_letter(@Param("id") Long id,
                                                   @Param("pis_code") String pis_code,
                                                   @Param("isOfficeHead") Boolean isOfficeHead,
                                                   @Param("office_code") String office_code);

    @Select(" select receiver_pis_code from dispatch_letter_review dlr where dlr.is_active = true and dlr.dispatch_id = #{id}\n" +
            "  and dlr.receiver_section_code = #{sectionCode}")
    List<String> getApproverList(@Param("id") Long id, @Param("sectionCode") String sectionCode);

    boolean checkApproverList(@Param("id") Long id, @Param("previousPisCode") Set<String> previousPisCode, @Param("sectionCode") String sectionCode);

    @Select(" select receiver_pis_code from dispatch_letter_review dlr where dlr.is_active = true and dlr.dispatch_id = #{id}\n" +
            "     and dlr.receiver_section_code = #{sectionCode}\n" +
            "     union all\n" +
            "     select sender_pis_code from dispatch_letter dl where  dl.is_active = true and dl.status != 'A' and dl.id = #{id} and dl.sender_section_code = #{sectionCode} and\n" +
            "     dl.id not in (select dlr.dispatch_id  from dispatch_letter_review dlr where dlr.is_active = true and dlr.dispatch_id = #{id})")
    List<String> getReviewerList(@Param("id") Long id, @Param("sectionCode") String sectionCode);

    boolean checkReviewerList(@Param("id") Long id, @Param("previousPisCode") Set<String> previousPisCode, @Param("sectionCode") String sectionCode);

    @Select("select dl.sender_pis_code from dispatch_letter dl where dl.id = #{id} and dl.sender_section_code = #{sectionCode}\n" +
            "union\n" +
            "select dl.remarks_pis_code from dispatch_letter dl where dl.id = #{id} and remarks_pis_code is not null and dl.remarks_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct dlri.receiver_pis_code from dispatch_letter_receiver_internal dlri where dlri.dispatch_letter_id = #{id} and receiver_pis_code is not null and dlri.receiver_section_id = #{sectionCode}\n" +
            "union\n" +
            "select distinct dlr.receiver_pis_code from dispatch_letter_review dlr where dlr.dispatch_id = #{id} and dlr.receiver_section_code  = #{sectionCode}\n" +
            "union\n" +
            "select distinct sender_pis_code  from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where chalani_reference_id = #{id}) and dl.sender_section_code = #{sectionCode}\n" +
            "union\n" +
            "select dl.remarks_pis_code from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where chalani_reference_id = #{id}) and remarks_pis_code is not null and dl.remarks_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from dispatch_letter_review dlr where dlr.dispatch_id in (select dispatch_id  from memo_reference mr where chalani_reference_id = #{id}) and dlr.receiver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from dispatch_letter_receiver_internal dlri where dlri.dispatch_letter_id in (select dispatch_id  from memo_reference mr where chalani_reference_id = #{id}) and dlri.receiver_pis_code is not null and dlri.receiver_section_id  = #{sectionCode}\n" +
            "union\n" +
            "select distinct pis_code from memo m where m.id in (select mr.memo_id  from memo_reference mr where mr.chalani_reference_id  = #{id}) and m.section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct approver_pis_code  from memo_approval ma where ma.memo_id in (select mr.memo_id  from memo_reference mr where mr.chalani_reference_id  = #{id}) and ma.approver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct approver_pis_code  from memo_suggestion ms where ms.memo_id in (select mr.memo_id  from memo_reference mr where mr.chalani_reference_id  = #{id}) and ms.approver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from received_letter_forward rlf2 where rlf2.received_letter_id in (select id  from received_letter rl where dispatch_id in (select dispatch_id  from memo_reference mr where chalani_reference_id = #{id})) and rlf2.receiver_section_id  = #{sectionCode}\n")
    List<String> getInvolvedUsers(@Param("id") Long id, @Param("sectionCode") String sectionCode);

    @Select("select * from check_involved_chalani( #{id}, #{sectionCode}, #{previousPisCode} )")
    boolean checkInvolvedChalani(@Param("id") Long id, @Param("sectionCode") String sectionCode, @Param("previousPisCode") String previousPisCode);

    @Select("select dl.sender_office_code from dispatch_letter dl where dl.id = #{id}\n" +
            "union\n" +
            "select dlri.receiver_office_code from dispatch_letter_receiver_internal dlri where dlri.dispatch_letter_id = #{id}\n" +
            "union\n" +
            "select office_code from received_letter rl where dispatch_id in (select dispatch_id  from memo_reference mr where chalani_reference_id  = #{id})\n" +
            "union\n" +
            "select distinct sender_office_code from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where chalani_reference_id  = #{id})\n" +
            "union\n" +
            "select distinct office_code from memo m where m.id in (select memo_id from memo_reference mr where chalani_reference_id  = #{id})")
    List<String> getInvolvedOffices(@Param("id") Long id);

}
