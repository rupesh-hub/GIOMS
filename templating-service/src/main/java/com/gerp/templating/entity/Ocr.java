package com.gerp.templating.entity;

import lombok.Data;

@Data
public class Ocr {

    private String review;

    private String requester_name;

    private String requester_position;

    private VerificationInformation verificationInformation;

    private String sectionLetter;

}
