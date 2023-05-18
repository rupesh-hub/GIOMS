package com.gerp.dartachalani.service;

import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.dartachalani.dto.FileConverterPojo;

public interface FileConvertService {
    byte[] convertToPdf(FileConverterPojo fileConverterPojo);

    byte[] convertToPdf1(FileConverterPojo fileConverterPojo);

}
