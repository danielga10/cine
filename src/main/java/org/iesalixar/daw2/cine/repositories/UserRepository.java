package org.iesalixar.daw2.cine.repositories;

import
        org.iesalixar.daw2.cine.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
/**
 * Repositorio para la entidad User que extiende JpaRepository.
 * Proporciona operaciones CRUD y consultas personalizadas para la entidad User.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username el nombre de usuario a buscar.
     * @return un Optional que contiene el usuario si se encuentra, o vacío si
    no existe.
     */
    Optional<User> findByUsername(String username);
}