package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad User.
 * Gestiona la persistencia de usuarios locales y de Discord.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByDiscordId(String discordId);
    boolean existsByUsername(String username);
    //Optional<User> findByEmail(String email);
}