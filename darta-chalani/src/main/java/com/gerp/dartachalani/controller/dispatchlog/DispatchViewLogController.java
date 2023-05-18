package com.gerp.dartachalani.controller.dispatchlog;

import com.gerp.dartachalani.dto.DispatchLetterDTO;
import com.gerp.dartachalani.dto.OtpRequestDto;
import com.gerp.dartachalani.dto.OtpResponseDto;
import com.gerp.dartachalani.dto.OtpVerifyDto;
import com.gerp.dartachalani.dto.sms.OtpResponse;
import com.gerp.dartachalani.service.distatchlog.DispatchedLetterViewLogService;
import com.gerp.shared.generic.controllers.BaseController;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


@RestController
@RequestMapping("/dispatch-view-log")
public class DispatchViewLogController extends BaseController {

    private final DispatchedLetterViewLogService dispatchedLetterViewLogService;

    public DispatchViewLogController(DispatchedLetterViewLogService dispatchedLetterViewLogService) {
        this.dispatchedLetterViewLogService = dispatchedLetterViewLogService;
    }


    /**
     * @param otpRequestDto Object of class {@link com.gerp.dartachalani.dto.OtpRequestDto} to send otp to verified mobile number
     * @param bindingResult object to verify the constraint on object
     * @return true if the otp send successfully
     * @throws IOException
     * @throws BindException
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody @Valid OtpRequestDto otpRequestDto, BindingResult bindingResult  ) throws IOException, BindException {

        if(!bindingResult.hasErrors()){
            OtpResponseDto otpResponse = dispatchedLetterViewLogService.sendOtp(otpRequestDto);
                return ResponseEntity.ok(successResponse("Otp successfully sent", otpResponse));
        } else {
            throw new BindException(bindingResult);
        }

    }


    /**
     * @param otpVerifyDto Object of the class {@link com.gerp.dartachalani.dto.OtpVerifyDto} to verify the otp
     * @param bindingResult Object of the validation class
     * @return chalani view object to verify on mobile
     * @throws BindException
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody @Valid OtpVerifyDto otpVerifyDto, BindingResult bindingResult) throws BindException {
        Long response;
        if(!bindingResult.hasErrors()){
             response  = dispatchedLetterViewLogService.verifyOtp(otpVerifyDto);
        } else {
            throw new BindException(bindingResult);
        }

        return ResponseEntity.ok(successResponse("Successfully verified", response));

    }

    @PostMapping("/get-token-for-qr-user")
    public ResponseEntity<?> test(){

        Keycloak instance = Keycloak.getInstance("http://103.69.127.118:9091/auth", "gerp-services", "qrapp", "Admin@123","QR-APP", "5bb42ac2-386b-4b3d-9017-42a577ac3f06");
        TokenManager tokenmanager = instance.tokenManager();
        String accessToken = tokenmanager.getAccessTokenString();

        return null;
    }

    @PostMapping("/get-chalani")
    public ResponseEntity<?> getChalani(@RequestParam("key") String key, @RequestBody OtpVerifyDto otpRequestDto){
        DispatchLetterDTO dispatchLetterDTO = dispatchedLetterViewLogService.getDispatchByKey(key, otpRequestDto.getId());
        return  ResponseEntity.ok(successResponse("Successfully verified", dispatchLetterDTO));
    }





}
