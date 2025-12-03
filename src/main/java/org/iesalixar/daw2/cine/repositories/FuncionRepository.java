package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Funcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    Page<Funcion> findAll(Pageable pageable);

    Page<Funcion> findByCodeContainingIgnoreCase(String code, Pageable pageable);


}
