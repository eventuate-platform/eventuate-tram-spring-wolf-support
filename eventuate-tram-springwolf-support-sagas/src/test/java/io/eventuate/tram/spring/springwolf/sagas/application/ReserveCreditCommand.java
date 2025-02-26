package io.eventuate.tram.spring.springwolf.sagas.application;

import io.eventuate.tram.commands.common.Command;

public record ReserveCreditCommand(Long customerId, long orderId, io.eventuate.examples.common.money.Money orderTotal) implements Command {
}
