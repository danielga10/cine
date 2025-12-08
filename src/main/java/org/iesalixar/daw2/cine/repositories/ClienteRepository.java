package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Cliente.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre clientes.
 * 
 * @author IES Alixar DAW2
 * @version 1.0
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    /**
     * Busca clientes cuyo email contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param email Texto a buscar en el campo email
     * @param pageable Configuración de paginación
     * @return Página de clientes que coinciden con la búsqueda
     */
    Page<Cliente> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    
    /**
     * Cuenta el número total de clientes cuyo email contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param email Texto a buscar en el campo email
     * @return Número total de clientes que coinciden con la búsqueda
     */
    long countByEmailContainingIgnoreCase(String email);
}
