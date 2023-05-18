package com.gerp.usermgmt.model.office;

import com.gerp.shared.generic.api.AuditAbstract;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Getter
@Setter
@Table(name ="office_group_detail")
public class OfficeGroupDetail extends AuditAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_group_detail_seq_gen")
    @SequenceGenerator(name = "office_group_detail_seq_gen", sequenceName = "office_group_detail_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;
    @Column(columnDefinition = "VARCHAR(50)",name ="name_np" )
    private String nameNp;
    @Column(columnDefinition = "VARCHAR(50)",name ="name_en" )
    private String nameEn;


    private boolean savedByAdmin;
    @Column(columnDefinition = "VARCHAR(10)",name ="office_code" )
    private String officeCode;

    private String salutation;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "office_group_detail_id", foreignKey = @ForeignKey(name = "FK_office_group_detail"))
    private List<OfficeGroup> officeGroups;
}
