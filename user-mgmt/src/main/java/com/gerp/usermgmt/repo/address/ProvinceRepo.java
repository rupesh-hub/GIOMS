package com.gerp.usermgmt.repo.address;


import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.address.Province;

public interface ProvinceRepo extends GenericSoftDeleteRepository<Province, Integer> {

//    @Query(value = "select p from Province p where p.country.id= :cId")
//    List<Province> findAllByCountryId(@Param("cId") Integer cId);
}
