//package com.gerp.templating.Proxy.fallback;
//
//import com.gerp.sbm.Proxy.MicroserviceOneService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MicroserviceOneFallback implements MicroserviceOneService {
//    private Exception exception;
//    public MicroserviceOneFallback injectException(Exception cause ) {
//        exception=cause;
//        return this;
//    }
//
//    private ResponseEntity<?> getResponse(){
//        String message=exception.getMessage();
//        String excep = message.substring(0,34);
//        if(excep.equals("com.netflix.client.ClientException"))
//            message = "unavailable";
//        return ResponseEntity.ok(message);
//    }
//
//}
