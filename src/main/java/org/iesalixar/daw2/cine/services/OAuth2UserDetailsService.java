package org.iesalixar.daw2.cine.services;

import org.iesalixar.daw2.cine.entities.Role;
import org.iesalixar.daw2.cine.entities.User;
import org.iesalixar.daw2.cine.repositories.RoleRepository;
import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona la autenticación OAuth2 y crea/actualiza usuarios en la base de datos.
 */
@Service
public class OAuth2UserDetailsService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("=== INICIO OAuth2 loadUser ===");
        
        // Obtener el usuario OAuth2 del proveedor
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        logger.info("Proveedor OAuth2: {}", registrationId);
        logger.info("Atributos OAuth2: {}", oauth2User.getAttributes());
        
        try {
            // Procesar y guardar el usuario en la base de datos
            User user = processOAuth2User(oauth2User, registrationId);
            
            // Convertir roles a authorities de Spring Security
            Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
            
            // Determinar el atributo clave según el proveedor
            String nameAttributeKey = getNameAttributeKey(registrationId);
            
            logger.info("Usuario OAuth2 procesado exitosamente: {}", user.getUsername());
            logger.info("=== FIN OAuth2 loadUser ===");
            
            // Retornar un OAuth2User con las authorities correctas
            return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), nameAttributeKey);
            
        } catch (Exception e) {
            logger.error("Error procesando usuario OAuth2", e);
            throw new OAuth2AuthenticationException("Error procesando usuario OAuth2: " + e.getMessage());
        }
    }

    private String getNameAttributeKey(String registrationId) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return "sub";
            case "gitlab":
                return "sub";
            default:
                return "sub";
        }
    }

    private User processOAuth2User(OAuth2User oauth2User, String provider) {
        String username = extractUsername(oauth2User, provider);
        String email = oauth2User.getAttribute("email");
        
        logger.info("Buscando usuario: {} (email: {})", username, email);
        
        // Buscar usuario existente
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            logger.info("Creando nuevo usuario OAuth2: {}", username);
            user = new User();
            user.setUsername(username);
            user.setPassword(""); // Password vacío para usuarios OAuth2
            user.setEnabled(true);
            
            // Asignar rol USER por defecto
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_USER no encontrado en la base de datos"));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
        } else {
            logger.info("Actualizando usuario OAuth2 existente: {}", username);
        }
        
        // Actualizar información del perfil
        updateUserProfile(user, oauth2User, provider);
        
        // Guardar en base de datos
        user = userRepository.save(user);
        logger.info("Usuario guardado en BD: id={}, username={}", user.getId(), user.getUsername());
        
        return user;
    }

    private String extractUsername(OAuth2User oauth2User, String provider) {
        switch (provider.toLowerCase()) {
            case "google":
                return oauth2User.getAttribute("email");
            case "gitlab":
                // GitLab devuelve username en el atributo username
                String gitlabUsername = oauth2User.getAttribute("username");
                if (gitlabUsername != null && !gitlabUsername.isEmpty()) {
                    return gitlabUsername;
                }
                // Si no hay username, usar email como fallback
                String email = oauth2User.getAttribute("email");
                if (email != null && !email.isEmpty()) {
                    return email;
                }
                // Si no hay email, usar ID como último recurso
                Object id = oauth2User.getAttribute("id");
                return "gitlab_" + id;
            default:
                return oauth2User.getAttribute("email");
        }
    }

    private void updateUserProfile(User user, OAuth2User oauth2User, String provider) {
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture"); // Google
        String avatarUrl = oauth2User.getAttribute("avatar_url"); // GitLab
        
        // Dividir el nombre en firstName y lastName
        if (name != null && !name.isEmpty()) {
            String[] nameParts = name.split(" ", 2);
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        } else {
            // Si no hay nombre, usar username
            user.setFirstName(user.getUsername());
            user.setLastName("");
        }
        
        // Actualizar imagen de perfil
        if (picture != null) {
            user.setImage(picture);
        } else if (avatarUrl != null) {
            user.setImage(avatarUrl);
        }
    }
}
