package org.iesalixar.daw2.cine.config;

import org.iesalixar.daw2.cine.handlers.CustomOAuth2FailureHandler;
import org.iesalixar.daw2.cine.handlers.CustomOAuth2SuccessHandler;
import org.iesalixar.daw2.cine.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @Autowired
    private CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private static final Logger logger =
            LoggerFactory.getLogger(SecurityConfig.class);
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Entrando en el método securityFilterChain");

        http
                .authorizeHttpRequests((auth) -> {
                    auth
                            .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                            .requestMatchers("/", "/hello", "/oauth2/**", "/login").permitAll()

                            // 1. Rutas exclusivas de GESTIÓN (Solo MANAGER y ADMIN)
                            .requestMatchers("/clientes/**", "/trabajadores/**", "/boletos/**").hasAnyRole("MANAGER", "ADMIN")

                            // 2. Rutas de CONSULTA y CINE (USER puede entrar a ver, pero limitaremos sus acciones en el Controller)
                            .requestMatchers("/peliculas/**", "/directores/**", "/funciones/**", "/salas/**")
                            .hasAnyRole("USER", "MANAGER", "ADMIN")

                            // 3. Administración total
                            .requestMatchers("/admin/**").hasRole("ADMIN")

                            .anyRequest().authenticated();
                })
                .formLogin(form -> {
                    logger.debug("Configurando formulario de inicio de sesión");
                    form.loginPage("/login") // Asegúrate de tener un controller para esto
                            .defaultSuccessUrl("/", true)
                            .permitAll();
                })
                .oauth2Login(oauth2 -> {
                    logger.debug("Configurando login con OAuth2");
                    oauth2
                            .loginPage("/login")
                            .successHandler(customOAuth2SuccessHandler)
                            .failureHandler(customOAuth2FailureHandler);
                })
                .sessionManagement(session -> {
                    logger.debug("Configurando política de gestión de sesiones");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                });

        logger.info("Saliendo del método securityFilterChain");
        return http.build();
    }
    /**
     * Configura el proveedor de autenticación para usar el servicio de detalles de
     usuario
     * personalizado y el codificador de contraseñas.
     *
     * @return una instancia de {@link DaoAuthenticationProvider} para la
    autenticación.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
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

