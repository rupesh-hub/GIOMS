package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualScreenDto {
    private Long id;

    @NotBlank
    @NotNull
    private String key;

    private String name;

    @NotNull
    private Long screenGroupId;

    private String screenGroupName;

}
