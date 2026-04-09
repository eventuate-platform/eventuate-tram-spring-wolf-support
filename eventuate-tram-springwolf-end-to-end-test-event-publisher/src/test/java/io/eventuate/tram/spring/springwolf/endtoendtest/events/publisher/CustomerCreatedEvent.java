package io.eventuate.tram.spring.springwolf.endtoendtest.events.publisher;

public class CustomerCreatedEvent implements CustomerEvent {
    private final String id;

    public CustomerCreatedEvent(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
