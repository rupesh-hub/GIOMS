package com.gerp.usermgmt.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "gerp")
@Configuration("encryptDecryptProperties")
public class EncryptDecryptProperties {
    private String key;
    private String initVector;
    private String transformation;
    private List<String> someList;
}
