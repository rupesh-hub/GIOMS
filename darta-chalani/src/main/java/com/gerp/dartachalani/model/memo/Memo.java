package com.gerp.dartachalani.model.memo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Entity
@Data
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "memo")
public class Memo extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memo_seq_gen")
    @SequenceGenerator(name = "memo_seq_gen", sequenceName = "seq_memo", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "subject", columnDefinition = "VARCHAR(500)")
    private String subject;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    private Long documentMasterId;

    @Column(name = "is_draft")
    private Boolean isDraft;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    private String officeCode;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(20)")
    private String pisCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "designation_code")
    private String designationCode;

    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    private String fiscalYearCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "pdf")
    @Type(type = "org.hibernate.type.TextType")
    private String pdf;

    @Column(name = "signature")
    @Type(type = "org.hibernate.type.TextType")
    private String signature;

    @Column(name = "signature_is_active")
    private Boolean signatureIsActive;

    @Column(name = "hash_content")
    private String hashContent;

    @Column(name = "tippani_no")
    private Long tippaniNo;

    @Column(name = "is_important")
    private Boolean isImportant;

    @Column(name = "last_modified_date")
    private Timestamp lastModifiedDate;

    @Column(name = "last_modified_date_imp")
    private Timestamp lastModifiedDateImp;

    @Column(name = "template_header_id")
    private Long templateHeaderId;

    @Column(name = "template_footer_id")
    private Long templateFooterId;

    @Column(name = "is_archive")
    private Boolean isArchive = Boolean.FALSE;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_memo"))
    private Memo memo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_id", foreignKey = @ForeignKey(name = "FK_memo_document_detail"))
    @JsonIgnore
    private Collection<MemoDocumentDetails> memoDocumentDetails;
}
