package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.dto.MemoResponsePojo;
import com.gerp.dartachalani.dto.ReceivedLetterMemoRequestPojo;
import com.gerp.dartachalani.mapper.ReceivedLetterMemoMapper;
import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.dartachalani.model.receive.ReceivedLetterMemo;
import com.gerp.dartachalani.repo.MemoRepo;
import com.gerp.dartachalani.repo.ReceivedLetterMemoRepo;
import com.gerp.dartachalani.repo.ReceivedLetterRepo;
import com.gerp.dartachalani.service.ReceivedLetterMemoService;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Service
@Transactional
public class ReceivedLetterMemoServiceImpl extends GenericServiceImpl<ReceivedLetterMemo, Long> implements ReceivedLetterMemoService {

    private final ReceivedLetterMemoRepo receivedLetterMemoRepo;
    private final MemoRepo memoRepo;
    private final ReceivedLetterRepo receivedLetterRepo;
    private final ReceivedLetterMemoMapper receivedLetterMemoMapper;

    public ReceivedLetterMemoServiceImpl(ReceivedLetterMemoRepo receivedLetterMemoRepo, MemoRepo memoRepo, ReceivedLetterRepo receivedLetterRepo, ReceivedLetterMemoMapper receivedLetterMemoMapper) {
        super(receivedLetterMemoRepo);
        this.receivedLetterMemoRepo = receivedLetterMemoRepo;
        this.memoRepo = memoRepo;
        this.receivedLetterRepo = receivedLetterRepo;
        this.receivedLetterMemoMapper = receivedLetterMemoMapper;
    }


    @Override
    public ReceivedLetterMemo saveLetterMemo(ReceivedLetterMemoRequestPojo receivedLetterMemoRequestPojo) {
        Memo memo = new Memo();
        memo.setSubject(receivedLetterMemoRequestPojo.getMemoRequestPojo().getSubject());
        memo.setContent(receivedLetterMemoRequestPojo.getMemoRequestPojo().getContent());
        memo.setIsDraft(receivedLetterMemoRequestPojo.getMemoRequestPojo().getIsDraft());
        memo = memoRepo.save(memo);

        ReceivedLetterMemo receivedLetterMemo = new ReceivedLetterMemo();
        receivedLetterMemo.setReceivedLetter(receivedLetterRepo.getOne(receivedLetterMemoRequestPojo.getReceivedLetterId()));
        receivedLetterMemo.setMemo(memo);
        return receivedLetterMemoRepo.save(receivedLetterMemo);
    }

    @Override
    public ArrayList<MemoResponsePojo> getByLetterId(Integer letterId) {
        return receivedLetterMemoMapper.getAllLetterMemo(letterId);
    }
}
