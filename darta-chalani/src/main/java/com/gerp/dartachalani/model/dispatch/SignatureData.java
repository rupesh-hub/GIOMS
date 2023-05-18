package com.gerp.dartachalani.model.dispatch;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "signature_data")
public class SignatureData extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "signature_data_seq_gen")
    @SequenceGenerator(name = "signature_data_seq_gen", sequenceName = "seq_signature_data", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "designation_code")
    private String designationCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "signature")
    @Type(type = "org.hibernate.type.TextType")
    private String signature;

    @Column(name = "signature_is_active")
    private Boolean signatureIsActive;

    @Column(name = "include_section_id")
    private Boolean includeSectionId;

    @Column(name = "included_section_id")
    private String includedSectionId;

    @Column(name = "hash_content")
    private String hashContent;

    @Column(name = "include_section_in_letter")
    private Boolean includeSectionInLetter;

    @OneToOne
    @JoinColumn(name = "dispatch_letter_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_dispatch_letter__signature_data"))
    @JsonBackReference
    private DispatchLetter dispatchLetter;

}
