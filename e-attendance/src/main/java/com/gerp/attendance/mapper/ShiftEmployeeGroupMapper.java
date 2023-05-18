package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Mapper
public interface ShiftEmployeeGroupMapper {

    ShiftEmployeeGroupPojo findById(Long id);

    @Select("select id, name_en, name_np, employee_count from shift_employee_group where fiscal_year = #{fiscalYear}")
    List<ShiftEmployeeGroupPojo> findAllByFiscalYear(Long fiscalYear);

    @Select("select id, name_en, name_np, employee_count from shift_employee_group where office_code = #{officeCode} ")
    List<ShiftEmployeeGroupPojo> findAll(String officeCode);

    List<ShiftEmployeeGroupPojo> findByIds(@Param("ids") List<Long> ids);

    List<ShiftEmployeeGroupPojo> findByGroupIds(@Param("ids") List<Long> ids);


    Page<ShiftEmployeeGroupPojo> findAllByFiscalYearPaginated(Page<ShiftEmployeeGroupPojo> page, @Param("fiscalYear") Long fiscalYear, @Param("map") Map<String, Object> searchField);

    @Select("select sg.id,\n" +
            "       sg.name_en,\n" +
            "       sg.name_np,\n" +
            "       sg.employee_count\n" +
            "from shift_employee_group sg\n" +
            "         left join (select * from shift_employee_group_config where shift_id =#{id}) as segc on sg.id = segc.shift_employee_group_id\n" +
            "where sg.office_code = #{officeCode}\n" +
            "  and segc.id is null")
    List<ShiftEmployeeGroupPojo> getUnusedShiftGroup(@Param("officeCode") String officeCode, @Param("id") Long id);

    @Select("select shift_employee_group_id from shift_employee_group_config where shift_id = #{shiftId} ")
    List<Long> getMappedGroupIds(Long shiftId);

    @Select("select segm.shift_employee_group_id from shift_employee_group_mapping segm where segm.pis_code= #{pisCode}")
    List<Long> getMappedGroupByPisCode(String pisCode);

    @Select("select segc.shift_id as shift_id from shift_employee_group_mapping segm\n" +
            "                         left join shift_employee_group_config segc on segc.shift_employee_group_id=segm.shift_employee_group_id\n" +
            "                               left join shift s on s.id=segc.shift_id\n" +
            "                          where segm.pis_code= #{pisCode} and s.office_code= #{officeCode}")
    List<Long>getMappedGroupByPisAndOffice(@Param("pisCode") String pisCode,@Param("officeCode") String officeCode);

    @Select("select pis_code from shift_employee_config where shift_id = #{shiftId} ")
    List<String> getMappedPisCodeIds(Long shiftId);

    @Select("select segm.pis_code from shift_employee_group seg inner join shift_employee_group_mapping segm on seg.id = segm.shift_employee_group_id\n" +
            "where seg.office_code = #{officeId} ")
    List<String> getMappedPisCodeByOffice(String officeCode);

    List<String> getMappedPisCodeByOfficeWithCheckedFilter(@Param("officeCode") String officeCode, @Param("pisCodes") List<String> pisCodes);
}
