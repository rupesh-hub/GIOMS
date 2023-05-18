package com.gerp.sbm.pojo.RequestPojo;

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


@Getter
@Setter
public class AgricultureDetailRequestPojo  {

    @NotNull
    private String agricultureDetail;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double costPrice;

    @NotNull
    private String source;

    @NotNull
    private LocalDate buyDate;

    private String remarks;

    @NotNull
    private String piscode;

}
