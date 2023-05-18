package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.dto.ExternalRecordExtDto;
import com.gerp.dartachalani.dto.ExternalRecordsDto;
import com.gerp.dartachalani.dto.ExternalRequestPojo;
import com.gerp.dartachalani.dto.UserDetailsPojo;
import com.gerp.dartachalani.mapper.DispatchLetterMapper;
import com.gerp.dartachalani.mapper.ExternalRecordsMapper;
import com.gerp.dartachalani.mapper.MemoMapper;
import com.gerp.dartachalani.mapper.ReceivedLetterMapper;
import com.gerp.dartachalani.model.external.ExternalRecords;
import com.gerp.dartachalani.repo.DispatchLetterRepo;
import com.gerp.dartachalani.repo.ExternalRecordsRepo;
import com.gerp.dartachalani.repo.MemoRepo;
import com.gerp.dartachalani.repo.ReceivedLetterRepo;
import com.gerp.dartachalani.service.ExternalRecordsService;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExternalRecordsServiceImpl extends GenericServiceImpl<ExternalRecords, Long> implements ExternalRecordsService {

    private final ExternalRecordsRepo externalRecordsRepo;
    private final ReceivedLetterRepo receivedLetterRepo;
    private final MemoRepo memoRepo;
    private final DispatchLetterRepo dispatchLetterRepo;
    private final ExternalRecordsMapper externalRecordsMapper;
    private final MemoMapper memoMapper;
    private final ReceivedLetterMapper receivedLetterMapper;
    private final DispatchLetterMapper dispatchLetterMapper;

    public ExternalRecordsServiceImpl(
            ExternalRecordsRepo externalRecordsRepo,
            ReceivedLetterRepo receivedLetterRepo,
            MemoRepo memoRepo,
            DispatchLetterRepo dispatchLetterRepo,
            ExternalRecordsMapper externalRecordsMapper,
            MemoMapper memoMapper,
            ReceivedLetterMapper receivedLetterMapper,
            DispatchLetterMapper dispatchLetterMapper
    ) {
        super(externalRecordsRepo);
        this.externalRecordsRepo = externalRecordsRepo;
        this.memoRepo = memoRepo;
        this.dispatchLetterRepo = dispatchLetterRepo;
        this.receivedLetterRepo = receivedLetterRepo;
        this.externalRecordsMapper = externalRecordsMapper;
        this.memoMapper = memoMapper;
        this.receivedLetterMapper = receivedLetterMapper;
        this.dispatchLetterMapper = dispatchLetterMapper;
    }

    @Override
    public ExternalRecords save(ExternalRequestPojo data) {

        List<ExternalRecords> records = new ArrayList<>();

        if (data.getDartaIds() != null && !data.getDartaIds().isEmpty()) {
            for (Long dartaId : data.getDartaIds()) {
                ExternalRecords dartaRecords = new ExternalRecords().builder()
                        .taskId(data.getTaskId())
                        .projectId(data.getProjectId())
                        .receivedLetter(receivedLetterRepo.findById(dartaId).get())
                        .build();
                records.add(dartaRecords);
            }
        }

        if (data.getChalaniIds() != null && !data.getChalaniIds().isEmpty()) {
            for (Long chalaniId : data.getChalaniIds()) {
                ExternalRecords chalaniRecords = new ExternalRecords().builder()
                        .taskId(data.getTaskId())
                        .projectId(data.getProjectId())
                        .dispatchLetter(dispatchLetterRepo.findById(chalaniId).get())
                        .build();
                records.add(chalaniRecords);
            }
        }

        if (data.getTippaniIds() != null && !data.getTippaniIds().isEmpty()) {
            for (Long tippaniId : data.getTippaniIds()) {
                ExternalRecords tippaniRecords = new ExternalRecords().builder()
                        .taskId(data.getTaskId())
                        .projectId(data.getProjectId())
                        .memo(memoRepo.findById(tippaniId).get())
                        .build();
                records.add(tippaniRecords);
            }
        }

        externalRecordsRepo.saveAll(records);

        return null;
    }

    @Override
    public ExternalRecordsDto getById(UserDetailsPojo data) {

        List<ExternalRecordExtDto> records = new ArrayList<>();
        ExternalRecordsDto externalRecordsDto = new ExternalRecordsDto();

        switch (data.getLetterType()) {
            case "task":
                records = externalRecordsMapper.getAllRecordsByTaskId(data.getId());
                externalRecordsDto.setTaskId(data.getId());
                for (ExternalRecordExtDto taskData : records) {
                    if (taskData.getDispatchId() != null)
                        taskData.setChalani(dispatchLetterMapper.getDispatchLetterDetailById(taskData.getDispatchId()));
                    if (taskData.getReceivedLetterId() != null)
                        taskData.setDarta(receivedLetterMapper.getReceivedLetter(taskData.getReceivedLetterId()));
                    if (taskData.getMemoId() != null)
                        taskData.setTippani(memoMapper.getMemoById(taskData.getMemoId()));
                }
                break;
            case "project":
                records = externalRecordsMapper.getAllRecordsByProjectId(data.getId());
                externalRecordsDto.setProjectId(data.getId());
                for (ExternalRecordExtDto projectData : records) {
                    if (projectData.getDispatchId() != null)
                        projectData.setChalani(dispatchLetterMapper.getDispatchLetterDetailById(projectData.getDispatchId()));
                    if (projectData.getReceivedLetterId() != null)
                        projectData.setDarta(receivedLetterMapper.getReceivedLetter(projectData.getReceivedLetterId()));
                    if (projectData.getMemoId() != null)
                        projectData.setTippani(memoMapper.getMemoById(projectData.getMemoId()));
                }
                break;
        }
        externalRecordsDto.setData(records);

        return externalRecordsDto;
    }

}
