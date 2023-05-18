package com.gerp.kasamu.model.committee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.kasamu.converter.AttributeEncryptor;
import com.gerp.kasamu.model.kasamu.KasamuMaster;
import com.gerp.shared.generic.api.AuditAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@DynamicUpdate
@Setter
@Getter
public class CommitteeIndicator extends AuditAbstract {

    @Id
    @GeneratedValue(generator = "committee_indicator_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "committee_indicator_seq_gen", sequenceName = "seq_committee_indicator", initialValue = 1, allocationSize = 1)
    private Long id;

//    @NotNull
//    @Column(nullable = false, columnDefinition = "TEXT")
////    @Convert(converter = AttributeEncryptor.class)
//    private String capacityType;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String secondMarks;
//
//    @NotNull
//    @Column(nullable = false)
////    @Convert(converter = AttributeEncryptor.class)
//    private String scoredLevel;

    @Convert(converter = AttributeEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String committeeIndicatorEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_master_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "FK_kasamu_master_committee_indicator"))
    @JsonIgnore
    private KasamuMaster kasamuMaster;
}
