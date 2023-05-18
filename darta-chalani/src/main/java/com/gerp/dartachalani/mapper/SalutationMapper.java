package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.SalutationPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SalutationMapper {
    List<SalutationPojo> findByCreator(@Param("creator") String code, @Param("pisCode") String pisCode);
}
