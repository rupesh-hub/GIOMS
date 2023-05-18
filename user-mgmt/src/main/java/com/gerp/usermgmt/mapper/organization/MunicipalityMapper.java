package com.gerp.usermgmt.mapper.organization;

import com.gerp.shared.pojo.IdNamePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MunicipalityMapper {

    @Select("select mv.code as code ,mv.name_en as name, mv.name_np as nameN from municipality_vdc mv inner join district_vdc dv on mv.code = dv.municipality_vdc_code\n" +
            "inner join district d on dv.district_code = d.code where district_code = #{districtCode}")
    List<IdNamePojo> municipalityByDistrictCode(String districtCode);
}
