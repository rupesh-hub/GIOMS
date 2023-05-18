package com.gerp.dartachalani.Proxy;

import com.gerp.dartachalani.dto.GeneralTemplate;
import com.gerp.dartachalani.dto.template.TippaniDetail;
import com.gerp.shared.generic.controllers.BaseController;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LetterTemplateProxy extends BaseController {

    private final LetterTemplate letterTemplate;

    public LetterTemplateProxy(LetterTemplate letterTemplate) {
        this.letterTemplate = letterTemplate;
    }

    @SneakyThrows
    public String getGeneralTemplate(GeneralTemplate generalTemplate, String lan) {
        ResponseEntity<String> responseEntity = letterTemplate.generalTemplate(generalTemplate, lan);
        if(responseEntity != null && responseEntity.getBody() != null)
            return responseEntity.getBody();
        else
            return null;
    }

    @SneakyThrows
    public String getTippaniTemplate(TippaniDetail tippaniDetail) {
        ResponseEntity<String> responseEntity = letterTemplate.tippaniTemplate(tippaniDetail);
        if(responseEntity != null && responseEntity.getBody() != null)
            return responseEntity.getBody();
        else
            return null;
    }


    @SneakyThrows
    public String getTippaniHeader(TippaniDetail tippaniDetail) {
        ResponseEntity<String> responseEntity = letterTemplate.tippaniHeader(tippaniDetail);
        if(responseEntity != null && responseEntity.getBody() != null)
            return responseEntity.getBody();
        else
            return null;
    }

    @SneakyThrows
    public String getGeneralMultipleTemplate(GeneralTemplate generalTemplate, String lan) {
        ResponseEntity<String> responseEntity = letterTemplate.generalMultipleTemplate(generalTemplate, lan);
        if(responseEntity != null && responseEntity.getBody() != null)
            return responseEntity.getBody();
        else
            return null;
    }

    @SneakyThrows
    public String getOcTemplate(GeneralTemplate generalTemplate, String lan) {
        ResponseEntity<String> responseEntity = letterTemplate.ocTemplate(generalTemplate, lan);
        if(responseEntity != null && responseEntity.getBody() != null)
            return responseEntity.getBody();
        else
            return null;
    }


    @SneakyThrows
    public String getGeneralTemplateForQR(GeneralTemplate generalTemplate, String lan) {
        ResponseEntity<String> responseEntity = letterTemplate.generalTemplateForQr(generalTemplate, lan);
        if(responseEntity != null && responseEntity.getBody() != null)
            return responseEntity.getBody();
        else
            return null;
    }

    @SneakyThrows
    public String getGeneralMultipleTemplateForQr(GeneralTemplate generalTemplate, String lan) {
        ResponseEntity<String> responseEntity = letterTemplate.generalMultipleTemplateForQr(generalTemplate, lan);
        if(responseEntity != null && responseEntity.getBody() != null)
            return responseEntity.getBody();
        else
            return null;
    }

}
