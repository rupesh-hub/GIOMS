package com.gerp.dartachalani.dto.sms;

import java.util.List;

public class OtpResponse {
    private String message;
    private String errors;
    private int status;
    private int ntc;
    private int ncell;
    private List<String> invalid_number;
    private String exception;

    public OtpResponse() {
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrors() {
        return this.errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNtc() {
        return this.ntc;
    }

    public void setNtc(int ntc) {
        this.ntc = ntc;
    }

    public int getNcell() {
        return this.ncell;
    }

    public void setNcell(int ncell) {
        this.ncell = ncell;
    }

    public List<String> getInvalid_number() {
        return this.invalid_number;
    }

    public void setInvalid_number(List<String> invalid_number) {
        this.invalid_number = invalid_number;
    }

    public String getException() {
        return this.exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}