package com.gerp.shared.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class IdEncryptor {

    private static String SECRET_KEY = "@#$%^&*";
    private static String SALT;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final int LENGTH = 5;

    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-[]{}\\\\|;:'\\\",.<>/";




    public static String encrypt(String strToEncrypt) throws Exception {
        SALT = generateSaltString();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];

        IvParameterSpec ivspec = new IvParameterSpec(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        String encryptedString = Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));

        return encryptedString + ":" + SALT;
    }


    public static String decrypt(String strToDecrypt) throws Exception {


        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        int indexOfColumn = strToDecrypt.lastIndexOf(":");
        SALT = strToDecrypt.substring( indexOfColumn+ 1);
        strToDecrypt = strToDecrypt.substring(0,indexOfColumn);

        byte[] iv = new byte[cipher.getBlockSize()];

        IvParameterSpec ivspec = new IvParameterSpec(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }

    private static String generateSaltString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String cipher(String data){
        StringBuilder sb = new StringBuilder();
        sb.append(generateSaltString());
        sb.append(data);
        sb.append(generateSaltString());

        return sb.toString();
    }

    public static String dicipher(String data){
        return data.substring(10, data.length() -10);
    }

    private String generateRandomString(){
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            String source = "";
            boolean isNumber = random.nextBoolean();
            boolean isSpecial = random.nextBoolean();
            if (isNumber) {
                source = NUMBERS;
            } else if (isSpecial) {
                source = SPECIAL_CHARACTERS;
            } else {
                source = ALPHABET;
            }
            int index = random.nextInt(source.length());
            char c = source.charAt(index);
            sb.append(c);
        }
        String randomString = sb.toString();

        return randomString;
    }

//    public static void main(String[] args) throws Exception {
//        String s = encrypt("2243");
//
//        String f = decrypt(
//
//                "TNabT+OYQxfLTjlFeTVUnA==:vibmjgjgdc");
//        long ss = Long.parseLong(f);
//
//        System.out.println(s);
//        System.out.println(f);
//        System.out.println(ss);
//    }


}
