package com.gerp.templating.servicesimpl;

import com.gerp.templating.entity.RamanaTemplate;
import com.gerp.templating.services.RamanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.List;

@Service
public class RamanaServiceImpl implements RamanaService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public String getRamanaTemplate(RamanaTemplate ramanaTemplate) {
        Context context = new Context();
        Context context1 = new Context();
        StringBuilder value=new StringBuilder();
        context.setVariable("ramana", ramanaTemplate);
        context1.setVariable("ramanaDetail", ramanaTemplate.getRamanaDetail());

        String html2 = templateEngine.process("ramana_detail_template.html", context1);

        String html1 = templateEngine.process("ramana_template.html", context);
         String html=value.append(html1).append(html2).toString();

        return html;
    }
}
