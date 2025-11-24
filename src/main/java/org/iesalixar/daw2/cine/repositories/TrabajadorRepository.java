package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {

    // Buscar trabajador por nombre
    Page<Trabajador> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    long countByNombreContainingIgnoreCase(String nombre);
}
