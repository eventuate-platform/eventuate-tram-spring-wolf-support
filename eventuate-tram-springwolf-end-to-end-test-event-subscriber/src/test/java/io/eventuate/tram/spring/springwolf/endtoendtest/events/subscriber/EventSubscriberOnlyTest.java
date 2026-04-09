package io.eventuate.tram.spring.springwolf.endtoendtest.events.subscriber;

import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.testing.AsyncApiDocument;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventSubscriberOnlyTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, CustomerEventConsumerConfiguration.class})
  public static class Config {
  }

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
  }

  private static final String CUSTOMER_CHANNEL = Customer.class.getName();
  private static final String CUSTOMER_CREATED_EVENT = CustomerCreatedEvent.class.getName();
  private static final String CUSTOMER_EVENT_SUBSCRIBER = "io.eventuate.tram.spring.springwolf.endtoendtest.events.subscriber.CustomerEventConsumer.handleCustomerCreatedEvent";

  @Test
  public void shouldExposeSpringWolf() {
    AsyncApiDocument doc = AsyncApiDocument.getSpringWolfDoc("springwolf-subscriber.json");

    assertThat(doc.getVersion())
        .as("AsyncAPI version should be 3.0.0")
        .isEqualTo("3.0.0");

    doc.assertReceivesMessage(CUSTOMER_EVENT_SUBSCRIBER, CUSTOMER_CHANNEL, CUSTOMER_CREATED_EVENT);
  }

}
