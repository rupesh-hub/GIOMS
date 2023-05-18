package com.gerp.shared.utils;

import com.gerp.shared.configuration.CustomMessageSource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class UtilityService {

    private final CustomMessageSource customMessageSource;

    public UtilityService(CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
    }

    public LocalDate stringToLocalDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(StringConstants.DATE_PATTERN);
        try {
            return this.convertToLocalDateViaInstant(sdf.parse(date));
        } catch (ParseException e) {
            throw new RuntimeException(customMessageSource.get("invalid.date.format"));
        }
    }

    private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public String fileName(String oldFileName, MultipartFile newFile, Boolean fileStatus, String type, MultipartHandler multipartHandler) {
        if (newFile != null) return multipartHandler.saveFile(newFile, type);
        else if (!fileStatus) return null;
        else return oldFileName;
    }

    public Timestamp getCurrentDate() {
        return new Timestamp(new Date().getTime());
    }

    public static String convertDateToString(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    @SneakyThrows
    public static Date convertStringToDate(String date, String pattern) {
        return new SimpleDateFormat(pattern).parse(date);
    }
}
