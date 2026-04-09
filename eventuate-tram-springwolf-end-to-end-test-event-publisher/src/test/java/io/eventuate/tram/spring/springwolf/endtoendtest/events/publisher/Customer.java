package io.eventuate.tram.spring.springwolf.endtoendtest.events.publisher;

public class Customer {
    private String id;

    public Customer() {
    }

    public Customer(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
