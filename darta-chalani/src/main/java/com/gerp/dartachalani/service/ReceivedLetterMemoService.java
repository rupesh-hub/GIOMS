package com.gerp.dartachalani.service;

import com.gerp.dartachalani.dto.MemoResponsePojo;
import com.gerp.dartachalani.dto.ReceivedLetterMemoRequestPojo;
import com.gerp.dartachalani.model.receive.ReceivedLetterMemo;
import com.gerp.shared.generic.api.GenericService;

import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface ReceivedLetterMemoService extends GenericService<ReceivedLetterMemo, Long> {


    ReceivedLetterMemo saveLetterMemo(ReceivedLetterMemoRequestPojo receivedLetterMemoRequestPojo);

    ArrayList<MemoResponsePojo> getByLetterId(Integer letterId);
}
