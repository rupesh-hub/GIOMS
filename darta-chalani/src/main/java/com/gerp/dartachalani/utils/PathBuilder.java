package com.gerp.dartachalani.utils;

/**
 * this utility method is use for chaining folder path for json file or document file
 *
 * @author Bibash Bogati on 2/14/2021
 */
public class PathBuilder {

    private static final String FILE_SEPARATOR = "/";
    private final String basePath;
    private String customerName;


    public PathBuilder(String basePath) {
        this.basePath = basePath;
    }

    private static String getDigitsFromString(String citizenNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < citizenNumber.length(); i++) {
            char c = citizenNumber.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public String buildBuildFormDownloadPath(String formName) {
        StringBuilder sb = new StringBuilder(this.basePath);
        sb.append("download/").append(formName).append(FILE_SEPARATOR);
        return sb.toString();
    }


}
