package com.gerp.usermgmt.model.office;

import com.gerp.shared.generic.api.AuditAbstract;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Getter
@Setter
@Table(name ="office_group")
public class OfficeGroup extends AuditAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_group_seq_gen")
    @SequenceGenerator(name = "office_group_seq_gen", sequenceName = "office_group_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(10)",name ="office_code" )
    private String code;

    @Column(name = "name_en",columnDefinition = "VARCHAR(150)")
    private String nameEn;

    @Column(name = "section_subSection_id")
    private Long sectionSubSectionId;

    @Column(name = "name_np",columnDefinition = "VARCHAR(150)")
    private String nameNp;
    @Column(name = "address",columnDefinition = "VARCHAR(255)")
    private String address;
    @Column(name = "phone_number",columnDefinition = "VARCHAR(15)")
    private String phoneNumber;
    @Column(name = "email",columnDefinition = "VARCHAR(150)")
    private String email;
    @Column(columnDefinition = "VARCHAR(20)",name ="type" )
    private String type;
    @Column(columnDefinition = "VARCHAR(255)",name ="section_name" )
    private String sectionName;
    private Integer ordering;
}
