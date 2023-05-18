package com.gerp.shared.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MultiReceiverNotificationPojo {
    private Long moduleId;
    private String module;
    private String sender;
    private Set<String> receiver;
    private String subject;
    private String detail;
    private String remarks;
    private Boolean pushNotification;
    private Boolean received;
}
