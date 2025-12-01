package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Sala;
import org.iesalixar.daw2.cine.entities.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
    Page<Trabajador> findAll(Pageable pageable);

    Page<Trabajador> findByNombreContainingIgnoreCase(String name, Pageable pageable);

    long countByNombreContainingIgnoreCase(String name);
    @Query("""
       SELECT DISTINCT t
       FROM Trabajador t
       LEFT JOIN FETCH t.salas
       """)
    List<Trabajador> findAllWithSalas();


}