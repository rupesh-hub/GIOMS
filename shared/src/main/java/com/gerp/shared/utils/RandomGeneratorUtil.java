package com.gerp.shared.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Random;

@Component
public class RandomGeneratorUtil {

    public String generateRandomNumber(int length) {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) otp.append(random.nextInt(10));
        return otp.toString();
    }


    public static String getAlphaNumericString(int length) {
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = (int) (alphaNumericString.length() * Math.random());
            sb.append(alphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public String base64Encode(String textToEncode) {
        String encodedText = Base64.getEncoder().encodeToString(textToEncode.getBytes());
        return encodedText;
    }

    public String base64Decode(String textToDecode) {
        byte[] decodedBytes = Base64.getDecoder().decode(textToDecode);
        String decodedText = new String(decodedBytes, Charset.defaultCharset());
        return decodedText;
    }


}
