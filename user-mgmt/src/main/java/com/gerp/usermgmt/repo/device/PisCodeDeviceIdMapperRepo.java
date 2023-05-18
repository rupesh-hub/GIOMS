package com.gerp.usermgmt.repo.device;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.device.PisCodeDeviceIdMapper;
import org.springframework.data.jpa.repository.Query;

public interface PisCodeDeviceIdMapperRepo extends GenericRepository<PisCodeDeviceIdMapper, Long> {

    @Query(value = "select * from piscode_device_id_mapper where pis_code= ?1 and office_code= ?2 ", nativeQuery = true)
    PisCodeDeviceIdMapper findByPisCodeAndOfficeCode(String pisCode, String officeCode);

    @Query("select p from PisCodeDeviceIdMapper p where p.deviceId = ?1")
    PisCodeDeviceIdMapper findByDeviceId(Long deviceId);
}
