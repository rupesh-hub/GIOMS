package com.gerp.usermgmt.converter.organiztion.office;

import com.gerp.shared.enums.ServiceType;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.usermgmt.model.employee.Service;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import org.apache.commons.beanutils.BeanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceGroupConverter extends AbstractConverter<ServicePojo , Service> {

    @Autowired
    ModelMapper modelMapper;
    @Override
    public Service toEntity(ServicePojo dto) {
        Service service = new Service();
       try {
           BeanUtils.copyProperties(service,dto);
       } catch (Exception exception) {
           throw new ServiceValidationException("Invalid Service Data ");
       }
       if(dto.getParentCode() != null) {
           service.setParent(new Service(dto.getParentCode()));
       }
        if(dto.getServiceType().equals(ServiceType.SERVICE)) {
            service.setParent(new Service("142"));

        }
        return service;
    }

    @Override
    public Service toEntity(ServicePojo dto, Service service) {
        try {
            BeanUtils.copyProperties(service,dto);
        } catch (Exception exception) {
            throw new ServiceValidationException("Invalid Service Data ");
        }
        if(dto.getParentCode() != null) {
            service.setParent(new Service(dto.getParentCode()));
        }
        return service;
    }

    @Override
    public ServicePojo toDto(Service entity) {
        return super.toDto(entity);
    }

    @Override
    public List<Service> toEntity(List<ServicePojo> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<ServicePojo> toDto(List<Service> entityList) {
        return super.toDto(entityList);
    }
}
