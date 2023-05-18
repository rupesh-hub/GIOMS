package com.gerp.dartachalani.service.digitalSignature.impl;

import com.gerp.dartachalani.dto.DigitalSignatureDto;
import com.gerp.dartachalani.emas.ws.ds.DSVerifyWSImplService;
import com.gerp.dartachalani.service.digitalSignature.GenerateHashService;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.dartachalani.service.digitalSignature.VerifySignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class GenerateHashServiceImpl implements GenerateHashService {

    @Autowired
    private VerifySignatureService service;

    @Override
    public DigitalSignatureDto generateHash(DigitalSignatureDto digitalSignatureDto) {

        try {
            String content = digitalSignatureDto.getContent().replaceAll("\r", "");

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            digitalSignatureDto.setHashValue(hexString.toString());
            return digitalSignatureDto;
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not generate Hash");
        }
    }

    @Override
    public VerificationInformation verify(DigitalSignatureDto digitalSignatureDto) {
        return service.verify(digitalSignatureDto.getContent(), digitalSignatureDto.getSignature(), true);
    }


}
