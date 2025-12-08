package org.iesalixar.daw2.cine.repositories;

import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Funcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionRepository extends JpaRepository<Funcion, Long>{
    /**
     * Obtiene todas las funciones paginadas.
     * 
     * @param pageable Configuración de paginación
     * @return Página de funciones
     */
    Page<Funcion> findAll(Pageable pageable);

    /**
     * Busca funciones cuyo código contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param code Texto a buscar en el campo code
     * @param pageable Configuración de paginación
     * @return Página de funciones que coinciden con la búsqueda
     */
    Page<Funcion> findByCodeContainingIgnoreCase(String code, Pageable pageable);

    /**
     * Busca funciones cuya película tenga un título que contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param titulo Texto a buscar en el título de la película
     * @param pageable Configuración de paginación
     * @return Página de funciones que coinciden con la búsqueda
     */
    Page<Funcion> findByPeliculaTituloContainingIgnoreCase(String titulo, Pageable pageable);

    /**
     * Cuenta el número total de funciones cuyo código contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param code Texto a buscar en el campo code
     * @return Número total de funciones que coinciden con la búsqueda
     */
    long countByCodeContainingIgnoreCase(String code);

    /**
     * Busca una función por su código exacto.
     * 
     * @param code Código de la función
     * @return Optional con la función encontrada o vacío si no existe
     */
    Optional<Funcion> findByCode(String code);
    
    /**
     * Busca una función por su código excluyendo un ID específico.
     * Útil para validar que no exista otra función con el mismo código al actualizar.
     * 
     * @param code Código de la función
     * @param id ID a excluir de la búsqueda
     * @return Optional con la función encontrada o vacío si no existe
     */
    Optional<Funcion> findByCodeAndIdNot(String code, Long id);
}
