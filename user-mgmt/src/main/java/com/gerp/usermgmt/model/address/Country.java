package com.gerp.usermgmt.model.address;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "country")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Country extends BaseEmployeeEntity {


    @Id
    @Size(max = StringConstants.DEFAULT_CODE_SIZE)
    private String code;

    @Override
    public Serializable getId() {
        return code;
    }
}
