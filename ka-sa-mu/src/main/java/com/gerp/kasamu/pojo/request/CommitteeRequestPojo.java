package com.gerp.kasamu.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CommitteeRequestPojo {

    @JsonIgnore
    private Long id;

    @NotNull
    private String pisCode;

    @NotNull
    private String officeCode;

}
