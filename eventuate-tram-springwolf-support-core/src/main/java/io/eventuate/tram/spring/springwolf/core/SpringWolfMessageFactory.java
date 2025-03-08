package io.eventuate.tram.spring.springwolf.core;

import io.github.springwolf.asyncapi.v3.model.channel.message.MessageHeaders;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessagePayload;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;
import io.github.springwolf.asyncapi.v3.model.schema.MultiFormatSchema;
import io.github.springwolf.core.asyncapi.components.ComponentsService;
import io.github.springwolf.core.asyncapi.scanners.common.headers.AsyncHeadersNotDocumented;
import io.github.springwolf.core.asyncapi.scanners.common.payload.PayloadSchemaObject;
import io.github.springwolf.core.asyncapi.scanners.common.payload.internal.PayloadService;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringWolfMessageFactory {

  @Autowired
  private PayloadService payloadService;

  @Autowired
  private ComponentsService componentsService;

  public MessageObject makeMessageFromClass(Class messageClass) {
    PayloadSchemaObject payloadSchemaObject = payloadService.buildSchema("application/json", messageClass);

    String headerModelName = componentsService.registerSchema(AsyncHeadersNotDocumented.NOT_DOCUMENTED);
    MessageHeaders messageHeaders = MessageHeaders.of(MessageReference.toSchema(headerModelName));

    MessageObject message = MessageObject.builder()
        .messageId(messageClass.getName())
        .name(messageClass.getName())
        .title(messageClass.getName())
        .headers(messageHeaders)
        .payload(MessagePayload.of(
            MultiFormatSchema.builder().schema(payloadSchemaObject.payload()).build()))
        .build();
    componentsService.registerMessage(message);
    return message;
  }
}
