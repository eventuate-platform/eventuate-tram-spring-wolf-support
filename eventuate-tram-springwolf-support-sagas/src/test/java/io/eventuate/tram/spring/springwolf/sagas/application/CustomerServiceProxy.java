package io.eventuate.tram.spring.springwolf.sagas.application;

import io.eventuate.examples.common.money.Money;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantOperation;
import io.eventuate.tram.sagas.simpledsl.annotations.SagaParticipantProxy;
import org.springframework.stereotype.Component;

@Component
@SagaParticipantProxy(channel=CustomerServiceProxy.CHANNEL)
public class CustomerServiceProxy {

  public static final String CHANNEL = "customerService";

  public static final Class<CustomerCreditLimitExceeded> creditLimitExceededReply = CustomerCreditLimitExceeded.class;
  public static final Class<CustomerNotFound> customerNotFoundReply = CustomerNotFound.class;

  @SagaParticipantOperation(commandClass=ReserveCreditCommand.class, replyClasses=ReserveCreditResult.class)
  public CommandWithDestination reserveCredit(long orderId, Long customerId, Money orderTotal) {
    return CommandWithDestinationBuilder.send(new ReserveCreditCommand(customerId, orderId, orderTotal))
        .to(CHANNEL)
        .build();
  }
}

