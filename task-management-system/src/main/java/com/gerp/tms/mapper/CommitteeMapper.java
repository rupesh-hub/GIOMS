package com.gerp.tms.mapper;

import com.gerp.tms.pojo.response.CommitteeWiseProjectResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface CommitteeMapper {

    CommitteeWiseProjectResponsePojo getCommitteeWiseProject(@Param("committeeId") int committeeId);
}
