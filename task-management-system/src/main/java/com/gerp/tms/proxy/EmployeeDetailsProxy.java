package com.gerp.tms.proxy;

import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.FiscalYearPojo;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.tms.pojo.OfficeDetailPojo;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.jboss.logging.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeDetailsProxy extends BaseController {
    private final EmployeeDetails employeeDetails;

    private static final Logger LOG = Logger.getLogger(EmployeeDetailsProxy.class);
    public EmployeeDetailsProxy(EmployeeDetails employeeDetails) {
        this.employeeDetails = employeeDetails;
    }

    @SneakyThrows
    @Cacheable(value = "employeeMinimal",key = "#pisCode")
    public EmployeeMinimalPojo getEmployeeDetailMinimal(String pisCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getEmployeeDetailMinimal(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), EmployeeMinimalPojo.class);
        }
        LOG.error(new Gson().toJson(globalApiResponse));
        return null;
    }

    @SneakyThrows
    @Cacheable(value = "officeDetail",key = "#pisCode")
    public OfficeDetailPojo getOfficeDetail(String pisCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getOfficeDetail(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), OfficeDetailPojo.class);
        }
        LOG.error(new Gson().toJson(globalApiResponse));
        return null;
    }

    @SneakyThrows
    @Cacheable(value = "officeParentDetail",key = "#pisCode")
    public OfficeDetailPojo getOfficeDetailParentOffice(String pisCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getOfficeDetailParentOffice(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), OfficeDetailPojo.class);
        }
        LOG.error(new Gson().toJson(globalApiResponse));
        return null;
    }

    @SneakyThrows
    @Cacheable(value = "employeeMinimal",key = "#officeCode")
    public EmployeeMinimalPojo getOfficeHead(String officeCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getOfficeHead(officeCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), EmployeeMinimalPojo.class);
        }
        LOG.error(new Gson().toJson(globalApiResponse));
        return null;
    }

    @SneakyThrows
    @Cacheable(value = "fiscalYear",key = "#fiscalYear")
    public FiscalYearPojo getFiscalYear(String fiscalYear) {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getFiscalYear(fiscalYear);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), FiscalYearPojo.class);
        }
        LOG.error(new Gson().toJson(globalApiResponse));
        return null;
    }
}
