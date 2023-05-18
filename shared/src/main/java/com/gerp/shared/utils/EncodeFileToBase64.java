package com.gerp.shared.utils;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EncodeFileToBase64 {
    public static String encodeFileToBase64Binary(File file) {
        try {
            byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
            return new String(encoded, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw new RuntimeException("Cannot Encode");
        }
    }
}
