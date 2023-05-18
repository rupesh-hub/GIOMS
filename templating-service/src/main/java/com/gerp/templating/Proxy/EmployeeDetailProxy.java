package com.gerp.templating.Proxy;

import com.gerp.shared.pojo.GlobalApiResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


@Component
public class EmployeeDetailProxy<T> {

    @Autowired
    EmployeeDetails employeeDetails;

    @SneakyThrows
    @Cacheable(value = "employeeMinimal", key = "#pisCode")
    public T getEmployeeDetailMinimal(String pisCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getEmployeeDetailMinimal(pisCode);
        if (!responseEntity.getBody().getStatus().toString().equals("SUCCESS")) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Employee Do Not exist !!!");
        }
        return (T) responseEntity.getBody().getData();
//        return null;
    }

    @SneakyThrows
    @Cacheable(value = "officeDetail", key = "#officeCode")
    public T getOfficeDetail(String officeCode) {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getOfficeDetail(officeCode);
        if (!responseEntity.getBody().getStatus().toString().equals("SUCCESS")) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Office Do Not exist !!!");
        }
        return (T) responseEntity.getBody().getData();
//        return null;
    }

    @SneakyThrows
    //@Cacheable(value = "getEmployeeListOfLoggedInOffice")
    public T getEmployeeByOffice() {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getEmployeeListOfLoggedInOffice();
        return (T) responseEntity.getBody().getData();
    }

    @SneakyThrows
    public T getActiveFiscalYear() {
        ResponseEntity<GlobalApiResponse> responseEntity = employeeDetails.getActiveFiscalYear();
        return (T) responseEntity.getBody().getData();

    }

}
