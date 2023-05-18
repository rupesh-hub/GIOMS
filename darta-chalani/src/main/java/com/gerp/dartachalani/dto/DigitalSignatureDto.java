package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DigitalSignatureDto {

    @NotBlank
    private String content;

    private String hashValue;

    private String signature;

}
