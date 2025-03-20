package io.eventuate.tram.spring.springwolf.commands;

import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventuateTramSpringWolfCommandsConfiguration {

    @Bean
    public ChannelsFromCommandHandlerScanner channelsFromCommandHandlerScanner(EventuateCommandDispatcher eventuateCommandDispatcher) {
        return new ChannelsFromCommandHandlerScanner(eventuateCommandDispatcher);
    }

    @Bean
    public OperationsFromCommandHandlerScanner operationsFromCommandHandlerScanner(EventuateCommandDispatcher eventuateCommandDispatcher) {
        return new OperationsFromCommandHandlerScanner(eventuateCommandDispatcher);
    }
}