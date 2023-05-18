package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.SetupPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface LocationMapper {

    @Select("select l.id as id, l.name_en from location l where l.district_id =#{districtId}")
    ArrayList<SetupPojo> getAllByDistrict(Long districtId);
}
