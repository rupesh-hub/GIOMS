package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.number.DartaNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DartaNumberRepo extends JpaRepository<DartaNumber, Integer> {

    @Query(value = "SELECT * FROM darta_number WHERE office_code = ?1 and fiscal_year_code = ?2", nativeQuery = true)
    List<DartaNumber> getByOfficeCode(String officeCode, String fiscalYear_Code);

    @Query(value = "select * from darta_number where office_code = ?1 and fiscal_year_code = ?2 for update", nativeQuery = true)
    DartaNumber getADarta(String officeCode, String fiscalYearCode);

    @Modifying
    @Query(value = "UPDATE darta_number set registration_no = ?2 WHERE id = ?1", nativeQuery = true)
    void updateDarta(Integer id, Long value);

    @Query(value = "select registration_no from darta_number where office_code = ?1 and fiscal_year_code = ?2", nativeQuery = true)
    Long getDartaNumByOfficeCode(String officeCode, String fiscalYearCode);

}
