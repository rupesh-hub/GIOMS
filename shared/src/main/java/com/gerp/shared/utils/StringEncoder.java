package com.gerp.shared.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class StringEncoder {
    public static String decodeToUTF8(String text) throws UnsupportedEncodingException {
        return text == null ? null : URLDecoder.decode(text, StandardCharsets.UTF_8.toString());
    }
}
