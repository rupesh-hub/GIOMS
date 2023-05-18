package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.Pojo.json.DurationTypeKeyValueOptionSerializer;
import com.gerp.attendance.Pojo.json.StatusKeyValueOptionSerializer;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KaajRequestMinimalPojo {

    private Integer id;

    private String officeCode;

    private String pisCode;

    private List<EmployeeDetailPojo> pisCodesDetail;

    private String kaajNameEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    private Integer kaajTypeId;

    private String kaajTypeName;

    private String kaajTypeNameNp;

    private Boolean isActive;

    private String kaajNameNp;

    private List<IdNamePojo> vehicleCategories;

    @JsonSerialize(using = DurationTypeKeyValueOptionSerializer.class, as = Enum.class)
    private DurationType durationType;
    private String location;
    private String purpose;

    private List<DocumentPojo> document;

    private List<DocumentPojo> referenceDocument;

    @JsonSerialize(using = StatusKeyValueOptionSerializer.class, as = Enum.class)
    private Status status;

    private String employeeNameEn;
    private String employeeNameNp;
}
