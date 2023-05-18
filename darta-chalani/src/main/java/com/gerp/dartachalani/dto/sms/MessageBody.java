package com.gerp.dartachalani.dto.sms;

public class MessageBody {
    private String message;
    private String mobile;

    public MessageBody() {
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}