package com.gerp.kasamu.mapper;

import com.gerp.kasamu.model.committee.Committee;
import com.gerp.kasamu.pojo.response.CommitteeIndicatorResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CommitteeIndicatorMapper {
    List<CommitteeIndicatorResponsePojo> getCommitteeIndicator(@Param("kasamuMasterId") Long kasamuMasterId,@Param("id") Long id);

    @Select("select * from committee where kasamu_master_id=#{id}")
    List<Committee> getAllCommittee(@Param("id") Long id);

    @Select("select * from committee where kasamu_master_id=#{id}::INTEGER and pis_code = #{pisCode} and decision is null")
    Committee findByPisCodeAndKasamuId(@Param("id") Long id,@Param("pisCode") String pisCode);
}
