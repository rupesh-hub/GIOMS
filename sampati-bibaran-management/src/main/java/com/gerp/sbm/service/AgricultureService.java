package com.gerp.sbm.service;

import com.gerp.sbm.pojo.RequestPojo.AgricultureDetailRequestPojo;

public interface AgricultureService {
    Long addAgriculture(AgricultureDetailRequestPojo agricultureService);

    Long updateAgriculture(AgricultureDetailRequestPojo agricultureDetailRequestPojo);

}
