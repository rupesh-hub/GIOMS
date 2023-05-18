package com.gerp.usermgmt.model.administrative;

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
@Table(name = "administrative_body" , uniqueConstraints = {
        @UniqueConstraint(columnNames = "name_np", name = "UNIQUE_name_np_administrative_body"),
        @UniqueConstraint(columnNames = "name_en", name = "UNIQUE_name_en_administrative_body"),
        @UniqueConstraint(columnNames = "code", name = "UNIQUE_code_administrative_body"),
})
@EqualsAndHashCode(callSuper = true)
public class AdministrativeBody extends BaseEmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "administrative_body_seq_gen")
    @SequenceGenerator(name = "administrative_body_seq_gen", sequenceName = "administrative_body_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administration_level_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_administration_level_administrative_body"))
    private AdministrationLevel administrationLevel;

    @Size(max = StringConstants.DEFAULT_CODE_SIZE)
    private String code;

}
