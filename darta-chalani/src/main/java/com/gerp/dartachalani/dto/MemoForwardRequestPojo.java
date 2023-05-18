package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoForwardRequestPojo {

    private Long memoId;
    private String senderSectionId;
    private String senderPisCode;
    private String description;
    private List<ReceiverPojo> receiver;

}
