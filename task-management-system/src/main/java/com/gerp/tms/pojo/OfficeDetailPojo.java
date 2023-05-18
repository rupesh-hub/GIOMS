package com.gerp.tms.pojo;

import com.gerp.shared.pojo.IdNamePojo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeDetailPojo {
    private String code;
    private String nameNp;
    private String nameEn;
    private String phoneNumber;
    private String addressEn;
    private String addressNp;
    private String parentCode;
    private IdNamePojo District;
    private  IdNamePojo organizationLevel;
    private OfficeDetailPojo parentOffice;

}
