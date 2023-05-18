package com.gerp.dartachalani.model.memo;

import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "memo_content")
public class MemoContent extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memo_content_seq_gen")
    @SequenceGenerator(name = "memo_content_seq_gen", sequenceName = "seq_memo_content", initialValue = 1, allocationSize = 1)
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(20)")
    private String pisCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "designation_code")
    private String designationCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    private String officeCode;

    @Column(name = "editable")
    private Boolean editable;

    @Column(name = "is_external")
    private Boolean isExternal;

    @Column(name = "is_external_editable")
    private Boolean isExternalEditable;

    @Column(name = "signature")
    @Type(type = "org.hibernate.type.TextType")
    private String signature;

    @Column(name = "signature_is_active")
    private Boolean signatureIsActive;

    @Column(name = "hash_content")
    private String hashContent;

    @ManyToOne
    @JoinColumn(name = "memo_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo__content"))
    private Memo memo;

}
