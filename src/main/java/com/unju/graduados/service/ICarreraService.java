package com.unju.graduados.service;

import com.unju.graduados.model.Carrera;

import java.util.List;

public interface ICarreraService {
    List<Carrera> findAll();
    Carrera findById(Long id);
}
