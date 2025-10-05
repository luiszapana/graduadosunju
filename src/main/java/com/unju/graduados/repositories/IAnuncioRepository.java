package com.unju.graduados.repositories;

import com.unju.graduados.model.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IAnuncioRepository extends JpaRepository<Anuncio, Long>, JpaSpecificationExecutor<Anuncio> {
/*
    @Query("""
        SELECT a FROM Anuncio a
        WHERE (:tipoId IS NULL OR a.tipoAnuncio.id = :tipoId)
          AND (:fechaDesde IS NULL OR a.fechaRegistro >= :fechaDesde)
          AND (:fechaHasta IS NULL OR a.fechaRegistro <= :fechaHasta)
    """)
    Page<Anuncio> findByFilters(
            @Param("tipoId") Long tipoId,
            @Param("fechaDesde") ZonedDateTime fechaDesde,
            @Param("fechaHasta") ZonedDateTime fechaHasta,
            Pageable pageable
    );*/


}

