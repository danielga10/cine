package org.iesalixar.daw2.cine.repositories;

import java.util.List;
import java.util.Optional;

import org.iesalixar.daw2.cine.entities.Director;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    Page<Director> findAll(Pageable pageable);

    Page<Director> findByNameContainingIgnoreCase(String name, Pageable pageable);

    long countByNameContainingIgnoreCase(String name);

    boolean existsDirectorByCode(String code);
    @Query("SELECT COUNT(p) > 0 FROM Director p WHERE p.code = :code AND p.id != :id")
    boolean existsDirectorByCodeAndNotId(@Param("code") String code, @Param("id") Long id);
}