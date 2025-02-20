package io.eventuate.tram.spring.springwolf.application.requestasyncresponse;

import io.eventuate.examples.common.money.Money;
import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.commands.ReserveCreditCommand;
import io.eventuate.tram.testutil.TestMessageConsumer;
import io.eventuate.tram.testutil.TestMessageConsumerFactory;
import io.eventuate.util.test.async.Eventually;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.mockito.Mockito.verify;

@SpringBootTest
public class CommandHandlingTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, RequestAsyncResponseConfiguration.class})
  public static class Config {

    @Bean
    TestMessageConsumerFactory testMessageConsumerFactory() {
      return new TestMessageConsumerFactory();
    }

    @Bean
    TestMessageConsumer testConsumer(TestMessageConsumerFactory testMessageConsumerFactory) {
      return testMessageConsumerFactory.make();
    }
  }

  @Autowired
  private CommandProducer commandProducer;

  @MockitoBean
  private CustomerService customerService;

  @Autowired
  private TestMessageConsumer testConsumer;

  @Test
  public void shouldHandleCommand() {
    var commandId = commandProducer.send("customerService", new ReserveCreditCommand(101L, 100L, new Money("12.34")),
        testConsumer.getReplyChannel(), Map.of());

    Eventually.eventually(() -> {
      verify(customerService).reserveCredit(101L, 100L, new Money("12.34"));
    });

    testConsumer.assertHasReplyTo(commandId);

  }

}
