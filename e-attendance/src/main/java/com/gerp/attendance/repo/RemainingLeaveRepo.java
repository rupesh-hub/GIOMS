package com.gerp.attendance.repo;

import com.gerp.attendance.model.leave.RemainingLeave;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RemainingLeaveRepo extends GenericSoftDeleteRepository<RemainingLeave, Long> {

    @Modifying
    @Query(value = " update remaining_leave set accumulated_leave_fy = ?2 where pis_code = ?1 and leave_policy_id= ?3 and is_active=true", nativeQuery = true)
    void updateAccumulatedDays(String pisCode, Long accumulatedDays, Long policyId);

    @Modifying
    @Query(value = " update remaining_leave set leave_taken = ?2, leave_taken_fy= ?3, accumulated_leave_fy= ?4, repetition=repetition + 1 where pis_code = ?1 and leave_policy_id= ?5 and is_active=true", nativeQuery = true)
    void updateLeaveTakenFy(String pisCode, Long leaveTaken, Long leaveTakenFy, Long accumulatedLeave, Long policyId);

//    @Modifying
//    @Transactional
//    @Query(nativeQuery = true, value = "delete from remaining_leave rl where rl.pis_code= ?1 and rl.office_code= ?2")
//    void updateByRemainingLeave(String pisCode, String officeCode);

    @Modifying
    @Transactional
    @Query(value = "delete from remaining_leave rl where rl.pis_code= ?1 and rl.office_code= ?2",nativeQuery = true)
    void updateByRemainingLeave(String pisCode, String officeCode);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from remaining_leave rl where rl.leave_policy_id= ?1")
    void deleteByLeavePolicyId(Long id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from remaining_leave rl where rl.id= ?1")
    void deleteByRemaining(Long id);


    @Query(value = "select * from  remaining_leave rl\n" +
            "    where rl.pis_code=?1 and rl.office_code=?2 and rl.is_active=true and rl.fiscal_year=?3 and rl.leave_policy_id=?4", nativeQuery = true)
    RemainingLeave findRemainingLeave(String pisCode, String officeCode, Integer fiscalYear, Long leavePolicyId);

    @Query(value = "select * from  remaining_leave where id = ?1", nativeQuery = true)
    RemainingLeave remainingLeaveById(Long id);


    @Query(value = "select * from remaining_leave rl where rl.pis_code=?1 and rl.office_code=?2", nativeQuery = true)
    List<RemainingLeave> findSpecificPisCodeRemaining(String pisCode, String officeCode);

    @Modifying
    @Query(value = "update RemainingLeave set officeCode = :officeCode  where pisCode = :pisCode")
    void updateRemainingLeave(@Param("officeCode") String officeCode,@Param("pisCode") String pisCode);

    @Modifying
    @Transactional
    @Query(value = "update remaining_leave set office_code = ?1,is_active=false where pis_code = ?2 and leave_policy_id=?3 and year=?4", nativeQuery = true)
    void softDeleteRemaining(String officeCode,String pisCode,Long leavePolicyId,String year);

}
