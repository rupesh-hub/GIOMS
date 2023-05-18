package com.gerp.attendance.model.kaaj;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.attendance.model.leave.KaajRequestDocumentDetails;
import com.gerp.attendance.model.leave.KaajRequestReferenceDocuments;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(name = "kaaj_request")
public class KaajRequest extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kaaj_request_seq_gen")
    @SequenceGenerator(name = "kaaj_request_seq_gen", sequenceName = "seq_kaaj_request", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    private String officeCode;

    //    @NotNull
//    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    private String pisCode;

    @NotNull
    private Boolean isInternational;

    @NotNull
    @Column(name = "fiscal_year")
    private Integer fiscalYear;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "from_date_en")
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "to_date_en")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    //    @NotNull
//    @NotBlank
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "purpose", columnDefinition = "VARCHAR(255)")
    private String purpose;

    @NotNull
    private Boolean isApproved;

    private String countryId;

    //    @NotNull
//    @NotBlank
    private String location;

    @Enumerated(EnumType.STRING)
    private DurationType durationType;

    private Long documentMasterId;

    private Long referenceDocumentMasterId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "kaaj_request_id", foreignKey = @ForeignKey(name = "FK_KaajRequest_Document"))
    @JsonIgnore
    private Collection<KaajRequestDocumentDetails> kaajRequestDocumentDetails;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "kaaj_request_id", foreignKey = @ForeignKey(name = "FK_KaajRequest_ReferenceDocument"))
    @JsonIgnore
    private Collection<KaajRequestReferenceDocuments> kaajRequestReferenceDocuments;

    @ManyToOne
    @JoinColumn(name = "kaaj_type_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_KaajRequest_KaajType"))
    private KaajType kaajType;

    @ManyToOne
    @JoinColumn(name = "vehicle_category_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_KaajRequest_VehicleCategory"))
    private VehicleCategory vehicleCategory;

    @ManyToMany
    @JoinTable(name = "kaaj_vehicles",
            foreignKey = @ForeignKey(name = "FK_kaaj_vehicles_kaaj_id"),
            joinColumns = @JoinColumn(name = "kaaj_id", referencedColumnName = "id"),
            inverseForeignKey = @ForeignKey(name = "FK_kaaj_vehicles_vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(name = "UNIQUE_kaajvehicle", columnNames = {"kaaj_id", "vehicle_id"})
    )
    @JsonIgnore
    private Collection<VehicleCategory> vehicleCategories = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "kaaj_request_id", foreignKey = @ForeignKey(name = "FK_kaajrequest_on_behalf"))
    @JsonIgnore
    private Collection<KaajRequestOnBehalf> kaajRequestOnBehalves;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "kaaj_request_id", foreignKey = @ForeignKey(name = "FK_kaajrequest_approval"))
    @JsonIgnore
    private Collection<DecisionApproval> kaajRequestApprovals;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.P;

    @Column(name = "record_id")
    private UUID recordId;

    private String advanceAmountTravel;

    @Column(columnDefinition = "TEXT")
    private String remarkRegardingTravel;

    private Long kaajApproveDartaNo;

    private String appliedPisCode;

    private Boolean appliedForOthers;

    @Type(type = "org.hibernate.type.TextType")
    private String kaajRequesterHashContent;

    @Type(type = "org.hibernate.type.TextType")
    private String kaajRequesterSignature;

    @Column(name = "content")
    @Type(type = "org.hibernate.type.TextType")
    private String content;


}
