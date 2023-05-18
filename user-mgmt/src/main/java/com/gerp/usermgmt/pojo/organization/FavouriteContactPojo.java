package com.gerp.usermgmt.pojo.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteContactPojo {
    private Long id;

    private String userId;

    private String pisCode;
}
