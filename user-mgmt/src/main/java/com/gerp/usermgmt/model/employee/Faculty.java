package com.gerp.usermgmt.model.employee;


import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "faculty")
public class Faculty extends BaseEmployeeEntity {

    @Id
    private String code;

    @Column(name = "short_name_np", length = 70)
    private String shortNameNp;

    @Column(name = "short_name_en", length = 70)
    private String shortNameEn;

    @Override
    public Serializable getId() {
        return code;
    }
}
