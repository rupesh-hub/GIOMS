package com.gerp.shared.utils;

import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
@Component
public class NumericConverter {
    public static String convertNepaliToEnglishNumerals(String ne_number) {
        String[] split = ne_number.split("-");
        StringBuilder monthNep = new StringBuilder();
        StringBuilder dayNep = new StringBuilder();
        try {
            NumberFormat nepaliToEnglish = NumberFormat.getInstance(new Locale("ne", "NP"));
            Number year = nepaliToEnglish.parse(split[0]);
            Number month = nepaliToEnglish.parse(split[1]);
            Number day = nepaliToEnglish.parse(split[2]);
            if (month.toString().length() != 2) {
                monthNep.append("0");
                monthNep.append(month);
            } else monthNep.append(month);
            if (day.toString().length() != 2) {
                dayNep.append("0");
                dayNep.append(day);
            } else dayNep.append(day);
            return year.toString() + "-" + monthNep + "-" + dayNep;
        } catch (ParseException e) {
            throw new RuntimeException("Cannot Parse date");
        }
    }

    public String convertEnglishNumbersToNepaliNumbers(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            try {
                String c = String.valueOf(Integer.parseInt(String.valueOf(text.charAt(i))));
                stringBuilder.append(getNepali(c));
            } catch (Exception ex) {
                stringBuilder.append(text.charAt(i));

            }
        }
        return stringBuilder.toString();
    }

    /**
     * Converts the double number to csv 3 digit seperated values
     * round off to 2 digits
     *
     * @param number
     * @return
     */
    public String convertToCsv(Double number) {
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        return myFormat.format(number);
    }

    private String getNepali(String english) {
        switch (english) {
            case "0":
                return "०";
            case "1":
                return "१";
            case "2":
                return "२";
            case "3":
                return "३";
            case "4":
                return "४";
            case "5":
                return "५";
            case "6":
                return "६";
            case "7":
                return "७";
            case "8":
                return "८";
            case "9":
                return "९";
            default:
                return "";

        }
    }
}
