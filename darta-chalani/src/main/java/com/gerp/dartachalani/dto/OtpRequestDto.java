package com.gerp.dartachalani.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OtpRequestDto {

    @NotNull(message = "Key must not be null")
    private String key;

    private String ipAddress;

    private String latitude;

    private String longitude;

    private String otp;

    @NotNull(message = "Mobile number must not be null")
    private String mobile;


}
