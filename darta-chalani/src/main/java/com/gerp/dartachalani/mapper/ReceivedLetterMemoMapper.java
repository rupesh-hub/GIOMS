package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.MemoResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since  1.0.0
 */

@Mapper
@Component
public interface ReceivedLetterMemoMapper {

    @Select("SELECT subject, content, document_id as documentId, is_draft as isDraft FROM memo INNER JOIN received_letter_memo as rlm ON memo.id = rlm.memo_id WHERE rlm.received_letter_id = #{letterId}")
    ArrayList<MemoResponsePojo> getAllLetterMemo(Integer letterId);
}
