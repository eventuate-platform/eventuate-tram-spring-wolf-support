package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.core.asyncapi.scanners.ChannelsScanner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EventuateTramChannelsScannerManager implements ChannelsScanner {

  private final List<EventuateTramChannelsScanner> scanners;
  private final SpringWolfMessageFactory springWolfMessageFactory;

  public EventuateTramChannelsScannerManager(List<EventuateTramChannelsScanner> scanners, SpringWolfMessageFactory springWolfMessageFactory) {
    this.scanners = scanners;
    this.springWolfMessageFactory = springWolfMessageFactory;
  }

  @Override
  public Map<String, ChannelObject> scan() {

    List<ElementsWithClasses<ChannelObject>> results = scanners.stream()
        .map(EventuateTramChannelsScanner::scan).toList();

    results.stream()
        .flatMap(r -> r.classes().stream())
        .collect(Collectors.toSet())
        .forEach(springWolfMessageFactory::makeMessageFromClass);

    return results.stream()
        .map(ElementsWithClasses::elements)
        .reduce(new HashMap<>(), (result, map) -> {
          result.putAll(map);
          return result;
        });

  }


}
