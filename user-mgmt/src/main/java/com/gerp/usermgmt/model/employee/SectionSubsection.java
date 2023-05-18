package com.gerp.usermgmt.model.employee;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import com.gerp.usermgmt.model.office.Office;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "section_subsection")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SectionSubsection extends BaseEmployeeEntity {

    @Id
    @SequenceGenerator(name = "section_seq", sequenceName = "section_seq", allocationSize = 1)
    @GeneratedValue(generator = "section_seq", strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(columnDefinition = "varchar(20)")
    private String code;

    @Column(name = "defined_code" , columnDefinition = "varchar(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String definedCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_code", foreignKey = @ForeignKey(name = "fk_office_section"), updatable = false)
    private Office office;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<SectionSubsection> sectionSubsections = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_id", foreignKey =
    @ForeignKey(name = "fk_self_section_subsection"))
    private SectionSubsection parent;

    @OneToMany(mappedBy = "sectionSubsection")
    @JsonBackReference
    private Collection<Employee> employees;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_en", columnDefinition = "VARCHAR(100)")
    private String shortNameEn;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_np", columnDefinition = "VARCHAR(100)")
    private String shortNameNp;

    @Column(name = "order_no")
    private Long orderNo;

    @Column(name = "room_no")
    private String roomNo;

    @Column(name = "darta_code")
    private String dartaCode;

    @Column(name = "chalani_code")
    private String chalaniCode;

    private String phone;
    private String fax;

    @Version
    @Column(columnDefinition = "int default 1")
    private int version;

    public SectionSubsection(Long id){
        this.id = id;
    }
}
