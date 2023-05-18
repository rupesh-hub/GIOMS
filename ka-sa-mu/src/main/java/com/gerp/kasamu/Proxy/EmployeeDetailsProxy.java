package com.gerp.kasamu.Proxy;

import com.gerp.kasamu.pojo.EmployeePojo;
import com.gerp.kasamu.pojo.OfficeDetailPojo;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.jboss.logging.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
        if (pisCode == null || pisCode.equals("")){
            return null;
        }
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getEmployeeDetailMinimal(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), EmployeeMinimalPojo.class);
        }
        LOG.error(new Gson().toJson(globalApiResponse));
        return null;
    }

    @SneakyThrows
    @Cacheable(value = "employeeMinimal",key = "#pisCode")
    public EmployeePojo getEmployeeDetail(String pisCode) {
        if (pisCode == null || pisCode.equals("")){
            return null;
        }
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getEmployeeDetail(pisCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), EmployeePojo.class);
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
}
