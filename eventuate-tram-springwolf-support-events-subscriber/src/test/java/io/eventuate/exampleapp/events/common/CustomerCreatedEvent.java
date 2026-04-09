package io.eventuate.exampleapp.events.common;

public class CustomerCreatedEvent extends AbstractCustomerEvent {
    private final String id;

    public CustomerCreatedEvent(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
