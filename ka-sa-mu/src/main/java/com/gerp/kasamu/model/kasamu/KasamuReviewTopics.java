package com.gerp.kasamu.model.kasamu;

import com.gerp.kasamu.converter.AttributeEncryptor;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
public class KasamuReviewTopics extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "Kasamu_review_topics_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "Kasamu_review_topics_seq_gen", sequenceName = "seq_Kasamu_review_topics", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    private String description;


    @Column(columnDefinition = "VARCHAR(20)")
    private String kasamuClass;

    @NotNull
    @Column(columnDefinition = "VARCHAR(20)")
    private String reviewType;

    public KasamuReviewTopics(@NotNull String description, String kasamuClass, @NotNull String reviewType) {
        this.description = description;
        this.kasamuClass = kasamuClass;
        this.reviewType = reviewType;
    }
}
