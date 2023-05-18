package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScGrp {
    private String screenGroup;
    private List<String> screen;
}
