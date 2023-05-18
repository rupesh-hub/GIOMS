package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.Proxy.ConvertHtlToFileProxy;
import com.gerp.dartachalani.dto.FileConverterPojo;
import com.gerp.dartachalani.service.FileConvertService;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class FileConverterServiceImpl implements FileConvertService {

    private final ConvertHtlToFileProxy convertHtlToFileProxy;

    public FileConverterServiceImpl(ConvertHtlToFileProxy convertHtlToFileProxy) {
        this.convertHtlToFileProxy = convertHtlToFileProxy;
    }

    @Override
    public byte[] convertToPdf(FileConverterPojo fileConverterPojo) {
        return convertHtlToFileProxy.getFileConverter(fileConverterPojo);
    }

    @Override
    public byte[] convertToPdf1(FileConverterPojo fileConverterPojo) {
        return fileConverterPojo.getHtml() != null ? fileConverterPojo.getHtml().getBytes() : null;
    }

}
