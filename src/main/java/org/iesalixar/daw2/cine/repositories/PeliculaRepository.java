package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    // Buscar películas por título
    Page<Pelicula> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    long countByTituloContainingIgnoreCase(String titulo);
}
