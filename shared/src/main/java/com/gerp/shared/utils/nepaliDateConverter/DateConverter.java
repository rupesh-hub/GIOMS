package com.gerp.shared.utils.nepaliDateConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

@Component
public class DateConverter {
    static final String DEFAULT_FORMAT = "yyyyMMdd";
    private static Logger logger = LoggerFactory.getLogger(DateConverter.class);
    /**
     * date input format for conversion
     */
    private String format = "-";
    /**
     * separate character to separate day of month, month, and year
     */
    private char separator;

    public DateConverter() {
        this(DEFAULT_FORMAT);
    }

    /**
     * @param format date format
     */
    private DateConverter(String format) {
        this(format, Character.MIN_VALUE);
    }

    private DateConverter(String format, char separator) {
        this.format = format;
        this.separator = separator;
        if (!format.equals(DEFAULT_FORMAT)) {
            throw new InvalidDateFormatException(
                    "Nepali date to Gregorian Date converter only supports "
                            + DEFAULT_FORMAT);
        }
    }

    /**
     * Converts Nepali Bikram Sambat date to Gregorian date
     *
     * @param bsDate nepali date
     * @return english date
     */
    public Date convertBsToAd(String bsDate) {
        int bsYear;
        int bsMonth;
        int bsDayOfMonth;

        if (separator == Character.MIN_VALUE) {
            if (!matchFormat(bsDate)) {
                throw new InvalidDateFormatException("incorrect date format  " + format
                        + " date provided was " + bsDate);
            }
            bsYear = Integer.parseInt(bsDate.substring(0, 4));
            bsMonth = Integer.parseInt(bsDate.substring(5, 7));
            bsDayOfMonth = Integer.parseInt(bsDate.substring(8, 10));
        } else {
            String[] bsDates = bsDate.split(String.valueOf(separator));
            bsYear = Integer.parseInt(bsDates[0]);
            bsMonth = Integer.parseInt(bsDates[1]);
            bsDayOfMonth = Integer.parseInt(bsDates[2]);
        }


        if (validateBsDate(bsYear, bsMonth, bsDayOfMonth)) {
            return convertBsToAd(bsMonth, bsDayOfMonth, bsYear);
        } else {
            throw new IllegalStateException("invalid BS date");
        }

    }

    /* *//**
     * Converts Nepali Bikram Sambat year to Gregorian year
     *
     * @param bsYear nepali year
     * @return english year
     *//*
    public Date convertBsYearToAdYear(String bsYear) {
        int bYear;
        int bsMonth;
        int bsDayOfMonth;

       
            if (!matchYearFormat(bsYear)) {
                throw new InvalidDateFormatException("incorrect date format  " + format
                        + " date provided was " + bsYear);
            }
            bYear = Integer.parseInt(bsYear);
            bsMonth = 1;
            bsDayOfMonth = 1;
        if (validateBsDate(bYear, bsMonth, bsDayOfMonth)) {
            Date date = convertBsYearToAdYear(bYear);
            return date;
        } else {
            throw new IllegalStateException("invalid BS date");
        }

    }*/

    /**
     * Converts Gregorian date to Bikram Sambat date
     *
     * @param adDate english date format string "yyyy-MM-dd"
     * @return Bikram Sambat date - String type yyyy-MM-dd. There would be 1 digit month and day of month.
     */
    public String convertAdToBs(String adDate) {
        String[] adDateParts = adDate.split("-");
        LocalDate inputAdDate = LocalDate.parse(adDate);
        LocalDate lookupNearestAdDate = null;
        int equivalentNepaliYear = Lookup.lookupNepaliYearStart;
        Byte[] monthDay = null;

        for (int i = 0; i < Lookup.adEquivalentDatesForNewNepaliYear.size(); i++) {
            String[] adEquivalentDateForNewNepaliYear = Lookup.adEquivalentDatesForNewNepaliYear.get(i).split("-");
            if (adEquivalentDateForNewNepaliYear[2].equals(adDateParts[0])) {
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                lookupNearestAdDate = LocalDate.parse(Lookup.adEquivalentDatesForNewNepaliYear.get(i), formatter1);
                monthDay = Lookup.numberOfDaysInNepaliMonth.get(i + Lookup.lookupNepaliYearStart);
                equivalentNepaliYear += i;
                if (inputAdDate.isBefore(lookupNearestAdDate)) {
                    if (i == 0) {
                        throw new DateRangeNotSupported("Date supplied before supported date.");
                    }
                    lookupNearestAdDate = LocalDate.parse(Lookup.adEquivalentDatesForNewNepaliYear.get(i - 1), formatter1);
                    equivalentNepaliYear -= 1;
                    monthDay = Lookup.numberOfDaysInNepaliMonth.get(i + Lookup.lookupNepaliYearStart - 1);
                }
            }
        }
        assert lookupNearestAdDate != null;
        //Positive day difference
        long difference = ChronoUnit.DAYS.between(lookupNearestAdDate, inputAdDate);

        int nepMonth = 0;
        int nepDay = 1;
        int daysInMonth;
        while (difference != 0) {
            if (difference >= 0) {
                //number of days in  Nepali months
                daysInMonth = monthDay[nepMonth];
                nepDay++;
                if (nepDay > daysInMonth) {
                    nepMonth++;
                    nepDay = 1;
                }
                if (nepMonth >= 12) {
                    equivalentNepaliYear++;
                    nepMonth = 0;
                }
                difference--;
            }
        }
//month index is initialised as 0 so increasing by 1
        nepMonth += 1;
        String nepMonthString,nepDayString;
        if(nepMonth<10)
            nepMonthString = "0"+String.valueOf(nepMonth);
        else
            nepMonthString = String.valueOf(nepMonth);
        if(nepDay<10)
            nepDayString = "0"+String.valueOf(nepDay);
        else
            nepDayString = String.valueOf(nepDay);

        return equivalentNepaliYear + "-" + nepMonthString + "-" + nepDayString;
    }

    public String getCurrentNepaliDate(){
        return convertAdToBs(LocalDate.now().toString());
    }
    /**
     * Converts nepali bikram sambat date to Gregorian date
     *
     * @param nepaliMonth      nepali date month
     * @param nepaliDayOfMonth nepali date day of month
     * @param nepaliYear       index to look number of days in month
     * @return english date
     */
    public Date convertBsToAd(int nepaliMonth, int nepaliDayOfMonth,
                              int nepaliYear) {
        // number of days
        // passed
        // since
        // start of year
        // 1 is decreased as year start day has already included
        int numberOfDaysPassed = nepaliDayOfMonth - 1;
        for (int i = 0; i <= nepaliMonth - 2; i++) {
            numberOfDaysPassed += Lookup.numberOfDaysInNepaliMonth.get(nepaliYear)[i];
        }
        // From look up table we need to find corresponding english date
        // for
        // nepali new year
        // we need to add number of days passed from new year to english
        // date
        // which will find
        // corresponding english date
        // we need what starts
        // where...
        String dateFormat = "dd-MMM-yyyy";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                dateFormat);
        sdf.setLenient(false);
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(Lookup.adEquivalentDatesForNewNepaliYear.get(getLookupIndex(nepaliYear))));
        } catch (ParseException e) {
            logger.error("error", e);
        }
        c1.add(Calendar.DATE, numberOfDaysPassed);
        return c1.getTime();
    }

    /**
     * Converts nepali bikram sambat year to Gregorian year
     *
     * @param nepaliYear      nepali year
     * @return english year
     */
 /*   public Date convertBsYearToAdYear(
                              int nepaliYear) {
        // From look up table we need to find corresponding english date
        // for
        // nepali new year
        // we need to add number of days passed from new year to english
        // date
        // which will find
        // corresponding english date
        // we need what starts
        // where...
        String dateFormat = "dd-MMM-yyyy";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                dateFormat);
        sdf.setLenient(false);
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(Lookup.adEquivalentDatesForNewNepaliYear.get(getLookupIndex(nepaliYear))));
        } catch (ParseException e) {
            logger.error("error", e);
        }
        return c1.getTime();
    }
*/

    /**
     * validates nepali date
     *
     * @param bsYear       nepali date year part
     * @param bsMonth      nepali date month part
     * @param bsDayOfMonth nepali date day of month
     * @return boolean returns false  if there is no lookup for provided year ,
     */
    public boolean validateBsDate(int bsYear, int bsMonth, int bsDayOfMonth) {
        if (bsYear < Lookup.lookupNepaliYearStart) {
            throw new DateRangeNotSupported("Bikam Sambat is earlier than supported date");
        } else if (bsYear > (Lookup.lookupNepaliYearStart + Lookup.numberOfDaysInNepaliMonth.size() - 1)) {
            throw new DateRangeNotSupported("Bikram Sambat is later than supported date");
        } else {
            logger.debug("debug: converter supports  year {} ", bsYear);
            if (bsMonth >= 1 && bsMonth <= 12) {
                logger.debug("debug: month between 1 and 12");
                int dayOfMonth = Lookup.numberOfDaysInNepaliMonth.get(bsYear)[bsMonth - 1];
                logger.debug("debug:total days in month {} ", dayOfMonth);
                if (bsDayOfMonth <= dayOfMonth) {
                    return true;
                } else {
                    String message = String.format("invalid day of month  %d for year  %d  and month  %d ", bsDayOfMonth, bsYear, bsMonth);
                    logger.warn(message);
                    throw new InvalidBsDayOfMonthException(
                            message);
                }
            }
        }
        return false;
    }

    /**
     * gets array lookup index in lookup datastructure
     *
     * @param bsYear nepali year
     * @return index where year is
     */
    public int getLookupIndex(int bsYear) {
        logger.debug("lookup index {} ", (bsYear - Lookup.lookupNepaliYearStart));
        return bsYear - Lookup.lookupNepaliYearStart;
    }

    /**
     * confirms whether date format is valid or not. date format should be
     * mm-dd-yyyy
     *
     * @param bsDate nepali date
     * @return true if format matches
     */
    boolean matchFormat(String bsDate) {
        if (format.equals(DEFAULT_FORMAT)) {
            logger.debug("date format want to test is {} real text is {}", format, bsDate);
            Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
            return p.matcher(bsDate).matches();
        } else {
            logger.debug("date format is {}", format);
            return Pattern.matches("\\d{4}-\\d{2}-\\d{2}", bsDate);
        }
    }

    boolean matchYearFormat(String bsYear) {
        if (format.equals(DEFAULT_FORMAT)) {
            logger.debug("date format want to test is {} real text is {}", format, bsYear);
            Pattern p = Pattern.compile("\\d{4}");
            return p.matcher(bsYear).matches();
        } else {
            logger.debug("date format is {}", format);
            return Pattern.matches("\\d{4}-\\d{2}-\\d{2}", bsYear);
        }
    }

    public String convertBSToDevnagari(String date) {
        char[] array = date.toCharArray();
        String nepaliDate = "";
        for (int i = 0; i < array.length; i++) {
            switch (array[i]) {
                case '0':
                    nepaliDate += '०';
                    break;
                case '1':
                    nepaliDate += '१';
                    break;
                case '2':
                    nepaliDate += '२';
                    break;
                case '3':
                    nepaliDate += '३';
                    break;
                case '4':
                    nepaliDate += '४';
                    break;
                case '5':
                    nepaliDate += '५';
                    break;
                case '6':
                    nepaliDate += '६';
                    break;
                case '7':
                    nepaliDate += '७';
                    break;
                case '8':
                    nepaliDate += '८';
                    break;
                case '9':
                    nepaliDate += '९';
                    break;
                case '-':
                    nepaliDate += '/';
                    break;
                default:
                    nepaliDate += array[i];

            }
        }
        return nepaliDate;
    }

    public String convertToNepali(String number) {
        char[] array = number.toCharArray();
        String nepaliDate = "";
        for (int i = 0; i < array.length; i++) {
            switch (array[i]) {
                case '0':
                    nepaliDate += '०';
                    break;
                case '1':
                    nepaliDate += '१';
                    break;
                case '2':
                    nepaliDate += '२';
                    break;
                case '3':
                    nepaliDate += '३';
                    break;
                case '4':
                    nepaliDate += '४';
                    break;
                case '5':
                    nepaliDate += '५';
                    break;
                case '6':
                    nepaliDate += '६';
                    break;
                case '7':
                    nepaliDate += '७';
                    break;
                case '8':
                    nepaliDate += '८';
                    break;
                case '9':
                    nepaliDate += '९';
                    break;
                case '_':
                    nepaliDate += '_';
                    break;
                default:
                    nepaliDate += array[i];

            }
        }
        return nepaliDate;
    }

    public String convertNepali(String date) {
        char[] array = date.toCharArray();
        String nepaliDate = "";
        for (int i = 0; i < array.length; i++) {
            switch (array[i]) {
                case '/':
                    nepaliDate += '-';
                    break;
                default:
                    nepaliDate += array[i];

            }
        }
        return nepaliDate;
    }

    public String getMonth(String number) {
        String nepaliDate = "";
            switch (number) {
                case "1":
                    nepaliDate += "वैशाख";
                    break;
                case "2":
                    nepaliDate += "ज्येष्ठ";
                    break;
                case "3":
                    nepaliDate += "असार";
                    break;
                case "4":
                    nepaliDate += "साउन";
                    break;
                case "5":
                    nepaliDate += "भदौ";
                    break;
                case "6":
                    nepaliDate += "असोज";
                    break;
                case "7":
                    nepaliDate += "कात्तिक";
                    break;
                case "8":
                    nepaliDate += "मंसिर";
                    break;
                case "9":
                    nepaliDate += "पौष";
                    break;
                case "10":
                    nepaliDate += "माघ";
                    break;
                case "11":
                    nepaliDate += "फागुन";
                    break;
                case "12":
                    nepaliDate += " चैत्र";
                    break;
                default:
                    nepaliDate += number;

            }

        return nepaliDate;
    }

    public String convertDevnagariToNormalDate(String date) {
        char[] array = date.toCharArray();
        String nepaliDate = "";
        for (int i = 0; i < array.length; i++) {
            switch (array[i]) {
                case '०':
                    nepaliDate += '0';
                    break;
                case '१':
                    nepaliDate += '1';
                    break;
                case '२':
                    nepaliDate += '2';
                    break;
                case '३':
                    nepaliDate += '3';
                    break;
                case '४':
                    nepaliDate += '4';
                    break;
                case '५':
                    nepaliDate += '5';
                    break;
                case '६':
                    nepaliDate += '6';
                    break;
                case '७':
                    nepaliDate += '7';
                    break;
                case '८':
                    nepaliDate += '8';
                    break;
                case '९':
                    nepaliDate += '9';
                    break;
                default:
                    nepaliDate += array[i];

            }
        }
        String[] adDateParts = nepaliDate.split("-");
        if (adDateParts[1].length() < 2)
            adDateParts[1] = "0" + adDateParts[1];
        if (adDateParts[2].length() < 2)
            adDateParts[2] = "0" + adDateParts[2];
        return adDateParts[0] + "-" + adDateParts[1] + "-" + adDateParts[2];
    }

    public String convertDevnagariToEnglishDate(String date) {
        char[] array = date.toCharArray();
        String nepaliDate = "";
        for (int i = 0; i < array.length; i++) {
            switch (array[i]) {
                case '०':
                    nepaliDate += '0';
                    break;
                case '१':
                    nepaliDate += '1';
                    break;
                case '२':
                    nepaliDate += '2';
                    break;
                case '३':
                    nepaliDate += '3';
                    break;
                case '४':
                    nepaliDate += '4';
                    break;
                case '५':
                    nepaliDate += '5';
                    break;
                case '६':
                    nepaliDate += '6';
                    break;
                case '७':
                    nepaliDate += '7';
                    break;
                case '८':
                    nepaliDate += '8';
                    break;
                case '९':
                    nepaliDate += '9';
                    break;
                case '/':
                    nepaliDate += '-';
                    break;
                default:
                    nepaliDate += array[i];

            }
        }
       return nepaliDate;
    }

    public String convertDateToString(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public Date convertStringToDate(String date, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(date);
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public String getTimeDifference(LocalTime checkIn, LocalTime shiftCheckin) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date1 = format.parse(checkIn.toString());
        Date date2 = format.parse(shiftCheckin.toString());

        long diff = date2.getTime() - date1.getTime();
        long diffMinutes = (diff /1000) /(60);
        long diffHours = diffMinutes / (60);
        long min = diffMinutes %(60);
        String minutes="0";
        String hours ="0";
        if(String.valueOf(min).length()==1){
            minutes=minutes.concat(String.valueOf(min));
        }else{
            minutes=String.valueOf(min);
        }
        if(String.valueOf(diffHours).length()==1){
            hours=hours.concat(String.valueOf(diffHours));
        }else{
            hours=String.valueOf(diffHours);
        }
        String time=hours.concat(":").concat(minutes);
        return time;

    }
}
