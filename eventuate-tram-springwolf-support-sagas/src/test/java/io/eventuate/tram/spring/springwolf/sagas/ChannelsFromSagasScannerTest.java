package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.eventuate.tram.spring.springwolf.sagas.application.CreateOrderSaga;
import io.eventuate.tram.spring.springwolf.sagas.application.CustomerServiceProxy;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class ChannelsFromSagasScannerTest {

  @Test
  public void shouldScan() {
    CustomerServiceProxy customerService = new CustomerServiceProxy();
    CreateOrderSaga saga = new CreateOrderSaga(customerService);

    ChannelsFromSagasScanner scanner = new ChannelsFromSagasScanner(List.of(saga));
    ElementsWithClasses<ChannelObject> result = scanner.scan();
    Map<String, ChannelObject> elements = result.elements();
    assertThat(elements).containsOnlyKeys(CreateOrderSaga.class.getName() + "-reply", "customerService");
  }
}