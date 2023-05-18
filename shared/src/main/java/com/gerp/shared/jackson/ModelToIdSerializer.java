package com.gerp.shared.jackson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.gerp.shared.generic.api.BaseEntity;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ModelToIdSerializer extends StdSerializer<BaseEntity> {


    public ModelToIdSerializer() {
        this(null);
    }

    protected ModelToIdSerializer(Class<BaseEntity> t) {
        super(t);
    }

    @Override
    public void serialize(BaseEntity s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(s.getId());
    }
}
