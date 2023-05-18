package com.gerp.usermgmt.pojo.organization.office;

import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.enums.OrganisationCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganisationTypePojo {

    private Long id;

    @NotNull
    private String nameEn;

    @NotNull
    private String nameNp;

    @NotNull
    private OrganisationCategory organisationCategory;

    @NotNull
    private String prefix;
}
