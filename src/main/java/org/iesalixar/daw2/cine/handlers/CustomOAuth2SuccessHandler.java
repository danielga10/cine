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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("=== INICIO CustomOAuth2SuccessHandler ===");

        try {
            // 1. Obtener los datos del usuario de Google
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            logger.info("Usuario OAuth2 autenticado: {}", oAuth2User.getAttributes());

            // 2. Extraer el EMAIL (Google usa 'email')
            String email = oAuth2User.getAttribute("email");
            logger.info("Email extraído: {}", email);

            if (email == null || email.isEmpty()) {
                logger.error("No se pudo obtener el email del usuario OAuth2");
                throw new OAuth2AuthenticationException("No se pudo obtener el email del usuario.");
            }

            // 3. Verificar si el usuario existe en nuestra base de datos
            if (!userRepository.existsByUsername(email)) {
                logger.warn("Usuario no encontrado en BD: {}", email);
                // Si no existe, lanzamos excepción para que lo capture el FailureHandler
                throw new OAuth2AuthenticationException("El usuario con email " + email + " no está registrado en el sistema.");
            }

            // 4. Si existe, cargamos sus datos y ROLES desde nuestra BD local
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            logger.info("UserDetails cargado: {}", userDetails.getUsername());
            logger.info("Authorities: {}", userDetails.getAuthorities());

            // 5. Crear una nueva autenticación con los roles correctos de nuestra BD
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // 6. Obtener o crear el SecurityContext
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticationToken);

            // 7. Guardar el SecurityContext en la sesión HTTP (IMPORTANTE para que persista)
            securityContextRepository.saveContext(securityContext, request, response);

            // 8. También establecerlo en el SecurityContextHolder para el request actual
            SecurityContextHolder.setContext(securityContext);

            logger.info("Autenticación establecida correctamente en SecurityContext y sesión");
            logger.info("=== FIN CustomOAuth2SuccessHandler ===");

            // 9. Redirigir a la página de inicio
            response.sendRedirect("/");

        } catch (Exception e) {
            logger.error("Error en CustomOAuth2SuccessHandler", e);
            throw e;
        }
    }
}