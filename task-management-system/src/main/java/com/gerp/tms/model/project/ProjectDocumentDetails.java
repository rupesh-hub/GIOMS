package com.gerp.tms.model.project;

/*
 * @project gerp-main
 * @author jitesh

 */

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
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
@Table(name = "project_document_details")

public class ProjectDocumentDetails extends AuditAbstractTms {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_document_details_seq_gen")
    @SequenceGenerator(name = "project_document_details_seq_gen", sequenceName = "seq_project_document_details", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Long documentId;
    private String documentName;


}
