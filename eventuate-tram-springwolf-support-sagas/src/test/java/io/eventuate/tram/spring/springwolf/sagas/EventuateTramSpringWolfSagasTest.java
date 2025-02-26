package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.EventuateSpringWolfConfiguration;
import io.eventuate.tram.spring.springwolf.sagas.application.*;
import io.eventuate.tram.spring.springwolf.test.common.AsyncApiDocument;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventuateTramSpringWolfSagasTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, EventuateSpringWolfSagasConfiguration.class, CustomersAndOrdersConfiguration.class})
  public static class Config {
  }

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
  }

  @Test
  public void shouldExposeSpringWolf() {
    AsyncApiDocument doc = AsyncApiDocument.getSpringWolfDoc();

    assertThat(doc.getVersion())
        .as("AsyncAPI version should be 3.0.0")
        .isEqualTo("3.0.0");

    doc.assertSendsMessage(CreateOrderSaga.class.getName() + "-customerService-reserveCredit",
        "customerService",
        ReserveCreditCommand.class.getName());

    Stream.of(CustomerCreditReserved.class, CustomerNotFound.class, CustomerCreditLimitExceeded.class)
        .map(Class::getName)
            .forEach(c -> doc.assertReceivesMessage("receive-io.eventuate.tram.spring.springwolf.sagas.application.CreateOrderSaga-reply",
                "io.eventuate.tram.spring.springwolf.sagas.application.CreateOrderSaga-reply",
                c));
  }

}
