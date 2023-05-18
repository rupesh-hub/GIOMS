package com.gerp.templating.servicesimpl;

import com.gerp.templating.entity.SaruwaRequestLetterTemplate;
import com.gerp.templating.services.SaruwaLetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class SaruwaLetterServiceImpl implements SaruwaLetterService {
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public String getSaruwaLetterTemplate(SaruwaRequestLetterTemplate saruwaRequestLetterTemplate) {
        Context context = new Context();
        context.setVariable("saruwaLetter", saruwaRequestLetterTemplate);

        String html = templateEngine.process("saruwa_request_letter.html", context);

        return html;
    }



}
