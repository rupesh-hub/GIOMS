package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@DynamicUpdate
@Table(name = "previous_work_details")
public class PreviousWorkDetails extends AuditActiveAbstract {

    @Id
    @SequenceGenerator(name = "transfer_request_seq", sequenceName = "transfer_request_seq", allocationSize = 1)
    @GeneratedValue(generator = "transfer_request_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)", name = "office_code")
    private String officeCode;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)", name = "designation_code")
    private String designationCode;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)", name = "service_code")
    private String serviceCode;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)", name = "position_code")
    private String positionCode;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)", name = "region_code")
    private String regionCode;

    @Column( name = "from_date_en")
    private LocalDate FromDateEn;

    @Column( name = "to_date_en")
    private LocalDate ToDateEn;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "from_date_np")
    private String FromDateNp;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "to_date_np")
    private String ToDateNp;


}
