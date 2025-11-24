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

    Page<Pelicula> findByNameContainingIgnoreCase(String name, Pageable pageable);

    long countByNameContainingIgnoreCase(String name);

    boolean existsPeliculaByCode(String code);
    @Query("SELECT COUNT(p) > 0 FROM Pelicula p WHERE p.code = :code AND p.id != :id")
    boolean existsPeliculaByCodeAndNotId(@Param("code") String code, @Param("id") Long id);
}