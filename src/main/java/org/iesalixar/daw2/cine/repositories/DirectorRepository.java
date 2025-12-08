package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Director;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    /**
     * Obtiene todos los directores paginados.
     * 
     * @param pageable Configuración de paginación
     * @return Página de directores
     */
    Page<Director> findAll(Pageable pageable);

    /**
     * Busca directores cuyo nombre contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param nombre Texto a buscar en el campo nombre
     * @param pageable Configuración de paginación
     * @return Página de directores que coinciden con la búsqueda
     */
    Page<Director> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    /**
     * Cuenta el número total de directores cuyo nombre contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param nombre Texto a buscar en el campo nombre
     * @return Número total de directores que coinciden con la búsqueda
     */
    long countByNombreContainingIgnoreCase(String nombre);

    /**
     * Obtiene todos los directores con sus películas cargadas (usando JOIN FETCH para evitar N+1 queries).
     * 
     * @return Lista de directores con sus películas asociadas
     */
    @Query("""
       SELECT DISTINCT d
       FROM Director d
       LEFT JOIN FETCH d.peliculas
       """)
    List<Director> findAllWithPeliculas();

}