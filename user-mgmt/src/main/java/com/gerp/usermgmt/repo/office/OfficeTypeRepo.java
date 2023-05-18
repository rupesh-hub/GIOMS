package com.gerp.usermgmt.repo.office;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.office.OfficeCategory;
import com.gerp.usermgmt.model.office.OfficeType;
import com.gerp.usermgmt.model.office.OrganisationType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OfficeTypeRepo extends GenericRepository<OrganisationType, Long> {

    @Query("select new com.gerp.shared.pojo.IdNamePojo(o.id, o.nameEn, o.nameNp) from OrganisationType o")
    List<IdNamePojo> findAllMinimal();

    @Query("update OrganisationType  o set o.isActive = case o.isActive" +
            " when true then false when false then true" +
            " else o.isActive end where o.id = ?1")
    @Transactional
    @Modifying
    void inactiveOfficeType(@Param("id") Long id);
}
