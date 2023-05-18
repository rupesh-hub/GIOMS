package com.gerp.templating.entity;

import lombok.Data;

import java.util.List;

@Data
public class GeneralTemplateOne {

    private String logo_url;

    private String header;

    private String letter_date;

    private String chali_no;

    private String letter_no;

    private String request_to_office;

    private String request_to_office_address;

    private String subject;

    private String body_message;

    private String requester_name;

    private String requester_position;

    private List<Bodartha> bodartha;

    private String footer;

    private List<RequestTo> request_to_many;
}
