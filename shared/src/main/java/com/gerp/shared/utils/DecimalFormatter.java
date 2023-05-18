package com.gerp.shared.utils;

import java.text.DecimalFormat;

public class DecimalFormatter {

    public static String convertToTwoDigit(double doubleValue) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(doubleValue);
    }

    public static Double convertToTwoDigitDouble(double doubleValue) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(doubleValue));
    }
}
