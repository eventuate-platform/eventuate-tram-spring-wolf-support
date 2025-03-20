package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import(EventuateSpringWolfConfiguration.class)
public class EventuateSpringWolfSagasConfiguration {


    @Bean
    public ChannelsFromSagasScanner channelsFromSagasScanner(List<SimpleSaga<?>> sagas) {
        return new ChannelsFromSagasScanner(sagas);
    }

    @Bean
    public OperationsFromSagasScanner operationsFromSagasScanner(List<SimpleSaga<?>> sagas) {
        return new OperationsFromSagasScanner(sagas);
    }
}
