package com.gerp.shared.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.shared.utils.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;

@Configuration
public class StringToTimeStampDeserializer extends StdDeserializer<LocalDate> {

    @Autowired
    private UtilityService utilityService;

    public StringToTimeStampDeserializer() {
        this(null);
    }

    protected StringToTimeStampDeserializer(Class<BaseEntity> t) {
        super(t);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String textDate = p.getText().trim();
        if (textDate.equals(""))
            return null;
        else {
            return utilityService.stringToLocalDate(textDate);
        }
    }

}
