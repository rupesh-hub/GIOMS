package com.gerp.usermgmt.model.address;

import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "province")
public class Province extends BaseEmployeeEntity {

    @Id
    @SequenceGenerator(name = "province_seq", sequenceName = "province_seq", allocationSize = 1)
    @GeneratedValue(generator = "province_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    private String code;

    @Override
    public Serializable getId() {
        return code;
    }
}
