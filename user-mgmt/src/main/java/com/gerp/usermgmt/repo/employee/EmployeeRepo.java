package com.gerp.usermgmt.repo.employee;


import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.employee.Employee;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface EmployeeRepo extends GenericRepository<Employee, String> {
    Employee findByPisCode(String pisCode);

    @Query(
            value = "\n" +
                    "WITH recursive q AS (select p.parent_position_code, p.code,p.order_no\n" +
                    "                     from position p\n" +
                    "                              inner join employee e on p.code = e.position_code\n" +
                    "                     where e.pis_code = ?1\n" +
                    "                     UNION ALL\n" +
                    "                     select z.parent_position_code, z.code,z.order_no\n" +
                    "                     from position z\n" +
                    "                              join q on z.code = q.parent_position_code\n" +
                    ")\n" +
                    "select  * from employee e inner join position p on p.code = e.position_code\n" +
                    "    inner join q on q.parent_position_code=p.parent_position_code\n" +
                    "inner join users u on u.pis_employee_code = e.pis_code\n" +
                    "where p.order_no<=q.order_no and e.pis_code <> ?1\n" +
                    "  and e.office_code = (select office_code from employee e where e.pis_code = ?1)" , nativeQuery = true
    )
    List<Employee> getHigherHierarchyEmployees(String pisCode);

    @Query(
            value = "WITH recursive q AS (select p.parent_position_code, p.code\n" +
                    "                     from position p\n" +
                    "                              inner join employee e on p.code = e.position_code\n" +
                    "                     where e.pis_code = ?1\n" +
                    "                     UNION ALL\n" +
                    "                     select z.parent_position_code, z.code\n" +
                    "                     from position z\n" +
                    "                              join q on z.code = q.parent_position_code\n" +
                    ")\n" +
                    "SELECT distinct *\n" +
                    "FROM employee e\n" +
                    "         inner join q on e.position_code = q.code\n" +
                    "where e.pis_code <> ?1\n" +
                    "  and e.office_code = (select office_code from employee e where e.pis_code = ?1)" , nativeQuery = true
    )
    List<Employee> getLowerHierarchyEmployee(String pisCode);


    @Query(
            value = "WITH recursive q AS (select p.parent_position_code, p.code\n" +
                    "                                         from position p \n" +
                    "                                                  inner join employee e on p.code = e.position_code \n" +
                    "                                         where e.pis_code = ?1\n" +
                    "                                         UNION ALL \n" +
                    "                                         select z.parent_position_code, z.code \n" +
                    "                                         from position z \n" +
                    "                                                  join q on z.code = q.parent_position_code \n" +
                    "                    ) \n" +
                    "                    SELECT distinct * \n" +
                    "                    FROM employee e \n" +
                    "                             inner join q on e.position_code = q.code\n" +
                    "                             inner join users u on e.pis_code = u.pis_employee_code\n" +
                    "                    where e.pis_code <> ?1\n" +
                    "                      and e.office_code = (select office_code from employee e where e.pis_code = ?1)" , nativeQuery = true
    )
    List<Employee>  getLowerHierarchyEmployeeUsers(String pisCode);


    boolean existsByPisCode(String pisCode);

    @Query(
            value = "\n" +
                    "WITH recursive q AS (select p.parent_position_code, p.code,p.order_no\n" +
                    "                     from position p\n" +
                    "                              inner join employee e on p.code = e.position_code\n" +
                    "                     where e.pis_code = ?1\n" +
                    "                     UNION ALL\n" +
                    "                     select z.parent_position_code, z.code,z.order_no\n" +
                    "                     from position z\n" +
                    "                              join q on z.code = q.parent_position_code\n" +
                    ")\n" +
                    "select  distinct * from employee e inner join position p on p.code = e.position_code\n" +
                    "    inner join q on q.parent_position_code=p.parent_position_code\n" +
                    "inner join users u on u.pis_employee_code = e.pis_code\n" +
                    "inner join section_designation sd on e.pis_code = sd.employee_pis_code and sd.is_active = true\n" +
                    "where p.order_no<=q.order_no and e.pis_code <> ?1\n" +
                    "  and e.office_code = (select office_code from employee e where e.pis_code = ?1) and sd.section_subsection_id = ?2" , nativeQuery = true
    )
    List<Employee> getSectionHigherHierarchyEmployees(String pisCode, Long sectionId);

    @Query(value = "select e.pis_code from employee e", nativeQuery = true)
    List<String> insertedPisCodes();

    @Modifying
    @Transactional
    @Query(value = "update employee set email_address=?1 where pis_code = ?2 ", nativeQuery = true)
    void updateEmployeeEmail(String email, String pisEmployeeCode);

    @Modifying
    @Transactional
    @Query(value = "update employee set office_code=?1 where pis_code = ?2 ", nativeQuery = true)
    void updateEmployeeOffice(String officeCode, String pisEmployeeCode);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "select count(*) as totalCount from  employee_joining_date ejd " +
            "inner join employee e on e.pis_code =ejd.employee_pis_code  \n" +
            "left join functional_designation fd on e.designation_code = fd.code\n" +
            "where e.office_code =?1 and e.pis_code like '%KR_%'and  ejd.is_active =true and (((DATE_PART('day', ejd.end_date_en- now())) <= ?2) \n" +
            "and ((DATE_PART('day', ejd.end_date_en- now()))>=0))")
    List<Map<String,Object>> getEmployeeValidCount(String officeCode,int days);

    @Query(value = " select count(*) from employee e where e.pis_code= ?1 ", nativeQuery = true)
    Integer getEmployeeCountByPisCode(String pisCode);
}
