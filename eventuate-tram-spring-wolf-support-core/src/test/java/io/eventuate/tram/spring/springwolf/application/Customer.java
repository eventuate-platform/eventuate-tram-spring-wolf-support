package io.eventuate.tram.spring.springwolf.application;

public class Customer {

  private final Long id;

  public Customer(Long id) {
    this.id = id;
  }

  public Long getId() {
      return id;
  }
}
