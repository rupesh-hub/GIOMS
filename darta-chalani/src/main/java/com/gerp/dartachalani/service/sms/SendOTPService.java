package com.gerp.dartachalani.service.sms;

import com.gerp.dartachalani.dto.sms.OtpResponse;

import java.util.List;

public interface SendOTPService {
    OtpResponse send(String mobileNumber, String message);

    OtpResponse send(List<String> mobileNumber, String message);
}