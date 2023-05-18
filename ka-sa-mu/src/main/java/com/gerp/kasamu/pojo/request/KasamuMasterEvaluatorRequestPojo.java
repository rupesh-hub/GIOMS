package com.gerp.kasamu.pojo.request;

import com.gerp.kasamu.pojo.NonGazTipadi;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class KasamuMasterEvaluatorRequestPojo {

    @NotNull
    private Long kasamuMasterId;

    private String valRemarks;

    @NotNull
    private String valuatorType;

    @NotNull
    @NotEmpty
    private String pisCode;


    @NotNull
    private List<KasamuEvaluatorRequestPojo> kasamuEvaluatorList;

    private NonGazTipadi tippadi;
}
