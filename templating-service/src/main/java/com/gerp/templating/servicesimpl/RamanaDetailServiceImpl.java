package com.gerp.templating.servicesimpl;

import com.gerp.templating.entity.RamanaDetail;
import com.gerp.templating.services.RamanaDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class RamanaDetailServiceImpl implements RamanaDetailService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public String getRamanaDetail(RamanaDetail ramanaDetail) {
        Context context = new Context();
        context.setVariable("ramanaDetail", ramanaDetail);

        String html = templateEngine.process("ramana_detail_template.html", context);
        return html;
    }
}
