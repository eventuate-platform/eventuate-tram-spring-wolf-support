package io.eventuate.tram.spring.springwolf.autoconfigure;

import io.eventuate.tram.sagas.orchestration.Saga;
import io.eventuate.tram.spring.springwolf.sagas.EventuateSpringWolfSagasConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnClass(Saga.class)
@Import(EventuateSpringWolfSagasConfiguration.class)
public class EventuateSpringWolfSagasAutoConfiguration {
}
