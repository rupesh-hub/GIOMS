package com.gerp.shared.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Name {
    private String firstName;
    private String middleName;
    private String lastName;

    public Name(String fullName) {
        this.firstName = StringDataUtils.get_first_name(fullName);
        this.middleName = StringDataUtils.getMiddleName(fullName);
        this.lastName = StringDataUtils.get_last_name(fullName);
    }
}
