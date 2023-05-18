package com.gerp.dartachalani.utils;

import java.util.Arrays;

/**
 * @author : Bibash Bogati on  3/14/2021
 **/
public class EnumUtils {

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

}
