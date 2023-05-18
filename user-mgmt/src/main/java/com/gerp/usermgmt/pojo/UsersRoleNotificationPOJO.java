package com.gerp.usermgmt.pojo;

import com.gerp.usermgmt.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersRoleNotificationPOJO {
    private String room_name;
    private Set<User> users;
}
