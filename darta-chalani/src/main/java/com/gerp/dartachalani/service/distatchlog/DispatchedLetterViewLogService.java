package com.gerp.dartachalani.service.distatchlog;

import com.gerp.dartachalani.dto.DispatchLetterDTO;
import com.gerp.dartachalani.dto.OtpRequestDto;
import com.gerp.dartachalani.dto.OtpResponseDto;
import com.gerp.dartachalani.dto.OtpVerifyDto;
import com.gerp.dartachalani.dto.sms.OtpResponse;

import java.io.IOException;

public interface DispatchedLetterViewLogService {

    OtpResponseDto sendOtp(OtpRequestDto otpRequestDto) throws IOException;

    Long verifyOtp(OtpVerifyDto otpVerifyDto);

    DispatchLetterDTO getDispatchByKey(String key, Long id);
}
