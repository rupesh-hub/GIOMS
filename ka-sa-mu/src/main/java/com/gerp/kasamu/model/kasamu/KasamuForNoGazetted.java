package com.gerp.kasamu.model.kasamu;

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
@Table(name = "kasamu_for_non_gazetted")
public class KasamuForNoGazetted extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "kasamu_for_no_gasetted_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "kasamu_for_no_gasetted_seq_gen", sequenceName = "seq_kasamu_for_no_gasetted", initialValue = 1, allocationSize = 1)
    private Long id;

    @Convert(converter = AttributeEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String kasamuNonGazettedEn;
//    @NotNull
//    @Column(nullable = false)
//    private String taskType;
//    private String description;
//    private String cost;
//    private String timeTaken;
//    private String quality;
//    private String quantity;
//    private String remarks;


}
