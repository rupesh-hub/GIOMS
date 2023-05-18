package com.gerp.shared.pojo.cache;

import com.gerp.shared.utils.StringConstants;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.Pattern;

@RedisHash(value = "employeeDetail", timeToLive =  3600)
public class EmployeeDetailCacheDto {

    private String pisCode;
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String nameNp;
    private String nameEn;

}
