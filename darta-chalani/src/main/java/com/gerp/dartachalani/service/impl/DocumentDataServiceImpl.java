package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.dto.DocumentDataPojo;
import com.gerp.dartachalani.mapper.DispatchLetterMapper;
import com.gerp.dartachalani.mapper.MemoMapper;
import com.gerp.dartachalani.mapper.ReceivedLetterMapper;
import com.gerp.dartachalani.service.DocumentDataService;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentDataServiceImpl implements DocumentDataService {

    private final MemoMapper memoMapper;
    private final ReceivedLetterMapper receivedLetterMapper;
    private final DispatchLetterMapper dispatchLetterMapper;

    @Autowired
    private DateConverter dateConverter;

    public DocumentDataServiceImpl(MemoMapper memoMapper,
                                   ReceivedLetterMapper receivedLetterMapper,
                                   DispatchLetterMapper dispatchLetterMapper) {
        this.memoMapper = memoMapper;
        this.receivedLetterMapper = receivedLetterMapper;
        this.dispatchLetterMapper = dispatchLetterMapper;
    }

    @Override
    public List<DocumentDataPojo> getDocumentData(Long id) {
        List<DocumentDataPojo> result = new ArrayList<>();

        List<Long> memoIds = memoMapper.getDocumentMemo(id);
        if (memoIds != null && !memoIds.isEmpty()) {
            List<Long> uniqueMemoId = memoIds.stream().distinct().collect(Collectors.toList());

            if (!uniqueMemoId.isEmpty()) {
                for (Long memoId : uniqueMemoId) {
                    DocumentDataPojo data = memoMapper.getMinimalMemo(memoId);
                    data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                    data.setLetterType("memo");
                    result.add(data);
                }
            }
        }

        List<Long> receivedIds = receivedLetterMapper.getDocumentReceived(id);
        if (receivedIds != null && !receivedIds.isEmpty()) {
            List<Long> uniqueReceivedId = receivedIds.stream().distinct().collect(Collectors.toList());

            if (!uniqueReceivedId.isEmpty()) {
                for (Long receivedId : uniqueReceivedId) {
                    DocumentDataPojo dataPojo = receivedLetterMapper.getMinimalReceived(receivedId);
                    dataPojo.setCreatedDateNp(dateConverter.convertAdToBs(dataPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                    dataPojo.setLetterType("received");
                    result.add(dataPojo);
                }
            }
        }

        List<Long> dispatchIds = dispatchLetterMapper.getDocumentReceived(id);
        if (dispatchIds != null && !dispatchIds.isEmpty()) {
            List<Long> uniqueDispatchId = dispatchIds.stream().distinct().collect(Collectors.toList());

            if (!uniqueDispatchId.isEmpty()) {
                for (Long dispatchId : uniqueDispatchId) {
                    DocumentDataPojo dataPojo = dispatchLetterMapper.getMinimalDispatch(dispatchId);
                    dataPojo.setCreatedDateNp(dateConverter.convertAdToBs(dataPojo.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                    dataPojo.setLetterType("dispatch");
                    result.add(dataPojo);
                }
            }
        }

        return result;
    }
}
