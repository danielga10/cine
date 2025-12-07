package org.iesalixar.daw2.cine;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile; // ¡Añadido!
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@Profile("testcontainers") // <-- Solo se carga si se activa el perfil "testcontainers"
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    MariaDBContainer<?> mariaDbContainer() {
        // Recomendación: Usar una versión específica en lugar de 'latest'
        return new MariaDBContainer<>(DockerImageName.parse("mariadb:10.6.4"));
    }

}