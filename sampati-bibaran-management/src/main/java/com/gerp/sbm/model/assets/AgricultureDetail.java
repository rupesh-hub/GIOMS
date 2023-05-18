package com.gerp.sbm.model.assets;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Getter
@Setter
public class AgricultureDetail extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "current_assets_loan_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "current_assets_loan_seq_gen", sequenceName = "seq_current_assets_loan", initialValue = 1, allocationSize = 1)
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
//    private String agricultureDetail;
//
//    @NotNull
//    @Column( nullable = false)
//    private Integer quantity;
//
//
//    @NotNull
//    @Column( nullable = false)
//    private Double costPrice;
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
//    @Column( columnDefinition = "VARCHAR(200)", nullable = false)
//    private String source;
//
//    @NotNull
//    @Column( nullable = false)
//    private LocalDate buyDate;
//
//    @Column( columnDefinition = "TEXT")
//    private String remarks;
}
