package com.gerp.usermgmt.mapper.organization;

import com.gerp.shared.pojo.IdNamePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FacultyMapper {
    @Select("select f.code as code, f.name_en as name, f.name_np as nameN\n" +
            "from faculty f\n" +
            "         inner join education_faculty ef on f.code = ef.faculty_code\n" +
            "         inner join education_level el on ef.education_level_code = el.code\n" +
            "where education_level_code = #{educationLevelCode}")
    List<IdNamePojo> facultyByEducationLevelCode(String educationLevelCode);
}
