package io.eventuate.tram.spring.springwolf.asyncapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.eventuate.tram.spring.springwolf.asyncapi.Channel;
import io.eventuate.tram.spring.springwolf.asyncapi.Operation;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncApiDocument {
    @JsonProperty("asyncapi")
    private String version;

    private Map<String, Channel> channels;
    private Map<String, Operation> operations;

    public String getVersion() {
        return version;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public Map<String, Operation> getOperations() {
        return operations;
    }
}
