package io.eventuate.tram.spring.springwolf;

import io.eventuate.tram.commands.consumer.annotations.EventuateCommandHandler;
import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.CustomerCommandHandler;
import io.eventuate.tram.spring.springwolf.application.requestasyncresponse.commands.ReserveCreditCommand;
import io.github.springwolf.asyncapi.v3.model.channel.Channel;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import io.github.springwolf.asyncapi.v3.model.channel.message.MessageObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChannelsFromCommandHandlerScannerTest {

    private ChannelsFromCommandHandlerScanner scanner;


    @Mock
    private SpringWolfMessageFactory springWolfMessageFactory;

    @Mock
    private EventuateCommandDispatcher eventuateCommandDispatcher;

    @BeforeEach
    public void setUp() {
        scanner = new ChannelsFromCommandHandlerScanner(eventuateCommandDispatcher,
            springWolfMessageFactory);
    }


    @Test
    public void shouldFindCommandHandlers() {
        var customerCommandHandler = new CustomerCommandHandler(null);

        when(springWolfMessageFactory.makeMessageFromClass(ReserveCreditCommand.class))
            .thenReturn(MessageObject.builder().messageId(ReserveCreditCommand.class.getName()).build());

        Method method = Arrays.stream(CustomerCommandHandler.class.getMethods())
            .filter(m -> m.getName().equals("reserveCredit"))
            .findFirst().orElseThrow();

        EventuateCommandHandler eventuateCommandHandler =
            method.getAnnotation(EventuateCommandHandler.class);

        when(eventuateCommandDispatcher.getCommandHandlers())
            .thenReturn(List.of(new CommandHandlerInfo(customerCommandHandler, "customerCommandDispatcher", "customerService", method)));

        Map<String, ChannelObject> handlers = scanner.scan().elements();

        assertThat(handlers)
                .isNotNull()
                .hasSize(1);

        Channel channel = handlers.get("customerService");
        assertThat(channel).isNotNull();
    }
}
