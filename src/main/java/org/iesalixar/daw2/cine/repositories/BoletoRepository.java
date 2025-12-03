package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Boleto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    Page<Boleto> findByAsientoContainingIgnoreCase(String asiento, Pageable pageable);
    long countByAsientoContainingIgnoreCase(String asiento);

}
