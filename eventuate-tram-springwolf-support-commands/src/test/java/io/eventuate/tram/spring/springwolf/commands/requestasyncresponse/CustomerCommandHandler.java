package io.eventuate.tram.spring.springwolf.commands.requestasyncresponse;


import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.commands.consumer.annotations.EventuateCommandHandler;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.commands.ReserveCreditCommand;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerCreditLimitExceeded;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerCreditReserved;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerNotFound;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.ReserveCreditResult;


public class CustomerCommandHandler {

  private final CustomerService customerService;

  public CustomerCommandHandler(CustomerService customerService) {
    this.customerService = customerService;
  }

  @EventuateCommandHandler(subscriberId="customerCommandDispatcher", channel="customerService")
  public ReserveCreditResult reserveCredit(CommandMessage<ReserveCreditCommand> cm) {
    ReserveCreditCommand cmd = cm.getCommand();
    try {
      customerService.reserveCredit(cmd.customerId(), cmd.orderId(), cmd.orderTotal());
      return new CustomerCreditReserved();
    } catch (CustomerNotFoundException e) {
      return new CustomerNotFound();
    } catch (CustomerCreditLimitExceededException e) {
      return new CustomerCreditLimitExceeded();
    }
  }



}
