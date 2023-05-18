package com.gerp.usermgmt.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Slf4j
@Component
public class AESEncryptDecrypt {
    /**
     * This is done using AES Advanced Encryption System.
     * This is symmetric algorithm [ECB Electronic Code Book  CBC Cipher block chaining]
     * ECB dont use IV
     * CBC use IV
     * Here we are using CBC because
     * identical plain text blocks will be encrypted into dis-similar cipher text blocks in CBC
     */

    private final EncryptDecryptProperties encryptDecryptProperties;

    public AESEncryptDecrypt(EncryptDecryptProperties encryptDecryptProperties) {
        this.encryptDecryptProperties = encryptDecryptProperties;
    }

    public String encrypt(String value) {
        try {
            IvParameterSpec iv = getInitializationVector();
            SecretKeySpec skeySpec = getSecretKeySpec();
            Cipher cipher = Cipher.getInstance(encryptDecryptProperties.getTransformation());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("Error in AES encrypt ", ex.getMessage());
        }
        return null;
    }

    public String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = getInitializationVector();
            SecretKeySpec skeySpec = getSecretKeySpec();
            Cipher cipher = Cipher.getInstance(encryptDecryptProperties.getTransformation());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("Error in AES decrypt ", ex.getMessage());
        }

        return null;
    }

    private IvParameterSpec getInitializationVector() throws UnsupportedEncodingException {
        return new IvParameterSpec(encryptDecryptProperties.getInitVector().getBytes("UTF-8"));
    }

    private SecretKeySpec getSecretKeySpec() throws UnsupportedEncodingException {
        return new SecretKeySpec(encryptDecryptProperties.getKey().getBytes("UTF-8"), "AES");
    }
}