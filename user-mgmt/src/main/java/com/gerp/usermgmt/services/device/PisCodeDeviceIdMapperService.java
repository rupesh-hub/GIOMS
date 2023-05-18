package com.gerp.usermgmt.services.device;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.device.PisCodeDeviceIdMapper;
import com.gerp.usermgmt.pojo.device.PisCodeToDeviceMapperPojo;

public interface PisCodeDeviceIdMapperService extends GenericService<PisCodeDeviceIdMapper, Long> {
    Long getDeviceIdByPisCode(String pisCode, String officeCode);

    void saveDeviceId(PisCodeToDeviceMapperPojo pisCodeToDeviceMapperPojo);
}
