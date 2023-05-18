package com.gerp.dartachalani.model.dispatch;

import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dispatch_letter_receive_internal_detail")
@Getter
@Setter
public class DispatchLetterReceiveInternalDetail extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_receive_internal_detail_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_receive_internal_detail_seq_gen", sequenceName = "dispatch_letter_receive_internal_detail_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_internal", foreignKey = @ForeignKey(name = "FK_dispatchletterinternal_dispatchletterinternaldetail"))
    private DispatchLetterReceiverInternal withinOrganization;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status staus;
    private String description;
    private String dateNp;
    @Column(name = "date")
    private LocalDate date;
}
