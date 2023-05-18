package com.gerp.dartachalani.service.impl.sms;

import com.gerp.dartachalani.dto.sms.MessageBody;
import com.gerp.dartachalani.dto.sms.OtpResponse;
import com.gerp.dartachalani.service.sms.SendOTPService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SendOTPServiceImpl implements SendOTPService {
    @Value("${otp.token}")
    private String token;
    private String api = "https://newsms.doit.gov.np/api/sms";

    public SendOTPServiceImpl() {
    }

    public OtpResponse send(String mobileNumber, String message) {
        RestTemplate restTemplate = new RestTemplate();
        MessageBody obj = new MessageBody();
        obj.setMobile(mobileNumber);
        obj.setMessage(message);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + this.token);
        HttpEntity<OtpResponse> request = new HttpEntity(obj, headers);
        OtpResponse response = (OtpResponse)restTemplate.postForObject(this.api, request, OtpResponse.class, new Object[0]);
        if (response.getInvalid_number() != null && !response.getInvalid_number().isEmpty()) {
            System.out.println("Invalid phone number".concat(": ").concat(response.getMessage()));
            throw new RuntimeException("Invalid phone number");
        } else if (response.getErrors() != null) {
            System.out.println(response.getErrors().concat(": ").concat(response.getMessage()));
            throw new RuntimeException("Failed to send OTP please contact to DOIT administration");
        } else {
            return response;
        }
    }

    public OtpResponse send(List<String> mobileNumbers, String message) {
        String result = (String)mobileNumbers.stream().collect(Collectors.joining(","));
        return this.send(result, message);
    }
}