package io.eventuate.tram.spring.springwolf.sagas.application;

import io.eventuate.examples.common.money.Money;
import io.eventuate.tram.commands.common.Command;

public record ReserveCreditCommand(Long customerId, long orderId, Money orderTotal) implements Command {
}
