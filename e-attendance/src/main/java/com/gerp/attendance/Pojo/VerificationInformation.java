package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.Status;
import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationInformation {

    private HttpStatus status;

    private Status documentAction;

    private String message;

    private String signature_name; //commonName

    private String email;

    private String organisation;

    private String locality;

    private String state;

    private String issuerName;

    private Boolean isSignatureExpire = Boolean.FALSE;

}
