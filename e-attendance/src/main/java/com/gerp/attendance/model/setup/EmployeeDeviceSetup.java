package com.gerp.attendance.model.setup;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "employee_device_setup")
public class EmployeeDeviceSetup extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_device_setup_seq_gen")
    @SequenceGenerator(name = "employee_device_setup_seq_gen", sequenceName = "seq_employee_device_setup", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Integer empDeviceCode;

    @Column(name = "emp_code")
    private String empCode;

//    @ManyToOne
//    @JoinColumn(name = "attendance_device_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_EmployeeDeviceSetup_AttendanceDevice"))
//    private AttendanceDevice attendanceDevice;


}
