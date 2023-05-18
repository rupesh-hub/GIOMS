package com.gerp.attendance.model.leave;

import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "kaaj_document_details")
public class KaajRequestDocumentDetails extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kaaj_document_details_seq_gen")
    @SequenceGenerator(name = "kaaj_document_details_seq_gen", sequenceName = "seq_kaaj_document_details", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Long documentId;
    private String documentName;
    private Double documentSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kaaj_request_id")
    private KaajRequest kaajRequest;


}
