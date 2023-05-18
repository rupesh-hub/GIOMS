package com.gerp.attendance.generator;

import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentGenerator {

    String leavePolicyId;
    String fromDate;
    String toDate;
    String pisCode;
    String approvalPisCode;
    String code;
    static final String  separator = ":";

   public String getContentFromData() {
        return code+approvalPisCode+pisCode;
   }

}
