package com.gerp.usermgmt.model.deisgnation;

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
@Table(name = "class_type")
@EqualsAndHashCode(callSuper = true)
public class ClassType extends BaseEmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_type_seq_gen")
    @SequenceGenerator(name = "class_type_seq_gen", sequenceName = "class_type_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;

}
