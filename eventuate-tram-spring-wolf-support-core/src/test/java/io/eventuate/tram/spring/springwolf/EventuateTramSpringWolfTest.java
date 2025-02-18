package io.eventuate.tram.spring.springwolf;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.spring.springwolf.asyncapi.AsyncApiDocument;
import io.eventuate.tram.spring.springwolf.asyncapi.Channel;
import io.eventuate.tram.spring.springwolf.asyncapi.Operation;
import java.util.Map;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.application.events.CustomerEventConsumer;
import io.eventuate.tram.spring.springwolf.application.events.CustomerEventPublisher;
import io.eventuate.tram.spring.springwolf.application.events.CustomerEventPublisherImpl;
import io.eventuate.tram.spring.springwolf.application.events.EventConfiguration;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomerCommandHandler;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.RequestAsyncResponseConfiguration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventuateTramSpringWolfTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, RequestAsyncResponseConfiguration.class, EventConfiguration.class})
  public static class Config {
    // TODO Exception handler for CustomerCreditLimitExceededException
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

  @Test
  public void shouldExposeSpringWolf() throws IOException {
    AsyncApiDocument doc = getSpringWolfDocs();

    assertThat(doc.getVersion())
        .as("AsyncAPI version should be 3.0.0")
        .isEqualTo("3.0.0");

    assertAsyncApiSpecHasChannelsSection(doc);
    assertCustomerChannelExists(doc);
    assertCustomerChannelAcceptsCustomerCreatedEvent(doc);
    assertCustomerChannelOperationsAreConfigured(doc);
  }

  private AsyncApiDocument getSpringWolfDocs() throws IOException {
    var s = RestAssured.get("/springwolf/docs")
        .then()
        .statusCode(200)
        .extract().response().prettyPrint();

    Files.writeString(Paths.get("build/springwolf.json"), s);
    return new ObjectMapper().readValue(s, AsyncApiDocument.class);
  }

  private void assertAsyncApiSpecHasChannelsSection(AsyncApiDocument doc) {
    assertThat(doc.getChannels())
        .as("Channels section should exist in AsyncAPI spec")
        .isNotNull()
        .isNotEmpty();
  }

  private void assertCustomerChannelExists(AsyncApiDocument doc) {
    assertThat(doc.getChannels())
        .as("Channels map should exist")
        .isNotNull()
        .containsKey(CUSTOMER_CHANNEL);
  }

  private void assertCustomerChannelAcceptsCustomerCreatedEvent(AsyncApiDocument doc) {
    assertThat(doc.getChannels())
        .as("Channels map should exist")
        .isNotNull()
        .containsKey(CUSTOMER_CHANNEL);

    Channel customerChannel = doc.getChannels().get(CUSTOMER_CHANNEL);
    assertThat(customerChannel.getMessages())
        .as("Channel messages should exist")
        .isNotNull()
        .containsKey(CUSTOMER_CREATED_EVENT);
  }

  private void assertCustomerChannelOperationsAreConfigured(AsyncApiDocument doc) {
    assertThat(doc.getOperations())
        .as("Operations section should exist in AsyncAPI spec")
        .isNotNull()
        .isNotEmpty();

    assertCustomerChannelPublishOperation(doc.getOperations());
    assertCustomerChannelSubscribeOperation(doc.getOperations());
  }

  private void assertCustomerChannelPublishOperation(Map<String, Operation> operations) {
    assertThat(operations)
        .as("Operations map should exist")
        .isNotNull()
        .containsKey(CUSTOMER_EVENT_PUBLISHER);

    Operation publisherOperation = operations.get(CUSTOMER_EVENT_PUBLISHER);
    assertThat(publisherOperation.getAction())
        .as("Publisher should have 'send' action")
        .isEqualTo("send");

    assertThat(publisherOperation.getChannel())
        .as("Publisher channel reference should exist")
        .isNotNull();

    assertThat(publisherOperation.getChannel().getRef())
        .as("Publisher should reference Customer channel")
        .contains(CUSTOMER_CHANNEL);
  }

  private void assertCustomerChannelSubscribeOperation(Map<String, Operation> operations) {
    assertThat(operations)
        .as("Operations map should exist")
        .isNotNull()
        .containsKey(CUSTOMER_EVENT_SUBSCRIBER);

    Operation subscriberOperation = operations.get(CUSTOMER_EVENT_SUBSCRIBER);
    assertThat(subscriberOperation.getAction())
        .as("Subscriber should have 'receive' action")
        .isEqualTo("receive");

    assertThat(subscriberOperation.getChannel())
        .as("Subscriber channel reference should exist")
        .isNotNull();

    assertThat(subscriberOperation.getChannel().getRef())
        .as("Subscriber should reference Customer channel")
        .contains(CUSTOMER_CHANNEL);
  }

}
