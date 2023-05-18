package com.gerp.usermgmt.model.employee;

import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "education_level")
public class EducationLevel extends BaseEmployeeEntity {

    @Id
    private String code;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<EducationLevel> educationLevels = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_education_level", foreignKey =
    @ForeignKey(name = "fk_self_education_level"))
    private EducationLevel parent;


    @Column(name = "short_name_np", length = 40)
    private String shortNameNp;

    @Column(name = "short_name_en", length = 40)
    private String shortNameEn;

    @Column(name = "order_no")
    private Long orderNo;


    @Column(name = "entered_by")
    private String enteredBy;

    @Override
    public Serializable getId() {
        return code;
    }
}
