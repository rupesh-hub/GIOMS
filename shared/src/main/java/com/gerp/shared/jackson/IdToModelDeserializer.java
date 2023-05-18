package com.gerp.shared.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.gerp.shared.generic.api.BaseEntity;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.Serializable;

@Configuration
public class IdToModelDeserializer extends StdDeserializer<BaseEntity> {

    public IdToModelDeserializer() {
        this(null);
    }

    protected IdToModelDeserializer(Class<BaseEntity> t) {
        super(t);
    }

    @Override
    public BaseEntity deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        try {
            BaseEntity baseEntity = new BaseEntity() {
                @Override
                public Serializable getId() {
                    try {
                        return Long.valueOf(p.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            };
            return baseEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
