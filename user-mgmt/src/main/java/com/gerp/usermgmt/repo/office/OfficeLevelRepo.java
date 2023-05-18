package com.gerp.usermgmt.repo.office;


import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.office.OrganizationLevel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficeLevelRepo extends GenericSoftDeleteRepository<OrganizationLevel, Integer> {

    @Query("select new com.gerp.shared.pojo.IdNamePojo(o.id, o.nameEn, o.nameNp) from OrganizationLevel o ")
    List<IdNamePojo> findAllDto();
}
