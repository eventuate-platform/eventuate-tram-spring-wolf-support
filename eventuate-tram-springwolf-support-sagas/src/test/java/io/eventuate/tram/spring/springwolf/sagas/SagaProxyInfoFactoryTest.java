package io.eventuate.tram.spring.springwolf.sagas;

import io.eventuate.tram.spring.springwolf.sagas.application.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SagaProxyInfoFactoryTest {

  private final CustomerServiceProxy customerService = new CustomerServiceProxy();
  private final SagaProxyInfo sagaProxyInfo = SagaProxyInfoFactory.make(new CreateOrderSaga(customerService));
  private final String replyChannel = CreateOrderSaga.class.getName() + "-reply";

  @Test
  public void shouldMake() {
    assertThat(sagaProxyInfo.sagaType()).isEqualTo(CreateOrderSaga.class.getName());
    assertThat(sagaProxyInfo.replyChannel()).isEqualTo(replyChannel);
    assertThat(sagaProxyInfo.participants()).hasSize(1);
  }

  @Test
  public void replyChannels() {
    var replyChannels = sagaProxyInfo.makeReplyChannelsToClasses();
    assertThat(replyChannels).isNotNull();
    assertThat(replyChannels)
        .isEqualTo(Map.of(replyChannel, Set.of(CustomerCreditLimitExceeded.class, CustomerCreditReserved.class, CustomerNotFound.class)));
  }

  @Test
  public void commandChannels() {
    var commandChannels = sagaProxyInfo.makCommandChannelsToClasses();
    assertThat(commandChannels).isNotNull();
    assertThat(commandChannels)
        .isEqualTo(Map.of("customerService", Set.of(ReserveCreditCommand.class)));
  }
}