package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@DynamicUpdate
@Table(name = "transfer_documents")
public class TransferDocuments extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "letter_documents_seq_gen")
    @SequenceGenerator(name = "letter_documents_seq_gen", sequenceName = "seq_letter_documents", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "document_id")
    private Long documentId;

    @Column(columnDefinition = "VARCHAR(100)",name = "name")
    private String name;

    @Column(columnDefinition = "VARCHAR(50)",name = "type")
    private String type;

    @Column(columnDefinition = "VARCHAR(10)",name = "size")
    private String size;

    public TransferDocuments(Long documentId, String name, String type,String size) {
        this.documentId = documentId;
        this.name = name;
        this.type = type;
        this.size = size;
    }
}
