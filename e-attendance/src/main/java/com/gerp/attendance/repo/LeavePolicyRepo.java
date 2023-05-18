package com.gerp.attendance.repo;

import com.gerp.attendance.model.leave.LeavePolicy;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LeavePolicyRepo extends GenericSoftDeleteRepository<LeavePolicy, Long> {

    @Query(value = "select lp.*\n" +
            "from leave_policy lp\n" +
            "         inner join leave_setup ls on lp.leave_setup_id = ls.id\n" +
            "where ls.is_active = true and lp.office_code in ?1 and lp.gender in ('A' , (select e.gender from employee e where e.pis_code=?2)) order by ls.order_value, office_code desc", nativeQuery = true)
    List<LeavePolicy> getAll(List<String> officeCode,String pisCode);

    @Query(value = "select lp.*\n" +
            "from leave_policy lp\n" +
            "         inner join leave_setup ls on lp.leave_setup_id = ls.id\n" +
            "where lp.office_code in ?1 order by ls.order_value, office_code desc", nativeQuery = true)
    List<LeavePolicy> getAllApplicable(List<String> officeCode);
}
