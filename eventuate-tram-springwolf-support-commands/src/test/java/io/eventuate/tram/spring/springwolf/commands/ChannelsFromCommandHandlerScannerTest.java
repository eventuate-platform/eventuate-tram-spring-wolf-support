package io.eventuate.tram.spring.springwolf.commands;

import io.eventuate.tram.spring.commands.consumer.CommandHandlerInfo;
import io.eventuate.tram.spring.commands.consumer.EventuateCommandDispatcher;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.CustomerCommandHandler;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.commands.ReserveCreditCommand;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerCreditLimitExceeded;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerCreditReserved;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerNotFound;
import io.eventuate.tram.spring.springwolf.core.ElementsWithClasses;
import io.github.springwolf.asyncapi.v3.model.channel.ChannelObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChannelsFromCommandHandlerScannerTest {

    private ChannelsFromCommandHandlerScanner scanner;

    @Mock
    private EventuateCommandDispatcher eventuateCommandDispatcher;

    @BeforeEach
    public void setUp() {
        scanner = new ChannelsFromCommandHandlerScanner(eventuateCommandDispatcher);
    }


    @Test
    public void shouldFindCommandHandlers() {
        var customerCommandHandler = new CustomerCommandHandler(null);

        Method method = Arrays.stream(CustomerCommandHandler.class.getMethods())
            .filter(m -> m.getName().equals("reserveCredit"))
            .findFirst().orElseThrow();

        when(eventuateCommandDispatcher.getCommandHandlers())
            .thenReturn(List.of(new CommandHandlerInfo(customerCommandHandler, "customerCommandDispatcher", "customerService", method)));

        ElementsWithClasses<ChannelObject> scanResult = scanner.scan();
        Map<String, ChannelObject> handlers = scanResult.elements();
        Set<Class<?>> classes = scanResult.classes();

        assertThat(handlers).hasSize(2);

        assertThat(handlers).containsKey("customerService");

        assertThat(handlers).containsKey(CustomerCommandHandler.class.getName() + ".reserveCredit-reply");

        assertThat(classes).containsExactlyInAnyOrder(ReserveCreditCommand.class,
            CustomerCreditReserved.class,
            CustomerNotFound.class,
            CustomerCreditLimitExceeded.class);
    }
}
