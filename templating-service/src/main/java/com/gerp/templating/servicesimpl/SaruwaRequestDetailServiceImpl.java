package com.gerp.templating.servicesimpl;

import com.gerp.templating.entity.SaruwaRequestDetail;
import com.gerp.templating.entity.SaruwaRequestLetterTemplate;
import com.gerp.templating.services.SaruwaRequestDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class SaruwaRequestDetailServiceImpl implements SaruwaRequestDetailService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public String getSaruwaRequestDetailTemplate(SaruwaRequestLetterTemplate saruwaRequestLetterTemplate) {
        Context context = new Context();
        Context context1 = new Context();
        StringBuilder value=new StringBuilder();
        context1.setVariable("saruwaRequest", saruwaRequestLetterTemplate.getSaruwaRequestDetail());
        context.setVariable("saruwaLetter", saruwaRequestLetterTemplate);

        String html1 = templateEngine.process("saruwa_request_letter.html", context);
        String html2 = templateEngine.process("saruwa_request_detail.html", context1);

        String html=value.append(html1).append(html2).toString();
        return html;
    }
}
