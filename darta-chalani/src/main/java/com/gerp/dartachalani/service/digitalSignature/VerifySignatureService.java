package com.gerp.dartachalani.service.digitalSignature;

public interface VerifySignatureService {

    VerificationInformation verify(String content, String signature, Boolean isHashed);

}
