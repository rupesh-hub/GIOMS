package com.gerp.shared.pojo.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.gerp.shared.enums.Gender;
import com.gerp.shared.pojo.KeyValuePojo;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GenderKeyValueOptionSerializer extends StdSerializer<Enum> {

    public GenderKeyValueOptionSerializer() {
        this(null);
    }

    protected GenderKeyValueOptionSerializer(Class<Enum> t) {
        super(t);
    }

    @Override
    public void serialize(Enum s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(this._getSerializedData(s));
    }

    private KeyValuePojo _getSerializedData(Enum s) {
        return Gender.valueOf(s.toString()).getEnum();
    }
}
