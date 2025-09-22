package com.unju.graduados.service;

import com.unju.graduados.model.Colacion;
import java.util.List;

public interface IColacionService {
    List<Colacion> findAll();
    Colacion findByAnioColacion(Long anioColacion);
    List<Colacion> findByFacultadId(Long facultadId); // opcional
}
