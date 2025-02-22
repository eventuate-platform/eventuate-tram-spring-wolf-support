package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomersAndOrdersConfiguration;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.commands.ReserveCreditCommand;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ChannelsFromCommandHandlerScannerTest {

    @Configuration
    @Import({CustomersAndOrdersConfiguration.class, })
    static class TestConfig {
        @Bean
        public ChannelsFromCommandHandlerScanner channelsFromCommandHandlerScanner() {
            return new ChannelsFromCommandHandlerScanner();
        }

    }

    @Autowired
    private ChannelsFromCommandHandlerScanner scanner;

    @Autowired
    private ApplicationContext ctx;

    @MockitoBean
    private SpringWolfMessageFactory springWolfMessageFactory;

    @Test
    public void shouldFindCommandHandlers() {

        when(springWolfMessageFactory.makeMessageFromClass(ReserveCreditCommand.class))
            .thenReturn(MessageObject.builder().messageId(ReserveCreditCommand.class.getName()).build());

        Map<String, ChannelObject> handlers = scanner.scan();

        assertThat(handlers)
                .isNotNull()
                .hasSize(1);

        ChannelObject channel = handlers.get("customerService");
        assertThat(channel).isNotNull();
    }
}
