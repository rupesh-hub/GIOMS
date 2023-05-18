package com.gerp.usermgmt.repo.employee;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.employee.EmployeeJobDetailLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EmployeeJobDetailLogRepository extends GenericRepository<EmployeeJobDetailLog, Long> {
    
    @Query(value = "select e from EmployeeJobDetailLog e where e.isActive = true and e.pisCode = :pisCode")
    EmployeeJobDetailLog getPrevActiveLog(@Param("pisCode") String pisCode);
    @Query(value = "select e.pis_code ,\n" +
            "     case\n" +
            "     when e.middle_name_np IS NOT NULL \n" +
            "        then concat(e.first_name_en, ' ', e.middle_name_en, ' ', e.last_name_en)\n" +
            "        else concat(e.first_name_en, ' ', e.last_name_en) \n" +
            "     end as name_en,\n" +
            "    case \n" +
            "    when e.middle_name_np IS NOT NULL \n" +
            "       then concat(e.first_name_np, ' ', e.middle_name_np, ' ', e.last_name_np)\n" +
            "       else concat(e.first_name_np, ' ', e.last_name_np) \n" +
            "    end as name_np,\n" +
            "   ejdl.promoted_office_code,o.name_en as org_name_en ,o.name_np as org_name_np,\n" +
            "   (select fd2.name_en from functional_designation fd2 where fd2.code=ejdl.old_designation_code) as old_designation_en,\n" +
            "   (select fd2.name_np  from functional_designation fd2 where fd2.code=ejdl.old_designation_code) as old_designation_np,\n" +
            "   (select fd2.name_en from functional_designation fd2 where fd2.code=ejdl.new_designation_code) as new_designation_en,\n" +
            "   (select fd2.name_np from functional_designation fd2 where fd2.code=ejdl.new_designation_code) as new_designation_np,\n" +
            "   (select fd2.code from functional_designation fd2 where fd2.code=ejdl.new_designation_code) as new_designation_code,\n" +
            "   (select fd2.code from functional_designation fd2 where fd2.code=ejdl.old_designation_code) as old_designation_code,\n" +
            "   to_char(ejdl.start_date,'YYYY-MM-DD') as start_date_en,\n" +
            "   ejdl.start_date_np as start_date_np\n" +
            "   from employee_job_detail_log ejdl\n" +
            "   left join office o on o.code=ejdl.promoted_office_code \n" +
            "   left  join employee e on ejdl.pis_code =e.pis_code where ejdl.pis_code =?1" +
            "   and ejdl.promoted_office_code=?2 order by ejdl.start_date ",nativeQuery = true)
    List<Map<String,Object>> getDesignationHistory(String pisCode,String officeCode);
}