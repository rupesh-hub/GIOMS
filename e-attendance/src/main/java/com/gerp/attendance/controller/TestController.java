package com.gerp.attendance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Proxy.AttendanceDeviceProxyService;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.service.EmployeeAttendanceService;
import com.gerp.attendance.service.LeaveRequestService;
import com.gerp.shared.generic.controllers.BaseController;
import feign.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalTime;
import java.util.*;


@RestController
@RequestMapping("/test")
public class TestController extends BaseController {
//    @Autowired
//    private UserMgmtProxy microserviceTwoService;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    @Autowired
    private EmployeeAttendanceService attendanceService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private AttendanceDeviceProxyService attendanceDeviceProxyService;

    @GetMapping
//    @PreAuthorize("hasPermission('','create')")
    public ResponseEntity<?> test() {
//        return microserviceTwoService.testMicroTwoApi();
        return ResponseEntity.ok("test");
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient webClient;

    String url = "http://192.168.50.130:2012/attlog/getdata";

    //    @GetMapping("/get-post/{id}")
//    public ResponseEntity<?> getAllPost(@PathVariable("id") Long id ){
//      TestPojo testPojo= restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/"+id,TestPojo.class);
//
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get_all", "Gender"), testPojo));
//
//    }

//    @GetMapping("/fiscal-year")
//    public ResponseEntity<?> getAllFiscal(){
//           List<IdNamePojo> idNamePojos= userMgmtServiceData.findActiveFiscalYear();
//
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get_all", "Gender"),idNamePojos.get(0)));
//
//    }


    @GetMapping("/get-post/{id}")
    public TestPojo getAllPost(@PathVariable("id") Long id ){
        TestPojo testPojo= webClient.get().uri("/posts/"+id)
               .retrieve()
               .bodyToMono(TestPojo.class).block();
        System.out.println("checking"+testPojo);
        return testPojo;

    }

    @PostMapping("/get-data")
    @Headers("Content-Type: application/json")
    public List<TestResultPojo> create()
    {
//        TestAttendancePojo testAttendancePojo=new TestAttendancePojo().builder()
//                .AttendanceDeviceTypeId(Long.parseLong("1"))
//                .ClientAlias("Admin")
//                .DeviceMachineNo(Long.parseLong("1"))
//                .DeviceTypeName("ZkTeco")
//                .IPAddress("192.168.20.201")
//                .Id(Long.parseLong("1"))
//                .Port(Long.parseLong("4370"))
//                .Status(Long.parseLong("1"))
//                .StatusChgUserId(Long.parseLong("1")).build();
//        Map<String, String> map = new HashMap<>();
//        map.put("AttendanceDeviceTypeId","1");
//        map.put("ClientAlias","Admin");
//        map.put("DeviceMachineNo","1");
//        map.put("DeviceTypeName","ZkTeco");
//        map.put("IPAddress","192.168.20.201");
//        map.put("Id","1");
//        map.put("Port","4370");
//        map.put("Status","1");
//        map.put("StatusChgUserId","1");

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();

        body.add("AttendanceDeviceTypeId",1);
        body.add("ClientAlias","Admin");
        body.add("DeviceMachineNo",1);
        body.add("DeviceTypeName", "ZkTeco");
        body.add("IPAddress", "192.168.20.201");
        body.add("Id", 1);
        body.add("Port",4370);
        body.add("Status",1);
        body.add("StatusChgUserId",1);


        ResponseEntity<Object> response = restTemplate.postForEntity(url, body, Object.class);
              System.out.println(response.getBody());
        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
        final TestResponsePojo pojo = mapper.convertValue(response.getBody(),TestResponsePojo.class);
        System.out.println(pojo);
        if(pojo.getDataRow()!=null){
            System.out.println("I am checking");
        }
//        TestResponsePojo testResponsePojo = response.getBody();
//        if (testResponsePojo.getDataRow()!=null) {
//            System.out.println("checking data" + testResponsePojo.getDataRow());
//            return testResponsePojo.getDataRow();
//        }
//        else
            return null;
//        TestResponsePojo testResponsePojo = objectMapper.convertValue(response.getBody(), TestResponsePojo.class);
//        if (testResponsePojo.getResultType()==1) {
////            testResponsePojo.getDataRow().stream().forEach(x-);
//            System.out.println("checking data" + testResponsePojo.getMessage());
//            return (TestFinalPojo) testResponsePojo.getDataRow().get(0);
//        }
//        else {
//            System.out.println("checking data"+testResponsePojo.getMessage());
//            return null;
//        }
//        if (response.getStatusCode() == HttpStatus.CREATED) {
//            System.out.println("Request Successful");
//            System.out.println(response.getBody());
//        } else {
//            System.out.println("Request Failed");
//            System.out.println(response.getStatusCode());
//        }


//        TestFinalPojo testFinalPojo=webClient.post()
//                .uri("/getdata")
//                .body(Mono.just(testAttendancePojo), TestAttendancePojo.class)
//                .retrieve()
//                .bodyToMono(TestFinalPojo.class).block();

//        TestFinalPojo testFinalPojo= restTemplate.postForObject("http://192.168.50.130:2012/attlog/getdata",testAttendancePojo,TestFinalPojo.class);

//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get_all", "Gender"), testFinalPojo));
//        System.out.println("checking data in test result"+testFinalPojo.getResultType());
//        return testFinalPojo;
    }


    @GetMapping("/check-post")
    public ResponseEntity<?> getAllPost(){
        TestPojo testPojos= attendanceDeviceProxyService.findAllPosts();

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "Gender"), testPojos));

    }


    @GetMapping("/check-data")
    public void getData(){
//         attendanceService.checkingdata();

    }


//    @GetMapping("/check-post")
//    public ResponseEntity<?> getEmployeeData(){
//        EmployeeNamePojo testPojos=leaveRequestService.checkEmployee();
//
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get_all", "Gender"), testPojos));
//
//    }

//map.put("name", "John Doe");
//map.put("job", "Java Developer");

}
