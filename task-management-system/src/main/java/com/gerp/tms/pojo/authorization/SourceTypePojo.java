package com.gerp.tms.pojo.authorization;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SourceTypePojo {

    private Integer id;
    private String sourceTypeUcd;
    private String sourceTypeNameE;
    private String sourceTypeNameN;
    private SourcePojo source;

}
