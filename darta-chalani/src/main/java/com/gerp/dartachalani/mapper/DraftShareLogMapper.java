package com.gerp.dartachalani.mapper;


import com.gerp.dartachalani.dto.DraftShareLogPojo;
import com.gerp.dartachalani.dto.DraftSharePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DraftShareLogMapper {

    List<DraftSharePojo> getDraftShareLog(@Param("dispatchId") Long dispatchId, @Param("memoId") Long memoId);

}
