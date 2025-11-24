package org.iesalixar.daw2.cine.repositories;

import org.iesalixar.daw2.cine.entities.Boleto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoletoRepository {
    Page<Boleto> findAll(Pageable pageable);

    Page<Boleto> findByCodeContainingIgnoreCase(String code, Pageable pageable);
}
