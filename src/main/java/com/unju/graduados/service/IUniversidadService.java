package com.unju.graduados.service;

import com.unju.graduados.model.Universidad;

import java.util.List;

public interface IUniversidadService {
    List<Universidad> findAll();
    Universidad findById(Long id);
}
