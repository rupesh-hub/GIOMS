package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.EmployeeDetailPojo;
import com.gerp.attendance.Pojo.ManualAttendanceDetailPojo;
import com.gerp.attendance.Pojo.ManualAttendancePojo;
import com.gerp.attendance.Pojo.ManualAttendanceResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.Pojo.manualattendance.ManualExcelDataPojo;
import com.gerp.shared.pojo.IdNamePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface ManualAttendanceMapper {

    ArrayList<ManualAttendancePojo> getAllManualAttendanceByOfficeCode(String officeCode);

    ManualAttendancePojo getManualAttendanceById(Long id);

    ArrayList<ManualAttendancePojo> getManualAttendanceByApproverPisCode(String approverPisCode);


    @Select("select ma.id,\n" +
            "       ma.date_en,\n" +
            "       ma.date_np,\n" +
            "       mad.from_date_np,\n" +
            "       mad.to_date_np,\n" +
            "       mad.from_date_en,\n" +
            "       mad.to_date_en,\n" +
            "       ma.office_code,\n" +
            "       mad.pis_code,\n" +
            "       mad.checkin_time,\n" +
            "       mad.checkout_time\n" +
            "from manual_attendance ma\n" +
            "         left join decision_approval da on ma.id = da.manual_attendance_id\n" +
            "         left join manual_attendance_detail mad on ma.id = mad.manual_attendance_id\n" +
            "where ma.is_active = true and da.is_active = true and mad.is_active=true and ma.id= #{id} order by ma.created_date")
    ArrayList<ManualAttendanceDetailPojo> getAllManualDetails(Long id);
    /* modify ma to mad date*/

    ArrayList<ManualAttendancePojo> checkManualForDate(@Param("dateEn") LocalDate dateEn, @Param("officeCode") String officeCode);

    @Select("select mad.pis_code from manual_attendance_detail mad where\n" +
            "            mad.manual_attendance_id=#{id} and mad.is_active=true;")
    List<String> getPiscodeByManual(Long id);


    ArrayList<ManualAttendancePojo> checkForApproval(@Param("dateEn") LocalDate dateEn,
                                                     @Param("officeCode") String officeCode);

    List<ManualExcelDataPojo> getExcelEmployeeDetail(Long id);
//    List<ManualExcelDataPojo> manualAttendanceDetailsBulk(Long id,String pisCode);

    @Select("select case\n" +
            "                when((select count(*)\n" +
            "                from employee e\n" +
            "                         left join users u on e.pis_code = u.pis_employee_code\n" +
            "                where (e.pis_code= #{pisCode} or e.employee_code=#{pisCode}) and e.office_code=#{officeCode} and e.is_active=true and u.is_active=true)>0)\n" +
            "                             then true\n" +
            "                         else false\n" +
            "    end AS employee;")
    Boolean validateEmployee(@Param("pisCode") String pisCode, @Param("officeCode") String officeCode);


    Page<ManualAttendancePojo> filterData(Page<ManualAttendancePojo> page,
                                          @Param("fiscalYear") Long fiscalYear,
                                          @Param("isApprover") Boolean isApprover,
                                          @Param("officeCode") String officeCode,
                                          @Param("approverPisCode") String approverPisCode,
                                          @Param("searchField") Map<String, Object> searchField);

    Page<ManualAttendanceResponsePojo> manualAttendanceFilter(Page<ManualAttendanceResponsePojo> page,
                                                              @Param("loginPis") String loginPis,
                                                              @Param("fiscalYear") Long fiscalYear,
                                                              @Param("officeCode") String officeCode,
                                                              @Param("approverPisCode") String approverPisCode,
                                                              @Param("searchField") Map<String, Object> searchField);

    Boolean checkForPendingManual(@Param("pisCode") String pisCode);

    Boolean checkForApprovalManual(@Param("pisCode") String pisCode);

    IdNamePojo getEmployeeByPisCode(@Param("pisCode") String pisCode, @Param("id") Long id);

    EmployeeDetailPojo getRequesterDetail(@Param("pisCode") String pisCode, @Param("id") Long id);

    List<DocumentPojo> selectDocument(@Param("id") Long id);

}
