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
public class Loan extends AuditActiveAbstract {

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
//    private String name;
//
//    @NotNull
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_2)
//    @Column( columnDefinition = "VARCHAR(2)", nullable = false)
//    private String loanStatus;
//
//
//    @NotNull
//    @Column( nullable = false)
//    private Double amount;
//
//    @NotNull
//    @Column( nullable = false)
//    private LocalDate date;
//
//    @Column( columnDefinition = "TEXT")
//    private String remarks;
}
