package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomerCommandHandler;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ChannelsFromCommandHandlerScannerTest {

    @Configuration
    static class TestConfig {
        @Bean
        public CustomerCommandHandler customerCommandHandler(CustomerService customerService) {
            return new CustomerCommandHandler(customerService);
        }

        @Bean
        public ChannelsFromCommandHandlerScanner channelsFromCommandHandlerScanner() {
            return new ChannelsFromCommandHandlerScanner();
        }

        @Bean
        CustomerService customerService() {
            return new CustomerService();
        }

    }

    @Autowired
    private ChannelsFromCommandHandlerScanner scanner;

    @MockitoBean
    private SpringWolfMessageFactory springWolfMessageFactory;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void shouldFindCommandHandlers() {

        List<CommandHandlerInfo> handlers = scanner.searchAppContextForCommandHandlers(ctx);

        assertNotNull(handlers);
        assertEquals(1, handlers.size());

        CommandHandlerInfo handler = handlers.get(0);
        assertEquals("customerCommandDispatcher", handler.eventuateCommandHandler().subscriberId());
        assertEquals("customerService", handler.eventuateCommandHandler().channel());
    }
}
