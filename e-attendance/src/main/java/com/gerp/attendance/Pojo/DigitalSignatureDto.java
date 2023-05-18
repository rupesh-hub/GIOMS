package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Builder
public class DigitalSignatureDto {

    @NotBlank
    private String content;

    private String hashValue;

    private String signature;

    public DigitalSignatureDto(String content, String hashValue, String signature) {
        this.content = content;
        this.hashValue = hashValue;
        this.signature = signature;
    }
}
