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
//import java.io.Serializable;
//
///**
// * @author Rohit Sapkota
// * @version 1.0.0
// * @since 1.0.0
// */
//@Data
//@Entity
//@RequiredArgsConstructor
//@DynamicUpdate
//@Table(name = "religion", uniqueConstraints = @UniqueConstraint(name = "UNIQUE_religion", columnNames = "name_np"))
//public class Religion extends AuditActiveAbstract {
//    @Id
//    private String code;
//
//    @NotNull
//    @NotBlank
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
//    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
//    @Column(name = "name_np", columnDefinition = "VARCHAR(200)")
//    private String nameNp;
//
//    @NotNull
//    @NotBlank
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
//    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
//    @Column(name = "name_en", columnDefinition = "VARCHAR(200)")
//    private String nameEn;
//
//    @Override
//    public Serializable getId() {
//        return code;
//    }
//}
