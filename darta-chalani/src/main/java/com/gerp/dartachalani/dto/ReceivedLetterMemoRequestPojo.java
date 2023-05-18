package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedLetterMemoRequestPojo {

    private Long receivedLetterId;
    private MemoRequestPojo memoRequestPojo;

}
