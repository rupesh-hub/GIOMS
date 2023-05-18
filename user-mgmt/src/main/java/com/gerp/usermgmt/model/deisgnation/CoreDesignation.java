package com.gerp.usermgmt.model.deisgnation;


import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "core_designation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"}, name = "unique_core_designation_code"),
})

@EqualsAndHashCode(callSuper = true)
public class CoreDesignation extends BaseEmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "core_designation_seq_gen")
    @SequenceGenerator(name = "core_designation_seq_gen", sequenceName = "core_designation_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;

/*    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", foreignKey = @ForeignKey(name = "fk_class_core_designation"))
    private DesignationClass designationClass;*/

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
    private String code;

    private Integer level;

}
