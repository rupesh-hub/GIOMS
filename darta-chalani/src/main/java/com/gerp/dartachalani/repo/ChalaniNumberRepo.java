package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.number.ChalaniNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChalaniNumberRepo extends JpaRepository<ChalaniNumber, Integer> {

    @Query(value = "SELECT * FROM chalani_number WHERE office_code = ?1 and fiscal_year_code = ?2", nativeQuery = true)
    List<ChalaniNumber> getByOfficeCode(String officeCode, String fiscalYearCode);

    @Query(value = "SELECT * FROM chalani_number WHERE office_code = ?1 AND fiscal_year_code = ?2 for update", nativeQuery = true)
    ChalaniNumber getAChalani(String officeCode, String code);

    @Modifying
    @Query(value = "UPDATE chalani_number set dispatch_no = ?2 WHERE id = ?1", nativeQuery = true)
    void updateChalani(Integer id, Long value);

    @Query(value = "select dispatch_no from chalani_number where office_code = ?1 and fiscal_year_code = ?2 ", nativeQuery = true)
    Long getChalaniNumByOfficeCode(String officeCode, String fiscalYearCode);

}
