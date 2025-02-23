package io.eventuate.tram.spring.springwolf.test.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static io.eventuate.tram.spring.springwolf.SetUtil.add;
import static org.assertj.core.api.Assertions.assertThat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncApiDocument {
  @JsonProperty("asyncapi")
  private String version;
  private Map<String, Channel> channels;
  private Map<String, Operation> operations;
  private Components components;

  public static AsyncApiDocument getSpringWolfDoc() {
    var s = RestAssured.get("/springwolf/docs")
        .then()
        .statusCode(200)
        .extract().response().prettyPrint();

    try {
      Files.writeString(Paths.get("build/springwolf.json"), s);
      return new ObjectMapper().readValue(s, AsyncApiDocument.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Map<String, Channel> getChannels() {
    return channels;
  }

  public Map<String, Operation> getOperations() {
    return operations;
  }

  public Components getComponents() {
    return components;
  }

  public void setComponents(Components components) {
    this.components = components;
  }

  public void assertEmpty() {
    assertThat(channels).isEmpty();
    assertThat(operations).isEmpty();
  }

  public void assertReceivesMessageAndReplies(String subscriberId, String channel, String eventType, Set<String> replyTypes) {
    assertMessagesDefined(add(replyTypes, eventType));;
    assertReceivesMessage(subscriberId, channel, eventType);
    Map<String, Operation> operations = getOperations();
    Operation operation = operations.get(subscriberId);
    assertThat(operation.getReply().getMessages().stream().map(messageReference ->
        messageReference.getRef().startsWith("#/components/messages/") ?
            messageReference.getRef().substring("#/components/messages/".length()) : messageReference.getRef()))
        .as("Reply messages should exist")
        .containsAll(replyTypes);
  }

  private void assertMessagesDefined(Set<String> messageTypes) {
    assertThat(components.getMessages()).containsKeys(messageTypes.toArray(new String[0]));
  }


  public void assertReceivesMessage(String subscriberId, String channel, String eventType) {

    assertThat(getChannels())
        .as("Channels map should exist")
        .isNotNull()
        .containsKey(channel)
        .satisfies(channels -> {
          Channel customerChannel = channels.get(channel);
          assertThat(customerChannel.getMessages())
              .as("Channel messages should exist")
              .isNotNull()
              .containsKey(eventType);
        });

    Map<String, Operation> operations = getOperations();
    assertThat(operations)
        .as("Operations map should exist")
        .isNotNull()
        .containsKey(subscriberId);

    Operation subscriberOperation = operations.get(subscriberId);
    assertThat(subscriberOperation)
        .isNotNull()
        .satisfies(op -> {
          assertThat(op.getAction())
              .as("Subscriber should have 'receive' action")
              .isEqualTo("receive");
          assertThat(op.getChannel())
              .as("Subscriber channel reference should exist")
              .isNotNull()
              .extracting(ChannelReference::getRef)
              .as("Subscriber should reference Order channel")
              .asString()
              .contains(channel);
        });
  }

  public void assertSendsMessage(String publisherId, String channel, String eventType) {

    assertThat(getChannels())
        .as("Channels map should exist")
        .isNotNull()
        .containsKey(channel)
        .satisfies(channels -> {
          Channel customerChannel = channels.get(channel);
          assertThat(customerChannel.getMessages())
              .as("Channel messages should exist")
              .isNotNull()
              .containsKey(eventType);
        });

    Map<String, Operation> operations = getOperations();
    assertThat(operations)
        .as("Operations map should exist")
        .isNotNull()
        .containsKey(publisherId);

    Operation subscriberOperation = operations.get(publisherId);
    assertThat(subscriberOperation)
        .isNotNull()
        .satisfies(op -> {
          assertThat(op.getAction())
              .as("Subscriber should have 'send' action")
              .isEqualTo("send");
          assertThat(op.getChannel())
              .as("Subscriber channel reference should exist")
              .isNotNull()
              .extracting(ChannelReference::getRef)
              .as("Subscriber should reference Order channel")
              .asString()
              .contains(channel);
        });
  }

}
