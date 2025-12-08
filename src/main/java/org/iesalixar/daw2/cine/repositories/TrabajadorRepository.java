package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Sala;
import org.iesalixar.daw2.cine.entities.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio para la entidad Trabajador.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre trabajadores.
 * 
 * @author IES Alixar DAW2
 * @version 1.0
 */
public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {
    /**
     * Obtiene todos los trabajadores paginados.
     * 
     * @param pageable Configuración de paginación
     * @return Página de trabajadores
     */
    Page<Trabajador> findAll(Pageable pageable);

    /**
     * Busca trabajadores cuyo nombre contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param name Texto a buscar en el campo nombre
     * @param pageable Configuración de paginación
     * @return Página de trabajadores que coinciden con la búsqueda
     */
    Page<Trabajador> findByNombreContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Cuenta el número total de trabajadores cuyo nombre contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param name Texto a buscar en el campo nombre
     * @return Número total de trabajadores que coinciden con la búsqueda
     */
    long countByNombreContainingIgnoreCase(String name);
    
    /**
     * Obtiene todos los trabajadores con sus salas cargadas (usando JOIN FETCH para evitar N+1 queries).
     * 
     * @return Lista de trabajadores con sus salas asociadas
     */
    @Query("""
       SELECT DISTINCT t
       FROM Trabajador t
       LEFT JOIN FETCH t.sala
       """)
    List<Trabajador> findAllWithSalas();
    
    /**
     * Busca un trabajador por el ID de su sala asignada.
     * 
     * @param id_sala ID de la sala
     * @return Trabajador asignado a esa sala o null si no existe
     */
    Trabajador findBySalaId(Long id_sala);


}