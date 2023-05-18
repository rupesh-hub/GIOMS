package com.gerp.dartachalani.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OtpVerifyDto {
    private String key;

    private String otp;

    private String ipAddress;

    private Long id;
}
