package io.eventuate.tram.spring.springwolf.sagas.application;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {

  private final CustomerServiceProxy customerServiceProxy;

  public CreateOrderSaga(CustomerServiceProxy customerServiceProxy) {
    this.customerServiceProxy = customerServiceProxy;
  }

  @Override
  public List<Object> getParticipantProxies() {
    return List.of(customerServiceProxy);
  }

  @Override
  public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
    throw new UnsupportedOperationException("Not implemented");
  }
}
