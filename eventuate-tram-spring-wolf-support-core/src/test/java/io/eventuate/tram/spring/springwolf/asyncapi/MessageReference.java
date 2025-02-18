package io.eventuate.tram.spring.springwolf.asyncapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageReference {
    @JsonProperty("$ref")
    private String ref;

    public String getRef() {
        return ref;
    }
}