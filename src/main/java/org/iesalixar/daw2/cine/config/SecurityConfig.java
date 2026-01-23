package org.iesalixar.daw2.cine.config;

import org.iesalixar.daw2.cine.services.OAuth2UserDetailsService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
/**
 * Configura la seguridad de la aplicación, definiendo autenticación y
 autorización
 * para diferentes roles de usuario, y gestionando la política de sesiones.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Activa la seguridad basada en métodos
public class SecurityConfig {
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
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private OAuth2UserDetailsService oauth2UserDetailsService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
            Exception {
        logger.info("Entrando en el método securityFilterChain");
        // Configuración de seguridad
        http
                .authorizeHttpRequests(auth -> {
                    logger.debug("Configurando autorización de solicitudes HTTP");
                    auth
                            .requestMatchers("/", "/hello").permitAll() // Acceso anónimo
                            .requestMatchers("/css/**", "/js/**", "/img/**", "/uploads/**").permitAll() // Recursos estáticos
                            .requestMatchers("/admin", "/provincias", "/regions").hasRole("ADMIN") // Solo ADMIN
                            .requestMatchers("/regions", "/provincias", "/supermarkets", "/locations", "/categories").hasRole("MANAGER") // Solo MANAGER
                            .requestMatchers("/tickets").hasRole("USER") // Solo USER
                            .anyRequest().authenticated(); // Cualquier otra solicitud requiere autenticación
                })
                .formLogin(form -> {
                    logger.debug("Configurando formulario de inicio de sesión");
                    form
                            .loginPage("/login") // Página personalizada de Login
                            .defaultSuccessUrl("/") // Redirige al inicio después del Login
                            .permitAll(); // Permite acceso a la página de Login a todos los usuarios
                })
                .sessionManagement(session -> {
                    logger.debug("Configurando política de gestión de sesiones");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED); // Usa sesiones cuando sea necesario
                })
                .oauth2Login(oauth2 -> {
                    logger.debug("Configurando login con OAuth2");
                    oauth2
                            .loginPage("/login") // Reutiliza la página de inicio de sesión personalizada
                            .defaultSuccessUrl("/", true) // Redirige al inicio después del login exitoso con OAuth2
                            .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserDetailsService)) // Servicio personalizado
                            .permitAll();
                });
        logger.info("Saliendo del método securityFilterChain");
        return http.build();
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

}