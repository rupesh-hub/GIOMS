package com.gerp.usermgmt.services.organization.employee.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.constants.cacheconstants.OfficeCacheConst;
import com.gerp.shared.enums.ServiceType;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.converter.organiztion.office.ServiceGroupConverter;
import com.gerp.usermgmt.mapper.organization.ServiceMapper;
import com.gerp.usermgmt.model.employee.Service;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePojo;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.usermgmt.repo.employee.ServiceGroupRepo;
import com.gerp.usermgmt.services.organization.employee.ServiceGroupService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class ServiceGroupServiceImpl extends GenericServiceImpl<com.gerp.usermgmt.model.employee.Service, String> implements ServiceGroupService {
    private final ServiceGroupRepo serviceGroupRepo;
    private final ServiceMapper serviceMapper;
    private final ServiceGroupConverter serviceGroupConverter;


    @Autowired
    private TokenProcessorService tokenProcessorService;

    public ServiceGroupServiceImpl(@Autowired ServiceGroupRepo repository, ServiceMapper serviceMapper, ServiceGroupConverter serviceGroupConverter) {
        super(repository);
        this.serviceGroupRepo = repository;
        this.serviceMapper = serviceMapper;
        this.serviceGroupConverter = serviceGroupConverter;
    }

    @Override
    public Object getAllServiceGroup() {
        return null;
    }

    @Override
    public Object getServiceHierarchy(String serviceCode) {
        try {
            return findServiceGroupAndSubGroup(new EmployeePojo(), serviceCode, null);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("service not found with code: " + serviceCode);
        }
    }

    @Override
    public List<ServicePojo> subService(String serviceCode) {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        return serviceMapper.subService(serviceCode, orgTypeId);
    }

    @Override
    public List<ServicePojo> serviceListByType(ServiceType serviceType) {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        return serviceMapper.getServicesByServiceType(serviceType, orgTypeId);
    }

    @Override
    public List<ServicePojo> serviceList() {
        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }
        return serviceMapper.getServices(orgTypeId);
    }

    @Transactional
    @Override
    public String save(ServicePojo servicePojo) {
        Service service = serviceGroupConverter.toEntity(servicePojo);

        service.getOrganisationType();
//        Service service1 = serviceGroupRepo.save(service);
        return serviceGroupRepo.save(service).getCode();
//
//        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
//        entityManager.getTransaction().begin();
//        entityManager1.persist(service);
//        entityManager.getTransaction().commit();
//
//        return null;




    }

    @Override
    @CacheEvict(value = OfficeCacheConst.CACHE_VALUE_SERVICE_POJO, key = "#result", condition = "#result !=null")
    public String update(ServicePojo servicePojo) {
        Service service = serviceGroupRepo.findById(servicePojo.getCode())
                .orElseThrow(() -> new ServiceValidationException("Service not found"));
        serviceGroupConverter.toEntity(servicePojo, service);
        return serviceGroupRepo.save(service).getCode();
    }

    @Override
    public ServicePojo findServiceId(String id) {
        return serviceMapper.detail(id);
    }

    @Override
    public Map<String, Object> findServiceIdWithHierarchy(String id) {

        Map<String, Object> map = new HashMap<>();

        ServicePojo servicePojo = serviceMapper.detail(id);
        if (servicePojo == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found with code: " + id);

        if (servicePojo.getServiceType() == ServiceType.SERVICE) {
            map.put(StringConstants.SERVICE, servicePojo);
            return map;
        }

        if (servicePojo.getServiceType() == ServiceType.GROUP) {
            map.put(StringConstants.GROUP, servicePojo);
            ServicePojo servicePojo1 = serviceMapper.detail(servicePojo.getParentCode() + "");
            map.put(StringConstants.SERVICE, servicePojo1);
            return map;
        }

        if (servicePojo.getServiceType() == ServiceType.SUBGROUP) {
            map.put(StringConstants.SUBGROUP, servicePojo);
            ServicePojo servicePojo1 = serviceMapper.detail(servicePojo.getParentCode() + "");
            map.put(StringConstants.GROUP, servicePojo1);
            ServicePojo servicePojo2 = serviceMapper.detail(servicePojo1.getParentCode() + "");
            map.put(StringConstants.SERVICE, servicePojo2);
            return map;
        }

        return null;
    }

    @Override
    public Page<ServicePojo> getPaginatedWithFilter(GetRowsRequest paginatedRequest) {
       Page<ServicePojo> page = new Page<>(paginatedRequest.getPage(), paginatedRequest.getLimit());
       if(!tokenProcessorService.isAdmin()) {
           if(paginatedRequest.getSearchField() == null){
               paginatedRequest.setSearchField(new HashMap<>());
           }
           paginatedRequest.getSearchField().put("organisationTypeId", tokenProcessorService.getOrganisationTypeId());
       }
        return serviceMapper.filter(page, paginatedRequest.getSearchField());
    }


    private EmployeePojo findServiceGroupAndSubGroup(EmployeePojo employeePojo, String code, ServicePojo servicePojo) {
        if (code == null) {
            return employeePojo;
        }
        ServicePojo service;
        if (servicePojo == null) {
            service = serviceMapper.detail(code);
        } else {
            service = servicePojo;
        }
        if (service.getParentCode() == null || String.valueOf(service.getParentCode()).equals("142")) {
            employeePojo.setService(service);
            return employeePojo;
        }

        ServicePojo service1 = serviceMapper.detail(service.getParentCode() + "");
        if (service1.getParentCode() == null || String.valueOf(service1.getParentCode()).equals("142")) {
            employeePojo.setGroup(service);
        } else {
            employeePojo.setSubGroup(service);
        }
        return findServiceGroupAndSubGroup(employeePojo, service.getCode(), service1);
    }
}
