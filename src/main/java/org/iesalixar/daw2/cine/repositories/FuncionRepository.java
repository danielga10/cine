package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    // Buscar funciones por sala
    Page<Funcion> findBySalaNumero(Integer numero, Pageable pageable);

    // Buscar funciones por película
    Page<Funcion> findByPeliculaTituloContainingIgnoreCase(String titulo, Pageable pageable);
}