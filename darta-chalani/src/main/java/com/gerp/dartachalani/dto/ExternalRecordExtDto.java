package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalRecordExtDto {
    private Long dispatchId;
    private Long receivedLetterId;
    private Long memoId;

    private DispatchLetterDTO chalani;
    private ReceivedLetterResponsePojo darta;
    private MemoResponsePojo tippani;
}
