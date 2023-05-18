package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.DraftSharePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DraftShareMapper {

    List<DraftSharePojo> getDraftShareList(@Param("dispatchId") Long dispatchId, @Param("memoId") Long memoId);

}
