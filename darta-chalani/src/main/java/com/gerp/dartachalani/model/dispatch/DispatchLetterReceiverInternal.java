package com.gerp.dartachalani.model.dispatch;

import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.enums.BodarthaEnum;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "dispatch_letter_receiver_internal")
public class DispatchLetterReceiverInternal extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_receiver_internal_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_receiver_internal_seq_gen", sequenceName = "seq_dispatch_letter_receiver_internal", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "receiver_office_code", columnDefinition = "VARCHAR(20)")
    private String receiverOfficeCode;

    @Column(name = "receiver_section_id")
    private String receiverSectionId;

    @Column(name = "receiver_section_name")
    private String receiverSectionName;

    @Column(name = "receiver_pis_code", columnDefinition = "VARCHAR(20)")
    private String receiverPisCode;

    @Column(name = "receiver_designation_code")
    private String receiverDesignationCode;

    private Boolean within_organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_id")
    private DispatchLetter dispatchLetter;

    private Boolean toReceiver;

    private Boolean toCC;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status completion_status = Status.IP;

    private String description;

    @Column(name = "sender_section_id")
    private String senderSectionId;

    @Column(name = "sender_pis_code")
    private String senderPisCode;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(name = "bodartha_type")
    @Enumerated(EnumType.STRING)
    private BodarthaEnum bodarthaType;

    @Column(name = "salutation")
    private String salutation;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "is_group_name")
    private Boolean isGroupName = Boolean.FALSE;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "is_section_name")
    private Boolean isSectionName;

    @Column(name = "remarks", length = 256)
    private String remarks;

    @Column(name = "is_transferred")
    private Boolean isTransferred = Boolean.FALSE;
}
