package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio para la entidad Sala.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre salas.
 * 
 * @author IES Alixar DAW2
 * @version 1.0
 */
public interface SalaRepository extends JpaRepository<Sala, Long> {
    /**
     * Obtiene todas las salas paginadas.
     * 
     * @param pageable Configuración de paginación
     * @return Página de salas
     */
    Page<Sala> findAll(Pageable pageable);

    /**
     * Busca salas cuyo número contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param numero Texto a buscar en el campo número
     * @param pageable Configuración de paginación
     * @return Página de salas que coinciden con la búsqueda
     */
    Page<Sala> findByNumeroContainingIgnoreCase(String numero, Pageable pageable);

    /**
     * Cuenta el número total de salas cuyo número contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param numero Texto a buscar en el campo número
     * @return Número total de salas que coinciden con la búsqueda
     */
    long countByNumeroContainingIgnoreCase(String numero);

    /**
     * Obtiene todas las salas con sus funciones y películas cargadas (usando JOIN FETCH para evitar N+1 queries).
     * 
     * @return Lista de salas con sus funciones y películas asociadas
     */
    @Query("""
       SELECT DISTINCT s
       FROM Sala s
       LEFT JOIN FETCH s.funciones f
       LEFT JOIN FETCH f.pelicula
       """)
    List<Sala> findAllWithFunciones();

    /**
     * Verifica si existe una sala con el número especificado.
     * 
     * @param numero Número de la sala a verificar
     * @return true si existe una sala con ese número, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sala s WHERE s.numero = :numero")
    boolean existsSalaByNumero(@Param("numero") String numero);
}