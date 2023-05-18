package com.gerp.templating.services;

import com.gerp.templating.entity.SaruwaRequestLetterTemplate;
import com.gerp.templating.entity.SaruwaTemplate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SaruwaLetterService {

    String getSaruwaLetterTemplate(SaruwaRequestLetterTemplate saruwaRequestLetterTemplate);
}
