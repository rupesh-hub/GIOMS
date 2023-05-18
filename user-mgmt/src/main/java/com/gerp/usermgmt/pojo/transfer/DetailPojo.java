package com.gerp.usermgmt.pojo.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailPojo {

    private String code;
    private String nameEn;
    private String nameNp;
    private DetailPojo vdc;
    private DetailPojo province;
    private DetailPojo district;
    private DetailPojo currentOffice;
    private DetailPojo currentDesignation;
    private DetailPojo currentService;
    private DetailPojo currentPosition;
    private String joinedDateNp;
    private String joinedDateEn;
    private String dobAd;
    private String dobBs;
    private String lastNameEn;
    private String lastNameNp;
    private String middleNameNp;
    private String middleNameEn;
    private Object officeLevels;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> codes;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> positionCodes;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> types;
}
