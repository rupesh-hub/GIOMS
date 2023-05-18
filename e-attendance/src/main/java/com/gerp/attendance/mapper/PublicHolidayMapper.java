package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.HolidayMapperPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface PublicHolidayMapper {

    @Select("SELECT id, office_code AS officeCode, name_np AS nameNp, name_en AS nameEn, short_name_np AS shortNameNp,short_name_en AS shortNameEn,holiday_for AS holidayFor, is_active AS isActive FROM public_holiday")
    ArrayList<HolidayMapperPojo> getAllHolidays();

    @Select("SELECT id, office_code AS officeCode, name_np AS nameNp, name_en AS nameEn, short_name_np AS shortNameNp,short_name_en AS shortNameEn,holiday_for AS holidayFor, is_active AS isActive FROM public_holiday WHERE id= #{id}")
    HolidayMapperPojo getHolidayById(Integer id);

    @Select("SELECT id, office_code AS officeCode, name_np AS nameNp, name_en AS nameEn, short_name_np AS shortNameNp,short_name_en AS shortNameEn, holiday_for AS holidayFor, is_active AS isActive FROM public_holiday WHERE office_code = #{officeCode}")
    ArrayList<HolidayMapperPojo> getByOfficeCode(String officeCode);

    @Select("SELECT id, office_code AS officeCode, name_np AS nameNp, name_en AS nameEn, short_name_np AS shortNameNp,short_name_en AS shortNameEn, holiday_for AS holidayFor, is_active AS isActive FROM public_holiday WHERE holiday_for = #{holidayFor}")
    ArrayList<HolidayMapperPojo> getHolidayFor(String holidayFor);
}
