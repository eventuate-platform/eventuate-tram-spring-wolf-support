package io.eventuate.tram.spring.springwolf.asyncapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {
    private Map<String, MessageReference> messages;

    public Map<String, MessageReference> getMessages() {
        return messages;
    }
}