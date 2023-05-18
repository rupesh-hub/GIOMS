package com.gerp.usermgmt.poststart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonStringUtils {
    @Autowired
    private ResourceLoader resourceLoader;
    public String readFileToString(String path, Class aClazz) throws IOException {


//        try (InputStream stream = resourceLoader.getResource("auth/apimapping.json").getInputStream()) {
//            return IOUtils.toString(stream, Charset.defaultCharset());
//        }
        return null;
    }
}