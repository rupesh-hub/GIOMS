package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRemarksPojo {
    private Long id;
    private DetailPojo remarksGivenByEmployee;
    private String remarks;
}
