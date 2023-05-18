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
@EqualsAndHashCode(callSuper = true)
@Table(name = "class", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"}, name = "unique_designation_class_code"),
})
public class DesignationClass extends BaseEmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_seq_gen")
    @SequenceGenerator(name = "class_seq_gen", sequenceName = "class_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
    private String code;
}
