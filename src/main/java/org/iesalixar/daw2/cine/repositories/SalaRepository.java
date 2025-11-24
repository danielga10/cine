package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalaRepository extends JpaRepository<Sala, Long> {
    Page<Sala> findAll(Pageable pageable);

    Page<Sala> findByNumeroContainingIgnoreCase(String numero, Pageable pageable);

    long countByNumeroContainingIgnoreCase(String numero);

}