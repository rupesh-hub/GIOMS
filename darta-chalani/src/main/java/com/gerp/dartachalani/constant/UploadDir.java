package com.gerp.dartachalani.constant;

import org.springframework.stereotype.Component;

/**
 * @author Rujan Maharjan on 4/3/2019
 */
@Component
public class UploadDir {

    public static String WINDOWS_PATH = "C:/";
    public static String Linux_PATH = "/var/";
    public static String initialDocument = "images/";

   /* @Value("${file.upload-directory}")
    public void setWindowsPath(String windowsPath) {
        WINDOWS_PATH = windowsPath;
    }*/
}
