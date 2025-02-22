package io.eventuate.tram.spring.springwolf.test.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplyInfo {
    private AddressInfo address;
    private List<MessageReference> messages;

    public AddressInfo getAddress() {
        return address;
    }

    public void setAddress(AddressInfo address) {
        this.address = address;
    }

    public List<MessageReference> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageReference> messages) {
        this.messages = messages;
    }
}