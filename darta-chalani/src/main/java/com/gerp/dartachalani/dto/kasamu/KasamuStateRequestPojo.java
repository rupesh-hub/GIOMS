package com.gerp.dartachalani.dto.kasamu;

import com.gerp.dartachalani.dto.ReceiverPojo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class KasamuStateRequestPojo {

    @NotNull
    @NotBlank
    private Long kasamuId;

    @NotNull
    @NotBlank
    private String description;
    private List<ReceiverPojo> receiver;
    private List<ReceiverPojo> rec;
}
