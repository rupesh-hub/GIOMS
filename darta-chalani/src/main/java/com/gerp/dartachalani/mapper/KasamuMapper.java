package com.gerp.dartachalani.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.kasamu.ExternalEmployeeResponsePojo;
import com.gerp.dartachalani.dto.kasamu.KasamuResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Map;

@Mapper
public interface KasamuMapper {

    @Select("select pis_code as pisCode, name, service_code as serviceCode, group_code as groupCode, sub_group_code as subGroupCode,\n" +
            "current_office_name as currentOfficeName, designation_code as designationCode, position_code as positionCode\n" +
            "from external_kasamu_employee where kasamu_id = #{id}")
    ExternalEmployeeResponsePojo getExternalEmployeeByKasamuId(@Param("id") Long id);

    Page<KasamuResponsePojo> getCreatedKasamuList(Page<KasamuResponsePojo> page, @Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode, @Param("searchField") Map<String, Object> searchField);

    Page<KasamuResponsePojo> getFinalizedKasamuList(Page<KasamuResponsePojo> page, @Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode, @Param("searchField") Map<String, Object> searchField);

    Page<KasamuResponsePojo> getEmployeeKasamuList(Page<KasamuResponsePojo> page, @Param("employeePisCode") String pisCode, @Param("employeeSectionCode") String sectionCode, @Param("searchField") Map<String, Object> searchField);

    Page<KasamuResponsePojo> getKasamuInboxList(Page<KasamuResponsePojo> page, @Param("receiverPisCode") String pisCode, @Param("receiverSectionCode") String sectionCode, @Param("searchField") Map<String, Object> searchField);

    List<String> getPisCodeInboxList(@Param("receiverPisCode") String receiverPisCode, @Param("receiverSectionCode") String receiverSectionCode, @Param("searchField") Map<String, Object> searchField);

    List<String> getExternalEmployeeInboxList(@Param("receiverPisCode") String receiverPisCode, @Param("receiverSectionCode") String receiverSectionCode, @Param("searchField") Map<String, Object> searchField);

    List<String> getPisCodeCreatedList(@Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode, @Param("searchField") Map<String, Object> searchField);

    List<String> getExternalEmployeeCreatedList(@Param("pisCode") String pisCode, @Param("sectionCode") String sectionCode, @Param("searchField") Map<String, Object> searchField);

    List<String> getPisCodeFinalizedList(@Param("receiverPisCode") String pisCode, @Param("receiverSectionCode") String sectionCode, @Param("searchField") Map<String, Object> searchField);

    List<String> getExternalEmployeeFinalizedList(@Param("receiverPisCode") String pisCode, @Param("receiverSectionCode") String sectionCode, @Param("searchField") Map<String, Object> searchField);
}
