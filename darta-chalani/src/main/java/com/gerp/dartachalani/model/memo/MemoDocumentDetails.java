package com.gerp.dartachalani.model.memo;

import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "memo_document_details")
public class MemoDocumentDetails extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memo_document_details_seq_gen")
    @SequenceGenerator(name = "memo_document_details_seq_gen", sequenceName = "seq_memo_document_details", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_size")
    private Double documentSize;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "section_code")
    private String sectionCode;

    @Column(name = "editable")
    private Boolean editable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_id")
    private Memo memo;

}
