package com.gerp.tms.service;

import com.gerp.tms.pojo.response.OfficeWiseProjectResponsePojo;

import java.util.List;

public interface OfficeService {

    List<OfficeWiseProjectResponsePojo> getOfficeWiseProject( String officeId);
}
