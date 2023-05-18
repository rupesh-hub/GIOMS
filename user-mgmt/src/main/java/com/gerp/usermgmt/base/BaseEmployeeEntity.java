package com.gerp.usermgmt.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public abstract class BaseEmployeeEntity extends AuditActiveAbstract {


    @Column(name = "name_np")
    private String nameNp;

    @Column(name = "name_en")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    private String nameEn;

    @Column(name = "approved_date_np", columnDefinition = "VARCHAR(20)")
    private String approvedDateNp;

    private Boolean approved;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "approved_date_En")
    private LocalDate approvedDateEN;

    @Column(name = "approved_by")
    private String approvedBy;
}
