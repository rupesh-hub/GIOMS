//package com.gerp.attendance.model.setup;
//
//import com.gerp.shared.generic.api.AuditActiveAbstract;
//import com.gerp.shared.utils.FieldErrorConstant;
//import com.gerp.shared.utils.StringConstants;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import org.hibernate.annotations.DynamicUpdate;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Pattern;
//import javax.validation.constraints.Size;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//@Data
//@Entity
//@RequiredArgsConstructor
//@DynamicUpdate
//@Table(name = "country", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_country_name", columnNames = "name_np"))
//public class Country extends AuditActiveAbstract {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_seq_gen")
//    @SequenceGenerator(name = "country_seq_gen", sequenceName = "seq_country", initialValue = 1, allocationSize = 1)
//    private Long id;
//
//    @NotNull
//    @NotBlank
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
//    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
//    @Column(name = "name_np", columnDefinition = "VARCHAR(50)")
//    private String nameNp;
//
//    @NotNull
//    @NotBlank
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
//    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
//    @Column(name = "name_en", columnDefinition = "VARCHAR(50)")
//    private String nameEn;
//}
