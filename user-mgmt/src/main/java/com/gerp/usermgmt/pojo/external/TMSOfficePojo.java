package com.gerp.usermgmt.pojo.external;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TMSOfficePojo {

   private String clientCode;

    private String clientName;

    private String clientNameNp;

    private String countryName;

    private String countryNameNp;

    private String districtName;

    private String districtNameNp;

    private String localBodyName;

    private String localBodyNameNp;
    private String stateName;

    private String id;

    String districtId;

}
