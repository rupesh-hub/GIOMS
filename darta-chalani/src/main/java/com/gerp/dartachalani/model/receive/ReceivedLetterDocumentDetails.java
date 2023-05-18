package com.gerp.dartachalani.model.receive;

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
@DynamicUpdate
@Builder
@Table(name = "received_letter_document_details")
public class ReceivedLetterDocumentDetails extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "received_letter_document_details_seq_gen")
    @SequenceGenerator(name = "received_letter_document_details_seq_gen", sequenceName = "seq_received_letter_document_details", initialValue = 1, allocationSize = 1)
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
    @JoinColumn(name = "received_letter_id")
    private ReceivedLetter receivedLetter;

}
