package com.gerp.kasamu.model.kasamu;

import com.gerp.kasamu.converter.AttributeEncryptor;
import com.gerp.shared.generic.api.AuditAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@DynamicUpdate
@Entity
@Getter
@Setter
public class KasamuDetail extends AuditAbstract {

    @Id
    @GeneratedValue(generator = "kasamu_detail_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "kasamu_detail_seq_gen", sequenceName = "seq_kasamu_detail", initialValue = 1, allocationSize = 1)
    private Long id;

    @Convert(converter = AttributeEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String kasamuDetailEn;
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String taskType;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String task;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String estimation;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String semiannualTarget;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String annualTarget;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String achievement;
//
//    @Column(columnDefinition ="TEXT")
////    @Convert(converter = AttributeEncryptor.class)
//    private String remarks;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "kasamu_master_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "FK_kasamu_master_detail"))
//    private KasamuMaster kasamuMaster;
}
