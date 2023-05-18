package com.gerp.usermgmt.repo.address;


import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.address.Country;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepo extends GenericSoftDeleteRepository<Country, String> {


    @Query("select new com.gerp.shared.pojo.IdNamePojo(d.code, d.nameEn, d.nameNp) from Country d")
    List<IdNamePojo> getAllCountry();
}
