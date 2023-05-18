package com.gerp.usermgmt.model.employee;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_service_status")
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmployeeServiceStatus extends BaseEmployeeEntity {

    @Id
    private String code;

    @Column(name = "defined_code" , columnDefinition = "varchar(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String definedCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_50)
    @Column(name = "short_name_en", columnDefinition = "VARCHAR(50)")
    private String shortNameEn;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_np", columnDefinition = "VARCHAR(100)")
    private String shortNameNp;

    @Column(name = "order_no")
    private Long orderNo;

    @Override
    public Serializable getId() {
        return code;
    }

    public EmployeeServiceStatus(String code) {
        this.code = code;
    }
}
