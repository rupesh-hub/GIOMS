package com.gerp.shared.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveToggle {
    @NotNull
    private Long id;
    private boolean status;
}
