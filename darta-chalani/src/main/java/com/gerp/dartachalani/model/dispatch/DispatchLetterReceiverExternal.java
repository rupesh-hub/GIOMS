package com.gerp.dartachalani.model.dispatch;

import com.gerp.shared.enums.BodarthaEnum;
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
@Table(name = "dispatch_letter_receiver_external")
public class DispatchLetterReceiverExternal extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_receiver_external_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_receiver_external_seq_gen", sequenceName = "seq_dispatch_letter_receiver_external", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "receiver_name", columnDefinition = "VARCHAR(250)")
    private String receiverName;

    @Column(name = "receiver_office_section_subsection", columnDefinition = "VARCHAR(250)")
    private String receiverOfficeSectionSubSection;

    @Column(name = "receiver_address", columnDefinition = "VARCHAR(250)")
    private String receiverAddress;

    @Column(name = "receiver_email", columnDefinition = "VARCHAR(50)")
    private String receiverEmail;

    @Column(name = "receiver_phone_no", columnDefinition = "VARCHAR(20)")
    private String receiverPhoneNumber;

    @Column(name = "dispatch_letter_type", columnDefinition = "VARCHAR(2)")
    private String dispatch_letter_type;

    @Column(name = "to_cc")
    private Boolean toCc;

    @Column(name = "sender_office_code")
    private String senderOfficeCode;

    @Column(name = "sender_pis_code")
    private String senderPisCode;

    @Column(name = "order_number")
    private Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_id")
    private DispatchLetter dispatchLetter;

    @Column(name = "bodartha_type")
    @Enumerated(EnumType.STRING)
    private BodarthaEnum bodarthaType;

    @Column(name = "salutation")
    private String salutation;

    @Column(name = "is_group_name")
    private Boolean isGroupName = Boolean.FALSE;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "remarks", length = 256)
    private String remarks;

}
