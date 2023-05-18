package com.gerp.shared.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringDataUtils {

    private StringDataUtils() {
        throw new IllegalStateException("Utility class");
        }

    public static String concatName(String firstName , String middleName , String lastName) {
        return Stream.of(firstName , middleName , lastName).filter(s -> !ObjectUtils.isEmpty(s)).map(s -> s.replaceAll("\\s+","")).collect(Collectors.joining(" "));
    }

    public static String getNimitConcated(String name  , boolean officeHead) {
        if(name == null ) {
            return  "-"; } else {
            return officeHead ? StringUtils.capitalize(name) + "(नि.)" : org.apache.commons.lang.StringUtils.capitalize(name);
        }
    }

    public static Name splitName(String fullName) {
        return new Name(fullName);
    }

    public static String get_first_name(String full_name)
    {
        int last_space_index = full_name.lastIndexOf(' ');
        return last_space_index == -1 ? full_name : full_name.substring(0, last_space_index);
    }

    public static String getMiddleName(String full_name)
    {
        int firstNameIndex = full_name.indexOf(' ');
        int last_space_index = full_name.lastIndexOf(' ');
        return firstNameIndex == -1 || last_space_index == -1 ? null : full_name.substring(firstNameIndex, last_space_index);
    }

    public static String get_last_name(String full_name)
    {
        int last_space_index = full_name.lastIndexOf(' ');
        return last_space_index == -1 ? null : full_name.substring(last_space_index + 1);
    }

    public static String setNAIfNull(String object) {
        return ObjectUtils.isEmpty(object) ? "-" : object;
    }



    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
