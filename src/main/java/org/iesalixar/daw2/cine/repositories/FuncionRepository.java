package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Funcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface FuncionRepository extends JpaRepository{
    Page<Funcion> findAll(Pageable pageable);

    Page<Funcion> findByCodeContainingIgnoreCase(String code, Pageable pageable);

    long countByCodeContainingIgnoreCase(String code);


}
