package com.gerp.attendance.Pojo.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.enums.MonthlyAttendanceStatus;
import com.gerp.shared.pojo.KeyValuePojo;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MonthlyAttendanceKeyValueOptionSerializer extends StdSerializer<Enum> {

    public MonthlyAttendanceKeyValueOptionSerializer() {
        this(null);
    }

    protected MonthlyAttendanceKeyValueOptionSerializer(Class<Enum> t) {
        super(t);
    }

    @Override
    public void serialize(Enum s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(this._getSerializedData(s));
    }

    private KeyValuePojo _getSerializedData(Enum s) {
        return MonthlyAttendanceStatus.valueOf(s.toString()).getEnum();
    }
}
