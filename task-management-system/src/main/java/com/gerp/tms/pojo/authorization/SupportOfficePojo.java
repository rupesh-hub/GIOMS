package com.gerp.tms.pojo.authorization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportOfficePojo  {

    private Integer id;
    private String pisCode;
    private String supportingPisOfficeCode;
}
