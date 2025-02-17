package io.eventuate.tram.spring.springwolf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.application.events.CustomerEventConsumer;
import io.eventuate.tram.spring.springwolf.application.events.CustomerEventPublisher;
import io.eventuate.tram.spring.springwolf.application.events.CustomerEventPublisherImpl;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomerCommandHandler;
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
  @Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class})
  public static class Config {

    @Bean
    public CustomerEventPublisher customerEventPublisher(DomainEventPublisher domainEventPublisher) {
      return new CustomerEventPublisherImpl(domainEventPublisher);
    }

    @Bean
    public CustomerEventConsumer orderEventConsumer() {
      return new CustomerEventConsumer();
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcher(CustomerEventConsumer customerEventConsumer, DomainEventDispatcherFactory domainEventDispatcherFactory) {
      return domainEventDispatcherFactory.make("customerServiceEvents", customerEventConsumer.domainEventHandlers());
    }

    @Bean
    public CustomerCommandHandler customerCommandHandler() {
      return new CustomerCommandHandler();
    }

    // TODO Exception handler for CustomerCreditLimitExceededException

    @Bean
    public CommandDispatcher consumerCommandDispatcher(CustomerCommandHandler target,
                                                       SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {

      return sagaCommandDispatcherFactory.make("customerCommandDispatcher", target.commandHandlerDefinitions());
    }
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

  @Test
  public void shouldExposeSpringWolf() throws IOException {
    JsonNode root = getSpringWolfDocs();

    assertAsyncApiSpecHasChannelsSection(root);
    assertCustomerChannelExists(root);
    assertCustomerChannelAcceptsCustomerCreatedEvent(root);
    assertCustomerChannelOperationsAreConfigured(root);
  }

  private JsonNode getSpringWolfDocs() throws IOException {
    var s = RestAssured.get("/springwolf/docs")
        .then()
        .statusCode(200)
        .extract().response().prettyPrint();

    Files.writeString(Paths.get("build/springwolf.json"), s);
    return new ObjectMapper().readTree(s);
  }

  private void assertAsyncApiSpecHasChannelsSection(JsonNode root) {
    JsonNode channels = root.path("channels");
    assertThat(channels).as("Channels section should exist in AsyncAPI spec").isNotNull();
  }

  private void assertCustomerChannelExists(JsonNode root) {
    JsonNode customerChannel = root.path("channels").path(CUSTOMER_CHANNEL);
    assertThat(customerChannel).as("Customer channel should exist").isNotNull();
  }

  private void assertCustomerChannelAcceptsCustomerCreatedEvent(JsonNode root) {
    JsonNode channelMessages = root.path("channels").path(CUSTOMER_CHANNEL).path("messages");
    assertThat(channelMessages).as("Channel should have messages section").isNotNull();
    assertThat(channelMessages.has(CUSTOMER_CREATED_EVENT))
        .as("Channel should have CustomerCreatedEvent message").isTrue();
  }

  private void assertCustomerChannelOperationsAreConfigured(JsonNode root) {
    JsonNode operations = root.path("operations");
    assertThat(operations).as("Operations section should exist in AsyncAPI spec").isNotNull();

    assertCustomerChannelPublishOperation(operations);
    assertCustomerChannelSubscribeOperation(operations);
  }

  private void assertCustomerChannelPublishOperation(JsonNode operations) {
    JsonNode publisherOperation = operations.path(CUSTOMER_EVENT_PUBLISHER);
    assertThat(publisherOperation).as("Publisher operation should exist").isNotNull();
    assertThat(publisherOperation.path("action").asText())
        .as("Publisher should have 'send' action").isEqualTo("send");
    assertThat(publisherOperation.path("channel").path("$ref").asText())
        .as("Publisher should reference Customer channel").contains(CUSTOMER_CHANNEL);
  }

  private void assertCustomerChannelSubscribeOperation(JsonNode operations) {
    JsonNode subscriberOperation = operations.path("operationId");
    assertThat(subscriberOperation).as("Subscriber operation should exist").isNotNull();
    assertThat(subscriberOperation.path("action").asText())
        .as("Subscriber should have 'receive' action").isEqualTo("receive");
    assertThat(subscriberOperation.path("channel").path("$ref").asText())
        .as("Subscriber should reference Customer channel").contains(CUSTOMER_CHANNEL);
  }

}
