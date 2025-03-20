package io.eventuate.tram.spring.springwolf.commands;

import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EventuateSpringWolfConfiguration.class})
public class EventuateSpringWolfCommandsConfiguration {

    @Bean
    public ChannelsFromCommandHandlerScanner channelsFromCommandHandlerScanner(EventuateCommandDispatcher eventuateCommandDispatcher) {
        return new ChannelsFromCommandHandlerScanner(eventuateCommandDispatcher);
    }

    @Bean
    public OperationsFromCommandHandlerScanner operationsFromCommandHandlerScanner(EventuateCommandDispatcher eventuateCommandDispatcher) {
        return new OperationsFromCommandHandlerScanner(eventuateCommandDispatcher);
    }


}
