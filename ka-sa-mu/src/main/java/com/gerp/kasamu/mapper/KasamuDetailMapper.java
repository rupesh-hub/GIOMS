package com.gerp.kasamu.mapper;

import com.gerp.kasamu.pojo.response.KasamuDetailResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface KasamuDetailMapper {
    List<KasamuDetailResponsePojo> getKasamuDetails(@Param("id") Long id, @Param("employeeCode") Long employeeCode);
}
