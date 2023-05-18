package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SabikDetail {
    public String group;
    public String service;
    public String category;
    public String rajPatraNo;

    public SabikDetail(String category, String rajPatraNo) {
        this.category = category;
        this.rajPatraNo = rajPatraNo;
    }
}

