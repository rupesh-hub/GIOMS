package com.gerp.templating.services;

import com.gerp.templating.entity.GeneralTemplate;
import com.gerp.templating.entity.Language;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface TemplatingServices {

	String getGeneralTemplate(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException;

	String getGeneralManyToTemplate(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException;

	String getOcrTemplate(GeneralTemplate generalTemplate, Language lan) throws IOException, WriterException;

	String getGeneralTemplateForQr(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException;

	String getGeneralManyToTemplateForQr(GeneralTemplate generalTemplate, Language lan) throws WriterException, IOException;


}
