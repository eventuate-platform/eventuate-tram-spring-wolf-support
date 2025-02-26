package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.core.EventuateTramChannelsScanner;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Component
public class ChannelsFromSagasScanner implements EventuateTramChannelsScanner {

  private final List<SagaProxyInfo> sagas;

  public ChannelsFromSagasScanner(List<SimpleSaga<?>> sagas) {
    this.sagas = sagas.stream().map(SagaProxyInfoFactory::make).toList();
  }

  @Override
  public ElementsWithClasses<ChannelObject> scan() {
    Map<String, Set<Class<?>>> commandChannelsToClasses = makCommandChannelsToClasses(sagas);
    Map<String, Set<Class<?>>> replyChannelsToClasses = makeReplyChannelsToClasses(sagas);
    Map<String, Set<Class<?>>> allChannels = MapUtils.combineMaps(commandChannelsToClasses, replyChannelsToClasses);
    return new ElementsWithClasses<ChannelObject>(makeChannelObjects(allChannels), makeAllClasses(allChannels));
  }

  private Map<String, ChannelObject> makeChannelObjects(Map<String, Set<Class<?>>> allChannels) {
    return allChannels.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> makeChannelObject(e.getKey(), e.getValue())));
  }

  private ChannelObject makeChannelObject(String channel, Set<Class<?>> messageClasses) {
    return ChannelObject.builder()
        .channelId(channel)
        .messages(messageClasses.stream().collect(Collectors.toMap(Class::getName, this::makeMessageReference)))
        .build();
  }

  private MessageReference makeMessageReference(Class<?> aClass) {
    return MessageReference.toComponentMessage(aClass.getName());
  }

  private Set<Class<?>> makeAllClasses(Map<String, Set<Class<?>>> allChannels) {
    return allChannels.values().stream().flatMap(Set::stream).collect(toSet());
  }

  private Map<String, Set<Class<?>>> makCommandChannelsToClasses(List<SagaProxyInfo> sagas) {
    return sagas.stream().map(SagaProxyInfo::makCommandChannelsToClasses).reduce(Map.of(), MapUtils::combineMaps);
  }

  private Map<String, Set<Class<?>>> makeReplyChannelsToClasses(List<SagaProxyInfo> sagas) {
    return sagas.stream().map(SagaProxyInfo::makeReplyChannelsToClasses).reduce(Map.of(), MapUtils::combineMaps);
  }


}
