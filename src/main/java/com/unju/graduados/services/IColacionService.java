package com.unju.graduados.services;

import com.unju.graduados.model.Colacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IColacionService {
    // Cambiar para aceptar Pageable y devolver Page<Colacion>
    Page<Colacion> findAll(Pageable pageable);

    Colacion findByAnioColacion(Long anioColacion);
    List<Colacion> findByFacultadId(Long facultadId); // opcional

    // 🔥 Métodos CRUD
    Colacion findById(Long id);
    Colacion save(Colacion colacion);
    Colacion update(Long id, Colacion datos);
    void delete(Long id);
}
