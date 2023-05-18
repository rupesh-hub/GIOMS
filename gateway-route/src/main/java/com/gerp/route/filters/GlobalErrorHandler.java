package com.gerp.route.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.route.GlobalResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

  private ObjectMapper objectMapper;

  public GlobalErrorHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {

    DataBufferFactory bufferFactory = serverWebExchange.getResponse().bufferFactory();
    if (throwable instanceof NotFoundException) {
      serverWebExchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
      DataBuffer dataBuffer = null;
      try {
        dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(new GlobalResponse(false,getServiceNotAvailableMessage(Objects.requireNonNull(((NotFoundException) throwable).getReason())),null)));
      } catch (JsonProcessingException e) {
        dataBuffer = bufferFactory.wrap("".getBytes());
      }
      serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
      return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    serverWebExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
    DataBuffer dataBuffer = null;
    try {
      dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(new GlobalResponse(false, throwable.getMessage(), null)));
    }catch (JsonProcessingException e){
      dataBuffer = bufferFactory.wrap("".getBytes());
    }
     serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
  }

  private String getServiceNotAvailableMessage(String message){
    String service = message.substring(28);
    
    return getServiceName(service)+" is not online. Please contact Operator";
  }

  private String getServiceName(String name){
    switch (name){
      case "attendance":
        return "Attendance";
      case "darta-chalani":
        return "Darta Chalani";
      case "usermgmt":
        return "User Management / Organization Profiling";
      case "kasamu":
        return "Ka Sa Mu";
      default:
        return "Unknown Service";
    }
  }

}
