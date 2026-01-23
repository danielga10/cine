package org.iesalixar.daw2.cine.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler personalizado para manejar fallos en la autenticación con OAuth2.
 * Se ejecuta automáticamente cuando ocurre una excepción durante el login.
 */
@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2FailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 1. Loguear el error para depuración [cite: 235]
        logger.warn("Falló la autenticación: {}", exception.getMessage());

        // 2. Limpiar el contexto de seguridad para asegurar que no quedan datos residuales [cite: 236-237]
        SecurityContextHolder.clearContext();

        // 3. Invalidar la sesión actual (borrar datos de la sesión fallida) [cite: 238-239]
        if (request.getSession() != null) {
            request.getSession().invalidate();
        }

        // 4. Guardar el mensaje de error en la NUEVA sesión para mostrarlo en el HTML [cite: 240-241]
        // Al llamar a getSession() después de invalidate(), se crea una sesión limpia.
        request.getSession().setAttribute("errorMessage", "El usuario no está registrado en esta aplicación");

        // 5. Redirigir al usuario de vuelta al login [cite: 244-245]
        response.sendRedirect("/login");
    }
}