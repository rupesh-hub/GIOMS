package com.gerp.attendance.Proxy;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.attendance.Pojo.TestResponsePojo;
import com.gerp.attendance.Pojo.TestResultPojo;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class AttendanceDeviceServiceData {

    @Autowired
    private RestTemplate restTemplate;

//    String url = "http://103.109.230.17:2012/attlog/getdata";
    @Value("${attendance.dotnet.api}")
    private String DOTNET_API;

    @SneakyThrows
    public List<TestResultPojo> create(Map<String, Object> attendancePojo) {
        System.out.println(new Gson().toJson(attendancePojo));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(new Gson().toJson(attendancePojo), headers);

        ResponseEntity<Object> response = restTemplate.postForEntity(DOTNET_API, entity, Object.class);
        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
        final TestResponsePojo pojo = mapper.convertValue(response.getBody(),TestResponsePojo.class);
        if(pojo.getDataRow()!=null){
            return pojo.getDataRow();
        }
        else
            return null;
    }

}
