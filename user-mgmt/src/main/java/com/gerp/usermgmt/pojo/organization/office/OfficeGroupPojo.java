package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfficeGroupPojo {
    private int id;
    @NotNull
    @NotEmpty
    private String nameNp;

    @Transient
    private boolean savedByAdmin;

    private String nameEn;

    @NotEmpty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ExternalOfficePojo> office;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OfficePojo> officePojos;
}
