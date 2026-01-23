package org.iesalixar.daw2.cine.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler OAuth2 que simplemente conf�a en la autenticaci�n ya construida y redirige al home.
 */
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("=== INICIO CustomOAuth2SuccessHandler ===");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        logger.info("Usuario OAuth2 autenticado: {}", oAuth2User.getAttributes());

        logger.info("Autenticaci�n establecida, redirigiendo a /");
        logger.info("=== FIN CustomOAuth2SuccessHandler ===");
        response.sendRedirect("/");
    }
}
