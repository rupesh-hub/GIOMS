//package com.gerp.attendance.model.approval;
//
//import com.gerp.shared.enums.TableEnum;
//import com.gerp.shared.generic.api.AuditActiveAbstract;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.DynamicUpdate;
//import org.hibernate.annotations.Type;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//
//@Data
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@DynamicUpdate
//@Builder
//@Table(name = "approval_table")
//public class ApprovalTable extends AuditActiveAbstract {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_request_seq_gen")
//    @SequenceGenerator(name = "leave_request_seq_gen", sequenceName = "seq_leave_request", initialValue = 1, allocationSize = 1)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    @NotNull
//    private TableEnum code;
//
//    @Column(columnDefinition = "VARCHAR(200)")
//    private String name;
//
//    @Lob
//    @Type(type = "org.hibernate.type.TextType")
//    private String description;
//
//    @NotNull
//    private Integer fiscalYear;
//}
//
