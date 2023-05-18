package com.gerp.usermgmt.pojo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CustomPage<T> extends Page<T> {
    private static final long serialVersionUID = 5194933845448697148L;

    private Integer selectInt;
    private String selectStr;
    private String name;

    public CustomPage(long current, long size) {
        super(current, size);
    }
}
