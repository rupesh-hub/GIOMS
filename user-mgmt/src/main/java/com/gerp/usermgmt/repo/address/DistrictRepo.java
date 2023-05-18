package com.gerp.usermgmt.repo.address;


import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.address.District;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DistrictRepo extends GenericSoftDeleteRepository<District, String> {

//    @Query(value = "select d from District d where d.province.id= :pId")
//    List<District> findAllByProvinceId(@Param("pId") Integer pId);

    @Query("select new com.gerp.shared.pojo.IdNamePojo(d.code, d.nameEn, d.nameNp) from District d")
    List<IdNamePojo> findAllDto();

    @Query("select new com.gerp.shared.pojo.IdNamePojo(d.code, d.nameEn, d.nameNp) from District d where d.code= :code")
    IdNamePojo findDistrictDetailMinimal(@Param("code") String code);
}
