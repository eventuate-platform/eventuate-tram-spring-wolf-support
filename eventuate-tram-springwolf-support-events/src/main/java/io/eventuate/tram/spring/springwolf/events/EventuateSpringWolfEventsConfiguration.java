package io.eventuate.tram.spring.springwolf.events;

import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EventuateSpringWolfConfiguration.class, EventuateTramSpringWolfEventsConfiguration.class})
public class EventuateSpringWolfEventsConfiguration {
}