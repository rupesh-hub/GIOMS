package custom.keycloak.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import custom.keycloak.model.User;
import custom.keycloak.pojo.DelegatedPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    // todo refactor role wise
    @Select("select count(*) from users u\n" +
            "inner join users_roles ur on u.id = ur.user_id\n" +
            "inner join role_group rg on ur.role_id = rg.id\n" +
            "where rg.role_group_key = 'OFFICE_HEAD' and u.pis_employee_code = #{pisCode}\n" +
            "and u.office_code = #{officeCode}\n" +
            ";\n")
    Long checkIfOfficeHead(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);

    @Select("select sd.id from section_designation sd\n" +
            "inner join section_subsection ss on sd.section_subsection_id = ss.id\n" +
            "inner join office o on ss.office_code = o.code\n" +
            "where sd.employee_pis_code = #{pisCode}  and o.code = #{officeCode} and sd.is_active = true limit 1")
    Long employeeSectionDesignationId(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);
    @Select("select\n" +
            "    o.organisation_type_id\n" +
            "    from employee e inner join office o on e.office_code = o. code where e.pis_code = #{pisCode}")
    Long getEmployeeOfficeCategory(@Param("pisCode") String pisCode);

    @Select("select rg.role_group_key from users_roles inner join role_group rg on users_roles.role_id = rg.id where user_id = #{id}")
    List<String> getRoles(Long id);

    @Select("select email_address from employee where pis_code = #{pisCode}")
    String getEmail(String pisCode);

    @Update("update users set password = #{encode} where user_name = #{username};")
    void updatePassword(@Param("encode") String encode, @Param("username") String username);

    @Update("update users set password = #{encode}, is_password_changed = true where user_name = #{username};")
    void updatePasswordAndStatus(@Param("encode") String encode, @Param("username") String username);

    @Select("select password from users where user_name = #{username}")
    String getUserPasswordHash(@Param("username") String username);

    @Select("select is_password_changed from users where user_name = #{username}")
    Boolean getIsPasswordChange(String userName);

    @Select("select id,user_name,pis_employee_code,is_active from users where user_name = #{username}")
    User findByUserName(String username);

    @Select("select\n" +
            "    case\n" +
            "        when e.middle_name_np IS NOT NULL then concat(e.first_name_np, ' ', e.middle_name_np, ' ', e.last_name_np)\n" +
            "        else concat(e.first_name_np, ' ', e.last_name_np) end as emp_name_np\n" +
            "from employee e where pis_code = #{username}")
    String getName(String username);

//    @Select("select id, effective_date as effectiveDate, expire_date as expireDate from delegation " +
//            "where is_active = true and expire_date > #{now}  " +
//            "and upper(form_piscode) = upper(#{delegatedPisCode}) and upper(to_piscode) = upper(#{piscode}) and effective_date < #{now}")
//    DelegatedPojo checkIfDelegated(@Param("delegatedPisCode") String delegatedPisCode, @Param("piscode") String piscode,@Param("now") LocalDateTime now);

        @Select("select id, effective_date as effectiveDate, expire_date as expireDate from delegation " +
            "where  expire_date > #{now}  " +
            "and upper(form_piscode) = upper(#{delegatedPisCode}) and upper(to_piscode) = upper(#{piscode}) and effective_date < #{now}")
    DelegatedPojo checkIfDelegated(@Param("delegatedPisCode") String delegatedPisCode, @Param("piscode") String piscode,@Param("now") LocalDateTime now);



    @Select("select o.set_up_completed from employee e inner join office o on e.office_code = o.code where e.pis_code = #{pisCode};")
    Boolean checkSetupCompleted(@Param("pisCode") String pisCode);
}
