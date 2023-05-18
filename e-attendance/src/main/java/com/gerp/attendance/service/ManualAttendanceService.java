package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.model.attendances.ManualAttendance;
import com.gerp.attendance.model.attendances.ManualAttendanceDetail;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.ApprovalPojo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ManualAttendanceService extends GenericService<ManualAttendance, Long> {

    /**
     * This method saves a manual attendance.
     *
     * @param manualAttendancePojo
     * @return ManualAttendance - saved manual attendance.
     */
    ManualAttendance saveAttendance(ManualAttendancePojo manualAttendancePojo) throws IOException;
    ManualAttendance saveAttendanceBulk(ManualAttendancePojo manualAttendancePojo) throws IOException;

    List<ManualAttendanceDetail> readExcelData(MultipartFile attendanceFile, LocalDate dateEn) throws IOException;

    ManualAttendance updateAttendance(ManualAttendanceUpdatePojo manualAttendanceUpdatePojoPojo) throws IOException;

    /**
     * This method gets a list of all the manual attendances.
     *
     * @return ManualAttendanceMapperPojo - List of all manual attendances.
     */
    ArrayList<ManualAttendancePojo> getAllManualAttendance();

    ManualAttendancePojo getManualAttendanceById(Long id);

    void deleteManualAttendance(Long id);

    /**
     * This method is used to return attendance of an office.
     *
     * @param officeId
     * @return ManualAttendanceMapperPojo - List of attendances.
     */
    ArrayList<ManualAttendanceMapperPojo> getByOfficeId(Integer officeId);

    ManualPracticePojo saveAttendanceFile(ManualPracticePojo manualPracticePojo) throws IOException;

    void updateStatus(ApprovalPojo data);

    ArrayList<ManualAttendancePojo> getManualAttendanceByApproverPisCode();

    Page<ManualAttendancePojo> filterData(GetRowsRequest paginatedRequest);
    Page<ManualAttendanceResponsePojo> manualAttendanceFilter(GetRowsRequest paginatedRequest);
}
