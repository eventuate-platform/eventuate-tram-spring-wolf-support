package io.eventuate.tram.spring.springwolf.application.requestasyncresponse;

import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestAsyncResponseConfiguration {

    @Bean
    public CustomerCommandHandler customerCommandHandler() {
        return new CustomerCommandHandler();
    }

    @Bean
    public CommandDispatcher consumerCommandDispatcher(CustomerCommandHandler target,
                                                     SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make("customerCommandDispatcher", target.commandHandlerDefinitions());
    }
}
