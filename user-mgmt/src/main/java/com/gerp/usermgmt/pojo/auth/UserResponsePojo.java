package com.gerp.usermgmt.pojo.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.Gender;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponsePojo {
    private Long id;

    private String username;

    private String email;
    private String employeeEmail;

    private List<Long> roleIds;
    private List<String> roles;
    private List<String> rolesNp;

    private String pisEmployeeCode;
    private String employeeCode;

    private String name;

    private String nameN;

    private String officeCode;

    private Boolean isPasswordChanged;
    private Boolean status;
    private Boolean userExists;

    private IdNamePojo office;
    private IdNamePojo coreDesignation;
    private IdNamePojo section;
    private IdNamePojo functionalDesignation;
    private IdNamePojo employeeServiceStatus;

    private Boolean isOfficeHead;
    private Boolean isSuperAdmin;

    private Gender gender;

    private Boolean isKararEmployee;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate joinDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDateEn;


    private Boolean isDelegated = Boolean.FALSE;
    private Boolean isReassignment = Boolean.FALSE;
    private DetailPojo reassignmentSection;

    private String profilePic;

    private List<RolePojo> rolePojos;

//    private int page;
//    private int limit;
//
//    public int getPage() {
//        return page - 1;
//    }

}
