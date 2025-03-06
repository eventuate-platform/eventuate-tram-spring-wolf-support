package io.eventuate.tram.spring.springwolf.testing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelReference {
    @JsonProperty("$ref")
    private String ref;

    public String getRef() {
        return ref;
    }
}