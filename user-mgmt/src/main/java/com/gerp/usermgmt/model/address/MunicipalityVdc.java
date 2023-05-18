package com.gerp.usermgmt.model.address;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "municipality_vdc")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MunicipalityVdc extends BaseEmployeeEntity {
    @Id
    private String code;

    @Override
    public Serializable getId() {
        return code;
    }
}
