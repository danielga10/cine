package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    // Buscar por asiento
    Page<Boleto> findByAsientoContainingIgnoreCase(String asiento, Pageable pageable);

    // Buscar por cliente
    Page<Boleto> findByClienteNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Buscar por función
    Page<Boleto> findByFuncionTituloContainingIgnoreCase(String titulo, Pageable pageable);
}
