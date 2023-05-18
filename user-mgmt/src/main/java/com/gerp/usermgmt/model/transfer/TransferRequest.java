package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@DynamicUpdate
@Table(name = "transfer_request")
public class TransferRequest extends AuditActiveAbstract {

    @Id
    @SequenceGenerator(name = "transfer_request_seq", sequenceName = "transfer_request_seq", allocationSize = 1)
    @GeneratedValue(generator = "transfer_request_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "employee_ps_code")
    private String employeePsCode;

    @Column(name = "requested_date_en")
    private LocalDate requestedDateEn;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "requested_date_np")
    private String requestedDateNp;

//    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
//    @Column(columnDefinition = "VARCHAR(10)",name = "designation_code")
//    private String designationCode;
//
//    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
//    @Column(columnDefinition = "VARCHAR(10)",name = "service_code")
//    private String serviceCode;
//
//    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
//    @Column(columnDefinition = "VARCHAR(10)",name = "position_code")
//    private String positionCode;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "request_review_office_code")
    private String requestReviewOfficeCode;


    @Column(columnDefinition = "TEXT")
    private String remark;

    @OneToMany(fetch = FetchType.LAZY,targetEntity = RequestedOffice.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_request_id",referencedColumnName = "id",foreignKey =@ForeignKey(name= "FK_transfer_request_offices"))
    private List<RequestedOffice> requestedOfficeList;

    @OneToMany(fetch = FetchType.LAZY,targetEntity = TransferRequestDocuments.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_request_id",referencedColumnName = "id",foreignKey =@ForeignKey(name= "FK_transfer_request_related_documents"))
    private List<TransferRequestDocuments> relatedDocumentsList;

    @OneToMany(fetch = FetchType.LAZY,targetEntity = PreviousWorkDetails.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_request_id",referencedColumnName = "id",foreignKey =@ForeignKey(name= "FK_transfer_request_previous_detail"))
    private List<PreviousWorkDetails> previousWorkDetailsList;

    @OneToMany(fetch = FetchType.LAZY,targetEntity = EmployeeTransferRequestAndCheckList.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_request_id",referencedColumnName = "id",foreignKey =@ForeignKey(name= "FK_transfer_request_checklist"))
    private List<EmployeeTransferRequestAndCheckList> checkLists;

    @Column(name = "attendance_days")
    private Integer attendanceDays;

    @Column(name = "is_submitted")
    private Boolean isSubmitted;

}
