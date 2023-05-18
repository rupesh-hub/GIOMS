package com.gerp.shared.pojo;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponsePojo {
    private HttpStatus status;

    private String message;

    private String data;
}
