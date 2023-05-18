package com.gerp.kasamu.converter;

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
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

@Component
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static String SECRET_KEY  ;
    private static String SALT;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            if (attribute == null){
                return null;
            }
            return encrypt(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null){
                return null;
            }
            return decrypt(dbData);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Autowired
    public void setAccessTokenEncryptionKey(@Value("${encrypt.key}") String accessTokenEncryptionKey) {
        AttributeEncryptor.SECRET_KEY = accessTokenEncryptionKey + "@#$%^&*";
    }



    public static String convertToEntityAttributeStatic(String dbData) {
        final AttributeEncryptor attributeEncryptor;
        try {
            attributeEncryptor = new AttributeEncryptor();
            return attributeEncryptor.convertToEntityAttribute(dbData);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    private String encrypt(String strToEncrypt) throws Exception {
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


    private String decrypt(String strToDecrypt) throws Exception {


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

    private String generateSaltString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


}
