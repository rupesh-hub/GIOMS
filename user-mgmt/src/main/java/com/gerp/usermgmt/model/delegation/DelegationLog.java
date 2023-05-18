package com.gerp.usermgmt.model.delegation;

import com.gerp.shared.generic.api.AuditAbstract;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "delegation_log")
public class DelegationLog extends AuditAbstract {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "delegation_log_seq")
    @SequenceGenerator(name = "delegation_log_seq",allocationSize = 1, sequenceName = "delegation_log_seq")
    private Long id;

    @Column(name = "is_abort", nullable = false)
    private Boolean isAbort;

    @Column(name = "delegation_id", nullable = false)
    private Integer delegationId;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime previousEffectiveDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime previousExpireDate;

    private int previousFromSectionId;

    private String previousFromPisCode;

    private int previousToSectionId;

    private String previousToPisCode;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime newEffectiveDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime newExpireDate;

    private int newFromSectionId;

    private String newFromPisCode;

    private int newToSectionId;

    private String newToPisCode;

    @Column(name ="is_reassignment")
    private Boolean isReassignment;

    @Column(name= "is_office_head")
    private Boolean isOfficeHead;




//
//    public void setPreviousEffectiveDate(String previousEffectiveDate) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime parse = LocalDateTime.parse(previousEffectiveDate, formatter);
//        this.previousEffectiveDate = parse;    }
//
//    public void setPreviousExpireDate(String previousExpireDate) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime parse = LocalDateTime.parse(previousExpireDate, formatter);
//        this.previousExpireDate = parse;
//    }
//
//    public void setNewEffectiveDate(String newEffectiveDate) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime parse = LocalDateTime.parse(newEffectiveDate, formatter);
//        this.newEffectiveDate = parse;
//    }
//
//    public void setNewExpireDate(String newExpireDate) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime parse = LocalDateTime.parse(newExpireDate, formatter);
//        this.newExpireDate = parse;    }
}
