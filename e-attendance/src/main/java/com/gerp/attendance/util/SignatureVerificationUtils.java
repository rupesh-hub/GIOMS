package com.gerp.attendance.util;

import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.approvalActivity.ApprovalActivityPojo;
import com.gerp.attendance.Proxy.DartaChalaniServiceData;
import com.gerp.attendance.mapper.*;
import com.gerp.shared.enums.TableEnum;
import com.gerp.shared.exception.ServiceValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SignatureVerificationUtils {

    @Autowired
    private DartaChalaniServiceData dartaChalaniServiceData;

    private final LeaveRequestMapper leaveRequestMapper;
    private final ManualAttendanceMapper manualAttendanceMapper;
    private final KaajRequestMapper kaajRequestMapper;
    private final DailyLogMapper dailyLogMapper;
    private final DecisionApprovalMapper decisionApprovalMapper;

    public SignatureVerificationUtils(LeaveRequestMapper leaveRequestMapper, ManualAttendanceMapper manualAttendanceMapper, KaajRequestMapper kaajRequestMapper, DailyLogMapper dailyLogMapper, DecisionApprovalMapper decisionApprovalMapper) {
        this.leaveRequestMapper = leaveRequestMapper;
        this.manualAttendanceMapper = manualAttendanceMapper;
        this.kaajRequestMapper = kaajRequestMapper;
        this.dailyLogMapper = dailyLogMapper;
        this.decisionApprovalMapper = decisionApprovalMapper;
    }

    public boolean verifyHash(DigitalSignatureDto digitalSignatureDto) {
        String savedHashValue = digitalSignatureDto.getHashValue();

        // reassign dto with generated value. content is in dto which is hashed and returned
        digitalSignatureDto = dartaChalaniServiceData
                .generateHash(digitalSignatureDto);

        // compare saved hash value and returned value
        if (!digitalSignatureDto.getHashValue().equals(savedHashValue)) {
            throw new ServiceValidationException("saved Hash Value didnt match");
        } else {
            return true;
        }
    }

    public VerificationInformation verifySignatureAndHash(DigitalSignatureDto digitalSignatureDto) {
        if (digitalSignatureDto.getSignature() == null && digitalSignatureDto.getHashValue() == null) {
            return null;
        }
        // old saved values
        String savedHashValue = digitalSignatureDto.getHashValue();
        String savedSignature = digitalSignatureDto.getSignature();
        VerificationInformation verificationInformation = new VerificationInformation();

        // reassign dto with generated value. content is in dto which is hashed and returned
        digitalSignatureDto = dartaChalaniServiceData
                .generateHash(digitalSignatureDto);
        if (digitalSignatureDto == null) {
            return verificationInformation;
        }
        // compare saved hash value and returned value
        if (digitalSignatureDto.getHashValue().equals(savedHashValue)) {
            digitalSignatureDto.setSignature(savedSignature);

            // verify signature
            verificationInformation = dartaChalaniServiceData.verify(digitalSignatureDto);
        } else {
            // returns corrupted signature
            return verificationInformation;
        }
        if (verificationInformation == null) {
            // returns corrupted signature
            return new VerificationInformation();
        }
        return verificationInformation;
    }

    public VerificationInformation verifySignature(DigitalSignatureDto digitalSignatureDto) {
        VerificationInformation v = dartaChalaniServiceData.verify(digitalSignatureDto);
        if (v == null) {
            new VerificationInformation();
        }
        return v;

    }


    public VerificationInformation verifyContentAndGetRequesterVerificationInformation(Long id, TableEnum code) {
        return verifySignatureAndHash(getSignatureInformationWithContent(id, code.toString()));
    }

    public String checkingSignature(Long id) {
        LeaveRequestLatestPojo leaveRequest = leaveRequestMapper.getLeaveRequestById(id);
//        KaajRequestCustomPojo kaajRequestPojo = kaajRequestMapper.getKaajRequestById(id);
        return ContentGenerator.getLeaveContent(leaveRequest);
    }

    public DigitalSignatureDto getSignatureInformationWithContent(Long id, String code) {
        DigitalSignatureDto digitalSignatureDto = new DigitalSignatureDto();
        switch (code) {
            case "LR":
                LeaveRequestLatestPojo leaveRequest = leaveRequestMapper.getLeaveRequestById(id);
                digitalSignatureDto.setSignature(leaveRequest.getLeaveRequestSignature());
                digitalSignatureDto.setHashValue(leaveRequest.getLeaveRequestHashContent());
                digitalSignatureDto.setContent(ContentGenerator.getLeaveContent(leaveRequest));
                break;
            case "MA":
//                ManualAttendancePojo manualAttendancePojo = manualAttendanceMapper.getManualAttendanceById(id);
//                digitalSignatureDto.setSignature(manualAttendancePojo.getManualAttendanceRequestSignature());
//                digitalSignatureDto.setHashValue(manualAttendancePojo.getManualAttendanceRequestHashContent());
//                digitalSignatureDto.setContent("MA" + manualAttendancePojo.getPisCode() + manualAttendancePojo.getDateEn() + manualAttendancePojo.getFiscalYearCode());

                break;
            case "DL":
                DailyLogPojo dailyLogPojo = dailyLogMapper.getDailyLogById(id);
                digitalSignatureDto.setSignature(dailyLogPojo.getDailyLogRequesterSignature());
                digitalSignatureDto.setHashValue(dailyLogPojo.getDailyLogRequesterHashContent());
                digitalSignatureDto.setContent(ContentGenerator.getDailyLogContent(dailyLogPojo));
                break;
            case "KR":
                KaajRequestCustomPojo kaajRequestPojo = kaajRequestMapper.getKaajRequestById(id);
                digitalSignatureDto.setSignature(kaajRequestPojo.getKaajRequesterSignature());
                digitalSignatureDto.setHashValue(kaajRequestPojo.getKaajRequesterHashContent());
                digitalSignatureDto.setContent(ContentGenerator.getKaajRequestContent(kaajRequestPojo));


                break;
            default:
                return null;
        }

        return digitalSignatureDto;
    }

    public Object verifyAndGetAllVerificationInformation(Long id, TableEnum type, String requesterSignature) {
        List<ApprovalActivityPojo> data = decisionApprovalMapper.getActivityLogById(id, type.toString(), 0);
        Map<String, VerificationInformation> v = new HashMap<>();

        DigitalSignatureDto d = getSignatureInformationWithContent(id, type.toString());

        v.put("requesterInformation", requesterSignature == null ? null : this.verifySignatureAndHash(d));
        List<VerificationInformation> vList = new ArrayList<>();
        // verify signature and set verification information
        data.forEach(a ->
                vList.add(this.verifySignatureAndHash(
                        (DigitalSignatureDto.builder().signature(a.getSignature())
                                .hashValue(a.getHashContent()).content(d.getContent()).build()))));
        v.put("approvalInformation", vList.get(0));

        if (vList.size() > 1) {
            v.put("reviewerInformation", vList.get(1));
        }
        return v;
    }

    VerificationInformation getCorruptedSignatureData() {
        VerificationInformation v = new VerificationInformation();
        v.setSignature_name("Corrupted Signature");
        v.setMessage("Corrupted Signature");
        v.setIssuerName("Corrupted Signature");
        return v;
    }
}
