package com.gerp.tms.model.task;

/*
 * @project gerp-main
 * @author Diwakar

 */

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "task_document_details")

public class TaskDocumentDetails extends AuditAbstractTms {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_document_details_seq_gen")
    @SequenceGenerator(name = "project_document_details_seq_gen", sequenceName = "seq_project_document_details", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Long documentId;
    private String documentName;


}
