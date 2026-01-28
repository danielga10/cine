package org.iesalixar.daw2.cine.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.iesalixar.daw2.cine.entities.Role;
import org.iesalixar.daw2.cine.entities.User;
import org.iesalixar.daw2.cine.repositories.RoleRepository;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String usernameToSearch = null;

        // 1. Lógica de extracción (Mantenemos la que ya tenías)
        if ("discord".equals(clientRegistrationId)) {
            usernameToSearch = (String) attributes.get("username");
            logger.info("Login OAuth2: Discord detectado. Usuario: {}", usernameToSearch);
        } else if ("google".equals(clientRegistrationId)) {
            usernameToSearch = (String) attributes.get("email");
            logger.info("Login OAuth2: Google detectado. Email: {}", usernameToSearch);
        } else if ("gitlab".equals(clientRegistrationId)) {
            usernameToSearch = (String) attributes.get("preferred_username");
            logger.info("Login OAuth2: GitLab detectado. Usuario: {}", usernameToSearch);
        }

        if (usernameToSearch == null) {
            throw new OAuth2AuthenticationException("Error: No se pudo identificar al usuario desde " + clientRegistrationId);
        }

        // 2. CAMBIO PRINCIPAL: AUTO-REGISTRO
        // Si no existe, lo creamos en lugar de lanzar excepción
        if (!userRepository.existsByUsername(usernameToSearch)) {
            logger.info("El usuario {} no existe. Procediendo al auto-registro...", usernameToSearch);
            registerNewUser(usernameToSearch, oAuth2User, clientRegistrationId);
        }

        try {
            // 3. Login estándar (ahora el usuario ya existe seguro)
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(usernameToSearch);

            UsernamePasswordAuthenticationToken internalAuthentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(internalAuthentication);
            response.sendRedirect("/");

        } catch (Exception e) {
            logger.error("Error en login tras auto-registro: {}", e.getMessage());
            throw new OAuth2AuthenticationException("Error interno al procesar el login.");
        }
    }

    /**
     * Crea un nuevo usuario en la base de datos con datos básicos y rol por defecto.
     */
    private void registerNewUser(String username, OAuth2User oAuth2User, String provider) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword("OAUTH2_USER_NO_PASS"); // Contraseña dummy para evitar error de Null
        newUser.setEnabled(true);
        newUser.setCreatedDate(LocalDateTime.now());
        newUser.setLastModifiedDate(LocalDateTime.now());
        newUser.setLastPasswordChangeDate(LocalDateTime.now());

        // Lógica para intentar sacar Nombre y Apellido según el proveedor
        String firstName = "Usuario";
        String lastName = provider;

        Map<String, Object> attrs = oAuth2User.getAttributes();

        if ("google".equals(provider)) {
            if (attrs.containsKey("given_name")) firstName = (String) attrs.get("given_name");
            if (attrs.containsKey("family_name")) lastName = (String) attrs.get("family_name");
        } else if ("gitlab".equals(provider)) {
            if (attrs.containsKey("name")) firstName = (String) attrs.get("name");
        } else if ("discord".equals(provider)) {
            if (attrs.containsKey("global_name")) firstName = (String) attrs.get("global_name");
        }

        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);

        Optional<Role> defaultRole = roleRepository.findByName("ROLE_USER");

        if (defaultRole.isPresent()) {
            newUser.setRoles(Collections.singleton(defaultRole.get()));
        } else {
            logger.warn("¡OJO! No se encontró el rol 'ROLE_USER'. El usuario se creará sin permisos.");
        }

        userRepository.save(newUser);
        logger.info("Usuario {} creado exitosamente en BD local.", username);
    }
}