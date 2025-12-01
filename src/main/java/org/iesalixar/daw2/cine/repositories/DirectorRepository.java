package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Director;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    Page<Director> findAll(Pageable pageable);

    Page<Director> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    long countByNombreContainingIgnoreCase(String nombre);

}