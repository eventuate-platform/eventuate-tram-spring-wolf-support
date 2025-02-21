package io.eventuate.tram.spring.springwolf;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.application.events.EventConfiguration;
import io.eventuate.tram.spring.springwolf.application.events.OrderCreatedEvent;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomerCommandHandler;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomersAndOrdersConfiguration;
import io.eventuate.tram.spring.springwolf.asyncapi.AsyncApiDocument;
import io.eventuate.tram.spring.springwolf.asyncapi.Channel;
import io.eventuate.tram.spring.springwolf.asyncapi.Operation;
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
import java.util.Map;

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

    assertAsyncApiSpecHasChannelsSection(doc);
    assertCustomerChannelExists(doc);
    assertCustomerChannelAcceptsCustomerCreatedEvent(doc);
    assertCustomerChannelOperationsAreConfigured(doc);

    assertCustomerServiceChannelExists(doc);
    assertCustomerServiceChannelOperationsAreConfigured(doc);

    assertOrderEventsChannelAcceptsOrderCreatedEvent(doc);
    assertOrderEventsChannelOperationsAreConfigured(doc);

  }

  private void assertCustomerServiceChannelOperationsAreConfigured(AsyncApiDocument doc) {

    var customerCommandHandler = CustomerCommandHandler.class.getName() + ".reserveCredit";
    var customerServiceChannel = "customerService";

    Map<String, Operation> operations = doc.getOperations();
    assertThat(operations)
        .as("Operations map should exist")
        .isNotNull()
        .containsKey(customerCommandHandler);

    Operation subscriberOperation = operations.get(customerCommandHandler);
    assertThat(subscriberOperation.getAction())
        .as("Subscriber should have 'receive' action")
        .isEqualTo("receive");

    assertThat(subscriberOperation.getChannel())
        .as("Subscriber channel reference should exist")
        .isNotNull();

    assertThat(subscriberOperation.getChannel().getRef())
        .as("Subscriber should reference Customer channel")
        .contains(customerServiceChannel);

  }

  private void assertCustomerServiceChannelExists(AsyncApiDocument doc) {
    assertThat(doc.getChannels())
        .as("Channels map should exist")
        .isNotNull()
        .containsKey("customerService");
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

  private void assertOrderEventsChannelAcceptsOrderCreatedEvent(AsyncApiDocument doc) {
    assertThat(doc.getChannels())
        .as("Channels map should exist")
        .isNotNull()
        .containsKey(ORDER_CHANNEL);

    Channel customerChannel = doc.getChannels().get(ORDER_CHANNEL);
    assertThat(customerChannel.getMessages())
        .as("Channel messages should exist")
        .isNotNull()
        .containsKey(ORDER_CREATED_EVENT);
  }

  private void assertCustomerChannelOperationsAreConfigured(AsyncApiDocument doc) {
    assertThat(doc.getOperations())
        .as("Operations section should exist in AsyncAPI spec")
        .isNotNull()
        .isNotEmpty();

    assertCustomerChannelPublishOperation(doc.getOperations());
    assertCustomerChannelSubscribeOperation(doc.getOperations());
  }

  private void assertOrderEventsChannelOperationsAreConfigured(AsyncApiDocument doc) {
    assertThat(doc.getOperations())
        .as("Operations section should exist in AsyncAPI spec")
        .isNotNull()
        .isNotEmpty();

    assertOrderEventsChannelSubscribeOperation(doc.getOperations());
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

  private void assertOrderEventsChannelSubscribeOperation(Map<String, Operation> operations) {
    assertThat(operations)
        .as("Operations map should exist")
        .isNotNull()
        .containsKey(ORDER_EVENT_SUBSCRIBER);

    Operation subscriberOperation = operations.get(ORDER_EVENT_SUBSCRIBER);
    assertThat(subscriberOperation.getAction())
        .as("Subscriber should have 'receive' action")
        .isEqualTo("receive");

    assertThat(subscriberOperation.getChannel())
        .as("Subscriber channel reference should exist")
        .isNotNull();

    assertThat(subscriberOperation.getChannel().getRef())
        .as("Subscriber should reference Order channel")
        .contains(ORDER_CHANNEL);
  }

}
