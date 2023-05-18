package com.gerp.attendance.model.leave;

import com.gerp.attendance.model.kaaj.KaajRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "leave_document")
@AllArgsConstructor
public class LeaveRequestDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_document_seq_gen")
    @SequenceGenerator(name = "leave_document_seq_gen", sequenceName = "leave_document_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;

    private Long documentId;

    private String documentName;

    private Double documentSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_detail_id")
    private LeaveRequestDetail leaveRequestDetail;

    public LeaveRequestDocument(Long documentId, String documentName, Double documentSize, LeaveRequestDetail leaveRequestDetail) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentSize = documentSize;
        this.leaveRequestDetail = leaveRequestDetail;
    }

    public LeaveRequestDocument(Long documentId, String documentName, Double documentSize) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentSize = documentSize;
    }
}
