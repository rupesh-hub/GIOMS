package com.gerp.usermgmt.services.device;

import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.mapper.usermgmt.UserMapper;
import com.gerp.usermgmt.model.device.PisCodeDeviceIdMapper;
import com.gerp.usermgmt.pojo.device.PisCodeToDeviceMapperPojo;
import com.gerp.usermgmt.repo.device.PisCodeDeviceIdMapperRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PisCodeDeviceIdMapperServiceImpl extends GenericServiceImpl<PisCodeDeviceIdMapper, Long> implements PisCodeDeviceIdMapperService {
    private final PisCodeDeviceIdMapperRepo pisCodeDeviceIdMapperRepo;
    @Autowired private UserMapper userMapper;

    public PisCodeDeviceIdMapperServiceImpl(PisCodeDeviceIdMapperRepo pisCodeDeviceIdMapperRepo) {
        super(pisCodeDeviceIdMapperRepo);
        this.pisCodeDeviceIdMapperRepo = pisCodeDeviceIdMapperRepo;
    }

    @Override
    public void saveDeviceId(PisCodeToDeviceMapperPojo pisCodeToDeviceMapperPojo) {
        PisCodeDeviceIdMapper pisCodeDeviceIdMapper1 = pisCodeDeviceIdMapperRepo.findByDeviceId(pisCodeToDeviceMapperPojo.getDeviceID());
        if(pisCodeDeviceIdMapper1 != null) throw new CustomException("Device mapper id already used.");
        PisCodeDeviceIdMapper pisCodeDeviceIdMapper = pisCodeDeviceIdMapperRepo.findByPisCodeAndOfficeCode(pisCodeToDeviceMapperPojo.getPisCode(), pisCodeToDeviceMapperPojo.getOfficeCode());
        if(pisCodeDeviceIdMapper==null)
            pisCodeDeviceIdMapper = PisCodeDeviceIdMapper.builder()
                .deviceId(pisCodeToDeviceMapperPojo.getDeviceID())
                .pisCode(pisCodeToDeviceMapperPojo.getPisCode().trim().toUpperCase())
                .officeCode(pisCodeToDeviceMapperPojo.getOfficeCode())
                .build();
        else
            pisCodeDeviceIdMapper.setDeviceId(pisCodeToDeviceMapperPojo.getDeviceID());
        pisCodeDeviceIdMapperRepo.save(pisCodeDeviceIdMapper);
    }

    @Override
    public Long getDeviceIdByPisCode(String pisCode, String officeCode) {
        return userMapper.getDeviceIdByPisCode(pisCode, officeCode);
    }
}
