package io.eventuate.tram.spring.springwolf;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventuateCommandHandler {
  String subscriberId();

  String channel();

}
