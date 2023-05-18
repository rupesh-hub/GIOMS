package com.gerp.sbm.model.sampati;

import com.gerp.sbm.model.assets.*;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Entity
@DynamicUpdate
@Data
@Table(indexes = @Index(name = "uniquePiscodeAndFiscalyear", columnList = "emp_pis_code, fiscal_year_eng", unique = true))
public class SampatiMaster extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "sampati_master_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sampati_master_seq_gen", sequenceName = "seq_sampati_master", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
    private String emp_pis_code;

//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
//    private String bodyCode;

//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
//    private String officeCode;
    private String designation;

    private String office_name_eng;

    private String office_name_nep;

    private String full_name_eng;

    private String full_name_nep;

    private String temp_district_name;

    private String per_district_name;

    private String permanentMunicipalityVdc;

    private String temporaryMunicipalityVdc;

    private String tol;
    @NotNull
    private String fiscal_year_eng;

    @NotNull
    private String fiscal_year_nep;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
    private String panNo;

    private LocalDate subDate;

    @NotNull
    @Column( nullable = false)
    private Boolean status;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String agricultureDetailList;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String valuableItemsList;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String bankDetailsList;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String loanList;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String shareList;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String vehicleList;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String fixedAssetList;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String otherDetailList;

//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinColumn(name = "sampati_master_id", foreignKey = @ForeignKey(name = "FK_sampati_master_agriculture"))
//    private List<AgricultureDetail> agricultureDetailList;
//
//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinColumn(name = "sampati_master_id", foreignKey = @ForeignKey(name = "FK_sampati_master_current_asset_a"))
//    private List<ValuableItems> ;
//
//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinColumn(name = "sampati_master_id", foreignKey = @ForeignKey(name = "FK_sampati_master_current_asset_bank"))
//    private List<BankDetails> ;
//
//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinColumn(name = "sampati_master_id", foreignKey = @ForeignKey(name = "FK_sampati_master_current_asset_loan"))
//    private List<Loan> ;
//
//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinColumn(name = "sampati_master_id", foreignKey = @ForeignKey(name = "FK_sampati_master_current_asset_share"))
//    private List<Share> ;
//
//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinColumn(name = "sampati_master_id", foreignKey = @ForeignKey(name = "FK_sampati_master_current_asset_vehicle"))
//    private List<Vehicle> ;
//
//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinColumn(name = "sampati_master_id", foreignKey = @ForeignKey(name = "FK_sampati_master_fixed_asset"))
//    private List<FixedAsset> ;
//
//    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @JoinColumn(name = "sampati_master_id", foreignKey = @ForeignKey(name = "FK_sampati_master_other_detail"))
//    private List<OtherDetail> ;
}
