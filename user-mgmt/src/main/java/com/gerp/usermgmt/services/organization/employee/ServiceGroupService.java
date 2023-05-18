package com.gerp.usermgmt.services.organization.employee;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.enums.ServiceType;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.usermgmt.model.employee.Service;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;

import java.util.List;
import java.util.Map;

public interface ServiceGroupService extends GenericService<Service, String> {
    Object getAllServiceGroup();
    Object getServiceHierarchy(String serviceCode);

    List<ServicePojo> subService(String serviceCode);
    List<ServicePojo> serviceList();

    List<ServicePojo> serviceListByType(ServiceType serviceType);

    String save(ServicePojo servicePojo);

    String update(ServicePojo servicePojo);

    ServicePojo findServiceId(String id);

    Map<String, Object> findServiceIdWithHierarchy(String id);

    Page<ServicePojo> getPaginatedWithFilter(GetRowsRequest paginatedRequest);
}
