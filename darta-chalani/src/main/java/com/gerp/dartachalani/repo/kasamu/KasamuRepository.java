package com.gerp.dartachalani.repo.kasamu;

import com.gerp.dartachalani.model.kasamu.Kasamu;
import com.gerp.dartachalani.model.kasamu.KasamuState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KasamuRepository extends JpaRepository<Kasamu, Long> {

    @Query(value = "select * from kasamu where pis_code = ?1 and fiscal_year_code = ?2 order by id desc", nativeQuery = true)
    List<Kasamu> getByCreator(String pisCode, String fiscalYear, Pageable pageable);

    @Query(value = "select count(*) from kasamu where pis_code = ?1 and fiscal_year_code = ?2 ", nativeQuery = true)
    Integer countByCreator(String pisCode, String fiscalYear);

    @Query(value = "select * from kasamu k left join kasamu_state ks on k.id = ks.kasamu_id where ks.receiver_pis_code = ?1 and k.completion_status != 'FN' and k.fiscal_year_code = ?2 order by k.id desc", nativeQuery = true)
    List<Kasamu> getInbox(String pisCode, String fiscalYear, Pageable pageable);

    @Query(value = "select count(*) from kasamu k left join kasamu_state ks on k.id = ks.kasamu_id where ks.receiver_pis_code = ?1 and k.completion_status != 'FN' and k.fiscal_year_code = ?2 ", nativeQuery = true)
    Integer countInbox(String pisCode, String fiscalYear);

    @Query(value = "select * from kasamu k left join kasamu_state ks on k.id = ks.kasamu_id where ks.receiver_pis_code = ?1 and k.completion_status = 'FN' and k.fiscal_year_code = ?2 order by k.id desc", nativeQuery = true)
    List<Kasamu> getFinalized(String pisCode, String fiscalYear, Pageable pageable);

    @Query(value = "select count(*) from kasamu k left join kasamu_state ks on k.id = ks.kasamu_id where ks.receiver_pis_code = ?1 and k.completion_status = 'FN' and k.fiscal_year_code = ?2 ", nativeQuery = true)
    Integer countFinalized(String pisCode, String fiscalYear);

    @Query(value = "select * from kasamu where employee_pis_code = ?1 and fiscal_year_code = ?2 order by id desc", nativeQuery = true)
    List<Kasamu> getByEmployee(String pisCode, String fiscalYear, Pageable pageable);

    @Query(value = "select count(*) from kasamu where employee_pis_code = ?1 and fiscal_year_code = ?2 ", nativeQuery = true)
    Integer countByEmployee(String pisCode, String fiscalYear);

    @Query(value = "select * from kasamu where office_code = ?1 and fiscal_year_code = ?2 order by id desc", nativeQuery = true)
    List<Kasamu> getByOffice(String officeCode, String fiscalYear, Pageable pageable);

    @Query(value = "select count(*) from kasamu where office_code = ?1 and fiscal_year_code = ?2 ", nativeQuery = true)
    Integer countByOffice(String officeCode, String fiscalYear);

    @Query(value = "select distinct employee_pis_code  from kasamu k where k.pis_code = ?1", nativeQuery = true)
    List<String> getReceiverPisCodes(String pisCode);

    @Query(value = "select distinct pis_code from kasamu where employee_pis_code = ?1", nativeQuery = true)
    List<String> getCreatorPisCodes(String pisCode);

    @Query(value = "select case when count(id)> 0 then true else false end from kasamu where employee_pis_code = ?1 and subject_type = 'ANNUAL' and fiscal_year_code = ?2", nativeQuery = true)
    Boolean validateAnnual(String pisCode, String fiscalYearCode);


    @Query(value = "select case when count(k.id)> 0 then true else false end from kasamu k inner join external_kasamu_employee eke on k.id = eke.kasamu_id" +
            " where eke.pis_code = ?1 and k.subject_type = 'ANNUAL' and k.fiscal_year_code = ?2 ", nativeQuery = true)
    Boolean validateAnnualExternal(String pisCode, String fiscalYearCode);

    @Query(value = "select case when count(id)> 1 then true else false end from kasamu where employee_pis_code = ?1 and (subject_type = 'SEMIANNUALFIRST' or subject_type = 'SEMIANNUALSECOND') " +
            "and fiscal_year_code = ?2", nativeQuery = true)
    Boolean validateSemiAnnual(String pisCode, String fiscalYearCode);

    @Query(value = "select case when count(k.id)> 1 then true else false end from kasamu k inner join external_kasamu_employee eke on k.id = eke.kasamu_id" +
            " where eke.pis_code = ?1 and (k.subject_type = 'SEMIANNUALFIRST' or k.subject_type = 'SEMIANNUALSECOND') " +
            "and k.fiscal_year_code = ?2", nativeQuery = true)
    Boolean validateSemiAnnualExternal(String pisCode, String fiscalYearCode);

}
