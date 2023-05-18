package com.gerp.attendance.controller.Attendance.Device;

import com.gerp.attendance.Pojo.AttendanceDevicePojo;
import com.gerp.attendance.Pojo.AttendanceDeviceResponsePojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.attendances.EmployeeAttendance;
import com.gerp.attendance.model.device.AttendanceDevice;
import com.gerp.attendance.service.AttendanceDeviceService;
import com.gerp.attendance.service.EmployeeAttendanceService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/attendance-device")
public class AttendanceDeviceController extends GenericCrudController<AttendanceDevice, Integer> {
    private final AttendanceDeviceService attendanceDeviceService;
    private final EmployeeAttendanceService employeeAttendanceService;
    private final CustomMessageSource customMessageSource;

    public AttendanceDeviceController (AttendanceDeviceService attendanceDeviceService, EmployeeAttendanceService employeeAttendanceService,CustomMessageSource customMessageSource) {
        this.attendanceDeviceService = attendanceDeviceService;
        this.employeeAttendanceService=employeeAttendanceService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.ATTENDANCE_DEVICE_MODULE_NAME;
        this.permissionName = PermissionConstants.ATTENDANCE + "_" + PermissionConstants.ATTENDANCE_DEVICE_SETUP;
    }


    /**
     * This method adds new attendance device
     * @param attendanceDevicePojo
     * @param bindingResult
     * @return success response with id or failure response
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping(value="/save")
    public ResponseEntity<?> create(@Valid @RequestBody AttendanceDevicePojo attendanceDevicePojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            AttendanceDevice attendanceDevice= attendanceDeviceService.save(attendanceDevicePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            attendanceDevice.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * This method updates the attendance device
     * @param attendanceDevicePojoParam
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping(value="/update")
    public ResponseEntity<?> update(@Valid @RequestBody AttendanceDevicePojo attendanceDevicePojoParam, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            attendanceDeviceService.update(attendanceDevicePojoParam);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            attendanceDevicePojoParam.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * This method gets all attendance device of respective office
     * @return
     */
    @GetMapping(value="/get-All")
    public ResponseEntity<?> getAttendanceDevice()  {

            List<AttendanceDeviceResponsePojo> allAttendanceDevice= attendanceDeviceService.getAllAttendanceDevice();
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                            allAttendanceDevice)
            );

    }

    /**
     * This method get attendance device by id
     * @param id
     * @return
     */
    @GetMapping(value="find-by-id/{id}")
    public ResponseEntity<?> getAttendanceDevice(@PathVariable Integer  id)  {

      AttendanceDeviceResponsePojo attendanceDevice= attendanceDeviceService.getAttendanceDeviceById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        attendanceDevice)
        );

    }

    @GetMapping(value="get-by-officeCode/{officeCode}")
    public ResponseEntity<?> getByOfficeCode(@PathVariable String officeCode)  {

        List<AttendanceDeviceResponsePojo> attendanceDevice= attendanceDeviceService.getAttendanceDeviceByOfficeCode(officeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        attendanceDevice.get(0))
        );

    }

    /**
     * Soft Delete attendance device
     * @param deviceId
     * @return
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/delete/{deviceId}")
    public ResponseEntity<?> deleteDevice(@PathVariable("deviceId") Integer deviceId) {
        attendanceDeviceService.deleteDevice(deviceId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        deviceId)
        );
    }

    /**
     * This method save the attendance from attendance_device
     * @return
     */
    @GetMapping(value="/add-attendance")
    public ResponseEntity<?> addAttendance()  {

        List<EmployeeAttendance> employeeAttendances= employeeAttendanceService.saveEmployeeAttendance();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        employeeAttendances)
        );

    }


}
