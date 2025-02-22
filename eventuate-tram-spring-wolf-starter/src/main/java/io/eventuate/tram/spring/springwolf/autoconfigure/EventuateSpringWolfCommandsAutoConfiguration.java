package io.eventuate.tram.spring.springwolf.autoconfigure;

import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.spring.springwolf.EventuateSpringWolfCommandsConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnClass(CommandMessageHeaders.class)
@Import(EventuateSpringWolfCommandsConfiguration.class)
public class EventuateSpringWolfCommandsAutoConfiguration {
}
