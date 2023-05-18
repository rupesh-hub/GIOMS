package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.AttendanceDeviceResponsePojo;
import com.gerp.attendance.Pojo.PostAttendanceResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface PostAttendanceMapper {

    ArrayList<PostAttendanceResponsePojo> getAllPostAttendance();

    ArrayList<PostAttendanceResponsePojo> getPostAttendanceByPisCode(String pisCode);

    ArrayList<PostAttendanceResponsePojo> getPostAttendanceByOfficeCode(String officeCode);

    ArrayList<PostAttendanceResponsePojo> getPostAttendanceByFiscalYear(String fiscalYear);



}
