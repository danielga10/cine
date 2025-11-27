package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Page<Cliente> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    long countByEmailContainingIgnoreCase(String email);
}
