package com.gerp.shared.utils;

public class NepaliNumberFormatter {

    public static String format(String s) {

        Double d = new Double(s);
        String[] ss = String.format("%.2f", d).split("\\.");
        s = ss[0];
        String formatted = "";
        if (s.length() > 1) {
            formatted = s.substring(0, 1);
            s = s.substring(1);
        }

        while (s.length() > 3) {
            formatted += "," + s.substring(0, 2);
            s = s.substring(2);
        }
        return "NPR " + formatted + "," + s + "." + ss[1];

    }
}
