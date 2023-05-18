//package com.gerp.usermgmt.model.office;
//
//import com.gerp.shared.generic.api.AuditActiveAbstract;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.DynamicUpdate;
//
//import javax.persistence.*;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@DynamicUpdate
//@Getter
//@Setter
//@Table(name ="external_office")
//public class ExternalOffice extends AuditActiveAbstract {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "external_office_seq_gen")
//    @SequenceGenerator(name = "external_office_seq_gen", sequenceName = "external_office_seq_gen", initialValue = 1, allocationSize = 1)
//    private Integer id;
//    @Column(name = "name_en",columnDefinition = "VARCHAR(150)")
//    private String nameEn;
//    @Column(name = "name_np",columnDefinition = "VARCHAR(150)")
//    private String nameNp;
//    @Column(name = "address",columnDefinition = "VARCHAR(255)")
//    private String address;
//    @Column(name = "phone_number",columnDefinition = "VARCHAR(15)")
//    private String phoneNumber;
//    @Column(name = "email",columnDefinition = "VARCHAR(150)")
//    private String email;
//}
