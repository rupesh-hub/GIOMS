package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.GenericReferenceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MemoReferenceMapper {

    @Select("select id as referenceId, chalani_reference_id as id, pis_code as pisCode, office_code as officeCode, include, is_editable as referenceIsEditable from memo_reference where memo_id = #{memoId} and is_active = true")
    List<GenericReferenceDto> getDispatchReferences(@Param("memoId") Long memoId);

    @Select("select id as referenceId, darta_reference_id as id, pis_code as pisCode, office_code as officeCode, include, is_editable as referenceIsEditable from memo_reference where memo_id = #{memoId} and is_active = true")
    List<GenericReferenceDto> getMemoDartaReference(@Param("memoId") Long memoId);

    @Select("select id as referenceId, referenced_memo_id as id, pis_code as pisCode, office_code as officeCode, include, is_editable as referenceIsEditable from memo_reference where dispatch_id = #{dispatchId} and is_active = true")
    List<GenericReferenceDto> getChalaniMemoReference(@Param("dispatchId") Long dispatchId);

    @Select("select id as referenceId, darta_reference_id as id, pis_code as pisCode, office_code as officeCode, include, is_editable as referenceIsEditable from memo_reference where dispatch_id = #{dispatchId} and is_active = true")
    List<GenericReferenceDto> getChalaniDartaReference(@Param("dispatchId") Long dispatchId);

    @Select("select id as referenceId, chalani_reference_id as id, pis_code as pisCode, office_code as officeCode, include, is_editable as referenceIsEditable from memo_reference where dispatch_id = #{dispatchId} and is_active = true")
    List<GenericReferenceDto> getChalaniChalaniReference(@Param("dispatchId") Long dispatchId);

}
