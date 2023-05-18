package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.ActivityLogPojo;
import com.gerp.attendance.Pojo.DailyLogPojo;
import com.gerp.attendance.Pojo.DigitalSignatureDto;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import com.gerp.attendance.mapper.DecisionApprovalMapper;
import com.gerp.attendance.mapper.LeaveRequestMapper;
import com.gerp.attendance.service.DecisionApprovalService;
import com.gerp.attendance.util.ContentGenerator;
import com.gerp.attendance.util.SignatureVerificationUtils;
import com.gerp.shared.enums.TableEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Service
@Transactional
@RequiredArgsConstructor
public class DecisionApprovalServiceImpl implements DecisionApprovalService {

    private final DecisionApprovalMapper decisionApprovalMapper;
    private final SignatureVerificationUtils signatureVerificationUtils;
    private final LeaveRequestMapper leaveRequestMapper;

    @Override
    public String checkingSignature(Long id) {
        return signatureVerificationUtils.checkingSignature(id);
    }

    //TODO: ACTIVITY LOG FOR LEAVE/KAAJ - MANUAL AND SELF
    @Override
    public Map<String, Object> getActivityLog(final Long id, final TableEnum type) {

        final String code = type.toString();
        final Map<String, Object> map = new HashedMap();
        final List<ActivityLogPojo> activityLogList = new ArrayList<>();

        final List<ActivityLogPojo> approverLog = decisionApprovalMapper.getActivityLog(id, code);
        final ActivityLogPojo requesterLog = "LR".equalsIgnoreCase(code) ? decisionApprovalMapper.getLeaveLog(id) :
                "KR".equalsIgnoreCase(code) ? decisionApprovalMapper.getKaajLog(id) :
                        "MA".equalsIgnoreCase(code) ? decisionApprovalMapper.getAttendanceLog(id) :
                                decisionApprovalMapper.getDailyLog(id);

        activityLogList.addAll(approverLog);
        activityLogList.add(requesterLog);

        digitalSignatureVerification(id, code, approverLog, requesterLog);

        map.put("data", activityLogList);

        return map;
    }

    private void digitalSignatureVerification(Long id, String code, List<ActivityLogPojo> approverLog, ActivityLogPojo requesterLog) {

        if (!"MA".equalsIgnoreCase(code)) {
            //TODO: ALL DIGITAL SIGNATURE WILL BE IMPLEMENTED
            final DigitalSignatureDto digitalSignatureDto =
                    new DigitalSignatureDto(getContent(id, code), requesterLog.getRequesterHashContent(), requesterLog.getRequesterSignature());

            requesterLog.setRequesterHashContent(null);
            requesterLog.setRequesterSignature(null);

            //TODO: REQUESTER VERIFICATION
            requesterLog.setSignatureInformation(signatureVerificationUtils.verifySignatureAndHash(digitalSignatureDto));

            //TODO: APPROVER/FORWARDER SIGNATURE VERIFICATION
            if (digitalSignatureDto.getContent() != null) {
                approverLog.forEach(data -> {
                    data.setSignatureInformation(signatureVerificationUtils.verifySignatureAndHash(new DigitalSignatureDto(digitalSignatureDto.getContent(), data.getHashContent(), data.getSignature())));
                    data.setSignature(null);
                    data.setHashContent(null);

                });
            }
        }
    }

    private String getContent(final Long id, final String code) {
        return "LR".equalsIgnoreCase(code) ? leaveContent(id) :
                "DL".equalsIgnoreCase(code) ? dailyLogContent(id) : signatureVerificationUtils.getSignatureInformationWithContent(id, code).getContent();
    }

    private String leaveContent(final Long id) {
        List<Long> ids = decisionApprovalMapper.getDetailIds(id);

        StringBuilder finalContent = new StringBuilder();

        for (int i = 0; i < ids.size(); i++) {
            Long detailId = ids.get(i);
            String content = decisionApprovalMapper.getLeaveContent(detailId);
            List<DocumentPojo> documentList = leaveRequestMapper.selectDocument(detailId);

            if (Objects.nonNull(documentList)) {
                for (DocumentPojo document : documentList) {
                    content = content.concat(document.getName());
                }
            }

            finalContent.append(content);

            if (i < ids.size() - 1) {
                finalContent.append(",");
            }
        }

        return finalContent.toString().replaceAll("\\s", "");
    }


    private String dailyLogContent(final Long id) {
        final DailyLogPojo dailyLogPojo = decisionApprovalMapper.getDailyLogContent(id);

        String content = ContentGenerator.DAILY_LOG
                + (dailyLogPojo.getPisCode() == null ? " " : dailyLogPojo.getPisCode())
                + (dailyLogPojo.getTimeFrom() == null ? " " : dailyLogPojo.getTimeFrom())
                + (dailyLogPojo.getTimeTo() == null ? " " : dailyLogPojo.getTimeTo())
                + (dailyLogPojo.getRemarks() == null ? " " : dailyLogPojo.getRemarks());

        return content.replaceAll("\\s", "");
    }

}
