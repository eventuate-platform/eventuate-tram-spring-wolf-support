package io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies;

import io.eventuate.tram.commands.consumer.annotations.FailureReply;

@FailureReply
public class CustomerNotFound implements ReserveCreditResult {
}
