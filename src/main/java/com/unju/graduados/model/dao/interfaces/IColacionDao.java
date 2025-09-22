package com.unju.graduados.model.dao.interfaces;

import com.unju.graduados.model.Colacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IColacionDao extends JpaRepository<Colacion, Long> {
    Optional<Colacion> findByAnioColacion(Long anioColacion);
}