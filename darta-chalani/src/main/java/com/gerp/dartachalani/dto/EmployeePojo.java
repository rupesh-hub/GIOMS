package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.shared.enums.Gender;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.json.GenderKeyValueOptionSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeePojo {
    private String pisCode;
    private String positionCode;
    private String nameNp;
    private String nameEn;

    @JsonIgnore
    @JsonSerialize(using = GenderKeyValueOptionSerializer.class, as = Enum.class)
    private Gender gender;

    private String emailAddress;
    private String mobileNumber;
    private String sectionId;
    private String functionalDesignationCode;
    private IdNamePojo office;

    private IdNamePojo functionalDesignation;
    private IdNamePojo serviceGroup;
    private IdNamePojo coreDesignation;
    private IdNamePojo section;
    private Boolean userAlreadyExists;
    private IdNamePojo permanentDistrict;

}

