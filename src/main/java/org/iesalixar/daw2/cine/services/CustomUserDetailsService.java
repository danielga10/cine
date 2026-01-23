package org.iesalixar.daw2.cine.services;

import org.iesalixar.daw2.cine.entities.Role;
import org.iesalixar.daw2.cine.entities.User;
import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carga los detalles del usuario a partir de su nombre de usuario.
     *
     * @param username El nombre de usuario a buscar.
     * @return Un objeto UserDetails con la información de autenticación del usuario.
     * @throws UsernameNotFoundException Si el usuario no se encuentra en la base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Si el password es null o vacío, el usuario solo puede usar OAuth2
        String password = user.getPassword() != null ? user.getPassword() : "";
        
        // Convierte los roles de usuario en GrantedAuthority
        // Para llamar al User original de Spring y no confundirlo con el nuestro se suele poner el paquete completo
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(password)
                .authorities(user.getRoles().stream()
                        .map(Role::getName)
                        .toList()
                        .toArray(new String[0]))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
}