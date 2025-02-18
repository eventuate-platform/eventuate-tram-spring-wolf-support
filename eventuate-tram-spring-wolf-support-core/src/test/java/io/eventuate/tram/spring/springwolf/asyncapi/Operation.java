package io.eventuate.tram.spring.springwolf.asyncapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Operation {
    private String action;
    private ChannelReference channel;
    private List<MessageReference> messages;

    public String getAction() {
        return action;
    }

    public ChannelReference getChannel() {
        return channel;
    }

    public List<MessageReference> getMessages() {
        return messages;
    }
}