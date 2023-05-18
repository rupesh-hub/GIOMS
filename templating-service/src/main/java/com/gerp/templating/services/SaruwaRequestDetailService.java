package com.gerp.templating.services;

import com.gerp.templating.entity.SaruwaRequestDetail;
import com.gerp.templating.entity.SaruwaRequestLetterTemplate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SaruwaRequestDetailService {
    String getSaruwaRequestDetailTemplate(SaruwaRequestLetterTemplate saruwaRequestLetterTemplate);

}
