package org.iesalixar.daw2.cine.handlers;

import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.iesalixar.daw2.cine.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import
        org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Handler personalizado para manejar eventos de éxito en la autenticación con
 OAuth2.
 * Este handler verifica si el usuario autenticado con un proveedor externo (por
 ejemplo, GitHub)
 * ya está registrado en la base de datos de la aplicación. Si no está registrado,
 se redirige
 * al usuario a una página de registro. En caso contrario, el usuario es
 redirigido a la página principal.
 */
@Component
public class CustomOAuth2SuccessHandler implements
        AuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    /**
     * Maneja el evento de autenticación exitosa con OAuth2.
     * Verifica si el usuario autenticado ya existe en la base de datos de la
     aplicación.
     * Si no existe, se redirige a una página de registro. Si existe, se redirige
     al inicio.
     *
     * @param request Objeto {@link HttpServletRequest} que contiene la
    solicitud HTTP.
     * @param response Objeto {@link HttpServletResponse} que contiene la
    respuesta HTTP.
     * @param authentication Objeto {@link Authentication} que representa al
    usuario autenticado.
     * @throws IOException Si ocurre un error en la redirección.
     * @throws ServletException Si ocurre un error en el manejo de la solicitud.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws
            IOException, ServletException {
        // Obtener el objeto OAuth2User del usuario autenticado
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // Obtener el nombre de usuario (por ejemplo, el login de GitHub)
        String username = oAuth2User.getAttribute("login");
        // Verificar si el usuario está registrado en la base de datos
        if (!userRepository.existsByUsername(username)) {
            // Lanzar una excepción estándar de Spring Security. Esto hará que se gestione por el handler CustomOAuth2FailureHandler
            throw new OAuth2AuthenticationException("El usuario " + username +
                    " no está registrado en el sistema.");
        }
        // Cargar los detalles del usuario desde la base de datos
        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(username);
        // Crear un nuevo objeto Authentication con los detalles del usuario cargados
        // userDetails.getAuthorities() contiene los detalles de los roles y permisos que se han establecido en el customUserDetailsService.loadUserByUsername
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        // Establecer el nuevo objeto Authentication en el SecurityContextHolder

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // Si el usuario está registrado, redirigir a la página principal
        response.sendRedirect("/");
    }
}
