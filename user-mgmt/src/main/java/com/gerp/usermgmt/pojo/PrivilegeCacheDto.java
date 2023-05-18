package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeCacheDto implements Serializable {
    private String key;
    private Map<String, Map<String, Map<String, Object>>> value;
}
