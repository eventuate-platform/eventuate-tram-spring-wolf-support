package io.eventuate.tram.spring.springwolf.commands;

import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.CustomerCommandHandler;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.CustomersAndOrdersConfiguration;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.commands.ReserveCreditCommand;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerCreditLimitExceeded;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerCreditReserved;
import io.eventuate.tram.spring.springwolf.commands.requestasyncresponse.replies.CustomerNotFound;
import io.eventuate.tram.spring.springwolf.core.EventuateSpringWolfConfiguration;
import io.eventuate.tram.spring.springwolf.testing.AsyncApiDocument;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventuateTramSpringWolfCommandsTest {

  @Configuration
  @EnableAutoConfiguration
  @Import({TramInMemoryConfiguration.class, EventuateSpringWolfConfiguration.class, EventuateSpringWolfCommandsConfiguration.class, CustomersAndOrdersConfiguration.class})
  public static class Config {
  }

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
  }

  @Test
  public void shouldExposeSpringWolf() {
    AsyncApiDocument doc = AsyncApiDocument.getSpringWolfDoc();

    assertThat(doc.getVersion())
        .as("AsyncAPI version should be 3.0.0")
        .isEqualTo("3.0.0");

    doc.assertReceivesMessageAndReplies(
        CustomerCommandHandler.class.getName() + ".reserveCredit",
        "customerService",
        ReserveCreditCommand.class.getName(),
        Set.of(CustomerNotFound.class.getName(), CustomerCreditLimitExceeded.class.getName(), CustomerCreditReserved.class.getName())
    );

    // Verify components section
    assertThat(doc.getComponents())
        .as("Components section should exist")
        .isNotNull();

    // Verify schemas
    assertThat(doc.getComponents().getSchemas())
        .as("Schemas section should exist")
        .isNotNull()
        .containsKey(ReserveCreditCommand.class.getName());

    // Verify messages
    assertThat(doc.getComponents().getMessages())
        .as("Messages section should exist")
        .isNotNull()
        .containsKey(ReserveCreditCommand.class.getName())
        .satisfies(messages -> {
            var message = messages.get(ReserveCreditCommand.class.getName());
            assertThat(message.getPayload().getSchemaFormat())
                .as("Schema format should be AsyncAPI 3.0.0")
                .isEqualTo("application/vnd.aai.asyncapi+json;version=3.0.0");
            assertThat(message.getPayload().getSchema())
                .as("Message schema should reference the schema definition")
                .extracting("ref")
                .asString()
                .isEqualTo("#/components/schemas/" + ReserveCreditCommand.class.getName());
        });
  }

  @Test
  public void shouldExposeSpringWolfUi() {

    RestAssured.given()
        .get("/springwolf/asyncapi-ui.html")
        .then()
        .statusCode(200);

  }

}
