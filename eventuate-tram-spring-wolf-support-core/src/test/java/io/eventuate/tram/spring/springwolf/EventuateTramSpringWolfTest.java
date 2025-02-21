package io.eventuate.tram.spring.springwolf;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.application.events.EventConfiguration;
import io.eventuate.tram.spring.springwolf.application.events.OrderCreatedEvent;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomersAndOrdersConfiguration;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.replies.CustomerCreditLimitExceeded;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.replies.CustomerCreditReserved;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.replies.CustomerNotFound;
import io.eventuate.tram.spring.springwolf.asyncapi.AsyncApiDocument;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventuateTramSpringWolfTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, CustomersAndOrdersConfiguration.class, EventConfiguration.class})
  public static class Config {
  }

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
  }

  private static final String CUSTOMER_CHANNEL = "io.eventuate.tram.spring.springwolf.application.Customer";
  private static final String CUSTOMER_CREATED_EVENT = "io.eventuate.tram.spring.springwolf.application.events.CustomerCreatedEvent";
  private static final String CUSTOMER_EVENT_PUBLISHER = "io.eventuate.tram.spring.springwolf.application.events.CustomerEventPublisherImpl";
  private static final String CUSTOMER_EVENT_SUBSCRIBER = "operationId";

  private static final String ORDER_CHANNEL = "orderServiceEvents";
  private static final String ORDER_CREATED_EVENT = OrderCreatedEvent.class.getName();
  private static final String ORDER_EVENT_SUBSCRIBER = "io.eventuate.tram.spring.springwolf.application.events.OrderEventConsumer.handleOrderCreatedEvent";

  @Test
  public void shouldExposeSpringWolf() throws IOException {
    AsyncApiDocument doc = getSpringWolfDocs();

    assertThat(doc.getVersion())
        .as("AsyncAPI version should be 3.0.0")
        .isEqualTo("3.0.0");

    doc.assertSendsMessage(CUSTOMER_EVENT_PUBLISHER, CUSTOMER_CHANNEL, CUSTOMER_CREATED_EVENT);

    doc.assertReceivesMessage(CUSTOMER_EVENT_SUBSCRIBER, CUSTOMER_CHANNEL, CUSTOMER_CREATED_EVENT);

    doc.assertReceivesMessageAndReplies(
        "io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomerCommandHandler.reserveCredit",
        "customerService",
        "io.eventuate.tram.spring.springwolf.application.requestasyncresponse.commands.ReserveCreditCommand",
        Set.of(CustomerNotFound.class.getName(), CustomerCreditLimitExceeded.class.getName(), CustomerCreditReserved.class.getName())
    );

    doc.assertReceivesMessage(ORDER_EVENT_SUBSCRIBER, ORDER_CHANNEL, ORDER_CREATED_EVENT);

  }

  private AsyncApiDocument getSpringWolfDocs() throws IOException {
    var s = RestAssured.get("/springwolf/docs")
        .then()
        .statusCode(200)
        .extract().response().prettyPrint();

    Files.writeString(Paths.get("build/springwolf.json"), s);
    return new ObjectMapper().readValue(s, AsyncApiDocument.class);
  }


}
