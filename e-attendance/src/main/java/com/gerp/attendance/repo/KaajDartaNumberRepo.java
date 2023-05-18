package com.gerp.attendance.repo;

import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.attendance.model.setup.KaajDartaNumber;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface KaajDartaNumberRepo extends GenericSoftDeleteRepository<KaajDartaNumber, Long> {

    @Query(value = "select * from kaaj_darta_number where office_code = ?1 and is_active=true and fiscal_year_code = ?2 and registration_no=(\n" +
            "    select max(registration_no) from kaaj_darta_number where office_code= ?1 and fiscal_year_code= ?2);", nativeQuery = true)
    KaajDartaNumber getADarta(String officeCode, String fiscalYearCode);


    @Transactional
    @Modifying
    @Query(value = "update kaaj_darta_number set registration_no=?1 where office_code = ?2 and is_active=true and fiscal_year_code = ?3 and id=?4 ", nativeQuery = true)
    void updateRegistration(Long registrationNo,String officeCode, String fiscalYearCode,Long id);
}
