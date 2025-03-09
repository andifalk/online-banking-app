package com.example.banking;

import dasniko.testcontainers.keycloak.KeycloakContainer;
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

    private final static String POSTGRES_IMAGE = "postgres:latest";
    private final static String MAILPIT_IMAGE = "axllent/mailpit:v1.23.1";
    private final static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.1.3";
    private final static String realmImportFile = "/banking-demo-realm.json";
    private final static String realmName = "banking-demo";

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));
    }


    @Bean
    GenericContainer<?> mailpitContainer() {
        return new GenericContainer<>(MAILPIT_IMAGE)
                .withExposedPorts(1025, 8025)
                .waitingFor(Wait.forLogMessage(".*accessible via.*", 1));
    }


    @Bean
    KeycloakContainer keycloak() {
        return new KeycloakContainer(KEYCLOAK_IMAGE)
                .withAdminUsername("admin")
                .withAdminPassword("admin")
                .withRealmImportFile(realmImportFile);
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
    public DynamicPropertyRegistrar keycloakRegistrar(KeycloakContainer keycloakContainer) {
        return registry -> {
            registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                    () -> keycloakContainer.getAuthServerUrl() + "/realms/" + realmName + "/protocol/openid-connect/certs");
            registry.add("auth-server-url",
                    keycloakContainer::getAuthServerUrl);
        };
    }

    @Bean
    public ApplicationRunner logTestContainerInfos(
            @Value("${spring.mail.host}") String host,
            @Value("${mailpit.web.port}") int port,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String keycloakRealm,
            @Value("${auth-server-url}") String keycloakUrl) {
        Logger log = LoggerFactory.getLogger(getClass());
        return args -> {
            log.info("Mailpit accessible through http://{}:{}", host, port);
            log.info("Keycloak is accessible through {}", keycloakUrl);
            log.info("Keycloak jwk-set is at {}", keycloakRealm);
        };
    }
}
