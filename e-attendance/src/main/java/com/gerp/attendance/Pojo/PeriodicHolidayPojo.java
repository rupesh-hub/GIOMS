package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodicHolidayPojo {

    private Long id;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate toDateEn;

    private String from_date_np;
    private String to_date_np;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
    private String fiscalYearCode;

    private Boolean status;

   @NotNull
    private Integer publicHolidayId;

    private String yearNp;

}
