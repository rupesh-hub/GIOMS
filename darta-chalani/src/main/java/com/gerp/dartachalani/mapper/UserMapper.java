package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.LetterTransferResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Mapper
@Component
public interface UserMapper {
    List<String> findUserBySection(@Param("sectionCode") String sectionCode);

    List<Long> getInvolvedReceivedLetter(@Param("previousPisCode") Set<String> previousPisCode, @Param("sectionCode") String sectionCode);

    List<Long> getInvolvedDispatchLetter(@Param("previousPisCode") Set<String> previousPisCode, @Param("sectionCode") String sectionCode);

    List<Long> getInvolvedMemo(@Param("previousPisCode") Set<String> previousPisCode, @Param("sectionCode") String sectionCode);

    List<LetterTransferResponsePojo> getLetterHistoryByOfficeCode(@Param("officeCode") String officeCode);
}
