package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.FooterData;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FooterDataRepo extends GenericSoftDeleteRepository<FooterData, Long> {

    @Query(value = "select * from footer_data where office_code = ?1 order by is_active desc ", nativeQuery = true)
    List<FooterData> findByOfficeCode(String officeCode);

    @Modifying
    @Query(value = "update footer_data set is_active = not is_active where id = ?1", nativeQuery = true)
    void softDeleteFooterById(Long id);

    @Modifying
    @Query(value = "update footer_data set is_active = false where office_code = ?1", nativeQuery = true)
    void setIsActiveByOfficeCode(String officeCode);

}
