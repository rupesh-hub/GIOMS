package com.gerp.dartachalani.Proxy.fallback;

import com.gerp.dartachalani.Proxy.LetterTemplate;
import com.gerp.dartachalani.dto.GeneralTemplate;
import com.gerp.dartachalani.dto.template.TippaniDetail;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LetterTemplateFallback extends BaseController implements LetterTemplate {
    private Exception exception;
    public LetterTemplateFallback injectException(Exception cause ) {
        exception=cause;
        return this;
    }

    @Override
    public ResponseEntity<String> generalTemplate(GeneralTemplate generalTemplate, String lan) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<String> tippaniTemplate(TippaniDetail tippaniDetail) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<String> tippaniHeader(TippaniDetail tippaniDetail) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<String> generalMultipleTemplate(GeneralTemplate generalTemplate, String lan) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<String> ocTemplate(GeneralTemplate generalTemplate, String lan) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<String> generalTemplateForQr(GeneralTemplate generalTemplate, String lan) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<String> generalMultipleTemplateForQr(GeneralTemplate generalTemplate, String lan) {
        return ResponseEntity.ok(getResponse());
    }

    private String getResponse() {
        String message = exception.getMessage();
        String exception = message.substring(0, 34);
        if (exception.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available", customMessageSource.get(LetterTemplate.SERVICE_NAME));
        return message;
    }

}
