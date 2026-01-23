package org.iesalixar.daw2.cine.services;

import org.iesalixar.daw2.cine.entities.Role;
import org.iesalixar.daw2.cine.entities.User;
import org.iesalixar.daw2.cine.repositories.RoleRepository;
import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio personalizado para manejar usuarios autenticados mediante OAuth2.
 * Procesa usuarios de Google, GitLab, y otros proveedores OAuth2.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Carga el usuario de OAuth2 y crea/actualiza el usuario en la base de datos.
     *
     * @param userRequest Solicitud de usuario OAuth2 con tokens y datos del proveedor
     * @return Usuario OAuth2 cargado con atributos del proveedor
     * @throws OAuth2AuthenticationException Si hay un error en la autenticación
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("Procesando usuario OAuth2...");
        
        // Cargar información del usuario desde el proveedor OAuth2
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        // Obtener información del proveedor (google, gitlab, etc.)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        logger.info("Proveedor OAuth2: {}", registrationId);
        
        // Procesar usuario según el proveedor
        processOAuthUser(oauth2User, registrationId);
        
        return oauth2User;
    }

    /**
     * Procesa y crea/actualiza el usuario en la base de datos local.
     *
     * @param oauth2User Usuario OAuth2 con atributos del proveedor
     * @param provider Nombre del proveedor (google, gitlab, etc.)
     */
    private void processOAuthUser(OAuth2User oauth2User, String provider) {
        String email = oauth2User.getAttribute("email");
        String username = extractUsername(oauth2User, provider);
        
        logger.info("Procesando usuario OAuth2 - Username: {}, Email: {}, Provider: {}", username, email, provider);
        
        // Buscar si el usuario ya existe por username
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            // Crear nuevo usuario
            logger.info("Creando nuevo usuario OAuth2: {}", username);
            user = new User();
            user.setUsername(username);
            user.setEnabled(true);
            
            // Asignar rol USER por defecto
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_USER no encontrado"));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
        } else {
            logger.info("Actualizando usuario OAuth2 existente: {}", username);
        }
        
        // Actualizar información del perfil
        updateUserProfile(user, oauth2User, provider);
        
        // Guardar usuario
        userRepository.save(user);
        logger.info("Usuario OAuth2 guardado: {}", username);
    }

    /**
     * Extrae el username del usuario OAuth2 según el proveedor.
     *
     * @param oauth2User Usuario OAuth2 con atributos
     * @param provider Nombre del proveedor
     * @return Username del usuario
     */
    private String extractUsername(OAuth2User oauth2User, String provider) {
        switch (provider.toLowerCase()) {
            case "google":
                // Google usa el email como identificador único
                return oauth2User.getAttribute("email");
            case "gitlab":
                // GitLab tiene username específico
                String gitlabUsername = oauth2User.getAttribute("username");
                return gitlabUsername != null ? gitlabUsername : oauth2User.getAttribute("email");
            default:
                // Por defecto, usar email
                return oauth2User.getAttribute("email");
        }
    }

    /**
     * Actualiza el perfil del usuario con información del proveedor OAuth2.
     *
     * @param user Usuario local a actualizar
     * @param oauth2User Usuario OAuth2 con atributos
     * @param provider Nombre del proveedor
     */
    private void updateUserProfile(User user, OAuth2User oauth2User, String provider) {
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture"); // Google
        String avatarUrl = oauth2User.getAttribute("avatar_url"); // GitLab
        
        // Dividir el nombre en firstName y lastName
        if (name != null && !name.isEmpty()) {
            String[] nameParts = name.split(" ", 2);
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        }

        
        // No establecer password para usuarios OAuth2 (usarán siempre OAuth2 para login)
        // La validación @NotEmpty en User.password debe hacerse solo en formularios, no en OAuth2
        if (user.getPassword() == null) {
            user.setPassword(""); // Password vacío para usuarios OAuth2
        }
    }
}
