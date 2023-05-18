package com.gerp.dartachalani.dto.template;

import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TippaniContent {

    private String content;

    private SenderEmployeeDetail senderEmployeeDetail;

    private VerificationInformation verificationInformation;

}
