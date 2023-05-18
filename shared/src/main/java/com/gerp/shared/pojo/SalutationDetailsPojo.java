package com.gerp.shared.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalutationDetailsPojo {
    private String nameEn;
    private String nameNp;
    private String phoneNumber;
    private String address;
    private String code;
    private String email;
    private IdNamePojo district;
    private IdNamePojo level;
    private String sectionName;
}
