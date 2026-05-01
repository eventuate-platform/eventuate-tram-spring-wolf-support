package io.eventuate.tram.spring.springwolf.autoconfigure;

import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventDispatcher;
import io.eventuate.tram.spring.springwolf.events.subscriber.EventuateSpringWolfEventsSubscriberConfiguration;
import io.eventuate.tram.spring.events.autoconfigure.TramEventsSubscriberAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@AutoConfigureAfter(TramEventsSubscriberAutoConfiguration.class)
@ConditionalOnBean(EventuateDomainEventDispatcher.class)
@Import(EventuateSpringWolfEventsSubscriberConfiguration.class)
public class EventuateSpringWolfEventsSubscriberAutoConfiguration {
}
