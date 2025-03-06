package io.eventuate.tram.spring.springwolf.commands.requestasyncresponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomersAndOrdersConfiguration {
  @Bean
  CustomerCommandHandler customerCommandHandler(CustomerService customerService) {
    return new CustomerCommandHandler(customerService);
  }

  @Bean
  CustomerService customerService() {
    return new CustomerService();
  }

}
