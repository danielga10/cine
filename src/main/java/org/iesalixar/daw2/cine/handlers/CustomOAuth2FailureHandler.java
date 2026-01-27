package org.iesalixar.daw2.cine.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Handler personalizado para manejar fallos en la autenticación con OAuth2.
 * Este handler se encarga de limpiar el contexto de seguridad, invalidar la
 sesión
 * actual y redirigir al usuario a la página de inicio de sesión, mostrando un
 mensaje
 * de error específico en caso de que la autenticación falle.
 */
@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {
    private static final Logger logger =
            LoggerFactory.getLogger(CustomOAuth2FailureHandler.class);
    /**
     * Maneja los fallos en la autenticación con OAuth2.
     * Este método se ejecuta automáticamente cuando ocurre un fallo de
     autenticación.
     * Realiza las siguientes acciones:
     * - Limpia el contexto de seguridad.
     * - Invalida la sesión actual.
     * - Agrega un mensaje de error a la sesión.
     * - Redirige al usuario a la página de inicio de sesión.
     *
     * @param request El objeto {@link HttpServletRequest} que contiene la
    solicitud HTTP.
     * @param response El objeto {@link HttpServletResponse} que contiene la
    respuesta HTTP.
     * @param exception La excepción de autenticación que indica el motivo del
    fallo.
     * @throws IOException Si ocurre un error de E/S durante la redirección.
     * @throws ServletException Si ocurre un error relacionado con el manejo de
    la solicitud.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,

                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {
        logger.warn("Falló la autenticación: {}", exception.getMessage());
        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();
        // Invalidar la sesión actual
        request.getSession().invalidate();
        // Agregar el mensaje de error como un atributo de sesión
        request.getSession().setAttribute("errorMessage", "El usuario no está registrado en esta aplicación");
                // Redirigir al login con el parámetro de error
                response.sendRedirect("/login");
    }
}
