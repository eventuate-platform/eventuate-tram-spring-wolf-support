package io.eventuate.tram.spring.springwolf.application.requestasyncresponse;


import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import io.eventuate.tram.spring.springwolf.EventuateCommandHandler;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.commands.ReserveCreditCommand;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.replies.CustomerCreditLimitExceeded;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.replies.CustomerCreditReserved;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.replies.CustomerNotFound;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class CustomerCommandHandler {

  private final CustomerService customerService;

  public CustomerCommandHandler(CustomerService customerService) {
    this.customerService = customerService;
  }

  public CommandHandlers commandHandlerDefinitions() {
    return SagaCommandHandlersBuilder
            .fromChannel("customerService")
            .onMessage(ReserveCreditCommand.class, this::reserveCredit)
            .build();
  }

  @EventuateCommandHandler(subscriberId="customerCommandDispatcher", channel="customerService")
  public Message reserveCredit(CommandMessage<ReserveCreditCommand> cm) {
    ReserveCreditCommand cmd = cm.getCommand();
    try {
      customerService.reserveCredit(cmd.customerId(), cmd.orderId(), cmd.orderTotal());
      return withSuccess(new CustomerCreditReserved());
    } catch (CustomerNotFoundException e) {
      return withFailure(new CustomerNotFound());
    } catch (CustomerCreditLimitExceededException e) {
      return withFailure(new CustomerCreditLimitExceeded());
    }
  }


}
