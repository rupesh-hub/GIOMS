package com.gerp.dartachalani.service;

import com.gerp.dartachalani.dto.DocumentDataPojo;

import java.util.List;

public interface DocumentDataService {
    List<DocumentDataPojo> getDocumentData(Long id);
}
