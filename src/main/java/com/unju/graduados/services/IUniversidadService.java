package com.unju.graduados.services;

import com.unju.graduados.model.Universidad;

import java.util.List;

public interface IUniversidadService {
    List<Universidad> findAll();
    Universidad findById(Long id);
}
