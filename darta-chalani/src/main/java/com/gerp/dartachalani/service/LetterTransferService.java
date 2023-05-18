package com.gerp.dartachalani.service;

import com.gerp.dartachalani.dto.LetterTransferPojo;
import com.gerp.dartachalani.dto.LetterTransferResponsePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;

import java.util.List;

public interface LetterTransferService {
    List<EmployeeMinimalPojo> findUserBySection(Long sectionId);

    Long persistLetterTransfer(LetterTransferPojo data);

    List<LetterTransferResponsePojo> getLetterTransferHistoryByOfficeCode();
}
