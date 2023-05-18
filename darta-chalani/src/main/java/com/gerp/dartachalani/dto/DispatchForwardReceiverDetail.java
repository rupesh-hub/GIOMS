package com.gerp.dartachalani.dto;

import lombok.Data;

@Data
public class DispatchForwardReceiverDetail {

    private String receiverOfficeCode;

    private String receiverSectionId;

    private String receiverPisCode;

    private Boolean within_organization;
    private boolean toReceiver;
    private boolean toCC = Boolean.FALSE;
}
