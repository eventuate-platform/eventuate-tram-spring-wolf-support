package io.eventuate.tram.spring.springwolf.application.requestasyncresponse;

import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestAsyncResponseConfiguration {

    @Bean
    public CustomerCommandHandler customerCommandHandler(CustomerService customerService) {
        return new CustomerCommandHandler(customerService);
    }


    @Bean
    public EventuateCommandHandlerBeanPostProcessor eventuateCommandHandlerBeanPostProcessor(EventuateCommandDispatcher eventuateCommandDispatcher) {
        return new EventuateCommandHandlerBeanPostProcessor(eventuateCommandDispatcher);
    }

    @Bean
    public EventuateCommandDispatcher eventuateCommandDispatcher(SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return new EventuateCommandDispatcher(sagaCommandDispatcherFactory);
    }
}
