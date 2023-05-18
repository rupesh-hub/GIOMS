package com.gerp.sbm.Proxy;

import com.gerp.shared.pojo.GlobalApiResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class NepaliDateProxy<T> {

    @Autowired
    private NepaliDate nepaliDate;

    @SneakyThrows
    //@Cacheable(value = "employeeMinimal")
    public void date() {
        ResponseEntity<?> responseEntity = nepaliDate.getNepaliDate();
        System.out.println(responseEntity.toString());
       // return (T) responseEntity.getBody();
//        return null;
    }
}
