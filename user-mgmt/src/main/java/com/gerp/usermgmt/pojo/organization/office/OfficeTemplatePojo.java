package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.TemplateType;
import com.gerp.shared.pojo.OfficeMinimalPojo1;
import com.gerp.usermgmt.constant.OfficeConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class OfficeTemplatePojo {

    private Long id;

    private Long parentId;

    @JsonIgnore
    private Boolean isDefault;

    private String templateEn;

    private String templateNp;

    private Boolean isNonLogoTemplate;

    private String nameEn;

    private String nameNp;

    private String leftImage;

    private String rightImage;

    @NotNull(message = "Template type cannot be null")
    private TemplateType type;

    private Boolean isActive = Boolean.FALSE;

    private String officeCode;

    private OfficeMinimalPojo1 office;

    private Boolean isQrTemplate;

    private Boolean isSuspended = Boolean.FALSE;

//    private Map<String, Object>

    List<OfficePojo> hierarchyOffices;

    private String imageHeight;

    public OfficeTemplatePojo(Long id, String nameEn, String nameNp, String officeCode, Boolean isActive, TemplateType type,
       Boolean isNonLogoTemplate, String leftImage , String rightImage,Boolean isQrTemplate, String imageHeight) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameNp = nameNp;
        this.officeCode = officeCode;
        this.isActive = isActive;
        this.type = type;
        this.isNonLogoTemplate = isNonLogoTemplate;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.isQrTemplate = isQrTemplate;
        this.imageHeight = imageHeight;
    }
    public OfficeTemplatePojo(Long id, String nameEn, String nameNp, String officeCode, Boolean isActive, TemplateType type,
                              String templateEn, String templateNp,String leftImage, String rightImage, Boolean isNonLogoTemplate,Boolean isQrTemplate,
                              String imageHeight) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameNp = nameNp;
        this.officeCode = officeCode;
        this.isActive = isActive;
        this.type = type;
        this.templateEn = templateEn;
        this.templateNp = templateNp;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.isNonLogoTemplate = isNonLogoTemplate;
        this.isQrTemplate = isQrTemplate;
        this.imageHeight =  imageHeight;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode =  Boolean.TRUE.equals(this.getIsDefault()) ? OfficeConstants.ADMIN_OFFICE : officeCode;
    }
}
