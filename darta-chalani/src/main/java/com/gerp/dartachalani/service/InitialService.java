package com.gerp.dartachalani.service;

import com.gerp.dartachalani.config.exception.CustomException;
import com.gerp.dartachalani.dto.DCNumberPojo;
import com.gerp.dartachalani.dto.InitialPojo;

public interface InitialService {

    void save(DCNumberPojo data) throws CustomException;

    String getDartaNumber(String officeCode);

    String getChalaniNumber(String sectionCode);

    Long getMemoNumber(String officeCode);

    InitialPojo getByOfficeCode(String officeCode);
}
