package com.gerp.usermgmt.mapper.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.pojo.organization.employee.FunctionalDesignationPojo;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

@Mapper
public interface DesignationMapper {
    List<IdNamePojo> getDesignationByFilter(@Param("map") Map<String, Object> map, @Param("orgTypeId") Long orgTypeId);
    List<IdNamePojo> getOfficeDesignationByFilter(@Param("map") Map<String, Object> map, @Param("orgTypeId") Long orgTypeId);

    List<IdNamePojo> getOfficeDesignations(@Param("officeCode") String officeCode, @Param("orgTypeId") Long orgTypeId);

    List<IdNamePojo> getSectionDesignations(@Param("sectionId") Long sectionId, @Param("orgTypeId") Long orgTypeId);

    Page<FunctionalDesignationPojo> filterPaginated(@Param("page")Page<FunctionalDesignationPojo> page, @Param("searchField")Map<String, Object> searchField,
                                                    @Param("orgTypeId") Long orgTypeId);

    @Select("select code, name_en, name_np from functional_designation fd where fd.code  =  #{code}")
    DetailPojo getDesignationDetailById(@Param("code") String code);
}
