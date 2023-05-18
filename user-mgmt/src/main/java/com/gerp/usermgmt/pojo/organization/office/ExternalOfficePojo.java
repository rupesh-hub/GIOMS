package com.gerp.usermgmt.pojo.organization.office;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalOfficePojo {
    private Integer id;
    private String code;
    private String nameEn;
    private String nameNp;
    private String address;
    private String phoneNumber;
    private String email;
    private String type;
    private int order;
    private Long sectionSubSectionId;
    private String sectionName;
}
