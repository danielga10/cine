package org.iesalixar.daw2.cine.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.iesalixar.daw2.cine.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Identificar si es Google o Discord
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId(); // "google" o "discord"

        OAuth2User oAuth2User = oauthToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Esta variable almacenará lo que vamos a buscar en TU campo 'username' de la base de datos
        String usernameToSearch = null;

        // 2. Extraer el dato correcto según el proveedor
        if ("discord".equals(clientRegistrationId)) {
            // En Discord, el identificador único es el 'username'
            usernameToSearch = (String) attributes.get("username");
            logger.info("Login OAuth2: Discord detectado. Usuario: {}", usernameToSearch);

        } else if ("google".equals(clientRegistrationId)) {
            // En Google, el identificador es el 'email'.
            // Como tú guardas el email en el campo 'username', usamos el email para buscar.
            usernameToSearch = (String) attributes.get("email");
            logger.info("Login OAuth2: Google detectado. Email: {}", usernameToSearch);
        }

        // 3. Validación de seguridad
        if (usernameToSearch == null) {
            throw new OAuth2AuthenticationException("Error: No se pudo identificar al usuario desde " + clientRegistrationId);
        }

        // 4. Buscar en TU base de datos local
        // Aquí es donde ocurre la magia: busca "jaime@gmail.com" en la columna "username"
        if (!userRepository.existsByUsername(usernameToSearch)) {
            logger.error("El usuario {} no existe en la base de datos local.", usernameToSearch);
            throw new OAuth2AuthenticationException("El usuario '" + usernameToSearch + "' no está registrado en el sistema.");
        }

        try {
            // 5. Cargar roles y datos del usuario local
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(usernameToSearch);

            // 6. Autenticar en Spring Security con los datos de TU base de datos (incluyendo roles)
            UsernamePasswordAuthenticationToken internalAuthentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(internalAuthentication);

            // 7. Redirigir al home
            response.sendRedirect("/");

        } catch (Exception e) {
            logger.error("Error en el proceso de login OAuth2: {}", e.getMessage());
            throw new OAuth2AuthenticationException("Error interno al procesar el login.");
        }
    }
}