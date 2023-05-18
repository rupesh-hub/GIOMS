package com.gerp.usermgmt.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePrivilegeDto {

    private int create;
    private int update;
    private int access;
    private int delete;
    private int view;
    private int status;
}
