package com.gerp.dartachalani.dto;

import com.gerp.dartachalani.constant.SalutationTypeConstant;
import com.gerp.shared.utils.DataTypeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SalutationPojo {
    private Long id;
    @NotNull
    @NotEmpty
    private String pisCode;
    private int sectionId;
    private String customSalutationEn;
    private String customSalutationNp;

    private EmployeePojo employee;
    private SectionPojo section;


    private SalutationTypeConstant type;

    private String officeCode;
    private OfficePojo office;

    public void setType(String type) {
        if (DataTypeUtils.isInteger(type)){
            this.type = SalutationTypeConstant.getEnum(Integer.valueOf(type));
        }
        else {
            this.type = SalutationTypeConstant.valueOf(type);
        }
    }
}
