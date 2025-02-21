package io.eventuate.tram.spring.springwolf.application;

public class Order {

  private final Long id;

  public Order(Long id) {
    this.id = id;
  }

  public Long getId() {
      return id;
  }
}
