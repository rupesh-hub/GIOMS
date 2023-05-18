package com.gerp.usermgmt.mapper.delegation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.pojo.delegation.TempDelegationResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Mapper
public interface DelegationMapper {
    Page<TempDelegationResponsePojo> getTemporaryDelegation(Page<TempDelegationResponsePojo> page, String searchKey, @Param("pisCode") Long pisCode, @Param("now") LocalDateTime now,@Param("isReassignment") Boolean isReassignment , @Param("officeCode")String officeCode);

    Page<TempDelegationResponsePojo> getTemporaryDelegationForUser(Page<TempDelegationResponsePojo> page, String searchKey, @Param("pisCode") String pisCode, @Param("isDelegatedSelf") Boolean isDelegatedSelf, @Param("now") LocalDateTime now, @Param("isReassignment") Boolean isReassignment, @Param("officeCode")String officeCode);

    @Select("select count(*) from delegation where ((expire_date >= #{expireDate} and effective_date <= #{expireDate}) or (expire_date >= #{effectiveDate} and effective_date <= #{effectiveDate}))" +
            " and form_piscode= #{fromPisCode} ")
    Integer existsByExpireAndEffectiveDate(@Param("expireDate") LocalDateTime expireDate, @Param("effectiveDate") LocalDateTime effectiveDate, @Param("fromPisCode") String fromPisCode);

    @Select("select count(*) from delegation where form_piscode= #{fromPisCode} and is_active=true")
    Integer existsActiveDelegation(@Param("fromPisCode") String fromPisCode);


    Page<TempDelegationResponsePojo> getUserDetails(Page<TempDelegationResponsePojo> page,@Param("pisCode") String preferredUsername);

    TempDelegationResponsePojo getTemporaryDelegationById(@Param("id") int id);

    @Select("select count(*)\n" +
            "from users u\n" +
            "    inner join users_roles ur on u.id = ur.user_id\n" +
            "    inner join role_group rg on ur.role_id = rg.id\n" +
            "where rg.role_group_key in ('OFFICE_HEAD') and u.pis_employee_code=#{fromPisCode} ")
    Integer isOfficeHead(@Param("fromPisCode") String fromPisCode);


    @Select("select count(*) from delegation where ((expire_date >= #{expireDate} and effective_date <= #{expireDate}) or (expire_date >= #{effectiveDate} and effective_date <= #{effectiveDate}))" +
            " and form_piscode= #{fromPisCode}  and id != #{delegationId}")
    Integer existsByExpireAndEffectiveDateAndIsNotId(@Param("expireDate") LocalDateTime expireDate, @Param("effectiveDate") LocalDateTime effectiveDate, @Param("fromPisCode") String fromPisCode, @Param("delegationId") Integer delegationId);


    @Select("select count(*) from delegation where form_piscode= #{fromPisCode} and (effective_date <= #{now} and expire_date >= #{now}) ")
    Integer existsDelegationByTime(@Param("now") LocalDateTime now,@Param("fromPisCode") String pisCode);

    List<TempDelegationResponsePojo> getAllDelegation(@Param("pisCode") String pisCode, @Param("isReassignment") Boolean isReassignment);
}
