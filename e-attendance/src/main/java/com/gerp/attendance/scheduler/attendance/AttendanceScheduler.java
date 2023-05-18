package com.gerp.attendance.scheduler.attendance;

import com.gerp.attendance.service.EmployeeAttendanceService;
import com.gerp.attendance.service.LeaveRequestService;
import com.gerp.attendance.service.RemainingLeaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AttendanceScheduler {

    @Autowired
    private EmployeeAttendanceService employeeAttendanceService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private RemainingLeaveService remainingLeaveService;

    /**
     * One time a day or midnight
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void cronJobSch() {
        log.info("before attendance initialization" + new Date());
        employeeAttendanceService.initializeAllAttendanceData();
        log.info("after attendance initialization" + new Date());
    }

    /**
     * One time a day or midnight for leave yearly
     */

    @Scheduled(cron = "0 0 2 * * *")
    public void updateLeave() {
        // for yearly update
        log.info("checking yearly schedular");
        remainingLeaveService.remainingLeave();
    }

    /**
     * daily leave update
     */
    @Scheduled(cron = "0 15 22 * * *")
    public void updateDailyAttendance() {
        // for daily update
        log.info("before checking daily schedular leave :" + new Date());
        remainingLeaveService.updateRemainingLeaveDaily();
        log.info("after checking daily schedular leave :" + new Date());

    }

    //    @Scheduled(cron = "0 15 20 * * *")
    public void updateDatabaseSchedular() {
        // for daily update
        log.info("checking attendance schedular");
        employeeAttendanceService.updateAttendanceSchedular();

    }

//    /**
//     * One time a day for updating leave
//     */
//    @Scheduled(cron = "0 0 1 * * *")
//    public void leaveUpdateJobSch(){
//       leaveRequestService.updateLeave();
//    }

    /**
     * runs in every 5 minutes
     */
//    @Scheduled(cron = "0 0/10 * * * *")
//        @Scheduled(cron = "0 10 13 * * *")
    public void employeeAttendanceSchedular() {
        employeeAttendanceService.saveEmployeeAttendance();
    }


}
