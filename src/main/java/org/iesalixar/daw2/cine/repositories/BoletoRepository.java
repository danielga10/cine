package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Boleto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Boleto.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre boletos.
 * 
 * @author IES Alixar DAW2
 * @version 1.0
 */
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    /**
     * Busca boletos cuyo asiento contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param asiento Texto a buscar en el campo asiento
     * @param pageable Configuración de paginación
     * @return Página de boletos que coinciden con la búsqueda
     */
    Page<Boleto> findByAsientoContainingIgnoreCase(String asiento, Pageable pageable);
    
    /**
     * Cuenta el número total de boletos cuyo asiento contenga el texto especificado (búsqueda case-insensitive).
     * 
     * @param asiento Texto a buscar en el campo asiento
     * @return Número total de boletos que coinciden con la búsqueda
     */
    long countByAsientoContainingIgnoreCase(String asiento);

}
