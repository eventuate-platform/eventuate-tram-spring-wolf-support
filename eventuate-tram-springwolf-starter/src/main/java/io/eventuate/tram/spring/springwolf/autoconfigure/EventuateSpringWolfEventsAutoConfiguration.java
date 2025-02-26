package io.eventuate.tram.spring.springwolf.autoconfigure;

import io.eventuate.tram.events.common.EventMessageHeaders;
import io.eventuate.tram.spring.springwolf.EventuateSpringWolfEventsConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnClass(EventMessageHeaders.class)
@Import(EventuateSpringWolfEventsConfiguration.class)
public class EventuateSpringWolfEventsAutoConfiguration {
}
