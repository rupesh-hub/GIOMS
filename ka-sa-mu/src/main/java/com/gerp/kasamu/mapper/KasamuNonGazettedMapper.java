package com.gerp.kasamu.mapper;

import com.gerp.kasamu.pojo.request.KasamuForNoGazettedPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface KasamuNonGazettedMapper {
    List<KasamuForNoGazettedPojo> getNonGazettedList(@Param("id") Long id, @Param("employeeCode") Long code);

}
