package com.gerp.usermgmt.pojo.auth;

import com.gerp.shared.enums.BloodGroup;
import com.gerp.shared.enums.Gender;
import com.gerp.usermgmt.model.RoleGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPojo {

    private Long id;

    private String username;
    private String email;
    private Boolean isOagian;
    private Boolean isPasswordChanged;
    private String ipAddress;
    private String lastLoginDate;


    private boolean accountNonExpired;

    private boolean credentialsNonExpired;

    private boolean accountNonLocked;


    private Collection<RoleGroup> roles;

    private String pisEmployeeCode;

    private String name;

    private String nameN;


    private String mobileNumber;

    private Gender gender;

    private BloodGroup bloodGroup;

    private String citizenshipNumber;

    private Date dateOfBirth;

    private long designation;

    private long oagStructure;

    private long office;

    private Long employeeDetail;

}
