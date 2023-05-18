package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.SetupPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface DistrictMapper {
    @Select("select d.id as id, d.name_en from district d where d.province_id =#{provinceId}")
    ArrayList<SetupPojo> getAllByProvince(Long provinceId);

//    @Select("select d.id as id, d.name_en,p.name_en as provinceName from district d inner join province p where d.province_id=p.id")
//    select d.id as id, d.name_en,p.name_en as provinceName from district d inner join province p on d.province_id=p.id;
//    ArrayList<SetupPojo> getAllDistrict();
}
