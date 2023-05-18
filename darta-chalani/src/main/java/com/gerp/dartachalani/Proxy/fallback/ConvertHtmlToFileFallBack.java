package com.gerp.dartachalani.Proxy.fallback;

import com.gerp.dartachalani.Proxy.ConvertHtmlToFile;
import com.gerp.dartachalani.dto.FileConverterPojo;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ConvertHtmlToFileFallBack  extends BaseController implements ConvertHtmlToFile {
    private Exception exception;

    public ConvertHtmlToFileFallBack injectException(Exception cause ) {
        exception=cause;
        return this;
    }

    @Override
    public ResponseEntity<byte[]> convertToFile(FileConverterPojo fileConverterPojo) {
        throw  new CustomException(exception.getMessage());
    }
}
