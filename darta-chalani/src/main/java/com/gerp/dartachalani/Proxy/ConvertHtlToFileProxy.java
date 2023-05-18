package com.gerp.dartachalani.Proxy;

import com.gerp.dartachalani.dto.FileConverterPojo;
import com.gerp.shared.generic.controllers.BaseController;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConvertHtlToFileProxy extends BaseController {

    private final ConvertHtmlToFile convertHtmlToFile;
    @SneakyThrows
    @Cacheable(value = "convertToFile",key = "#fileConverterPojo")
    public  byte[] getFileConverter(FileConverterPojo fileConverterPojo) {
        ResponseEntity< byte[]> responseEntity = convertHtmlToFile.convertToFile(fileConverterPojo);
        if (responseEntity != null && responseEntity.getBody() != null){
            return responseEntity.getBody();
        }
        return null;
    }


}
