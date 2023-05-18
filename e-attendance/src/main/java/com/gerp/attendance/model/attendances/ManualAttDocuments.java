package com.gerp.attendance.model.attendances;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "manual_att_documents")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
@DynamicUpdate
public class ManualAttDocuments extends AuditActiveAbstract {
    @Id
    @SequenceGenerator(name = "manual_att_documents_seq", sequenceName = "manual_att_documents_seq", allocationSize = 1)
    @GeneratedValue(generator = "manual_att_documents_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long documentId;
    private String documentName;
    private Double documentSize;
}
