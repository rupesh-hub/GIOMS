package com.gerp.dartachalani.dto;

import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ocr {

    private String review;

    private String requester_name;

    private String requester_position;

    private VerificationInformation verificationInformation;

    private String sectionLetter;

}
