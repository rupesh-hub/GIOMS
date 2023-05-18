package com.gerp.usermgmt.pojo.email;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailPojo {

    private String email;
    private String firstname;
    private String lastname;
    private String piscode;
    private String username;
    private String password;
}
