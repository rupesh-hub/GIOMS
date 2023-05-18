package com.gerp.dartachalani.service.digitalSignature.impl;

import com.gerp.dartachalani.emas.ws.ds.DSVerifyWS;
import com.gerp.dartachalani.emas.ws.ds.DSVerifyWSImplService;
import com.gerp.dartachalani.emas.ws.ds.VerifyDetached;
import com.gerp.dartachalani.service.digitalSignature.VerificationInformation;
import com.gerp.dartachalani.service.digitalSignature.VerifySignatureService;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class VerifySignatureServiceImpl implements VerifySignatureService {

    @Override
    @Async
    public VerificationInformation verify(String content, String signature, Boolean isHashed) {
        DSVerifyWSImplService service = new DSVerifyWSImplService();
        DSVerifyWS port = service.getDSVerifyWSImplPort();

        String contentAfterRemoveLineBreak = content.replaceAll("\r", "");

        String contentToVerify;

        if (isHashed) {
            contentToVerify = generateHash(contentAfterRemoveLineBreak);
        } else {
            contentToVerify = generateBase64(contentAfterRemoveLineBreak);
        }

        VerifyDetached verify = new VerifyDetached();
        verify.setArg0("pkcs7");
        verify.setArg1(contentToVerify);
        verify.setArg2(signature);
        verify.setArg3("xml");

        final String[] response = {null};

        Thread thread = new Thread(() -> response[0] = port.verifyDetached(verify.getArg0(), verify.getArg1(), verify.getArg2(), verify.getArg3()));
        thread.start();
        long endTimeMillis = System.currentTimeMillis() + 15000;
        while (thread.isAlive()) {
            try {
                System.out.println("Waiting...");
                if (System.currentTimeMillis() > endTimeMillis) {
                    break;
                }
                Thread.sleep(500);
            } catch (InterruptedException t) {
            }
        }

        JSONObject jsonObject = null;
        try {
            if (response[0] != null)
                jsonObject = XML.toJSONObject(response[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VerificationInformation information = new VerificationInformation();

        try {
            if (jsonObject != null && jsonObject.getJSONObject("verificationProfile").get("status").equals("success")) {
                information = getVerifificationInformation(jsonObject);
            } else if (jsonObject != null && jsonObject.getJSONObject("verificationProfile").get("status").equals("failed")) {
                if (jsonObject.getJSONObject("verificationProfile").getJSONObject("transaction").get("data").equals(contentToVerify)) {
                    information = getVerifificationInformation(jsonObject);
                } else {
                    information.setStatus(HttpStatus.EXPECTATION_FAILED);
                    information.setMessage("Signature verification process failed");
                }
                information.setIsSignatureExpire(Boolean.TRUE);
            } else if (jsonObject != null && jsonObject.getJSONObject("verificationProfile").get("status").equals("exception")) {
                information.setStatus(HttpStatus.EXPECTATION_FAILED);
                information.setMessage("Signature verification process failed");
            } else {
                information = null;
            }
        } catch (Exception e) {
            information = null;
        }

        return information;
    }

    private VerificationInformation getVerifificationInformation(JSONObject soapDatainJsonObject) {
        VerificationInformation information = new VerificationInformation();
        information.setMessage("success");
        information.setStatus(HttpStatus.OK);
        information.setSignature_name(soapDatainJsonObject.getJSONObject("verificationProfile")
                .getJSONObject("transaction")
                .getJSONObject("transactionStatus")
                .getJSONObject("transactionStatusDetails")
                .getJSONObject("certificate")
                .get("commonName").toString());

        information.setEmail(soapDatainJsonObject.getJSONObject("verificationProfile")
                .getJSONObject("transaction")
                .getJSONObject("transactionStatus")
                .getJSONObject("transactionStatusDetails")
                .getJSONObject("certificate")
                .get("email").toString());

        try {
            information.setOrganisation(soapDatainJsonObject.getJSONObject("verificationProfile")
                    .getJSONObject("transaction")
                    .getJSONObject("transactionStatus")
                    .getJSONObject("transactionStatusDetails")
                    .getJSONObject("certificate")
                    .get("organisation").toString());

            information.setLocality(soapDatainJsonObject.getJSONObject("verificationProfile")
                    .getJSONObject("transaction")
                    .getJSONObject("transactionStatus")
                    .getJSONObject("transactionStatusDetails")
                    .getJSONObject("certificate")
                    .get("locality").toString());


            information.setState(soapDatainJsonObject.getJSONObject("verificationProfile")
                    .getJSONObject("transaction")
                    .getJSONObject("transactionStatus")
                    .getJSONObject("transactionStatusDetails")
                    .getJSONObject("certificate")
                    .get("state").toString());


            information.setIssuerName(soapDatainJsonObject.getJSONObject("verificationProfile")
                    .getJSONObject("transaction")
                    .getJSONObject("transactionStatus")
                    .getJSONObject("transactionStatusDetails")
                    .getJSONObject("certificate")
                    .get("issuerCommonName").toString());
        } catch (Exception e) {
            return information;
        }

        return information;
    }

    private String generateHash(String content) {

        try {
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
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not generate Hash");
        }
    }

    private String generateBase64(String content) {
        String nepali = "";
        try {
            nepali = URLEncoder.encode(content, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64Content = Base64.getEncoder().encodeToString(nepali.getBytes());
        return base64Content;
    }
}
