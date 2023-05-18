package com.gerp.usermgmt.model.administrative;

import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "administration_level", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"}, name = "unique_administration_level_code"),
})
@EqualsAndHashCode(callSuper = true)
public class AdministrationLevel extends BaseEmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "administration_level_seq_gen")
    @SequenceGenerator(name = "administration_level_seq_gen", sequenceName = "administration_level_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(nullable = false, length = StringConstants.DEFAULT_MAX_SIZE_6)
    private String code;

}
