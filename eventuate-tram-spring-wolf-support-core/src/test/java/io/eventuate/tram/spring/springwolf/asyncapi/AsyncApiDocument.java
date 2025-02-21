package io.eventuate.tram.spring.springwolf.asyncapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncApiDocument {
    @JsonProperty("asyncapi")
    private String version;

    private Map<String, Channel> channels;
    private Map<String, Operation> operations;

    public String getVersion() {
        return version;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public Map<String, Operation> getOperations() {
        return operations;
    }

    public void assertReceivesMessageAndReplies(String subscriberId, String channel, String eventType, Set<String> replyTypes) {
        assertReceivesMessage(subscriberId, channel, eventType);
        Map<String, Operation> operations = getOperations();
        Operation operation = operations.get(subscriberId);
        assertThat(operation.getReply().getMessages().stream().map(messageReference ->
            messageReference.getRef().startsWith("#/components/messages/") ?
            messageReference.getRef().substring("#/components/messages/".length()) : messageReference.getRef()))
                .as("Reply messages should exist")
                .containsAll(replyTypes);
    }

    public void assertReceivesMessage(String subscriberId, String channel, String eventType) {

      assertThat(getChannels())
          .as("Channels map should exist")
          .isNotNull()
          .containsKey(channel);

      Channel customerChannel = getChannels().get(channel);
      assertThat(customerChannel.getMessages())
          .as("Channel messages should exist")
          .isNotNull()
          .containsKey(eventType);

      Map<String, Operation> operations = getOperations();
      assertThat(operations)
          .as("Operations map should exist")
          .isNotNull()
          .containsKey(subscriberId);

      Operation subscriberOperation = operations.get(subscriberId);
      assertThat(subscriberOperation.getAction())
          .as("Subscriber should have 'receive' action")
          .isEqualTo("receive");

      assertThat(subscriberOperation.getChannel())
          .as("Subscriber channel reference should exist")
          .isNotNull();

      assertThat(subscriberOperation.getChannel().getRef())
          .as("Subscriber should reference Order channel")
          .contains(channel);
    }

    public void assertSendsMessage(String publisherId, String channel, String eventType) {

      assertThat(getChannels())
          .as("Channels map should exist")
          .isNotNull()
          .containsKey(channel);

      Channel customerChannel = getChannels().get(channel);
      assertThat(customerChannel.getMessages())
          .as("Channel messages should exist")
          .isNotNull()
          .containsKey(eventType);

      Map<String, Operation> operations = getOperations();
      assertThat(operations)
          .as("Operations map should exist")
          .isNotNull()
          .containsKey(publisherId);

      Operation subscriberOperation = operations.get(publisherId);
      assertThat(subscriberOperation.getAction())
          .as("Subscriber should have 'send' action")
          .isEqualTo("send");

      assertThat(subscriberOperation.getChannel())
          .as("Subscriber channel reference should exist")
          .isNotNull();

      assertThat(subscriberOperation.getChannel().getRef())
          .as("Subscriber should reference Order channel")
          .contains(channel);
    }
}
