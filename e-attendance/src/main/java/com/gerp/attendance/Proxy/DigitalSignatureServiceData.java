package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.DigitalSignatureDto;
import com.gerp.attendance.Pojo.VerificationInformation;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import lombok.SneakyThrows;
import org.jboss.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DigitalSignatureServiceData extends BaseController {
    private static final Logger LOG = Logger.getLogger(DigitalSignatureServiceData.class);

    private final DigitalSignatureServiceProxy digitalSignatureServiceProxy;
    private final DartaChalaniServiceProxy dartaChalaniServiceProxy;

    public DigitalSignatureServiceData(DigitalSignatureServiceProxy digitalSignatureServiceProxy, DartaChalaniServiceProxy dartaChalaniServiceProxy) {
        this.digitalSignatureServiceProxy = digitalSignatureServiceProxy;
        this.dartaChalaniServiceProxy = dartaChalaniServiceProxy;
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
