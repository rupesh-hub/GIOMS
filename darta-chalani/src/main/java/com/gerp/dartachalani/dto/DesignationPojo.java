package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DesignationPojo {

    @JsonProperty("name_en")
    private String nameEn;

    @JsonProperty("name_np")
    private String nameNp;

    @JsonProperty("code")
    private String code;

    @JsonProperty("created_date")
    private String createdDate;

}
