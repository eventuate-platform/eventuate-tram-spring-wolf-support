package io.eventuate.tram.spring.springwolf.events.publisher;

import io.eventuate.tram.events.publisher.DomainEventPublisherForAggregate;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import(EventuateSpringWolfConfiguration.class)
public class EventuateSpringWolfEventsPublisherConfiguration {

    @Bean
    public ChannelsFromEventPublishersScanner channelsFromEventPublishersScanner(List<DomainEventPublisherForAggregate> eventPublishers) {
        return new ChannelsFromEventPublishersScanner(eventPublishers);
    }

    @Bean
    public OperationsFromEventPublisherScanner operationsFromEventPublisherScanner(List<DomainEventPublisherForAggregate> domainEventPublisherForAggregates) {
        return new OperationsFromEventPublisherScanner(domainEventPublisherForAggregates);
    }

}
