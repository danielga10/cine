package org.iesalixar.daw2.cine.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    // Logger para registrar eventos en esta clase
    private static final Logger logger = LoggerFactory.getLogger(LocaleConfig.class);

    /**
     * Define el `LocaleResolver` que se usará para almacenar la configuración de idioma del usuario.
     * En este caso, utilizamos `SessionLocaleResolver` para almacenar el idioma en la sesión.
     *
     * @return una instancia de `SessionLocaleResolver` con el idioma predeterminado configurado.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.of("es")); // Establece el español como idioma por defecto
        logger.info("LocaleResolver configurado con el idioma predeterminado: es");
        return slr;
    }

    /**
     * Define un `LocaleChangeInterceptor` que intercepta las peticiones HTTP para cambiar el idioma
     * utilizando un parámetro llamado "lang" en la URL.
     *
     * @return una instancia de `LocaleChangeInterceptor` con el parámetro configurado.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // El idioma se cambia con ?lang=es o ?lang=en
        logger.info("LocaleChangeInterceptor configurado con el parámetro 'lang'");
        return interceptor;
    }

    /**
     * Registra el `LocaleChangeInterceptor` para que se aplique a todas las solicitudes.
     * Esto permite que el idioma de la aplicación se pueda cambiar dinámicamente.
     *
     * @param registry el registro de interceptores de Spring.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        logger.info("LocaleChangeInterceptor registrado en el InterceptorRegistry");
    }
}
