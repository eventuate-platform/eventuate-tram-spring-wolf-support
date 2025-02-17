package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.core.asyncapi.scanners.ChannelsScanner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventuateTramChannelsScannerManager implements ChannelsScanner {

  private final List<EventuateTramChannelsScanner> scanners;

  public EventuateTramChannelsScannerManager(List<EventuateTramChannelsScanner> scanners) {
    this.scanners = scanners;
  }

  @Override
  public Map<String, ChannelObject> scan() {
    return scanners.stream()
        .map(EventuateTramChannelsScanner::scan)
        .reduce(new HashMap<>(), (result, map) -> {
          result.putAll(map);
          return result;
        });
  }


}
