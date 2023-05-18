package com.gerp.sbm.model.assets;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DynamicUpdate
@Getter
@Setter
public class FixedAsset extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "decision_approval_seq_gen")
    @SequenceGenerator(name = "fixed_asset_seq_gen", sequenceName = "seq_current_fixed_asset", initialValue = 1, allocationSize = 1)

//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "decision_approval_seq_gen")
//    @SequenceGenerator(name = "decision_approval_seq_gen", sequenceName = "seq_decision_approval", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @Column( columnDefinition = "TEXT")
    private String content;

    @NotNull
    private String piscode;
    @NotNull
    private String fiscal_year_eng;

    @NotNull
    private String fiscal_year_nep;



//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
//    @Column( columnDefinition = "VARCHAR(200)", nullable = false)
//    private String ownerName;
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
//    private String districtCode;
//
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_2)
//    @Column( columnDefinition = "VARCHAR(2)", nullable = false)
//    private String fixedAssetType;
//
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
//    private String localLevelType;
//
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
//    private String localLevel;
//
//    @NotNull
//    @Column( nullable = false)
//    private Integer wardNo;
//
//
//    @NotNull
//    @Column( nullable = false)
//    private Integer kittaNo;
//
//    @NotNull
//    @Column( nullable = false)
//    private Double costPrice;
//
//    @NotNull
//    @Column( nullable = false)
//    private Double area;
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
//    @Column( columnDefinition = "VARCHAR(200)", nullable = false)
//    private String source;
//
//    @Column( columnDefinition = "TEXT")
//    private String remarks;

}
