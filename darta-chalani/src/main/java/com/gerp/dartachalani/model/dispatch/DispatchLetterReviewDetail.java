package com.gerp.dartachalani.model.dispatch;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.map.repository.config.EnableMapRepositories;

import javax.persistence.*;

@Entity
@Table(name = "dispatch_letter_review_detail")
@Getter
@Setter
public class DispatchLetterReviewDetail extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_review_detail_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_review_detail_seq_gen", sequenceName = "seq_dispatch_review_detail_letter", initialValue = 1, allocationSize = 1)
    private Long id;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
            @JoinColumn(name = "dispatch_letter_review_id", foreignKey = @ForeignKey(name = "FK_dispatchletterreview_dispatchletterreviewdetail"))
    private DispatchLetterReview dispatchLetterReview;
    private String staus;
    private String description;

}
