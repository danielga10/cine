package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    Page<Pelicula> findAll(Pageable pageable);

    Page<Pelicula> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    long countByTituloContainingIgnoreCase(String name);

    @Query("""
       SELECT DISTINCT p
       FROM Pelicula p
       LEFT JOIN FETCH p.director
       """)
    List<Pelicula> findAllWithDirector();

}