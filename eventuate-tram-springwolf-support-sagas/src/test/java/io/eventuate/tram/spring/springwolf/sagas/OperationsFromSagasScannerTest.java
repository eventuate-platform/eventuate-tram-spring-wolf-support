package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.sagas.application.CreateOrderSaga;
import io.eventuate.tram.spring.springwolf.sagas.application.CustomerServiceProxy;
import io.github.springwolf.asyncapi.v3.model.operation.Operation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OperationsFromSagasScannerTest {

  @Test
  public void shouldScan() {
    CustomerServiceProxy customerService = new CustomerServiceProxy();
    CreateOrderSaga saga = new CreateOrderSaga(customerService);

    OperationsFromSagasScanner scanner = new OperationsFromSagasScanner(List.of(saga));
    ElementsWithClasses<Operation> result = scanner.scan();
    Map<String, Operation> elements = result.elements();
    assertThat(elements).containsOnlyKeys("receive-" + CreateOrderSaga.class.getName() + "-reply",
        CreateOrderSaga.class.getName() + "-customerService-reserveCredit");
  }


}