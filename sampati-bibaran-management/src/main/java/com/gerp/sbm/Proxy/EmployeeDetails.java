package com.gerp.sbm.Proxy;

import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "EmployeeDetail",url = "http://103.69.124.84:9090/usermgmt")
//@FeignClient(name = "EmployeeDetail",url = "http://localhost:9001")
@RibbonClient(EmployeeDetails.SERVICE_NAME)
public interface EmployeeDetails {
    String SERVICE_NAME="usermgmt";
//    @GetMapping("/employee/employee-detail-minimal/{pisCode}")
//    ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(@PathVariable(name = "pisCode") String pisCode);

    @GetMapping("/employee/employee-detail")
    ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(@RequestParam(name = "pisCode") String pisCode);

    @GetMapping("/office/detail?officeCode={pisCode}")
    ResponseEntity<GlobalApiResponse> getOfficeDetail(@PathVariable(name = "pisCode") String pisCode);


    @GetMapping("/employee/office-employee-list")
    ResponseEntity<GlobalApiResponse> getEmployeeListOfLoggedInOffice();
    @GetMapping("/fiscal-year/get-active-year")
    ResponseEntity<GlobalApiResponse> getActiveFiscalYear();

}
