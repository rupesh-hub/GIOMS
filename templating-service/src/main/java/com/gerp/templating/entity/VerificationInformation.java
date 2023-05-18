package com.gerp.templating.entity;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class VerificationInformation {
    private HttpStatus status;

    private String message;

    private String signature_name; //commonName

    private String email;

    private String organisation;

    private String locality;

    private String state;

    private String issuerName;
}
