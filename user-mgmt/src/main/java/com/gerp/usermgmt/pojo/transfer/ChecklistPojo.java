package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChecklistPojo {
    @NotNull
    private int checkListId;
    @NotNull
    private Boolean status;

}
