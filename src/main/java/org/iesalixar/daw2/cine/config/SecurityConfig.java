package org.iesalixar.daw2.cine.config;

import org.iesalixar.daw2.cine.handlers.CustomOAuth2FailureHandler;
import org.iesalixar.daw2.cine.handlers.CustomOAuth2SuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
/**
 * Configura la seguridad de la aplicación, definiendo autenticación y
 autorización
 * para diferentes roles de usuario, y gestionando la política de sesiones.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @Autowired
    private CustomOAuth2FailureHandler customOAuth2FailureHandler;
    /**
     * Configura el filtro de seguridad para las solicitudes HTTP, especificando
     las
     * rutas permitidas y los roles necesarios para acceder a diferentes
     endpoints.
     *
     * @param http instancia de {@link HttpSecurity} para configurar la
    seguridad.
     * @return una instancia de {@link SecurityFilterChain} que contiene la
    configuración de seguridad.
     * @throws Exception si ocurre un error en la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
            Exception {
        logger.info("Entrando en el método securityFilterChain");
        // Configuración de seguridad
        http.authorizeHttpRequests(auth -> {
                    logger.debug("Configurando autorización de solicitudes HTTP");
                    auth
                            .requestMatchers("/").permitAll()
// Acceso anónimo
                            .requestMatchers("/admin").hasRole("ADMIN")
// Solo ADMIN
                            .requestMatchers("/aeropuertos", "/aviones",
                                    "/directores", "/pasajeros", "/rutas", "/tickets", "/trabajadores").hasRole("MANAGER") // Solo MANAGER
                            .requestMatchers("/rutas").hasRole("USER")
// Solo USER
                            .anyRequest().authenticated(); //Cualquier otra solicitud requiere autenticación
                })
                .formLogin(form -> {
                    logger.debug("Configurando formulario de inicio de sesión");
                    form
                            .loginPage("/login") // Página personalizada de login
                            .successHandler(customOAuth2SuccessHandler) // Usa el Success Handler personalizado
                            .failureHandler(customOAuth2FailureHandler) // Handler para fallo en autenticación
                            .permitAll(); // Permite acceso a la página de login a todos los usuarios
                })
                .oauth2Login(oauth2 -> {
                    logger.debug("Configurando login con OAuth2");
                    oauth2
                            .loginPage("/login")// Reutiliza la página de inicio de sesión personalizada
                            .successHandler(customOAuth2SuccessHandler) // Handler de éxito
                            .failureHandler(customOAuth2FailureHandler) // Handler de error
                            .permitAll(); // Permite acceso a la página de login a todos los usuarios

                })
                .logout(form -> {
                    logger.debug("Configurando fin de sesión");
                    form
                            .logoutUrl("/logout") // Página personalizada de logout
                            .logoutSuccessUrl("/login?logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll();
                })
                .sessionManagement(session -> {
                    logger.debug("Configurando política de gestión de sesiones");

                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED); // Usa sesiones cuando sea necesario
                });
        logger.info("Saliendo del método securityFilterChain");
        return http.build();
    }
    /**
     * Configura los detalles de usuario en memoria para pruebas y desarrollo,
     asignando
     * roles específicos a cada usuario.
     *
     * @return una instancia de {@link UserDetailsService} que proporciona
    autenticación en memoria.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("Entrando en el método userDetailsService");
        logger.debug("Creando usuario con rol USER");
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        logger.debug("Creando usuario con rol ADMIN");
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN")
                .build();
        logger.debug("Creando usuario con rol MANAGER");
        UserDetails manager = User.builder()
                .username("manager")
                .password(passwordEncoder().encode("password"))
                .roles("MANAGER")
                .build();
        logger.info("Saliendo del método userDetailsService");
        return new InMemoryUserDetailsManager(user, admin, manager);
    }
    /**
     * Configura el codificador de contraseñas para cifrar las contraseñas de
     los usuarios
     * utilizando BCrypt.
     *
     * @return una instancia de {@link PasswordEncoder} que utiliza BCrypt para
    cifrar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Entrando en el método passwordEncoder");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        logger.info("Saliendo del método passwordEncoder");
        return encoder;
    }
}
