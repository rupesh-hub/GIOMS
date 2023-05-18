package com.gerp.sbm.model.assets;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@DynamicUpdate
@Getter
@Setter
public class Share extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "current_assets_share_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "current_assets_share_seq_gen", sequenceName = "seq_current_assets_share", initialValue = 1, allocationSize = 1)
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
//    private String shareHolderName;
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
//    private String compNameAdr;
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    @Column( columnDefinition = "VARCHAR(20)", nullable = false)
//    private String shareType;
//
//    @NotNull
//    @Column( nullable = false)
//    private Double shareAmount;
//
//    @NotNull
//    @Column( nullable = false)
//    private Integer shareQuantity;
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
//    @Column( columnDefinition = "VARCHAR(200)", nullable = false)
//    private String source;
//
//    @Column( columnDefinition = "TEXT")
//    private String remarks;
}
