package com.gerp.usermgmt.repo.office;

import com.gerp.usermgmt.model.office.OfficeGroupDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OfficeGroupRepo extends JpaRepository<OfficeGroupDetail,Integer> {

    @Transactional
    @Modifying
    @Query(value = "delete from Office_group where office_group_detail_id is null and type !='EXTERNAL_OFFICE'",nativeQuery = true)
    void deleteRow();
}
