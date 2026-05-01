package io.eventuate.tram.spring.springwolf.autoconfigure;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;
import io.eventuate.tram.spring.springwolf.events.publisher.EventuateSpringWolfEventsPublisherConfiguration;
import io.eventuate.tram.spring.events.autoconfigure.TramEventsPublisherAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@AutoConfigureAfter(TramEventsPublisherAutoConfiguration.class)
@ConditionalOnBean(DomainEventPublisherForAggregate.class)
@Import(EventuateSpringWolfEventsPublisherConfiguration.class)
public class EventuateSpringWolfEventsPublisherAutoConfiguration {
}
