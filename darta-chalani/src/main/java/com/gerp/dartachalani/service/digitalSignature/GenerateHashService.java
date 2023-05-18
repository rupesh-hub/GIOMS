package com.gerp.dartachalani.service.digitalSignature;

import com.gerp.dartachalani.dto.DigitalSignatureDto;

public interface GenerateHashService {
    DigitalSignatureDto generateHash(DigitalSignatureDto digitalSignatureDto);

    VerificationInformation verify(DigitalSignatureDto digitalSignatureDto);
}
