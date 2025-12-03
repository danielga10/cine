package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalaRepository extends JpaRepository<Sala, Long> {
    Page<Sala> findAll(Pageable pageable);

    Page<Sala> findByNumero(int numero, Pageable pageable);

    long countByNumero(int numero);

    @Query("""
       SELECT DISTINCT s
       FROM Sala s
       LEFT JOIN FETCH s.funciones f
       LEFT JOIN FETCH f.pelicula
       """)
    List<Sala> findAllWithFunciones();

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sala s WHERE s.numero = :numero")
    boolean existsSalaByNumero(@Param("numero") String numero);


}