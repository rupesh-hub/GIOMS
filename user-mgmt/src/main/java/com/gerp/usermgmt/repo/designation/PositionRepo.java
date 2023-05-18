package com.gerp.usermgmt.repo.designation;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.employee.Position;
import com.gerp.usermgmt.model.office.OrganisationType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionRepo extends GenericSoftDeleteRepository<Position, String> {

    @Query("select new com.gerp.shared.pojo.IdNamePojo(p.code, p.nameEn, p.nameNp) from Position p where (:organisationType is null or p.organisationType = :organisationType)")
    List<IdNamePojo> positions(@Param("organisationType") OrganisationType organisationType);
}
