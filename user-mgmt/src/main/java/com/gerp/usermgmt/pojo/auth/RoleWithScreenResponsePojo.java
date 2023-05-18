package com.gerp.usermgmt.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoleWithScreenResponsePojo {
    private Long id;
    private String name;
    private String description;
    private Map<UUID, List<UUID>> screens;
}
