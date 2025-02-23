package io.eventuate.tram.spring.springwolf.test.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Components {
    private Map<String, Schema> schemas;
    private Map<String, Message> messages;

    public Map<String, Schema> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, Schema> schemas) {
        this.schemas = schemas;
    }

    public Map<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Message> messages) {
        this.messages = messages;
    }
}