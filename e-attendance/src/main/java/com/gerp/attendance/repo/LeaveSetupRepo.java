package com.gerp.attendance.repo;

import com.gerp.attendance.model.leave.LeaveSetup;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface LeaveSetupRepo extends GenericSoftDeleteRepository<LeaveSetup, Long> {

    @Query(value= "select * from leave_setup ls where ls.office_code in ?1 order by ls.order_value", nativeQuery = true)
    List<LeaveSetup> getAll(List<String> officeCodes);


    @Modifying
    @Query(value= "update leave_setup set short_name_en=?2, short_name_np=?3 where id=?1", nativeQuery = true)
    void updateLeaveSetup(Long id, String shortNameEn,String shortNameNp);
}
