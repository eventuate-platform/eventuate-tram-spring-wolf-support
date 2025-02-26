package io.eventuate.tram.spring.springwolf.core;

import io.github.springwolf.asyncapi.v3.model.channel.message.MessageReference;

public class SpringWolfUtils {
  public static MessageReference makeMessageReference(Class<?> clasz) {
    return MessageReference.toComponentMessage(clasz.getName());
  }
}
