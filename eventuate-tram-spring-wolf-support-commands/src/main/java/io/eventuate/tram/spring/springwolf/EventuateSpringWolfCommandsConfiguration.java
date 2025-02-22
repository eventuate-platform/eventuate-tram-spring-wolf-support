package io.eventuate.tram.spring.springwolf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EventuateSpringWolfConfiguration.class)
@ComponentScan(basePackageClasses = EventuateSpringWolfCommandsConfiguration.class)
public class EventuateSpringWolfCommandsConfiguration {
}