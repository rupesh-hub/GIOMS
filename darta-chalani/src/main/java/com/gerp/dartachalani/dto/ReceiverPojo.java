package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiverPojo {

    private String receiverOfficeCode;
    private String receiverSectionId;
    private String receiverPisCode;
    private String receiverDesignationCode;

}
