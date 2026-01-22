package org.iesalixar.daw2.cine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // ¡Añadido!

// @Import(TestcontainersConfiguration.class) <-- ¡Eliminado!
@SpringBootTest
@ActiveProfiles("testcontainers") // <-- ¡Activa el perfil para usar el contenedor!
class CineApplicationTests {
/*
    @Test
    void contextLoads() {
        // ...
    }
*/
}