package io.eventuate.tram.spring.springwolf.autoconfigure;

import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.eventuate.tram.spring.springwolf.commands.EventuateSpringWolfCommandsConfiguration;
import io.eventuate.tram.spring.commands.autoconfigure.EventuateTramCommandsAutoConfigure;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@AutoConfigureAfter(EventuateTramCommandsAutoConfigure.class)
@ConditionalOnBean(EventuateCommandDispatcher.class)
@Import(EventuateSpringWolfCommandsConfiguration.class)
public class EventuateSpringWolfCommandsAutoConfiguration {
}
