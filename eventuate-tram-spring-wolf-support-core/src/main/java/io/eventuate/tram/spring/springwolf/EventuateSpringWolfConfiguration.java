package io.eventuate.tram.spring.springwolf;

import io.github.springwolf.core.asyncapi.scanners.ChannelsScanner;
import io.github.springwolf.core.asyncapi.scanners.OperationsScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EventuateSpringWolfConfiguration {

  @Bean
  public ChannelsScanner eventuateTramChannelsScanner(List<EventuateTramChannelsScanner> scanners) {
    return new EventuateTramChannelsScannerManager(scanners);
  }

  @Bean
  OperationsScanner eventuateTramOperationsScanner(List<EventuateTramOperationsScanner> scanners) {
    return new EventuateTramOperationsScannerManager(scanners);
  }

  @Bean
  SpringWolfMessageFactory springWolfMessageFactory() {
    return new SpringWolfMessageFactory();
  }
}
