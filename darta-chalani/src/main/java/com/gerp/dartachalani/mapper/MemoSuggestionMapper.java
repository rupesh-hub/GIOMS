package com.gerp.dartachalani.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gerp.dartachalani.dto.MemoContentPojo;
import com.gerp.dartachalani.dto.MemoSuggestionDto;
import com.gerp.dartachalani.model.memo.MemoSuggestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Mapper
@Component
public interface MemoSuggestionMapper extends BaseMapper<MemoSuggestion> {

    @Select("SELECT id, approver_office_code as approverOfficeCode, approver_pis_code as approverPisCode, approver_designation_code as approverDesignationCode, approver_section_code as approverSectionCode, sender_pis_code as senderPisCode, sender_office_code as senderOfficeCode, sender_designation_code as senderDesignationCode, remarks, status, log, initial_sender as initialSender, first_sender as firstSender FROM memo_suggestion WHERE is_active = true AND memo_id = #{memoId}")
    MemoSuggestion findActive(@Param("memoId") Long id);

    @Select("select id, content, pis_code as pisCode, office_code as officeCode, created_date as createdDate, is_active as isActive, editable, is_external as isExternal, signature, signature_is_active as signatureIsActive, section_code as sectionCode, designation_code as designationCode, hash_content as hashContent, delegated_id as delegatedId from memo_content where (pis_code = #{pisCode} or created_date <= (select max(created_date) from memo_suggestion where memo_id = #{memoId} and approver_pis_code = #{pisCode})) and memo_id = #{memoId} order by id")
    List<MemoContentPojo> getAllContentTillDate(@Param("memoId") Long memoId, @Param("pisCode") String pisCode);

    @Select("select approver_pis_code from memo_suggestion where memo_id = #{memoId}")
    List<String> findSuggestionUsers(@Param("memoId") Long memoId);

    @Select("select approver_pis_code from memo_suggestion where memo_id = #{memoId} and created_date >=(select max(last_modified_date) from memo_approval ma where ma.memo_id = #{memoId} and is_back = true)")
    List<String> findSuggestionUsersWithBack(@Param("memoId") Long memoId);

    @Select("select case when count(id)>0 then true else false end from memo_approval ma where ma.memo_id = #{memoId} and is_back = true")
    boolean isBack(@Param("memoId") Long memoId);

    MemoSuggestionDto findSenderByMemo(@Param("firstSenderHistory") Set<String> firstSenderHistory, @Param("memoId") Long id);
}
