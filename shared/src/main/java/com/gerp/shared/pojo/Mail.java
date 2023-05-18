package com.gerp.shared.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Mail<T> {

    private String to;
    private String subject;
    private String template;
    private List<Object> attachments;
    private T model;

    public void setUserData(T data, String email, String subject) {
        this.setSubject(subject);
        this.setTo(email);
        this.setModel(data);
    }
}
