# Eventuate Tram SpringWolf Support Guidelines

## Project Overview
This project provides integration between Eventuate Tram and SpringWolf, enabling AsyncAPI documentation for event-driven microservices. It supports events, commands, and sagas functionality.

## Tech Stack
- Java 17
- Spring Boot
- Gradle
- Eventuate Tram
- SpringWolf
- JUnit (for testing)

## Project Structure
The project is organized into several modules:
- `eventuate-tram-springwolf-support-core`: Core functionality
- `eventuate-tram-springwolf-support-events`: Event handling support
- `eventuate-tram-springwolf-support-commands`: Command handling support
- `eventuate-tram-springwolf-support-sagas`: Saga pattern support
- `eventuate-tram-springwolf-starter`: Spring Boot starter for easy integration

## Build and Run Instructions
1. Build the project:
   ```bash
   ./gradlew build
   ```
2. Run tests:
   ```bash
   ./gradlew test
   ```

## Testing Guidelines
- Tests are written using JUnit 5
- Each module contains its own test suite
- Integration tests are available in respective modules
- Use `@SpringBootTest` for integration testing
- REST Assured is used for API testing
- AssertJ is used for assertions
- Tests include AsyncAPI documentation verification
- Use appropriate Spring Boot test annotations (`@SpringBootTest`, `@Import`, etc.)
- Test configuration classes are typically inner classes marked with `@Configuration`

## Best Practices
1. Follow standard Java code conventions
2. Write unit tests for new functionality
3. Keep modules focused and maintain separation of concerns
4. Use appropriate Spring Boot annotations for configuration
5. Follow event-driven architecture principles when implementing new features
