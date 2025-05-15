package com.han56.weather.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.codec.ServerSentEvent;

import java.io.IOException;

public class CustomServerSentEventDeserializer extends JsonDeserializer<ServerSentEvent> {
    @Override
    public ServerSentEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String id = node.get("id").asText();

        return null;
    }
}
