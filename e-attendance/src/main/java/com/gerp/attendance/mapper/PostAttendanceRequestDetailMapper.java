package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.FiscalYearPojo;
import com.gerp.attendance.Pojo.DetailPojo;
import com.gerp.attendance.Pojo.PostAttendanceGetPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface PostAttendanceRequestDetailMapper {

//    @Select("select from_date_en, from_date_np, remarks, status, supporting_documents_id, to_date_en, to_date_np from post_attendance_request_detail where post_attendance_request_id = #{id} and is_active = true ")
//    PostAttendanceRequestDetail findPostAttendanceRequest(Long id);

    @Select("select\n" +
            "    case when e.middle_name_en IS NOT NULL then concat(e.first_name_en,' ',e.middle_name_en,' ',e.last_name_en)\n" +
            "                else concat(e.first_name_en,' ',e.last_name_en) end as nameEn,\n" +
            "\n" +
            "    case when e.middle_name_np IS NOT NULL then concat(e.first_name_np,' ',e.middle_name_np,' ',e.last_name_np)\n" +
            "                else concat(e.first_name_np,' ',e.last_name_np) end as nameNp\n" +
            "from employee e where e.pis_code= #{pisCode} and e.is_active=true")
    DetailPojo getEmployeeName(@Param("pisCode") String pisCode);

    @Select("select fy.year_np as fiscalYearNp,fy.year as fiscalYearEn from fiscal_year fy where fy.code= #{fiscalYearCode} and fy.is_active=true;\n")
    FiscalYearPojo getFiscalYear(@Param("fiscalYearCode") String fiscalYearCode);

    @Select("select of.name_np as nameNp,of.name_en as nameEn from office of where of.code= #{officeCode}")
    DetailPojo getOfficeName(@Param("officeCode") String officeCode);

    PostAttendanceGetPojo getPostAttendanceById(Long id);

    ArrayList<PostAttendanceGetPojo> getAllPostAttendance();

    ArrayList<PostAttendanceGetPojo>getPostAttendanceByApprover(String pisCode);

}
