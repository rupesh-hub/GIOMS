package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class TransferDecisionPojo {
    @NotNull
    private Long id;
    @NotNull
    @NotEmpty
    private String remarks;
    @NotNull
    private Boolean approved;
    private String pisCode;
    private Set<String> sendToOffices;
}
