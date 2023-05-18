package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.number.TippaniNumber;
import com.gerp.shared.pojo.IdNamePojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TippaniNumberRepo extends JpaRepository<TippaniNumber, Long> {

    @Query(value = "select * from tippani_number where office_code = ?1 and fiscal_year_code = ?2", nativeQuery = true)
    TippaniNumber getATippani(String officeCode, String activeFiscalYear);

}
