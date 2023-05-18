package com.gerp.tms.proxy;

import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "EmployeeDetail",url = "http://103.69.124.84:9090/usermgmt")
@RibbonClient(EmployeeDetails.SERVICE_NAME)
public interface EmployeeDetails {
    String SERVICE_NAME="usermgmt";
    @GetMapping("/employee/employee-detail-minimal/{pisCode}")
    ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(@PathVariable(name = "pisCode") String pisCode);

    @GetMapping("/office/detail?officeCode={pisCode}")
    ResponseEntity<GlobalApiResponse> getOfficeDetail(@PathVariable(name = "pisCode") String pisCode);

    @GetMapping("/office/officeByCode")
    ResponseEntity<GlobalApiResponse> getOfficeDetailParentOffice(@RequestParam(name = "officeCode") String officeCode);

    @GetMapping("/employee/office-head")
    ResponseEntity<GlobalApiResponse> getOfficeHead(@RequestParam(name = "officeCode") String officeCode);

    @GetMapping("/fiscal-year/detail")
    ResponseEntity<GlobalApiResponse> getFiscalYear(@RequestParam(name = "fiscalYear") String fiscalYear);

}
