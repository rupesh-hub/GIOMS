package com.gerp.shared.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Base64Utils {

// change config to base64 configuration with salt
    public static byte[] encodeBase64(Object data) throws IOException {
        return Base64.encodeBase64(getByteArray(data));
    }

    public static String decodeBase64(String data){
        byte[] decodedBytes = Base64.decodeBase64(data);
        return new String(decodedBytes);
    }

    public static byte[] getByteArray(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        return baos.toByteArray();
    }

}
