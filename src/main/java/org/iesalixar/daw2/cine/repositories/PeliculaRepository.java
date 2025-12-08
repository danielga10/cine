package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio para la entidad Pelicula.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre películas.
 * 
 * @author IES Alixar DAW2
 * @version 1.0
 */
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    /**
     * Obtiene todas las películas paginadas.
     * 
     * @param pageable Configuración de paginación
     * @return Página de películas
     */
    Page<Pelicula> findAll(Pageable pageable);

    /**
     * Busca películas cuyo título contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param titulo Texto a buscar en el campo título
     * @param pageable Configuración de paginación
     * @return Página de películas que coinciden con la búsqueda
     */
    Page<Pelicula> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    /**
     * Cuenta el número total de películas cuyo título contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param name Texto a buscar en el campo título
     * @return Número total de películas que coinciden con la búsqueda
     */
    long countByTituloContainingIgnoreCase(String name);

    /**
     * Obtiene todas las películas con su director cargado (usando JOIN FETCH para evitar N+1 queries).
     * 
     * @return Lista de películas con sus directores asociados
     */
    @Query("""
       SELECT DISTINCT p
       FROM Pelicula p
       LEFT JOIN FETCH p.director
       """)
    List<Pelicula> findAllWithDirector();

}