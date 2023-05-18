package com.gerp.usermgmt.model.office;

import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.usermgmt.config.generator.OrganizationIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "office_template")
@EqualsAndHashCode(callSuper = true)
public class OfficeTemplate extends AuditActiveAbstract {
    
    @Id
    @SequenceGenerator(name = "office_template_seq", sequenceName = "office_template_seq", allocationSize = 1)
    @GeneratedValue(generator = "office_template_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "is_non_logo_template")
    private Boolean isNonLogoTemplate = Boolean.FALSE;


    @Lob
    @Column(name = "name_en")
    private String nameEn;

    @Lob
    @Column(name = "template_en")
    private String templateEn;

    @Lob
    @Column(name = "template_np")
    private String templateNp;

    @Lob
    @Column(name = "left_image")
    private String leftImage;

    @Lob
    @Column(name = "right_image")
    private String rightImage;

    @Lob
    @Column(name = "name_np")
    private String nameNp;

    @Enumerated(EnumType.STRING)
    private TemplateType type;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "is_global_template", columnDefinition = "boolean default false")
    private Boolean isGlobalTemplate;


    @ManyToOne
    @JoinColumn(name = "office_code" , foreignKey = @ForeignKey(name = "FK_office_template_office_code"))
    private Office office;

    @Column(name = "is_suspended")
    private Boolean isSuspended;

    @Column(name = "is_qr_template")
    private Boolean isQrTemplate;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "image_height")
    private String imageHeight;

}
