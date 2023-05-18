package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TransferModelPojo {
    private List<TransferPojo> transferRequest;
}
