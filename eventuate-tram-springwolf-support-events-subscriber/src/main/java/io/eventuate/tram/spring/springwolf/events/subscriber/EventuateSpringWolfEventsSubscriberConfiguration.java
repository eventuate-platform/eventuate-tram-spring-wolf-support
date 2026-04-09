package io.eventuate.tram.spring.springwolf.events.subscriber;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.spring.events.subscriber.EventuateDomainEventDispatcher;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import(EventuateSpringWolfConfiguration.class)
public class EventuateSpringWolfEventsSubscriberConfiguration {

    @Bean
    public ChannelsFromEventHandlersScanner channelsFromEventHandlersScanner(List<DomainEventDispatcher> domainEventDispatchers) {
        return new ChannelsFromEventHandlersScanner(domainEventDispatchers);
    }

    @Bean
    public ChannelsFromAnnotationBasedEventHandlerScanner channelsFromAnnotationBasedEventHandlerScanner(EventuateDomainEventDispatcher eventuateDomainEventDispatcher) {
        return new ChannelsFromAnnotationBasedEventHandlerScanner(eventuateDomainEventDispatcher);
    }

    @Bean
    public OperationsFromEventHandlersScanner operationsFromEventHandlersScanner(List<DomainEventDispatcher> domainEventDispatchers) {
        return new OperationsFromEventHandlersScanner(domainEventDispatchers);
    }

    @Bean
    public OperationsFromAnnotationBasedEventHandlerScanner operationsFromAnnotationBasedEventHandlerScanner(EventuateDomainEventDispatcher eventuateDomainEventDispatcher) {
        return new OperationsFromAnnotationBasedEventHandlerScanner(eventuateDomainEventDispatcher);
    }

}
