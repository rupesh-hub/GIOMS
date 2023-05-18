package com.gerp.dartachalani.model.dispatch;


import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "dispatch_letter_update_history")
public class DispatchLetterUpdateHistory extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_letter_update_history_seq_gen")
    @SequenceGenerator(name = "dispatch_letter_update_history_seq_gen", sequenceName = "seq_dispatch_letter_update_history", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "designation_code")
    private String designationCode;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "content")
    private String content;

    @Column(name = "subject", columnDefinition = "VARCHAR(5000)")
    private String subject;

    @Column(name = "signature_data")
    @Type(type = "org.hibernate.type.TextType")
    private String signatureData;

    @Column(name = "signature_is_active")
    private Boolean signatureIsActive;

    @Column(name = "hash_content")
    private String hashContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_letter_id")
    private DispatchLetter dispatchLetter;

}
