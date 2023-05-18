package com.gerp.kasamu.model.kasamu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.kasamu.converter.AttributeEncryptor;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Setter
@Getter
@DynamicUpdate
public class KasamuEvaluator extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "kasamu_evaluator_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "kasamu_evaluator_seq_gen", sequenceName = "seq_kasamu_evaluator", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @Column(nullable = false)
//    @Convert(converter = AttributeEncryptor.class)
    private String valuatorType;

    @Convert(converter = AttributeEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String kasamuEvaluatorEn;
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String type;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String fullMarks;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String scoredMarks;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String scoredLevel;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_master_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "FK_kasamu_master_detail"))
    @JsonIgnore
    private KasamuMaster kasamuMaster;

}
