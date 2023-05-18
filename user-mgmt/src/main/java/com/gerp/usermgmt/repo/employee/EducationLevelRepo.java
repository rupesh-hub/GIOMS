package com.gerp.usermgmt.repo.employee;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.employee.EducationLevel;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EducationLevelRepo  extends GenericRepository<EducationLevel, String> {

    @Query("select new com.gerp.shared.pojo.IdNamePojo(e.code , e.nameEn, e.nameNp) from EducationLevel e")
    List<IdNamePojo> getAllEducationLevels();
}
