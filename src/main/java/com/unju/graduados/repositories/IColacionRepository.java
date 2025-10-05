package com.unju.graduados.repositories;

import com.unju.graduados.model.Colacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IColacionRepository extends JpaRepository<Colacion, Long> {
    Optional<Colacion> findByAnioColacion(Long anioColacion);
    List<Colacion> findAllByOrderByFechaColacionDesc();

}