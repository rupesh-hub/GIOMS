package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.DigitalSignatureDto;
import com.gerp.attendance.Pojo.MasterDashboardPojo;
import com.gerp.attendance.Pojo.OfficePojo;
import com.gerp.attendance.Pojo.VerificationInformation;
import com.gerp.attendance.mapper.UserMgmtMapper;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import lombok.SneakyThrows;
import org.jboss.logging.Logger;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@CacheConfig(cacheNames = {"dartaChalani"})
public class DartaChalaniServiceData extends BaseController {
    private static final Logger LOG = Logger.getLogger(DartaChalaniServiceData.class);

    private final DartaChalaniServiceProxy dartaChalaniServiceProxy;

    public DartaChalaniServiceData(DartaChalaniServiceProxy dartaChalaniServiceProxy) {
        this.dartaChalaniServiceProxy = dartaChalaniServiceProxy;
    }

    @SneakyThrows
    @Cacheable(value = "dartaChalani", key = "#dartaCount")
    public List<MasterDashboardPojo> getDartaChalani(Timestamp fromDate, Timestamp toDate) {
        ResponseEntity<GlobalApiResponse> responseEntity = dartaChalaniServiceProxy.getDartaTotal(fromDate,toDate);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            List<MasterDashboardPojo> dartaDashboard= objectMapper.convertValue(globalApiResponse.getData(), List.class);
            if(dartaDashboard == null) {
                LOG.info("No value present in darta chalani");
            }
            return dartaDashboard;
        } else {
            return null;
        }
    }

    @SneakyThrows
    public DigitalSignatureDto generateHash(DigitalSignatureDto digitalSignatureDto) {
        ResponseEntity<GlobalApiResponse> responseEntity = dartaChalaniServiceProxy.generateHasValue(digitalSignatureDto);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), DigitalSignatureDto.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public VerificationInformation verify(DigitalSignatureDto digitalSignatureDto) {
        ResponseEntity<GlobalApiResponse> responseEntity = dartaChalaniServiceProxy.verifySignature(digitalSignatureDto);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            return objectMapper.convertValue(globalApiResponse.getData(), VerificationInformation.class);
        }
        else {
            return null;
        }
    }
}
