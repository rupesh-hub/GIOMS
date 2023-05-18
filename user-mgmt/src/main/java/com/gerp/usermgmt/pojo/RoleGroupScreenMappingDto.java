package com.gerp.usermgmt.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleGroupScreenMappingDto {
    private Long id;
    private Long individualScreenId;
    private String individualScreenName;
    private Long roleGroupId;
    private String roleGroupName;

    public RoleGroupScreenMappingDto(Long id, Long individualScreenId) {
        this.id = id;
        this.individualScreenId = individualScreenId;
    }
}
