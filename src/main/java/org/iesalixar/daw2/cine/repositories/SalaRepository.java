package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    // Buscar salas por número
    Page<Sala> findByNumero(Integer numero, Pageable pageable);

    long countByNumero(Integer numero);
}