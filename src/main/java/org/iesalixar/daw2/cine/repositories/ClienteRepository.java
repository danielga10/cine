package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepository {
    Page<Cliente> findAll(Pageable pageable);

    Page<Cliente> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    long countByEmailContainingIgnoreCase(String email);
}
