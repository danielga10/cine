package org.iesalixar.daw2.cine.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.iesalixar.daw2.cine.services.CustomUserDetailsService; // O tu servicio de UserDetails
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

        // 1. Obtener los datos del usuario de Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. Extraer el EMAIL (Google usa 'email', GitHub usaba 'login')
        String email = oAuth2User.getAttribute("email");

        // 3. Verificar si el usuario existe en nuestra base de datos
        if (!userRepository.existsByUsername(email)) {
            // Si no existe, lanzamos excepción para que lo capture el FailureHandler
            throw new OAuth2AuthenticationException("El usuario con email " + email + " no está registrado en el sistema.");
        }

        // 4. Si existe, cargamos sus datos y ROLES desde nuestra BD local
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // 5. Crear una nueva autenticación con los roles correctos de nuestra BD
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // 6. Actualizar el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 7. Redirigir a la página de inicio
        response.sendRedirect("/");
    }
}