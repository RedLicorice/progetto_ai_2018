package it.polito.ai.models.archive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class MeasureSerializer extends StdSerializer<Measure> {

    public MeasureSerializer() {
        this(null);
    }

    public MeasureSerializer(Class<Measure> m) {
        super(m);
    }

    @Override
    public void serialize(
            Measure m, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("timestamp", m.getTimestamp());
        jgen.writeNumberField("longitude", m.getLongitude());
        jgen.writeNumberField("latitude", m.getLatitude());
        jgen.writeEndObject();
    }
}