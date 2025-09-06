package com.unju.graduados.model.dao.interfaces;

import com.unju.graduados.model.Anuncio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;

public interface IAnuncioDao extends JpaRepository<Anuncio, Long> {
    @Query("SELECT a FROM Anuncio a WHERE (:tipoId IS NULL OR a.tipoAnuncio.id = :tipoId) " +
            "AND (:desde IS NULL OR a.fechaRegistro >= :desde) AND (:hasta IS NULL OR a.fechaRegistro <= :hasta)")
    Page<Anuncio> findByFilters(@Param("tipoId") Long tipoId,
                                @Param("desde") ZonedDateTime desde,
                                @Param("hasta") ZonedDateTime hasta,
                                Pageable pageable);
}
