package com.gerp.dartachalani.model.template;

import com.gerp.dartachalani.enums.GeneratedTemplateType;
import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "template_log")
public class TemplateLog extends AuditActiveAbstract {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "template_log_seq_gen")
    @SequenceGenerator(name = "template_log_seq_gen", sequenceName = "seq_template_log", initialValue = 1, allocationSize = 1)
    private Long id;

    @Lob
    @Column(name = "template_en")
    private String templateEn;

    @Lob
    @Column(name = "template_np")
    private String templateNp;

    private Long officeTemplateId;

    @Column(name = "received_letter_id",columnDefinition = "int references received_letter(id)")
    private Long receivedLetterId;

    @Column(name = "dispatch_letter_id",columnDefinition = "int references dispatch_letter(id)")
    private Long dispatchLetterId;

    @Column(name = "memo_id",columnDefinition = "int references memo(id)")
    private Long memoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "generated_template_type", columnDefinition = "varchar(15)")
    private GeneratedTemplateType generatedTemplateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type",columnDefinition = "varchar(10)", nullable = false)
    private TemplateType templateType;
}
