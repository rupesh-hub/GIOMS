package com.gerp.templating.servicesimpl;

import com.gerp.templating.entity.ReportTemplate;
import com.gerp.templating.services.ReportTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class ReportTemplateServiceImpl implements ReportTemplateService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public String getReportTemplate( ReportTemplate reportTemplate) {
        Context context = new Context();
        context.setVariable("reportTemplate", reportTemplate);

        String html = templateEngine.process("report_template.html", context);

        return html;
    }

    @Override
    public String getHeaderTemplate(ReportTemplate reportTemplate) {
        Context context = new Context();
        context.setVariable("headerss", reportTemplate);

        String html = templateEngine.process("header.html", context);

        return html;
    }
}
