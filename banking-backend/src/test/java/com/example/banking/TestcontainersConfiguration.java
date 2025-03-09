package com.example.banking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

    @Bean
    GenericContainer<?> mailpitContainer() {
        var container = new GenericContainer<>("axllent/mailpit:v1.15")
                .withExposedPorts(1025, 8025)
                .waitingFor(Wait.forLogMessage(".*accessible via.*", 1));
        return container;
    }

    @Bean
    public DynamicPropertyRegistrar mailpitRegistrar(GenericContainer<?> mailpitContainer) {
        return registry -> {
            registry.add("spring.mail.host", mailpitContainer::getHost);
            registry.add("spring.mail.port", mailpitContainer::getFirstMappedPort);
            registry.add("mailpit.web.port", () -> mailpitContainer.getMappedPort(8025));
        };
    }

    @Bean
    public ApplicationRunner logMailpitWebPort(@Value("${spring.mail.host}") String host, @Value("${mailpit.web.port}") int port) {
        Logger log = LoggerFactory.getLogger(getClass());
        return args -> log.info("Mailpit accessible through http://{}:{}", host, port);
    }
}
