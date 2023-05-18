package com.gerp.dartachalani.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class CustomResponseEntity<T, P> {

    private HttpStatus status;
    private String message;
    private T payload;
    private P pagination;

    public CustomResponseEntity(HttpStatus httpStatus, String message, T payload){
        this.status = httpStatus;
        this.message = message;
        this.payload = payload;
    }

    public CustomResponseEntity(HttpStatus httpStatus, String message){
        this.status = httpStatus;
        this.message = message;
    }

    public CustomResponseEntity(HttpStatus httpStatus, String message, T payload, P pagination){
        this.status = httpStatus;
        this.message = message;
        this.payload = payload;
        this.pagination = pagination;
    }
}
