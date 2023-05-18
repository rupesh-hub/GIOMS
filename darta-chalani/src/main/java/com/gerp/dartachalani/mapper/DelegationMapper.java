package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.template.StandardTemplatePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface DelegationMapper {

    @Select("SELECT delegated_pis_code from delegation_history where pis_code = #{pisCode} and office_code = #{officeCode}")
    List<String> getAllDelegation(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);

    @Select("SELECT delegated_pis_code from delegation_history where pis_code = #{pisCode} and office_code = #{officeCode} and delegation_type = 'D'")
    List<String> getDartaDelegation(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);

    @Select("SELECT delegated_pis_code from delegation_history where pis_code = #{pisCode} and office_code = #{officeCode} and delegation_type = 'S'")
    List<String> getSigneeDelegation(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);

    List<StandardTemplatePojo> getStandardTemplate(@Param("superAdmin") String superAdmin, @Param("officeCode") String officeCode, @Param("pisCode") Long pisCode);
}
