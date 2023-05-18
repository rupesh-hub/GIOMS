package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.SetupPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface ProvinceMapper {
    @Select("select p.id as id, p.name_en from province p where p.country_id= #{countryId}")
    ArrayList<SetupPojo> getByCountryId(Long countryId);
}
