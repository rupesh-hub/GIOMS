package com.gerp.usermgmt.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleGroupScreenMappingPojo {
    private Long roleGroupId;
    private Long screenGroupId;
    private List<Long> individualScreenIds;
}
