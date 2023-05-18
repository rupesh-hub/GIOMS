package com.gerp.templating.servicesimpl;

import com.gerp.templating.entity.SaruwaTemplate;
import com.gerp.templating.services.SaruwaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class SaruwaServiceImpl implements SaruwaService {
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public String getSaruwaTemplate(SaruwaTemplate saruwaTemplate) {
        Context context = new Context();
        context.setVariable("saruwa", saruwaTemplate);

        String html = templateEngine.process("saruwa_template.html", context);

        return html;
    }
}
