package org.iesalixar.daw2.cine.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.iesalixar.daw2.cine.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler para el éxito de autenticación con Discord.
 * Cruza el usuario de Discord con la base de datos local para cargar los roles reales.
 */
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Extraer el usuario que viene de Discord
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. Discord devuelve el nombre de usuario en el atributo "username"
        // Asegúrate de tener 'user-name-attribute=username' en tu application.properties
        String username = oAuth2User.getAttribute("username");

        // 3. Validación: El usuario debe existir previamente en nuestra DB
        if (username == null || !userRepository.existsByUsername(username)) {
            // Si no existe, lanzamos excepción para que la capture tu CustomOAuth2FailureHandler
            throw new OAuth2AuthenticationException("El usuario de Discord '" + username + "' no está registrado en la base de datos del cine.");
        }

        try {
            // 4. Cargar los detalles del usuario (y sus ROLES) desde tu CustomUserDetailsService
            // Esto es vital para que .hasRole("ADMIN") o .hasRole("MANAGER") funcionen
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // 5. Crear un nuevo Token de Autenticación basado en los datos de nuestra DB
            UsernamePasswordAuthenticationToken internalAuthentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // 6. Sobrescribir el contexto de seguridad con el usuario de nuestra base de datos
            SecurityContextHolder.getContext().setAuthentication(internalAuthentication);

            // 7. Redirigir al usuario a la página de inicio
            response.sendRedirect("/");

        } catch (Exception e) {
            throw new OAuth2AuthenticationException("Error al vincular el usuario de Discord con la base de datos local.");
        }
    }
}