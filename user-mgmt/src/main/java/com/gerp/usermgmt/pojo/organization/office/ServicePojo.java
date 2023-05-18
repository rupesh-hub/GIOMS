package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.ServiceType;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.model.employee.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ServicePojo {
    private String code;
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String nameNp;
    private String nameEn;
    private String parentCode;
    private ServiceType serviceType;

    private ServicePojo parent;

    @JsonIgnore
   public Service getEntity(){
        Service service = new Service();
        service.setCode(this.getCode());
        return service;
    }
}
