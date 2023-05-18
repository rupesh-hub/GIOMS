package com.gerp.shared.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.SalutationTypeConstant;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalutationPojo {
    private long id;
    @NotNull
    @NotEmpty
    private String pisCode;
    private int sectionId;
    private String customSalutationEn;
    private String customSalutationNp;
    @NotNull
    @NotEmpty
    private SalutationTypeConstant type;
    private SalutationDetailsPojo section;
    private SalutationDetailsPojo employee;
    private SalutationDetailsPojo office;
    private SalutationDetailsPojo externalOffice;

}
