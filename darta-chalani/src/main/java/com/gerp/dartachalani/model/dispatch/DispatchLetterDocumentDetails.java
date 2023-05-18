package com.gerp.dartachalani.model.dispatch;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
public class DispatchLetterDocumentDetails extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "received_letter_document_details_seq_gen")
    @SequenceGenerator(name = "received_letter_document_details_seq_gen", sequenceName = "seq_received_letter_document_details", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Long documentId;
    private String documentName;
    private Double sizeKB;

    @Column(name = "include")
    private Boolean include;

    @ManyToOne
    private DispatchLetter dispatchLetter;

}
