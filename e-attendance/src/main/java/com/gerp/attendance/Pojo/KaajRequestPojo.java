package com.gerp.attendance.Pojo;

import com.gerp.attendance.model.kaaj.VehicleCategory;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KaajRequestPojo {

    private Long id;
    private String officeCode;
    private String pisCode;
    private Boolean appliedForOthers;
    private String appliedPisCode;
    private Boolean isApprover;

    @NotNull
    private Boolean isInternational;

    @NotNull
    private Integer fiscalYear;

    private Boolean isApproved = false;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDateEn;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDateEn;
    private String fromDateNp;
    private String toDateNp;
    private String countryId;
    private String purpose;

    @NotBlank
    private String location;

    private String hashContent;
    private String signature;
    private VerificationInformation verificationInformation;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    private String approverPiscode;

    private Integer documentId;
    private List<MultipartFile> document;
    private List<MultipartFile> referenceDocument;
    private Integer kaajTypeId;
    private List<Integer> vehicleCategoryIds;
    private List<Long> documentsToRemove;
    private List<Long> referenceDocumentToRemove;
    private DurationType durationType;
    private String advanceAmountTravel;
    private String remarkRegardingTravel;
    private List<KaajAppliedOthersPojo> kaajAppliedOthersPojo;
    private String kaajRequesterHashContent;
    private String kaajRequesterSignature;
    private String kaajRequesterContent;

}
