package com.gerp.dartachalani.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.model.receive.ReceivedLetterForward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
@Component
public interface ReceivedLetterMapper {

    List<ReceivedLetterResponsePojo> getAllManuallyReceivedLetters(@Param("officeCode") String officeCode, @Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode);

    List<ReceivedLetterResponsePojo> getAllManuallyReceivedLettersByOfficeCode(@Param("officeCode") String officeCode);

    List<ReceivedLetterResponsePojo> getAllManualLettersForward(@Param("officeCode") String officeCode, @Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode);

    List<ReceivedLetterResponsePojo> getAllByReceiverPisCode(@Param("receiverPisCode") String receiverPisCode, @Param("receiverSectionCode") String receiverSectionCode);

    Page<ReceivedLetterResponsePojo> getAllReceivedLettersForward(Page<ReceivedLetterResponsePojo> page, @Param("senderPisCode") String senderPisCode, @Param("senderSectionCode") String senderSectionCode,
                                                                  @Param("previousPisCode") Set<String> previousPisCode,
                                                                  @Param("isReceived") Boolean isReceived, @Param("searchField") Map<String, Object> searchField, @Param("officeCode") String officeCode);

    Page<ReceivedLetterResponsePojo> getAllReceivedLettersForwardFilterByCC(Page<ReceivedLetterResponsePojo> page, @Param("senderPisCode") String senderPisCode, @Param("senderSectionCode") String senderSectionCode,
                                                                  @Param("previousPisCode") Set<String> previousPisCode,
                                                                  @Param("isReceived") Boolean isReceived, @Param("searchField") Map<String, Object> searchField, @Param("officeCode") String officeCode);

    Page<ReceivedLetterResponsePojo> getDartaReportData(Page<ReceivedLetterResponsePojo> page, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    ReceivedLetterResponsePojo getReceivedLetter(@Param("id") Long id);

    Page<ReceivedLetterResponsePojo> getReceiverInProgress(Page<ReceivedLetterResponsePojo> page, @Param("receiverPisCode") String receiverPisCode, @Param("receiverSectionCode") String receiverSectionCode,@Param("previousPisCode") Set<String> previousPisCode, @Param("searchField") Map<String, Object> searchField, @Param("status") String status, @Param("isHead") boolean isHead, @Param("officeCode") String officeCode, @Param("isImp") Boolean isImp);

    Page<ReceivedLetterResponsePojo> getReceiverFinalized(Page<ReceivedLetterResponsePojo> page, @Param("receiverPisCode") String receiverPisCode, @Param("receiverSectionCode") String receiverSectionCode, @Param("previousPisCode") Set<String> previousPisCode, @Param("searchField") Map<String, Object> searchField, @Param("officeCode") String officeCode);

    Page<ReceivedLetterResponsePojo> filterData(Page<ReceivedLetterResponsePojo> page, @Param("pisCode") String pisCode,@Param("previousPisCode") Set<String> previousPisCode, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    Page<ReceivedLetterResponsePojo> getArchiveDarta(Page<ReceivedLetterResponsePojo> page,
                                                @Param("previousPisCode") Set<String> previousPisCode,
                                                @Param("officeCode") String officeCode,
                                                @Param("sectionCode") String sectionCode,
                                                @Param("searchField") Map<String, Object> searchField);


    ReceivedLetterForward findActive(@Param("id") Long receivedLetterId, @Param("previousPisCode") Set<String> previousPisCode, @Param("sectionCode") String sectionCode);

    @Select("select * from received_letter_forward where received_letter_id = #{id} and is_active = true and receiver_pis_code = #{pisCode} and is_cc = true and receiver_section_id = #{sectionCode} LIMIT 1")
    ReceivedLetterForward findActiveCc(@Param("id") Long receivedLetterId, @Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode);

    @Select("select * from received_letter_forward where received_letter_id = #{id} and is_active = true and receiver_pis_code = #{pisCode} and receiver_section_id = #{sectionCode} LIMIT 1")
    ReceivedLetterForward findAllActive(@Param("id") Long receivedLetterId, @Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode);

    @Select("select received_letter_id from received_letter_document_details where document_id = #{id} and is_active = true")
    List<Long> getDocumentReceived(@Param("id") Long id);

    @Select("select id, created_date as createdDate, subject from received_letter where id = #{receivedId}")
    DocumentDataPojo getMinimalReceived(@Param("receivedId") Long receivedId);

    Page<DartaChalaniGenericPojo> getDataForReference(Page<DartaChalaniGenericPojo> page, @Param("pisCode") String pisCode, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    @Select("select receiver_pis_code from received_letter_forward where received_letter_id = #{id}")
    List<String> getInvolvedUsers(@Param("id") Long id);

    Page<ReceivedLetterResponsePojo> pageData(Page<ReceivedLetterResponsePojo> page, @Param("pisCode") String pisCode, @Param("officeCode") String officeCode,@Param("previousPisCode") Set<String> previousPisCode, @Param("searchField") Map<String, Object> searchField, @Param("officeHeadCode") String code);

    @Select("select id, created_date as createdDate, receiver_pis_code as receiverPisCode, receiver_section_id, sender_pis_code, sender_section_id, description, created_date, completion_status as status, is_active, is_cc, is_reverted as isReverted, is_important as isImportant, delegated_id as delegatedId, is_transferred as isTransferred from received_letter_forward where received_letter_id = #{id} order by id desc")
    List<ForwardResponsePojo> getForwards(@Param("id") Long id);

    @Select("select id as manualId, sender_name as manualSenderName from manual_received_letter_detail where received_letter_id = #{id}")
    ReceivedLetterDetailResponsePojo getDetails(@Param("id") Long id);

    List<Long> getNextPrevValues(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    List<Long> getNextPrevValuesUser(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode, @Param("searchField") Map<String, Object> searchField);

    @Select("select receiver_pis_code as receiverPisCode, is_cc as isCc, sender_pis_code as senderPisCode, completion_status as status, is_active as isActive from received_letter_forward where received_letter_id = #{id}")
    List<ForwardResponsePojo> findActiveDarta(@Param("id") Long id);

    @Select("select pdf from dispatch_pdf_data where dispatch_id = #{dispatchId}")
    List<String> getDispatchPdf(@Param("dispatchId") Long dispatchId);

    @Select("select subject from received_letter where id = #{id}")
    String getSubjectById(@Param("id") Long id);

    @Select("SELECT DISTINCT\n" +
            "                case when not entry_type\n" +
            "                then rl.sender_office_code\n" +
            "                else 'na' end as senderOfficeCode,\n" +
            "                case when not rl.entry_type then 'SYSTEM' else 'MANUAL' end as type,\n" +
            "                case when entry_type then\n" +
            "                mrld.sender_name\n" +
            "                else 'na' end as manualSenderName\n" +
            "FROM received_letter rl\n" +
            "         LEFT JOIN manual_received_letter_detail mrld ON rl.id = mrld.received_letter_id\n" +
            "where office_code = #{officeCode}")
    List<DartaSearchRecommendationDto> getDartaSearchRecommendationData(String officeCode);

    @Select("select distinct rl.id, mrld.sender_name as manualSenderName, rl.sender_office_code, case when rl.entry_type = true then 'MANUAL' else 'SYSTEM' end as type from received_letter rl \n" +
            "left join received_letter_forward rlf on rl.id = rlf.received_letter_id \n" +
            "left join manual_received_letter_detail mrld on rl.id = mrld.received_letter_id \n" +
            "where office_code = #{officeCode} and rl.is_active = true\n" +
            "union all\n" +
            "select distinct dl.id, null as manualSenderName, dl.sender_office_code, 'SYSTEM' as entryType  from dispatch_letter dl \n" +
            "left join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id \n" +
            "where dlri.receiver_office_code = #{officeCode} and dl.is_active = true\n" +
            "order by id desc")
    List<DartaSearchRecommendationDto> getDartaReportSearchRecommendation(String officeCode);

    @Select("select * from is_involved_user_received_letter(#{received_letter_id}, #{pis_code}, #{is_office_head}, #{office_code})")
    IsInvolvedDto is_involved_user_received_letter(@Param("received_letter_id") Long received_letter_id,
                                                   @Param("pis_code") String pis_code,
                                                   @Param("is_office_head") boolean is_office_head,
                                                   @Param("office_code") String office_code);

    @Select("select d.count as countDarta, c.count as countChalani, t.count as coundTippani , case when (d.count + c.count + t.count)>0 \n" +
            "then true else false end as isSectionInvolved  from \n" +
            "(select count(distinct rl.id) from received_letter rl\n" +
            "left join received_letter_forward rlf on rl.id = rlf.received_letter_id \n" +
            "where  rl.section_code = #{sectionCode} or rlf.receiver_section_id = #{sectionCode} or rlf.sender_section_id = #{sectionCode}) as d,\n" +
            "(select count( distinct dl.id) from dispatch_letter dl \n" +
            "left join dispatch_letter_review dlr on dl.id = dlr.dispatch_id \n" +
            "left join dispatch_letter_receiver_internal dlri on dl.id = dlri.dispatch_letter_id \n" +
            "where dl.sender_section_code = #{sectionCode} or dlr.receiver_section_code = #{sectionCode} or dlri.receiver_section_id = #{sectionCode}) as c,\n" +
            "(select count( distinct m.id) from memo m \n" +
            "left join memo_approval ma on m.id = ma.memo_id \n" +
            "left join memo_suggestion ms on m.id = ms.memo_id \n" +
            "where m.section_code = #{sectionCode} or ma.approver_section_code = #{sectionCode} or ms.approver_section_code = #{sectionCode}) as t")
    SectionInvolvedPojo checkSectionIsInvolved(@Param("sectionCode") String sectionCode);

    @Select("select receiver_pis_code from received_letter_forward rlf where rlf.received_letter_id = #{id} and rlf.receiver_section_id = #{sectionCode} and rlf.is_active = true")
    List<String> getReceiverPisCodes(@Param("id") Long id, @Param("sectionCode") String sectionCode);

    //gives true if user not received letter
    boolean checkIsReceiver(@Param("id") Long id,@Param("previousPisCode") Set<String> previousPisCode, @Param("sectionCode") String sectionCode);

    //get all the involved users in received letters
    @Select("select pis_code  from received_letter rl where rl.id = #{id} and rl.section_code = #{sectionCode} and rl.entry_type is true\n" +
            "union\n" +
            "select distinct receiver_pis_code  from received_letter_forward rlf where rlf.received_letter_id = #{id} and rlf.receiver_section_id = #{sectionCode}\n" +
            "union\n" +
            "select distinct sender_pis_code  from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where darta_reference_id = #{id}) and dl.sender_section_code = #{sectionCode}\n" +
            "union\n" +
            "select dl.remarks_pis_code from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where darta_reference_id = #{id}) and remarks_pis_code is not null and dl.remarks_section_code  = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from dispatch_letter_review dlr where dlr.dispatch_id in (select dispatch_id  from memo_reference mr where darta_reference_id = #{id}) and dlr.receiver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from dispatch_letter_receiver_internal dlri where dlri.dispatch_letter_id in (select dispatch_id  from memo_reference mr where darta_reference_id = #{id}) and dlri.receiver_pis_code is not null and dlri.receiver_section_id = #{sectionCode}\n" +
            "union\n" +
            "select distinct pis_code from memo m where m.id in (select mr.memo_id  from memo_reference mr where mr.darta_reference_id = #{id}) and  m.section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct approver_pis_code  from memo_approval ma where ma.memo_id in (select mr.memo_id  from memo_reference mr where mr.darta_reference_id = #{id}) and ma.approver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct approver_pis_code  from memo_suggestion ms where ms.memo_id in (select mr.memo_id  from memo_reference mr where mr.darta_reference_id = #{id}) and ms.approver_section_code = #{sectionCode}\n" +
            "union\n" +
            "select distinct receiver_pis_code  from received_letter_forward rlf2 where rlf2.received_letter_id in (select id  from received_letter rl where dispatch_id in (select dispatch_id  from memo_reference mr where darta_reference_id = #{id})) and rlf2.receiver_section_id = #{sectionCode}\n")
    List<String> getAllInvolvedUsers(@Param("id") Long id, @Param("sectionCode") String sectionCode);

    @Select("select * from check_involved_darta(#{id}, #{sectionCode}, #{previousPisCode})")
    boolean checkInvolvedDarta(@Param("id") Long id, @Param("sectionCode") String sectionCode, @Param("previousPisCode") String previousPisCode);

    //gives all the office code in which the letter in involved
    @Select("select office_code  from received_letter rl where rl.id = #{id}\n" +
            "union\n" +
            "select office_code  from received_letter rl where dispatch_id in (select dispatch_id  from memo_reference mr where darta_reference_id = #{id})\n" +
            "union\n" +
            "select distinct sender_office_code  from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where darta_reference_id = #{id})\n" +
            "union\n" +
            "select distinct office_code  from memo m where m.id in (select memo_id from memo_reference mr where darta_reference_id = #{id})")
    List<String> getInvolvedOffices(@Param("id") Long id);

    Page<ReceivedLetterResponsePojo> getReceivedLetterForTransfer(Page<ReceivedLetterResponsePojo> page, @Param("receiverPisCode") String receiverPisCode, @Param("receiverSectionCode") String receiverSectionCode,@Param("previousPisCode") Set<String> previousPisCode, @Param("searchField") Map<String, Object> searchField, @Param("officeCode") String officeCode);

}
