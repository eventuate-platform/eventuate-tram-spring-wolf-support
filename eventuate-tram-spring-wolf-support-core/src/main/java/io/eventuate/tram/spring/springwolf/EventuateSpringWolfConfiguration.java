package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.core.asyncapi.scanners.ChannelsScanner;
import io.github.springwolf.core.asyncapi.scanners.OperationsScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EventuateSpringWolfConfiguration {

  @Bean
  public ChannelsScanner eventuateTramChannelsScanner(List<EventuateTramChannelsScanner> scanners, SpringWolfMessageFactory springWolfMessageFactory) {
    return new EventuateTramChannelsScannerManager(scanners, springWolfMessageFactory);
  }

  @Bean
  OperationsScanner eventuateTramOperationsScanner(List<EventuateTramOperationsScanner> scanners, SpringWolfMessageFactory springWolfMessageFactory) {
    return new EventuateTramOperationsScannerManager(scanners, springWolfMessageFactory);
  }

  @Bean
  SpringWolfMessageFactory springWolfMessageFactory() {
    return new SpringWolfMessageFactory();
  }
}
