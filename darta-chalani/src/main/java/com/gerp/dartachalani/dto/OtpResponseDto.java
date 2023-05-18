package com.gerp.dartachalani.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OtpResponseDto {

    private LocalDateTime startTime;

    private LocalDateTime expiryTime;

    private String mobileNumber;
}
