package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobilePrivilegeCacheDto implements Serializable {
    private String key;
    private List<Map<String, Object>> mobileValue;
}
