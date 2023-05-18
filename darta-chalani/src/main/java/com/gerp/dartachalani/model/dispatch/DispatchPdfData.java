package com.gerp.dartachalani.model.dispatch;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "dispatch_pdf_data")
public class DispatchPdfData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_pdf_data_seq_gen")
    @SequenceGenerator(name = "dispatch_pdf_data_seq_gen", sequenceName = "seq_dispatch_pdf_data", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "pdf")
    @Type(type = "org.hibernate.type.TextType")
    private String pdf;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", foreignKey = @ForeignKey(name = "FK_dispatch_pdf_data"))
    private DispatchLetter dispatchLetter;

}
