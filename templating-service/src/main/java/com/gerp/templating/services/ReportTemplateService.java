package com.gerp.templating.services;

import com.gerp.templating.entity.ReportTemplate;
import com.gerp.templating.entity.SaruwaTemplate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ReportTemplateService {

    String getReportTemplate(ReportTemplate reportTemplate);

    String getHeaderTemplate(ReportTemplate reportTemplate);

}
