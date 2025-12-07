package org.iesalixar.daw2.cine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.testcontainers.context.ImportTestcontainers; // ¡Añadido!

// Esta clase usa la configuración de Testcontainers de forma automática
// para cuando ejecutas tu aplicación manualmente (fuera de las pruebas Maven).
public class TestCineApplication {

    public static void main(String[] args) {
        // Asumiendo que tu clase principal de Spring Boot es CineApplication
        SpringApplication.from(CineApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}