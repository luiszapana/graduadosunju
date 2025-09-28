package com.unju.graduados.services;

import com.unju.graduados.model.Colacion;
import java.util.List;

public interface IColacionService {
    List<Colacion> findAll();
    Colacion findByAnioColacion(Long anioColacion);
    List<Colacion> findByFacultadId(Long facultadId); // opcional

    // 🔥 Métodos CRUD
    Colacion findById(Long id);
    Colacion save(Colacion colacion);
    Colacion update(Long id, Colacion datos);
    void delete(Long id);
}
