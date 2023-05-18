package com.gerp.dartachalani.model.kasamu;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KasamuDocumentDetails extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kasamu_document_details_seq_gen")
    @SequenceGenerator(name = "kasamu_document_details_seq_gen", sequenceName = "seq_kasamu_document_details", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_size")
    private Double documentSize;

    @Column(name = "is_main")
    private Boolean isMain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_id")
    private Kasamu kasamu;
}
