package org.iesalixar.daw2.cine.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    // Se crea un logger para la clase utilizando SLF4J para registrar eventos importantes y posibles errores
    private static final Logger logger = LoggerFactory.getLogger(DotenvConfig.class);

    // Bloque estático para inicializar y cargar las variables de entorno del
    // archivo .env
    static {
        try {
            // Registrar en el log un mensaje informativo indicando que el proceso de carga del archivo .env ha comenzado
            logger.info("Loading environment variables from .env file...");

            // Configuración y carga del archivo .env usando la librería Dotenv
            Dotenv dotenv = Dotenv.configure().load();

            // Iterar sobre todas las entradas (clave-valor) del archivo .env y establecerlas como propiedades del sistema
            dotenv.entries().forEach(entry -> {
                // Establecer cada variable de entorno como una propiedad del sistema
                System.setProperty(entry.getKey(), entry.getValue());

                // Registrar en el log el valor de cada variable cargada para fines de depuración (nivel DEBUG)
                logger.debug("Set system property: {} = {}", entry.getKey(),
                        entry.getValue());
            });

            // Registrar en el log un mensaje informativo indicando que el archivo .env se cargó correctamente
            logger.info(".env file loaded successfully.");
        } catch (Exception e) {
            // Si ocurre alguna excepción durante el proceso de carga del archivo .env, se captura y se registra un mensaje de error
            logger.error("Failed to load .env file", e);
        }
    }
}
