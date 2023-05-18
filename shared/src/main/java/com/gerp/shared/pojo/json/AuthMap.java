package com.gerp.shared.pojo.json;

import lombok.Data;

import java.util.List;

@Data
public class AuthMap {
    private String moduleKey;
    private List<ApiDetail> apis;
}
