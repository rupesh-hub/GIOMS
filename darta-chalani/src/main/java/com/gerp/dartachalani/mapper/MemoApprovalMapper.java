package com.gerp.dartachalani.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gerp.dartachalani.dto.MemoApprovalPojo;
import com.gerp.dartachalani.dto.MemoContentPojo;
import com.gerp.dartachalani.model.memo.MemoApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Mapper
@Component
public interface MemoApprovalMapper extends BaseMapper<MemoApproval> {

    @Select("SELECT id, created_date as createdDate, approver_office_code as approverOfficeCode, approver_pis_code as approverPisCode,approver_section_code as approverSectionCode, approver_designation_code as approverDesignationCode, sender_pis_code as senderPisCode, sender_office_code as senderOfficeCode, sender_section_code as senderSectionCode, remarks, status, reverted, is_external as isExternal, suggestion, log FROM memo_approval WHERE is_active = true AND memo_id = #{memoId}")
    MemoApproval findActive(@Param("memoId") Long memoId);

    @Select("SELECT approver_pis_code FROM memo_approval WHERE memo_id = #{memoId} AND status != 'RV' AND is_external != true ORDER BY id ASC")
    ArrayList<String> approvers(@Param("memoId") Long memoId);

    @Select("SELECT distinct pis_code FROM memo_forward_history WHERE memo_id = #{memoId}")
    ArrayList<String> getInvolvedUsers(@Param("memoId") Long memoId);

    @Select("SELECT id, is_active as isActive, created_date as createdDate, approver_office_code as officeCode, approver_pis_code as approverPisCode, approver_section_code as sectionCode, sender_pis_code as senderPisCode, remarks, status, reverted, is_external as isExternal, suggestion, log, is_important as isImportant, delegated_id as delegatedId, is_transferred as isTransferred FROM memo_approval WHERE memo_id = #{memoId} order by id desc")
    List<MemoApprovalPojo> getApprovalByMemoId(@Param("memoId") Long memoId);

    @Select("SELECT id, is_active as isActive, created_date as createdDate, approver_office_code as officeCode, approver_pis_code as approverPisCode, approver_section_code as sectionCode, sender_pis_code as senderPisCode, remarks, status, log, is_important as isImportant, delegated_id as delegatedId, is_transferred as isTransferred FROM memo_suggestion WHERE memo_id = #{memoId} order by id desc")
    List<MemoApprovalPojo> getSuggestionByMemoId(@Param("memoId") Long memoId);

    @Select("SELECT id, created_date as createdDate, last_modified_date as lastModifiedDate, approver_office_code as approverOfficeCode, approver_pis_code as approverPisCode, approver_designation_code as approverDesignationCode, sender_pis_code as senderPisCode, sender_office_code as senderOfficeCode, sender_section_code as senderSectionCode, remarks, status, reverted, is_external as isExternal, suggestion, log FROM memo_approval WHERE suggestion = true AND memo_id = #{memoId} AND status = 'P'")
    MemoApproval findActiveSuggestionApproval(@Param("memoId") Long memoId);

    @Select("SELECT id from memo_content where memo_id = #{memoId}")
    List<MemoContentPojo> getContentsByMemoId(@Param("memoId") Long memoId);

    @Select("SELECT id, content, pis_code as pisCode, office_code as officeCode, created_date as createdDate, is_active as isActive, editable, is_external as isExternal, signature, signature_is_active as signatureIsActive, section_code as sectionCode, designation_code as designationCode, hash_content as hashContent, delegated_id as delegatedId from memo_content where memo_id = #{memoId} and (created_date <= #{createdDate} or pis_code = #{pisCode}) order by id")
    List<MemoContentPojo> getAllContentTillDate(@Param("memoId") Long memoId, @Param("pisCode") String pisCode, @Param("createdDate") Timestamp createdDate);

}
