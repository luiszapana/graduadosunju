package com.unju.graduados.repositories;

import com.unju.graduados.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICarreraRepository extends JpaRepository<Carrera, Long> {
    List<Carrera> findByNombreContainingIgnoreCase(String nombre);
    List<Carrera> findByFacultadId(Long facultadId);
    List<Carrera> findByFacultadIdAndNombreContainingIgnoreCase(Long facultadId, String nombre);
}
