package io.eventuate.tram.spring.springwolf.autoconfigure;

import io.eventuate.tram.sagas.orchestration.Saga;
import io.eventuate.tram.spring.springwolf.sagas.EventuateSpringWolfSagasConfiguration;
import io.eventuate.tram.sagas.spring.orchestration.autoconfigure.SpringOrchestratorSimpleDslAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@AutoConfigureAfter(SpringOrchestratorSimpleDslAutoConfiguration.class)
@ConditionalOnBean(Saga.class)
@Import(EventuateSpringWolfSagasConfiguration.class)
public class EventuateSpringWolfSagasAutoConfiguration {
}
