package com.gerp.attendance.model.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalTime;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "office_time_config",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"office_code"}, name = "unique_office_code")
        })
public class OfficeTimeConfig extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_time_config_seq_gen")
    @SequenceGenerator(name = "office_time_config_seq_gen", sequenceName = "seq_office_time_config", initialValue = 1, allocationSize = 1)
    private Integer id;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    private String officeCode;


    @JsonFormat(pattern = "mm:ss")
    @Column(name = "maximum_late_checkin", columnDefinition = "TIME")
    private LocalTime maximumLateCheckin;


    @JsonFormat(pattern = "mm:ss")
    @Column(name = "maximum_early_checkout", columnDefinition = "TIME")
    private LocalTime maximumEarlyCheckout;


    @Column(name = "allowed_limit")
    private Integer allowedLimit;



}
