package com.gerp.dartachalani.service.impl.dispatchlog;


import com.gerp.dartachalani.dto.DispatchLetterDTO;
import com.gerp.dartachalani.dto.OtpRequestDto;
import com.gerp.dartachalani.dto.OtpResponseDto;
import com.gerp.dartachalani.dto.OtpVerifyDto;
import com.gerp.dartachalani.enums.IpErrorEnum;
import com.gerp.dartachalani.model.dispatch.DispatchedLetterViewLog;
import com.gerp.dartachalani.repo.dipatchlog.DispatchedLetterViewLogRepo;
import com.gerp.dartachalani.service.DispatchLetterService;
import com.gerp.dartachalani.service.distatchlog.DispatchedLetterViewLogService;
import com.gerp.dartachalani.service.sms.SendOTPService;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.utils.IdEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@Transactional
public class DispatchedLetterViewLogServiceImpl implements DispatchedLetterViewLogService {


    private final DispatchedLetterViewLogRepo dispatchedLetterViewLogRepo;

    private final DispatchLetterService dispatchLetterService;

    private final SendOTPService otpService;

    public DispatchedLetterViewLogServiceImpl(DispatchedLetterViewLogRepo dispatchedLetterViewLogRepo,
                                              DispatchLetterService  dispatchLetterService,
                                              SendOTPService otpService
    ) {
        this.dispatchedLetterViewLogRepo = dispatchedLetterViewLogRepo;
        this.dispatchLetterService = dispatchLetterService;
        this.otpService = otpService;
    }


    @Override
    public OtpResponseDto sendOtp(OtpRequestDto otpRequestDto) throws IOException {
        long dispatchId;
        String decrypt;
        try {
             decrypt =IdEncryptor.decrypt(otpRequestDto.getKey());
        } catch (Exception e) {
            throw new CustomException("Failed to decrypt the key");
        }
        dispatchId = Long.parseLong(decrypt);
        String otp = generateOtp();
        DispatchedLetterViewLog dispatchedLetterViewLog = new DispatchedLetterViewLog();

        try {
            String ipAddress = getPublicIp();
            dispatchedLetterViewLog.setIpAddress(ipAddress);
        }catch (Exception ex){
            dispatchedLetterViewLog.setIpAddress(IpErrorEnum.ERROR_IP.toString());
        }
//        if(otpRequestDto.getLatitude() == null || otpRequestDto.getLongitude() ==null) throw new CustomException("Geo location must be send");



        System.out.println(otp);
        dispatchedLetterViewLog.setOtp(otp);
        dispatchedLetterViewLog.setLatitude(otpRequestDto.getLatitude());
        dispatchedLetterViewLog.setLongitude(otpRequestDto.getLongitude());
        dispatchedLetterViewLog.setEncryptedKey(otpRequestDto.getKey());
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(2);
        dispatchedLetterViewLog.setOtpExpiryTime(Timestamp.valueOf(localDateTime));
        dispatchedLetterViewLog.setMobile(otpRequestDto.getMobile());
        try {
            dispatchedLetterViewLogRepo.deactivateAllOtp(otpRequestDto.getMobile(), otpRequestDto.getKey());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        try {
            dispatchedLetterViewLogRepo.save(dispatchedLetterViewLog);
        } catch (Exception ex){
            throw new CustomException("Error saving the letter view log");
        }
        // todo send to mobile number

        otpService.send(otpRequestDto.getMobile(), otp + " is your OTP to view chalani. " );


        OtpResponseDto otpResponseDto = new OtpResponseDto();
        otpResponseDto.setExpiryTime(LocalDateTime.now().plusMinutes(2));
        otpResponseDto.setMobileNumber(dispatchedLetterViewLog.getMobile());

        return otpResponseDto;

    }

    private String generateOtp(){
        long time= System.currentTimeMillis();
        String timeStamp = String.valueOf(time);

        return timeStamp.substring(timeStamp.length() -6 , timeStamp.length());
    }

    private String getPublicIp() throws IOException {
        URL url = new URL("https://api.ipify.org/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String ipAddress = in.readLine();
        return ipAddress;
    }

    /**
     * @param otpRequestDto
     * @return
     */
    @Override
    public Long verifyOtp(OtpVerifyDto otpRequestDto) {

        DispatchedLetterViewLog dispatchedLetterViewLog = dispatchedLetterViewLogRepo.findByEncryptedKeyAndOtp(otpRequestDto.getKey(), otpRequestDto.getOtp());
        if(dispatchedLetterViewLog == null) throw new CustomException("Invalid OTP");
        if(dispatchedLetterViewLog.getIsVerified() !=null && dispatchedLetterViewLog.getIsVerified().equals(Boolean.TRUE)) throw new CustomException("OTP already used");
        if(!dispatchedLetterViewLog.getOtp().equals(otpRequestDto.getOtp()) || dispatchedLetterViewLog.getOtpExpiryTime().toLocalDateTime().isBefore(LocalDateTime.now())) throw new CustomException("Otp not match or expired");

        try {
            String ipAddress = getPublicIp();
            otpRequestDto.setIpAddress(ipAddress);
        }catch (Exception ex){
            otpRequestDto.setIpAddress(IpErrorEnum.ERROR_IP.toString());
        }

        if(!otpRequestDto.getIpAddress().equals(dispatchedLetterViewLog.getIpAddress())  && !otpRequestDto.getIpAddress().equals(IpErrorEnum.ERROR_IP.toString())) throw new CustomException("you switched the network vulnerability may exist");


        try {
            dispatchedLetterViewLog.setIsVerified(true);
            dispatchedLetterViewLogRepo.save(dispatchedLetterViewLog);
        } catch ( Exception ex){
            throw new CustomException("error saving the dispatched view log");
        }
        return dispatchedLetterViewLog.getId();
    }

    @Override
    public DispatchLetterDTO getDispatchByKey(String key, Long id) {

        long dispatchId;
        String decrypted;
       try {
           key =key.replace(" ", "+");
           decrypted  = IdEncryptor.decrypt(key);
        } catch (Exception e) {
            throw new CustomException("Failed to decrypt the key");
        }
       dispatchId = Long.parseLong(decrypted);
        DispatchedLetterViewLog dispatchedLetterViewLog = dispatchedLetterViewLogRepo.findById(id).orElseThrow( () ->  new CustomException("View letter log not found"));
        if(dispatchedLetterViewLog.getIsSeen()){
            throw new CustomException("Generate new otp to view again");
        }
        dispatchedLetterViewLog.setIsSeen(true);
        dispatchedLetterViewLogRepo.save(dispatchedLetterViewLog);

        return dispatchLetterService.getDispatchLetterByIdForQr(dispatchId, null, dispatchedLetterViewLog.getMobile());
    }
}
