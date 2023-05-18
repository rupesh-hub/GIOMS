package com.gerp.attendance.model.gayalKatti;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "gayal_katti_document_details")
public class GayalKattiDocumentDetails extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gayal_katti_document_details_seq_gen")
    @SequenceGenerator(name = "gayal_katti_document_details_seq_gen", sequenceName = "seq_gayal_katti_document_details", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Long documentId;
    private String documentName;
    private Double documentSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gayal_katti_id")
    private GayalKatti gayalKatti;

}
