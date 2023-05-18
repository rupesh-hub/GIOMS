package com.gerp.dartachalani.config.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse<T, V> {

  private T payload;

  private V pages;

  private String message;

  private HttpStatus httpStatus;

  public CustomResponse(T payload, String message, HttpStatus httpStatus) {
    this.payload = payload;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
