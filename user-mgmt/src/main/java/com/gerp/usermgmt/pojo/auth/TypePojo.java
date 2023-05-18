package com.gerp.usermgmt.pojo.auth;

import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TypePojo {
    private int id;
    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String name;
}
