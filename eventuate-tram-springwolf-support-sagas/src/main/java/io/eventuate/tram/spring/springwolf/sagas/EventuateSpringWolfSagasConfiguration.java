package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EventuateSpringWolfConfiguration.class)
@ComponentScan
public class EventuateSpringWolfSagasConfiguration {
}