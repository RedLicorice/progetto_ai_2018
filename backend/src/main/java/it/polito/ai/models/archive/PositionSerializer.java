package it.polito.ai.models.archive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PositionSerializer extends StdSerializer<Position> {

    public PositionSerializer() {
        this(null);
    }

    public PositionSerializer(Class<Position> m) {
        super(m);
    }

    @Override
    public void serialize(
            Position m, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("longitude", m.getLongitude());
        jgen.writeNumberField("latitude", m.getLatitude());
        jgen.writeEndObject();
    }
}