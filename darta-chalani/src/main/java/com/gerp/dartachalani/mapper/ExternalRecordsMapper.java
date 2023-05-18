package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.ExternalRecordExtDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ExternalRecordsMapper {

    @Select("select dispatch_id as dispatchId, received_letter_id as receivedLetterId, memo_id as memoId from external_records where task_id = #{id}")
    List<ExternalRecordExtDto> getAllRecordsByTaskId(@Param("id") Long id);

    @Select("select dispatch_id as dispatchId, received_letter_id as receivedLetterId, memo_id as memoId from external_records where project_id = #{id}")
    List<ExternalRecordExtDto> getAllRecordsByProjectId(@Param("id") Long id);

}
