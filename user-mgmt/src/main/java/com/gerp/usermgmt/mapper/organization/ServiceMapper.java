package com.gerp.usermgmt.mapper.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.enums.ServiceType;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

@Mapper
public interface ServiceMapper {
    List<ServicePojo> subService(@Param("serviceCode") String serviceCode, @Param("orgTypeId") Long orgTypeId);

    List<ServicePojo> getServices(@Param("orgTypeId") Long orgTypeId);

    @Select("select code, name_en, name_np,parent_code , service_type, is_active from service where code = #{serviceCode} and code != '142'")
    ServicePojo detail(String serviceCode);

    List<ServicePojo> getServicesByServiceType(@Param("serviceType") ServiceType serviceType, @Param("orgTypeId") Long orgTypeId);

    Page<ServicePojo> filter(Page<ServicePojo> page,@Param("searchField") Map<String, Object> searchField);
}
